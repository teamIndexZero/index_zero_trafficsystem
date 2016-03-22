package kcl.teamIndexZero.traffic.simulator.osm;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.data.MapEntry.entry;

/**
 * Created by lexaux on 10/03/2016.
 */
public class OsmParseTest {
    public static final String STREAM_NAME = "Test Stream Name";
    public static final String RESOURCE_URL = "/testData/junction_sample.osm.xml";
    private InputStream stream;

    @Before
    public void setup() {
        stream = this.getClass().getResourceAsStream(RESOURCE_URL);
    }

    @After
    public void tearDown() throws IOException {
        if (stream != null) {
            stream.close();
        }
    }

    @Test
    public void shouldLoadRoadsFromFile() throws MapParseException {
        OsmParseResult result = (new OsmParser()).parse(STREAM_NAME, stream);
        assertThat(result).isNotNull();
        assertThat(result.boundingBox.getLength()).isEqualTo(157253.3733278164);
        assertThat(result.roadDescriptions).hasSize(4);
    }

    @Test
    public void shouldThrowExceptionIfTooBigFile() throws MapParseException {
    }

    @Test
    public void shouldConstructJunctionsCorrectly() throws MapParseException {
        OsmParseResult result = (new OsmParser()).parse(STREAM_NAME, stream);
        List<JunctionDescription> links = result.junctionDescriptions;
        assertThat(links).hasSize(2);
    }

    @Test
    public void shouldConstructLinksCorrectly() throws MapParseException {
        OsmParseResult result = (new OsmParser()).parse(STREAM_NAME, stream);

        List<JunctionDescription> junctions = result.junctionDescriptions;
        assertThat(junctions).hasSize(2);

        JunctionDescription junction = junctions.get(1);
        assertThat(junction.getConnectedIDs()).hasSize(3);

        assertThat(junction.getConnectedIDs()).contains(
                entry(new ID("w1"), JunctionDescription.RoadDirection.INCOMING),
                entry(new ID("w2"), JunctionDescription.RoadDirection.OUTGOING),
                entry(new ID("w3"), JunctionDescription.RoadDirection.OUTGOING)
        );
    }

    @Test
    public void shouldSplitWayIfIntersectsInMiddle() throws MapParseException {
        OsmParseResult result = (new OsmParser()).parse(
                "Two roads crossing mid point.",
                OsmParseTest.class.getResourceAsStream("/testData/ways_intersect_in_middle.osm.xml"));
        List<JunctionDescription> junctions = result.junctionDescriptions;

        assertThat(junctions).hasSize(1);
        assertThat(junctions.get(0).getConnectedIDs()).hasSize(4);

        List<RoadDescription> roads = result.roadDescriptions;
        assertThat(roads).hasSize(4);
    }

    @Test
    public void shouldSplitWayIfIntersectsInMiddleMultipleTimes() throws MapParseException {
        OsmParseResult result = (new OsmParser()).parse(
                "Paris issue part - isolated now.",
                OsmParseTest.class.getResourceAsStream("/testData/paris_isolated.osm.xml"));
        List<JunctionDescription> junctions = result.junctionDescriptions;

        assertThat(junctions).hasSize(2);

        List<RoadDescription> roads = result.roadDescriptions;
        assertThat(roads).hasSize(5);
    }

    @Test
    public void shouldSplitProduceTwoRoads() {
        GeoPoint point1 = new GeoPoint(0, 0);
        GeoPoint point2 = new GeoPoint(0, 1);
        GeoPoint point3 = new GeoPoint(1, 1);

        GeoPolyline polyline = new GeoPolyline();
        polyline.addPoint(point1);
        polyline.addPoint(point2);
        polyline.addPoint(point3);

        RoadDescription descr = new RoadDescription(new ID("Test"),
                "Road Name",
                polyline,
                1, 2, 0);

        RoadDescription[] split = OsmSaxHandler.splitRoadDescriptionAtPoint(descr, point2);
        assertThat(split).hasSize(2);
        RoadDescription firstPart = split[0];
        RoadDescription secondPart = split[1];

        assertThat(firstPart).isNotNull();
        assertThat(secondPart).isNotNull();

        assertThat(firstPart.getId()).isEqualTo(new ID("Test_s1"));
        assertThat(secondPart.getId()).isEqualTo(new ID("Test_s2"));

        assertThat(firstPart.getGeoPolyline().getStartPoint()).isEqualTo(point1);
        assertThat(firstPart.getGeoPolyline().getFinishPoint()).isEqualTo(point2);

        assertThat(secondPart.getGeoPolyline().getStartPoint()).isEqualTo(point2);
        assertThat(secondPart.getGeoPolyline().getFinishPoint()).isEqualTo(point3);

        assertThat(firstPart.getRoadName()).isEqualTo(descr.getRoadName());
        assertThat(secondPart.getRoadName()).isEqualTo(descr.getRoadName());

        assertThat(descr.getLength()).isEqualTo(firstPart.getLength() + secondPart.getLength());
    }
}
