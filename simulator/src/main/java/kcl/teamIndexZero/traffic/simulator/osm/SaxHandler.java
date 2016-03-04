package kcl.teamIndexZero.traffic.simulator.osm;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoSegment;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

/**
 * SAX Xml handler to read elements from OpenStreetMap.
 */
class SaxHandler extends DefaultHandler {

    public static final String BOUNDS_ELEMENT = "bounds";
    public static final String NODE_ELEMENT = "node";
    public static final String WAY_ELEMENT = "way";
    public static final String NODE_WITHIN_WAY_ELEMENT = "nd";
    public static final String TAG = "tag";

    private Logger LOG = Logger.getLoggerInstance(SaxHandler.class.getName());
    private OsmParseResult result;
    private Set<String> interestingElements = new HashSet<>(Arrays.asList(BOUNDS_ELEMENT, NODE_ELEMENT, WAY_ELEMENT, NODE_WITHIN_WAY_ELEMENT, TAG));
    private Map<String, GeoPoint> points = new HashMap<>();

    private GeoPolyline currentRoadPolyline = null;
    private String currentRoadId;
    private String currentRoadName;

    private double boundsMinLon;
    private double boundsMinLat;
    private boolean boundsHandled = false;

    public SaxHandler(OsmParseResult result) {
        this.result = result;
    }

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

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if (!interestingElements.contains(qName)) {
            return;
        }

        // if we are in bounds?
        if (BOUNDS_ELEMENT.equals(qName)) {
            handleBounds(attributes);
        }

        if (NODE_ELEMENT.equals(qName)) {
            handleNode(attributes);
        }

        if (WAY_ELEMENT.equals(qName)) {
            handleWayOpen(attributes);
        }

        if (NODE_WITHIN_WAY_ELEMENT.equals(qName)) {
            handleNodeWithinWay(attributes);
        }

        if (TAG.equals(qName)) {
            handleTag(attributes);
        }
    }

    private void handleTag(Attributes attributes) {
//        <tag k="name" v="Pall Mall"/>
//        <tag k="oneway" v="yes"/>
//        <tag k="maxspeed" v="30 mph"/>
//        <tag k="lanes" v="1"/>
        if (currentRoadId == null) {
            return;
        }

        String key = attributes.getValue("k");
        String value = attributes.getValue("v");
        if ("name".equals(key)) {
            this.currentRoadName = value;
        }
    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException {
        if (!interestingElements.contains(qName)) {
            return;
        }
        if (WAY_ELEMENT.equals(qName)) {
            handleWayClose();
        }
    }


    private void handleNodeWithinWay(Attributes attributes) {
        // <way id="8032768" version="20" timestamp="2016-03-02T22:08:00Z" uid="352985" user="ecatmur"
        //          changeset="32783057">
        // <nd ref="108192"/>

        String nodeId = attributes.getValue("ref");

        // at this moment, it is at least 2nd node in graph. So we can create a RoadDescriptor with a segment, and record
        // our current node as previous:
        GeoPoint thisPoint = points.get(nodeId);
        if (thisPoint != null) {
            currentRoadPolyline.addPoint(thisPoint);
        }
    }

    private void handleWayOpen(Attributes attributes) {
        //        <way id="8032768" version="20" timestamp="2016-03-02T22:08:00Z"
        //              uid="352985" user="ecatmur" changeset="32783057">
        //        <nd ref="108192"/>
        //        <nd ref="108193"/>
        //        <tag k="lit" v="yes"/>
        //        </way>
        currentRoadId = attributes.getValue("id");
        currentRoadPolyline = new GeoPolyline();
    }

    private void handleWayClose() {
        RoadDescription description = new RoadDescription(
                new ID(currentRoadId),
                currentRoadName,
                currentRoadPolyline,
                1,
                1
        );

        result.descriptionList.add(description);
        currentRoadPolyline = null;
        currentRoadId = null;
        currentRoadName = null;
    }


    private void handleNode(Attributes attributes) {
        // <node id="2684347240" version="1" timestamp="2016-03-02T22:08:00Z" uid="1016290"
        // user="Amaroussi" changeset="20704202" lat="51.4971865" lon="-0.1002579"/>
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

    private void handleBounds(Attributes attributes) {
        // <bounds minlon="-0.10625" minlat="51.49189" maxlon="-0.09609"
        //                          maxlat="51.49982" origin="osmconvert 0.7T"/>
        boundsHandled = true;
        this.boundsMinLat = Double.parseDouble(attributes.getValue("minlat"));
        this.boundsMinLon = Double.parseDouble(attributes.getValue("minlon"));

        double boundsMaxLat = Double.parseDouble(attributes.getValue("maxlat"));
        double boundsMaxLon = Double.parseDouble(attributes.getValue("maxlon"));

        GeoSegment segment = new GeoSegment(
                new GeoPoint(0, 0),
                new GeoPoint(
                        getDistanceMeters(this.boundsMinLat, this.boundsMinLon, this.boundsMinLat, boundsMaxLon),
                        getDistanceMeters(this.boundsMinLat, this.boundsMinLon, boundsMaxLat, this.boundsMinLon)
                ));
        LOG.log_Warning("Got bounds ", segment);
        result.boundingBox = segment;
    }

}
