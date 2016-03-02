package kcl.teamIndexZero.traffic.simulator.data.descriptors;

/**
 * Created by Es on 02/03/2016.
 */
public class RoadDescription {
    int laneCountA;
    int laneCountB;

    /**
     * Constructor
     *
     * @param laneCountA Number of lanes on side A of the road
     * @param laneCountB Number of lanes on side B of the road
     */
    public RoadDescription(int laneCountA, int laneCountB) {
        this.laneCountA = laneCountA;
        this.laneCountB = laneCountB;
    }
}
