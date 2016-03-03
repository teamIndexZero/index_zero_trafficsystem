package kcl.teamIndexZero.traffic.simulator.osm;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.simulator.data.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.GeoSegment;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

/**
 * SAX Xml handler to read stuff from OSM
 */
class SaxHandler extends DefaultHandler {
    private Logger LOG = Logger.getLoggerInstance(SaxHandler.class.getName());
    private OsmParseResult result;
    private Set<String> interestingElements = new HashSet<>(Arrays.asList("node", "bounds", "nd", "way"));
    private Map<String, GeoPoint> points = new HashMap<>();
    private String previousNodeInWayID;
    private String currentWayId;

    public SaxHandler(OsmParseResult result) {

        this.result = result;
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if (!interestingElements.contains(qName)) {
            return;
        }

        // if we are in bounds?
        if ("bounds".equals(qName)) {
            handleBounds(attributes);
        }

        if ("node".equals(qName)) {
            handleNode(attributes);
        }

        if ("way".equals(qName)) {
            handleWayOpen(attributes);
        }

        if ("nd".equals(qName)) {
            handleNodeWithinWay(attributes);
        }
    }

    private void handleNodeWithinWay(Attributes attributes) {
        // <way id="8032768" version="20" timestamp="2016-03-02T22:08:00Z" uid="352985" user="ecatmur"
        //          changeset="32783057">
        // <nd ref="108192"/>

        String nodeId = attributes.getValue("ref");
        if (previousNodeInWayID == null) {
            // quick exit - this is the first node in path; we don't do anything yet.
            previousNodeInWayID = nodeId;
            return;
        }

        // at this moment, it is at least 2nd node in graph. So we can create a RoadDescriptor with a segment, and record
        // our current node as previous:
        GeoPoint prevPoint = points.get(previousNodeInWayID);
        GeoPoint thisPoint = points.get(nodeId);

        if (thisPoint == null || prevPoint == null) {
            // some of the points is outside of bbox, which is OK
            return;
        }
        GeoSegment segment = new GeoSegment(
                prevPoint,
                thisPoint,
                2
        );
        RoadDescription description = new RoadDescription(
                1,
                1,
                new ID(currentWayId + "-" + previousNodeInWayID),
                100,
                segment);
        LOG.log_Warning("Adding a way for segment ", segment);
        result.descriptionList.add(description);
        previousNodeInWayID = nodeId;
    }

    private void handleWayOpen(Attributes attributes) {
        //        <way id="8032768" version="20" timestamp="2016-03-02T22:08:00Z"
        //              uid="352985" user="ecatmur" changeset="32783057">
        //        <nd ref="108192"/>
        //        <nd ref="108193"/>
        //        <tag k="lit" v="yes"/>
        //        </way>
        currentWayId = attributes.getValue("id");
    }

    private void handleWayClose() {
        currentWayId = null;
        previousNodeInWayID = null;
    }


    private void handleNode(Attributes attributes) {
        // <node id="2684347240" version="1" timestamp="2016-03-02T22:08:00Z" uid="1016290"
        // user="Amaroussi" changeset="20704202" lat="51.4971865" lon="-0.1002579"/>
        points.put(attributes.getValue("id"),
                new GeoPoint(
                        Double.parseDouble(attributes.getValue("lat")),
                        Double.parseDouble(attributes.getValue("lon"))
                ));
    }

    private void handleBounds(Attributes attributes) {
        // <bounds minlon="-0.10625" minlat="51.49189" maxlon="-0.09609" maxlat="51.49982" origin="osmconvert 0.7T"/>
        GeoSegment segment = new GeoSegment(
                new GeoPoint(
                        Double.parseDouble(attributes.getValue("minlat")),
                        Double.parseDouble(attributes.getValue("minlon"))
                ),
                new GeoPoint(
                        Double.parseDouble(attributes.getValue("maxlat")),
                        Double.parseDouble(attributes.getValue("maxlon"))
                ),
                2
        );
        LOG.log_Warning("Got bounds ", segment);
        result.boundingBox = segment;
    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException {
        if (!interestingElements.contains(qName)) {
            return;
        }
        if ("way".equals(qName)) {
            handleWayClose();
        }
    }
}
