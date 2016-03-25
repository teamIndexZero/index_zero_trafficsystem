package kcl.teamIndexZero.traffic.simulator.data.geo;

import kcl.teamIndexZero.traffic.simulator.data.ID;

/**
 * Represents a geographical point (in our synthetic coordinate system (offset to north and east from 0,0).
 */
public class GeoPoint {

    private static final ID SYNTETIC = new ID("SYNTH_POINT");

    public double xMeters;
    public double yMeters;
    public ID id;

    public GeoPoint(double xMeters, double yMeters) {
        this(xMeters, yMeters, SYNTETIC);
    }

    public GeoPoint(double xMeters, double yMeters, ID id) {
        this.xMeters = xMeters;
        this.yMeters = yMeters;
        this.id = id;

    }

    /**
     * Get distance in meters between two points (linear)
     *
     * @param point  first point
     * @param point1 second point
     * @return distance in meters.
     */
    public static double getDistanceBetween(GeoPoint point, GeoPoint point1) {
        return Math.sqrt(
                (point.xMeters - point1.xMeters) * (point.xMeters - point1.xMeters) +
                        (point.yMeters - point1.yMeters) * (point.yMeters - point1.yMeters));
    }

    /**
     * Get an ID of this point (useful for debugging and tracking back to the OSM files).
     *
     * @return id of this point.
     */
    public ID getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("{%.0f, %.0f}", xMeters, yMeters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoPoint geoPoint = (GeoPoint) o;

        if (Double.compare(geoPoint.xMeters, xMeters) != 0) return false;
        return Double.compare(geoPoint.yMeters, yMeters) == 0;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(xMeters);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(yMeters);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
