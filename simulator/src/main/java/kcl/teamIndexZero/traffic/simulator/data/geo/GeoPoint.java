package kcl.teamIndexZero.traffic.simulator.data.geo;

/**
 * Created by lexaux on 02/03/2016.
 */
public class GeoPoint {
    public GeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double latitude;
    public double longitude;

    @Override
    public String toString() {
        return String.format("{%.0f, %.0f}", latitude, longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoPoint geoPoint = (GeoPoint) o;

        if (Double.compare(geoPoint.latitude, latitude) != 0) return false;
        return Double.compare(geoPoint.longitude, longitude) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
