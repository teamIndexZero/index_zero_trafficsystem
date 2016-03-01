package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.ID;

/**
 * Created by Es on 28/02/2016.
 */
public class Road {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Road.class.getSimpleName());
    private ID id;
    private RoadSpecs roadSpecs = new RoadSpecs();
    public DirectedLanes rightSide;
    public DirectedLanes leftSide;

    /**
     * Constructor
     *
     * @param id              Feature ID tag
     * @param leftLanesCount  Number of ongoing lanes from the start viewpoint of the road
     * @param rightLanesCount Number of upcoming lanes from the start viewpoint of the road
     * @param roadLength      Length of the road
     * @throws IllegalArgumentException when the number of lanes given is less than 1 or the road length is < 0.5
     */
    public Road(ID id, int leftLanesCount, int rightLanesCount, int roadLength) throws IllegalArgumentException {
        if (leftLanesCount < 1 && rightLanesCount < 1) {
            LOG.log_Error("Total number of lanes given is ", leftLanesCount + rightLanesCount, ".");
            throw new IllegalArgumentException("Number of lanes on road is 0! A road must have at least 1 lane.");
        }
        if (roadLength < 1) {
            LOG.log_Error("Length of the road is too small ('", roadLength, "'m).");
            throw new IllegalArgumentException("Length of the road is too small.");
        }
        this.id = id;
        this.roadSpecs.length = roadLength;
        this.rightSide = new DirectedLanes(new ID(id, "R"), rightLanesCount, roadSpecs);
        this.leftSide = new DirectedLanes(new ID(id, "L"), leftLanesCount, roadSpecs);
    }

    /**
     * Gets the length of the road
     *
     * @return Length of road
     */
    public int getRoadLenth() {
        return this.roadSpecs.length;
    }
}
