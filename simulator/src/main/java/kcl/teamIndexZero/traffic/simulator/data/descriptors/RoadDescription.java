package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;

/**
 */
public class RoadDescription {
    private final int length;
    private GeoPolyline geoPolyline;
    private String roadName;
    private int laneCountA;
    private int laneCountB;
    private final ID id;

    public GeoPolyline getGeoPolyline() {
        return geoPolyline;
    }

    /**
     * Constructor
     *
     * @param laneCountA Number of lanes on side A of the road
     * @param laneCountB Number of lanes on side B of the road
     */
    public RoadDescription(int laneCountA, int laneCountB, ID id, int length, GeoPolyline geoPolyline, String roadName) {
        this.laneCountA = laneCountA;
        this.laneCountB = laneCountB;
        this.id = id;
        this.length = length;
        this.geoPolyline = geoPolyline;
        this.roadName = roadName;
    }

    public int getLaneCountA() {
        return laneCountA;
    }

    public int getLaneCountB() {
        return laneCountB;
    }

    public ID getId() {
        return id;
    }

    public int getLength() {
        return length;
    }

    public String getRoadName() {
        return roadName;
    }
}
