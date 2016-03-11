package kcl.teamIndexZero.traffic.simulator.data.osm;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.osm.MapParseException;
import kcl.teamIndexZero.traffic.simulator.osm.OsmParseResult;
import kcl.teamIndexZero.traffic.simulator.osm.OsmParser;
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
    public static final String RESOURCE_URL = "/testData/junction_sample.osm";
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
}
