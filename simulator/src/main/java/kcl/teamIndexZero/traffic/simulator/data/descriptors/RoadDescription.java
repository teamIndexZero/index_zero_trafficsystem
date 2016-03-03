package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.GeoSegment;
import kcl.teamIndexZero.traffic.simulator.data.ID;

/**
 */
public class RoadDescription {
    private final int length;
    private GeoSegment geoSegment;
    private int laneCountA;
    private int laneCountB;
    private final ID id;

    public GeoSegment getGeoSegment() {
        return geoSegment;
    }

    /**
     * Constructor
     *
     * @param laneCountA Number of lanes on side A of the road
     * @param laneCountB Number of lanes on side B of the road
     */
    public RoadDescription(int laneCountA, int laneCountB, ID id, int length, GeoSegment geoSegment) {
        this.laneCountA = laneCountA;
        this.laneCountB = laneCountB;
        this.id = id;
        this.length = length;
        this.geoSegment = geoSegment;

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
}
