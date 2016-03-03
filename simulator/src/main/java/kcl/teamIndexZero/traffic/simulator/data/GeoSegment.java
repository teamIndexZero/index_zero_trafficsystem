package kcl.teamIndexZero.traffic.simulator.data;

/**
 * Created by lexaux on 02/03/2016.
 */
public class GeoSegment {
    public GeoSegment(GeoPoint start, GeoPoint end, int pixelWidthTemporaryVariable) {
        this.start = start;
        this.end = end;
        this.pixelWidthTemporaryVariable = pixelWidthTemporaryVariable;
    }

    public GeoPoint start;
    public GeoPoint end;
    public int pixelWidthTemporaryVariable;

    @Override
    public String toString() {
        return String.format("{Segment:%s-%s}", start.toString(), end.toString());
    }
}
