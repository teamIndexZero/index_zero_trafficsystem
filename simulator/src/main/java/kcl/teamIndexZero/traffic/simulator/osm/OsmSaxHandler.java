package kcl.teamIndexZero.traffic.simulator.osm;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.LinkDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoSegment;
import kcl.teamIndexZero.traffic.simulator.data.links.LinkType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

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

    private static final Logger LOG = Logger.getLoggerInstance(OsmSaxHandler.class.getName());

    private Set<String> interestingElements = new HashSet<>(Arrays.asList(BOUNDS_ELEMENT, NODE_ELEMENT, WAY_ELEMENT, NODE_WITHIN_WAY_ELEMENT, TAG_ELEMENT));
    private Map<String, GeoPoint> points = new HashMap<>();
    private Map<String, List<String>> possibleJunctionsOrLinks = new HashMap<>();

    private GeoPolyline currentRoadPolyline = null;
    private int currentRoadLayer = 0;
    private String currentRoadId;
    private String currentRoadName;

    private double boundsMinLon;
    private double boundsMinLat;
    private boolean boundsHandled = false;
    private double boundsMaxLat;
    private double boundsMaxLon;

    private Map<String, RoadDescription> roadDescriptions = new HashMap<>();
    private List<JunctionDescription> junctionDescriptions = new ArrayList<>();
    private List<LinkDescription> linkDescriptions = new ArrayList<>();

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
        }

        // add this to the candidates for junctions and/or links
        List<String> connectedRoadIDs = possibleJunctionsOrLinks.get(nodeId);
        if (connectedRoadIDs == null) {
            connectedRoadIDs = new ArrayList<>();
            possibleJunctionsOrLinks.put(nodeId, connectedRoadIDs);
        }
        connectedRoadIDs.add(currentRoadId);
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
    }

    private void handleWayClose() {
        RoadDescription description = new RoadDescription(
                new ID(currentRoadId),
                currentRoadName,
                currentRoadPolyline,
                1,
                1,
                currentRoadLayer
        );

        roadDescriptions.put(currentRoadId, description);
        currentRoadPolyline = null;
        currentRoadId = null;
        currentRoadName = null;
    }

    // <node id="2684347240" version="1" timestamp="2016-03-02T22:08:00Z" uid="1016290"
    // user="Amaroussi" changeset="20704202" lat="51.4971865" lon="-0.1002579"/>
    private void handleNode(Attributes attributes) {
        if (!boundsHandled) {
            throw new IllegalStateException("Can not handle node as bounds are not loaded yet = no conversion to meters.");
        }
        double lat = Double.parseDouble(attributes.getValue("lat"));
        double lon = Double.parseDouble(attributes.getValue("lon"));
        points.put(attributes.getValue("id"),
                new GeoPoint(
                        getDistanceMeters(this.boundsMinLat, this.boundsMinLon, this.boundsMinLat, lon),
                        getDistanceMeters(this.boundsMinLat, this.boundsMinLon, lat, this.boundsMinLon)));
    }

    // <bounds minlon="-0.10625" minlat="51.49189" maxlon="-0.09609"
    //                          maxlat="51.49982" origin="osmconvert 0.7T"/>
    private void handleBounds(Attributes attributes) {
        boundsHandled = true;
        this.boundsMinLat = Double.parseDouble(attributes.getValue("minlat"));
        this.boundsMinLon = Double.parseDouble(attributes.getValue("minlon"));

        this.boundsMaxLat = Double.parseDouble(attributes.getValue("maxlat"));
        this.boundsMaxLon = Double.parseDouble(attributes.getValue("maxlon"));

    }

    private void createLinkAndJunctionDescriptions() {
        possibleJunctionsOrLinks.entrySet().forEach(entry -> {
            // only interested in nodes which belong to more than one road
            if (entry.getValue() == null || entry.getValue().size() <= 1) {
                return;
            }

            List<String> connectedRoadIDs = entry.getValue();
            String nodeId = entry.getKey();
            GeoPoint geoPoint = points.get(nodeId);
            if (connectedRoadIDs.size() == 2) {
                RoadDescription road1 = roadDescriptions.get(connectedRoadIDs.get(0));
                RoadDescription road2 = roadDescriptions.get(connectedRoadIDs.get(1));
                if (road1 == null || road2 == null || road1.getGeoPolyline().getSegments().size() == 0 || road2.getGeoPolyline().getSegments().size() == 0) {
                    LOG.log_Error("Found a link but not a corresponding road. Error. Will skip link ");
                    return;
                }
                NodePositionInRoad nodeOnRoad1 = getNodePositionInRoad(nodeId, road1);
                NodePositionInRoad nodeOnRoad2 = getNodePositionInRoad(nodeId, road2);
                // TODO add traffic light type!
                if (nodeOnRoad1 == NodePositionInRoad.END && nodeOnRoad2 == NodePositionInRoad.START) {
                    linkDescriptions.add(new LinkDescription(
                            road1.getId(),
                            road2.getId(),
                            LinkType.GENERIC,
                            new ID("link_" + nodeId),
                            geoPoint));
                } else {
                    // probably all other options could go here. If we are not even too accurate in this case, let's leave it as is.
                    linkDescriptions.add(new LinkDescription(
                            road2.getId(),
                            road1.getId(),
                            LinkType.GENERIC,
                            new ID("link_" + nodeId),
                            geoPoint));
                }
            } else {
                // TODO traffic light for a junction!
                Map<ID, JunctionDescription.RoadDirection> roadsWithDirections = new HashMap<>();
                for (String connectedRoadId : connectedRoadIDs) {
                    RoadDescription road = roadDescriptions.get(connectedRoadId);
                    if (road.getGeoPolyline().getSegments().size() == 0) {
                        LOG.log_Error("Empty road for junction found, will skip ", road.getRoadName(), " road");
                        continue;
                    }
                    NodePositionInRoad positionInRoad = getNodePositionInRoad(nodeId, road);
                    switch (positionInRoad) {
                        case START:
                            roadsWithDirections.put(road.getId(), JunctionDescription.RoadDirection.OUTGOING);
                            break;
                        case END:
                            roadsWithDirections.put(road.getId(), JunctionDescription.RoadDirection.INCOMING);
                            break;
                        default:
                            LOG.log_Error("Got a road and a junction, and road goes through junction - wrong.", road.getRoadName());
                            break;

                    }
                }
                JunctionDescription junctionDescription = new JunctionDescription(new ID("junction_" + nodeId),
                        roadsWithDirections, false, geoPoint);
                junctionDescriptions.add(junctionDescription);
            }
        });
    }

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
        LOG.log_Warning("Got bounds ", segment);
        result.boundingBox = segment;

        //roads
        result.roadDescriptions.addAll(roadDescriptions.values());

        // junctions
        result.junctionDescriptions.addAll(junctionDescriptions);

        // links
        result.linkDescriptions.addAll(linkDescriptions);

        return result;
    }

    private enum NodePositionInRoad {
        START,
        END,
        NOT_START_OR_END
    }
}
