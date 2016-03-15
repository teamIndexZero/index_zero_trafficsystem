package kcl.teamIndexZero.traffic.simulator.osm;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoSegment;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SAX Xml handler to read elements from OpenStreetMap.
 * <p>
 * Java SAX parser will invoke certain methods (overridden from {@link DefaultHandler}) of this class when it encounters
 * certain elements, attributes or other XML events. Different handle* methods of this class are actually handling different
 * XML parse events (bounds element found, tag element found, etc.)
 */
class OsmSaxHandler extends DefaultHandler {
    // TODO to complete links, we probably want to have Map<NodeID, WayID> participating, and every time we read
    // way > nd, we push that in. After all ways are read, we just collect these links altogether and voila we're good.

    public static final String BOUNDS_ELEMENT = "bounds";
    public static final String NODE_ELEMENT = "node";
    public static final String WAY_ELEMENT = "way";
    public static final String NODE_WITHIN_WAY_ELEMENT = "nd";
    public static final String TAG_ELEMENT = "tag";

    private static final Logger LOG = Logger.getLoggerInstance(OsmSaxHandler.class.getSimpleName());

    private Set<String> interestingElements = new HashSet<>(Arrays.asList(BOUNDS_ELEMENT, NODE_ELEMENT, WAY_ELEMENT, NODE_WITHIN_WAY_ELEMENT, TAG_ELEMENT));
    private Map<String, GeoPoint> points = new HashMap<>();
    private Map<String, List<String>> possibleJunctions = new HashMap<>();

    private GeoPolyline currentRoadPolyline = null;
    private String currentRoadId;
    private String currentRoadName;
    private Integer currentRoadLanes;
    private int currentRoadLayer = 0;
    private boolean isCurrentRoadOneWay = false;

    private boolean isBoundsHandled = false;
    private double boundsMinLon;
    private double boundsMinLat;
    private double boundsMaxLat;
    private double boundsMaxLon;

    private Map<String, RoadDescription> roadDescriptions = new HashMap<>();
    private List<JunctionDescription> junctionDescriptions = new ArrayList<>();

    /**
     * Constructor, we pass in the result which is to be filled by this class methods.
     */
    public OsmSaxHandler() {
    }

