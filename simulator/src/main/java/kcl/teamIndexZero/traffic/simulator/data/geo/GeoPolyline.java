package kcl.teamIndexZero.traffic.simulator.data.geo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class GeoPolyline {

    // list of segments? or map of points/lenght?
    private List<GeoSegment> segments = new ArrayList<>();

    private float polylineLength = 0;
    private GeoPoint lastPoint = null;

    public void addPoint(GeoPoint point) {
        if (lastPoint != null) {
            GeoSegment segment = new GeoSegment(lastPoint, point);
            polylineLength += segment.getLength();
            segments.add(segment);
        }
        lastPoint = point;
    }

    public List<GeoSegment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

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

    public float getPolylineLength() {
        return polylineLength;
    }
}
