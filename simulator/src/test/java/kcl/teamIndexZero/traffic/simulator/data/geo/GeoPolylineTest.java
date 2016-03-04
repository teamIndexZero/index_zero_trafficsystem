package kcl.teamIndexZero.traffic.simulator.data.geo;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 */
public class GeoPolylineTest {

    @Test
    public void shouldAddingPointsLeadToNewCorrectSegments() {
        GeoPolyline line = new GeoPolyline();
        line.addPoint(new GeoPoint(0, 0));
        line.addPoint(new GeoPoint(0, 1));
        line.addPoint(new GeoPoint(1, 1));

        List<GeoSegment> segments = line.getSegments();

        assertThat(segments).hasSize(2);
        assertThat(segments).containsAll(
                Arrays.asList(
                        new GeoSegment(new GeoPoint(0, 0), new GeoPoint(0, 1)),
                        new GeoSegment(new GeoPoint(0, 1), new GeoPoint(1, 1))));
    }

    @Test
    public void shouldAddingPointsLeadToCorrectLength() {
        // given
        GeoPolyline line = new GeoPolyline();

        // when
        line.addPoint(new GeoPoint(0, 0));
        line.addPoint(new GeoPoint(0, 1));
        line.addPoint(new GeoPoint(1, 1));

        // then
        Assert.assertEquals(2, line.getPolylineLength(), 0);
    }

    @Test
    public void shouldActualPositionBeOnPolyline() {
        // given
        GeoPolyline line = new GeoPolyline();

        // when
        line.addPoint(new GeoPoint(0, 0));
        line.addPoint(new GeoPoint(0, 1));
        line.addPoint(new GeoPoint(1, 1));
        line.addPoint(new GeoPoint(0, 1));
        line.addPoint(new GeoPoint(0, 0));

        // then
        assertThat(line.getGeoPointAtDistanceFromStart(0)).isEqualTo(new GeoPoint(0, 0));
        assertThat(line.getGeoPointAtDistanceFromStart(0.5)).isEqualTo(new GeoPoint(0, 0.5));
        assertThat(line.getGeoPointAtDistanceFromStart(1)).isEqualTo(new GeoPoint(0, 1));
        assertThat(line.getGeoPointAtDistanceFromStart(1.5)).isEqualTo(new GeoPoint(0.5, 1));
        assertThat(line.getGeoPointAtDistanceFromStart(4)).isEqualTo(new GeoPoint(0, 0));
    }
}
