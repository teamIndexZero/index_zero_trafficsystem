package kcl.teamIndexZero.traffic.simulator.data.geo;

/**
 * Segment (line) between two points in the simulation coordinate system (metersX, metersY).
 */
public class GeoSegment {

    public GeoPoint start;
    public GeoPoint end;

    /**
     * Constructor from two points
     *
     * @param start start of segment
     * @param end   end of segment
     */
    public GeoSegment(GeoPoint start, GeoPoint end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Get metric length of the segment.
     *
     * @return lenght of segment in meters.
     */
    public double getLength() {
        return GeoPoint.getDistance(start, end);
    }

    @Override
    public String toString() {
        return String.format("{Segment:%s-%s}", start.toString(), end.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoSegment that = (GeoSegment) o;

        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        return end != null ? end.equals(that.end) : that.end == null;

    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