    /**
     * Gets a distance in meters between two arbitrary points in WGS-84. Spherical abstraciton is used with no further
     * detailization of geoid.
     *
     * @param lat1 point 1 latitude
     * @param lon1 point 1 longitude
     * @param lat2 point 2 latitude
     * @param lon2 point 2 longitude
     * @return distance in meters.
     */
    private static double getDistanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000;

        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) + Math.cos(phi1) * Math.cos(phi2) * Math.sin(deltaLambda / 2)
                * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public static RoadDescription[] splitRoadDescriptionAtPoint(RoadDescription roadDescription, GeoPoint pivotPoint) {
        List<GeoSegment> firstSegments = new ArrayList<>();
        List<GeoSegment> secondSegments = new ArrayList<>();
        boolean afterPivot = false;
        List<GeoSegment> segments = roadDescription.getGeoPolyline().getSegments();

        for (GeoSegment s : segments) {
            if (s.start.equals(pivotPoint)) {
                afterPivot = true;
            }
            if (afterPivot) {
                secondSegments.add(s);
            } else {
                firstSegments.add(s);
            }
        }

        return new RoadDescription[]{
                new RoadDescription(
                        new ID(roadDescription.getId().toString() + "_s1"),
                        roadDescription.getRoadName(),
                        new GeoPolyline(firstSegments),
                        roadDescription.getLaneCountForward(),
                        roadDescription.getLaneCountBackward(),
                        roadDescription.getLayer()
                ),
                new RoadDescription(
                        new ID(roadDescription.getId().toString() + "_s2"),
                        roadDescription.getRoadName(),
                        new GeoPolyline(secondSegments),
                        roadDescription.getLaneCountForward(),
                        roadDescription.getLaneCountBackward(),
                        roadDescription.getLayer()
                )
        };
    }

    private NodePositionInRoad getNodePositionInRoad(String nodeId, RoadDescription road) {
        GeoPoint point = points.get(nodeId);
        if (road.getGeoPolyline().getStartPoint().equals(point)) {
            return NodePositionInRoad.START;
        }
        if (road.getGeoPolyline().getFinishPoint().equals(point)) {
            return NodePositionInRoad.END;
        }

        return NodePositionInRoad.NOT_START_OR_END;
    }

    /**
     * {@inheritDoc}
     */
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if (!interestingElements.contains(qName)) {
            return;
        }

        switch (qName) {
            case BOUNDS_ELEMENT:
                handleBounds(attributes);
                break;
            case NODE_ELEMENT:
                handleNode(attributes);
                break;
            case WAY_ELEMENT:
                handleWayOpen(attributes);
                break;
            case NODE_WITHIN_WAY_ELEMENT:
                handleNodeWithinWay(attributes);
                break;
            case TAG_ELEMENT:
                handleTag(attributes);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void endElement(String uri, String localName,
                           String qName) throws SAXException {
        if (!interestingElements.contains(qName)) {
            return;
        }
        if (WAY_ELEMENT.equals(qName)) {
            handleWayClose();
        }
    }

    //        <tag k="name" v="Pall Mall"/>
    //        <tag k="oneway" v="yes"/>
    //        <tag k="maxspeed" v="30 mph"/>
    //        <tag k="lanes" v="1"/>
    private void handleTag(Attributes attributes) {
        if (currentRoadId == null) {
            return;
        }

        String key = attributes.getValue("k");
        String value = attributes.getValue("v");
        if ("name".equals(key)) {
            this.currentRoadName = value;
        }
        if ("layer".equals(key)) {
            this.currentRoadLayer = Integer.valueOf(value);
        }
        if ("oneway".equals(key) && "yes".equals(value)) {
            this.isCurrentRoadOneWay = true;
        }
        if ("lanes".equals(key)) {
            this.currentRoadLanes = Integer.valueOf(value);
        }
    }

    // <way id="8032768" version="20" timestamp="2016-03-02T22:08:00Z" uid="352985" user="ecatmur"
    //          changeset="32783057">
    //  <nd ref="108192"/>
    //  ...
    // </way>
    private void handleNodeWithinWay(Attributes attributes) {

        String nodeId = attributes.getValue("ref");

        // at this moment, it is at least 2nd node in graph. So we can create a RoadDescriptor with a segment, and record
        // our current node as previous:
        GeoPoint thisPoint = points.get(nodeId);
        if (thisPoint != null) {
            currentRoadPolyline.addPoint(thisPoint);
            // add this to the candidates for junctions and/or links
            List<String> connectedRoadIDs = possibleJunctions.get(nodeId);
            if (connectedRoadIDs == null) {
                connectedRoadIDs = new ArrayList<>();
                possibleJunctions.put(nodeId, connectedRoadIDs);
            }
            connectedRoadIDs.add(currentRoadId);
        }

    }

    //        <way id="8032768" version="20" timestamp="2016-03-02T22:08:00Z"
    //              uid="352985" user="ecatmur" changeset="32783057">
    //        <nd ref="108192"/>
    //        <nd ref="108193"/>
    //        <tag k="lit" v="yes"/>
    //        </way>
    private void handleWayOpen(Attributes attributes) {
        currentRoadId = attributes.getValue("id");
        currentRoadPolyline = new GeoPolyline();
        currentRoadLayer = 0;
        isCurrentRoadOneWay = false;
        currentRoadLanes = 2;
    }

    private void handleWayClose() {
        int laneCountForward;
        int laneCountBackward;
        if (isCurrentRoadOneWay) {
            laneCountForward = currentRoadLanes;
            laneCountBackward = 0;
        } else {
            laneCountBackward = (int) Math.floor(currentRoadLanes / 2);
            laneCountForward = currentRoadLanes - laneCountBackward;
        }

        RoadDescription description = new RoadDescription(
                new ID(currentRoadId),
                currentRoadName,
                currentRoadPolyline,
                laneCountForward,
                laneCountBackward,
                currentRoadLayer
        );

        if (currentRoadPolyline.getSegments().size() > 0) {
            roadDescriptions.put(currentRoadId, description);
        }
        currentRoadPolyline = null;
        currentRoadId = null;
        currentRoadName = null;
    }

    // <node id="2684347240" version="1" timestamp="2016-03-02T22:08:00Z" uid="1016290"
    // user="Amaroussi" changeset="20704202" lat="51.4971865" lon="-0.1002579"/>
    private void handleNode(Attributes attributes) {
        if (!isBoundsHandled) {
            throw new IllegalStateException("Can not handle node as bounds are not loaded yet = no conversion to meters.");
        }
        double lat = Double.parseDouble(attributes.getValue("lat"));
        double lon = Double.parseDouble(attributes.getValue("lon"));
        String nodeId = attributes.getValue("id");
        points.put(nodeId,
                new GeoPoint(
                        (lon < this.boundsMinLon ? -1 : 1) * getDistanceMeters(this.boundsMinLat, this.boundsMinLon, this.boundsMinLat, lon),
                        (lat < this.boundsMinLat ? -1 : 1) * getDistanceMeters(this.boundsMinLat, this.boundsMinLon, lat, this.boundsMinLon),
                        new ID(nodeId)));
    }

    // <bounds minlon="-0.10625" minlat="51.49189" maxlon="-0.09609"
    //                          maxlat="51.49982" origin="osmconvert 0.7T"/>
    private void handleBounds(Attributes attributes) {
        isBoundsHandled = true;
        this.boundsMinLat = Double.parseDouble(attributes.getValue("minlat"));
        this.boundsMinLon = Double.parseDouble(attributes.getValue("minlon"));

        this.boundsMaxLat = Double.parseDouble(attributes.getValue("maxlat"));
        this.boundsMaxLon = Double.parseDouble(attributes.getValue("maxlon"));

    }

    private void createLinkAndJunctionDescriptions() {
        // Sometimes, roads are connected in OSM  via a 'middle part'. With this, we want to split these into two parts
        // as individual sub-roads, and add both of them to the connected IDs list.
        filterAndSplitRoadsWhichIntersectInMiddle();

        possibleJunctions.entrySet().forEach(entry -> {
            String nodeId = entry.getKey();
            if (!points.keySet().contains(nodeId)) {
                return;
            }
            List<String> connectedRoadIDs = entry.getValue();
            if (connectedRoadIDs.size() < 2) {
                return;
            }

            Map<ID, JunctionDescription.RoadDirection> connectedRoadIDsWithDirection = new HashMap<>();
            for (String roadId : connectedRoadIDs) {
                RoadDescription description = roadDescriptions.get(roadId);
                if (description != null) {
                    // we have it as is - it belongs to a map, and also it was not removed during splitting process.
                    NodePositionInRoad nodeStatus = getNodePositionInRoad(nodeId, description);
                    switch (nodeStatus) {
                        case START:
                            connectedRoadIDsWithDirection.put(description.getId(), JunctionDescription.RoadDirection.OUTGOING);
                            break;
                        case END:
                            connectedRoadIDsWithDirection.put(description.getId(), JunctionDescription.RoadDirection.INCOMING);
                            break;
                        case NOT_START_OR_END:
                            throw new IllegalStateException("Encountered a road which has junction in the middle. This " +
                                    "should have been handled by filterAndSplitRoadsWhichIntersectInMiddle");
                    }
                } else {
                    // we don't have that road by ID: possible 2 cases:
                    //   1. It does not belong to a map - skip it.
                    //   2. It has been split. We can find the new leaves, as their IDs have a specific form of {original_id}_s[12], like
                    //      4421232_s1, or if split happened multiple times, 4421232_s1_s2_s1 - so with startsWith.

                    // First of all, we find all sub-roads (the ones into which original road was transformed)
                    roadDescriptions.values()
                            .stream()
                            // First of all, we find all sub-roads (the ones into which original road was split, judging by ID)
                            .filter(desc -> desc.getId().toString().startsWith(roadId + "_s") && desc.getGeoPolyline().getSegments().size() > 0)
                            // Of these candidates, we do the check - if junction belongs to any of these (and position)
                            .forEach((desc -> {
                                // we get the position of the node related to subroad.
                                NodePositionInRoad nodeStatus = getNodePositionInRoad(nodeId, desc);
                                switch (nodeStatus) {
                                    case START:
                                        connectedRoadIDsWithDirection.put(desc.getId(), JunctionDescription.RoadDirection.OUTGOING);
                                        break;
                                    case END:
                                        connectedRoadIDsWithDirection.put(desc.getId(), JunctionDescription.RoadDirection.INCOMING);
                                        break;
                                    default:
                                        // perfectly legit here - this just means that the segment we are looking at does
                                        // not touch the point of interest.
                                        break;
                                }
                            }));
                }

            }

            GeoPoint geoPoint = points.get(nodeId);

            // TODO traffic light for a junction!
            if (geoPoint != null) {
                JunctionDescription junctionDescription = new JunctionDescription(
                        new ID("junction_" + nodeId),
                        connectedRoadIDsWithDirection,
                        false,
                        geoPoint);
                junctionDescriptions.add(junctionDescription);
            }
        });
    }

    private void filterAndSplitRoadsWhichIntersectInMiddle() {
        //remove the roads which are empty (due to bounding box cutting)
        roadDescriptions.entrySet()
                .removeIf(entry -> entry.getValue() == null || entry.getValue().getGeoPolyline().getSegments().size() < 1);

        // for every possible junction, that is (PointID -> List<RoadId>), or roads grouped by point.
        possibleJunctions.entrySet().forEach(entry -> {
            // only interested in nodes which belong to more than one road
            if (entry.getValue() == null || entry.getValue().size() < 2) {
                return;
            }

            List<String> connectedRoadIDs = entry.getValue();
            String nodeId = entry.getKey();
            GeoPoint node = points.get(nodeId);
            // referncing node which is off-screen.
            if (node == null) {
                return;
            }

            connectedRoadIDs
                    .forEach(roadId -> {
                        RoadDescription road = roadDescriptions.get(roadId);
                        if (road == null) {
                            // maybe it has been already split, let's try to find the parts:
                            List<RoadDescription> children = roadDescriptions
                                    .values()
                                    .stream()
                                    .filter(descr -> descr.getId().toString().startsWith(roadId + "_s"))
                                    .collect(Collectors.toList());
                            if (children.size() == 0) {
                                LOG.log_Warning("Found orphaned road. Not attempting to split.");
                                return;
                            }

                            for (RoadDescription dec : children) {
                                if (dec.getGeoPolyline().containsPoint(node)) {
                                    road = dec;
                                }
                            }
                            if (road == null) {
                                throw new IllegalStateException("Could not terminate split procedure. Found a road " +
                                        "which is not on list. It should have been already split, but none of " +
                                        "chunks contains given point");
                            }
                        }
                        NodePositionInRoad positionInRoad = getNodePositionInRoad(nodeId, road);
                        if (positionInRoad == NodePositionInRoad.NOT_START_OR_END) {
                            RoadDescription[] split = splitRoadDescriptionAtPoint(road, points.get(nodeId));
                            RoadDescription fromStartToMiddle = split[0];
                            RoadDescription fromMiddleToEnd = split[1];

                            // add new half-roads to the road descriptions, remove the old one
                            roadDescriptions.put(fromStartToMiddle.getId().toString(), fromStartToMiddle);
                            roadDescriptions.put(fromMiddleToEnd.getId().toString(), fromMiddleToEnd);
                            roadDescriptions.remove(road.getId().toString());
                        }
                    });
        });

        //remove the roads which are empty (due to bounding box cutting)
        roadDescriptions.entrySet()
                .removeIf(entry -> entry.getValue() == null || entry.getValue().getGeoPolyline().getSegments().size() < 1);
    }

    /**
     * Get results parsed out from an OpenStreetMap XML file. Some additional processing and improvement of data is required,
     * and also we may need to have a signal for XML reading end to get to this step.
     *
     * @return parse result - basically, bounding box, list of junction descriptions and list of road descriptions.
     */
    public OsmParseResult collectResult() {
        createLinkAndJunctionDescriptions();
        OsmParseResult result = new OsmParseResult();

        //bounding box
        GeoSegment segment = new GeoSegment(
                new GeoPoint(0, 0),
                new GeoPoint(
                        getDistanceMeters(this.boundsMinLat, this.boundsMinLon, this.boundsMinLat, boundsMaxLon),
                        getDistanceMeters(this.boundsMinLat, this.boundsMinLon, boundsMaxLat, this.boundsMinLon)
                ));
        LOG.log_Debug("Got bounds ", segment);
        result.boundingBox = segment;

        //roads
        result.roadDescriptions.addAll(roadDescriptions.values());

        // junctions
        result.junctionDescriptions.addAll(junctionDescriptions);

        return result;
    }

    private enum NodePositionInRoad {
        START,
        END,
        NOT_START_OR_END
    }
}
