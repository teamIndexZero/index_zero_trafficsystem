package kcl.teamIndexZero.traffic.simulator.data.geo;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Test for geo segment.
 */
public class GeoSegmentTest {

    @Test
    public void shouldGetAngleReturnCorrectValues() {
        GeoSegment segment1 = new GeoSegment(
                new GeoPoint(100, 100),
                new GeoPoint(200, 100)
        );
        assertThat(segment1.getAngleToEastRadians()).isEqualTo(0);

        segment1 = new GeoSegment(
                new GeoPoint(100, 100),
                new GeoPoint(100, 200)
        );
        assertThat(segment1.getAngleToEastRadians()).isEqualTo(Math.toRadians(90));

        segment1 = new GeoSegment(
                new GeoPoint(100, 100),
                new GeoPoint(0, 100)
        );
        assertThat(segment1.getAngleToEastRadians()).isEqualTo(Math.toRadians(180));


        segment1 = new GeoSegment(
                new GeoPoint(100, 100),
                new GeoPoint(100, 0)
        );
        assertThat(segment1.getAngleToEastRadians()).isEqualTo(Math.toRadians(270));

        segment1 = new GeoSegment(
                new GeoPoint(100, 100),
                new GeoPoint(0, 0)
        );
        assertThat(segment1.getAngleToEastRadians()).isEqualTo(Math.toRadians(270 - 45));
    }
}
