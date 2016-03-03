package kcl.teamIndexZero.traffic.simulator.data;

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
        return String.format("{%.6f, %.6f}", latitude, longitude);
    }
}
