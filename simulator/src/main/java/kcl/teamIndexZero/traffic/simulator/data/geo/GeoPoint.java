package kcl.teamIndexZero.traffic.simulator.data.geo;

/**
 * Created by lexaux on 02/03/2016.
 */
public class GeoPoint {

    public double xMeters;
    public double yMeters;

    public GeoPoint(double xMeters, double yMeters) {
        this.xMeters = xMeters;
        this.yMeters = yMeters;
    }

    public static double getDistance(GeoPoint point, GeoPoint point1) {

        return Math.sqrt(
                (point.xMeters - point1.xMeters) * (point.xMeters - point1.xMeters) +
                        (point.yMeters - point1.yMeters) * (point.yMeters - point1.yMeters));
    }

    @Override
    public String toString() {
        return String.format("{%.0f, %.0f}", xMeters, yMeters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoPoint geoPoint = (GeoPoint) o;

        if (Double.compare(geoPoint.xMeters, xMeters) != 0) return false;
        return Double.compare(geoPoint.yMeters, yMeters) == 0;

    }

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
