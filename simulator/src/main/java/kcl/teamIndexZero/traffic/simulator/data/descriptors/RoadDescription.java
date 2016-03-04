package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;

/**
 * Class to contain the specifications of a road
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
     * @param id         Road's ID tag
     * @param length     Length of the road
     * @param geoSegment
     */
    public RoadDescription(int laneCountA, int laneCountB, ID id, int length, GeoPolyline geoPolyline, String roadName) {
        this.laneCountA = laneCountA;
        this.laneCountB = laneCountB;
        this.id = id;
        this.length = length;
        this.geoPolyline = geoPolyline;
        this.roadName = roadName;
    }

    /**
     * Gets the lane count for side A
     *
     * @return Lane count
     */
    public int getLaneCountA() {
        return laneCountA;
    }

    /**
     * Gets the lane count for side B
     *
     * @return Lane count
     */
    public int getLaneCountB() {
        return laneCountB;
    }

    /**
     * Gets the Road's ID tag
     *
     * @return Road ID tag
     */
    public ID getId() {
        return id;
    }

    /**
     * Gets the length of the road
     *
     * @return Length
     */
    public int getLength() {
        return length;
    }

    public String getRoadName() {
        return roadName;
    }
}
