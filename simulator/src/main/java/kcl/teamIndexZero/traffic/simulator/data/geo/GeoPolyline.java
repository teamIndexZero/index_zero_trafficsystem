package kcl.teamIndexZero.traffic.simulator.data.geo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Polyline - that is - sequence of {@link GeoSegment} which form a polyline.
 */
public class GeoPolyline {

    // list of segments? or map of points/lenght?
    private List<GeoSegment> segments = new ArrayList<>();

    private float polylineLength = 0;
    private GeoPoint lastPoint = null;

    /**
     * Adds another point to the line. If this is point #2 and above, it will result in adding a new segment.
     *
     * @param point point to add
     */
    public void addPoint(GeoPoint point) {
        if (lastPoint != null) {
            GeoSegment segment = new GeoSegment(lastPoint, point);
            polylineLength += segment.getLength();
            segments.add(segment);
        }
        lastPoint = point;
    }

    /**
     * Get all individual segments of polyline.
     *
     * @return List of GeoSegment which is in turn defined by two GeoPoints.
     */
    public List<GeoSegment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    /**
     * This method gets a GeoPoint for any given point-by-length for the polyline. Consider square with side of 1 meter,
     * its polyline has a linear distance of 4 - starting from 0 and returning back to it. If we look from inside the
     * polyline, we can identify each its point by linear distance from the start point.
     *
     * @param distanceFromStart a distance fromt the start of the polyline
     * @return GeoPoint corresponding to that linear distance (already accounted for the multiple segments in different
     * sizes and directions.
     */
    public GeoPoint getGeoPointAtDistanceFromStart(double distanceFromStart) {
        GeoSegment containingSegment = null;
        for (GeoSegment segment : segments) {
            if (distanceFromStart <= segment.getLength()) {
                containingSegment = segment;
                break;
            }
            distanceFromStart -= segment.getLength();
        }
        if (containingSegment == null) {
            return null;
        }
        double distanceToEnd = containingSegment.getLength() - distanceFromStart;
        if (distanceToEnd == 0) {
            return containingSegment.end;
        }
        double relation = distanceFromStart / distanceToEnd;
        return new GeoPoint(
                (containingSegment.start.xMeters + relation * containingSegment.end.xMeters) / (1 + relation),
                (containingSegment.start.yMeters + relation * containingSegment.end.yMeters) / (1 + relation));
    }

    /**
     * Get total linear metric length of the polyline.
     *
     * @return meters of the polyline length.
     */
    public float getPolylineLength() {
        return polylineLength;
    }
}
