package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

/**
 * Created by Es on 28/02/2016.
 */
public class Road extends Feature {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Road.class.getSimpleName());
    private int leftLanesFromStart;
    private int rightLanesFromStart;

    /**
     * Constructor
     *
     * @param id         Feature ID tag
     * @param leftLanes  Number of ongoing lanes from the start viewpoint of the road
     * @param rightLanes Number of upcoming lanes from the start viewpoint of the road
     * @param roadLength Length of the road
     * @throws IllegalArgumentException when the number of lanes given is less than 1 or the road length is < 0.5
     */
    public Road(ID id, int leftLanes, int rightLanes, double roadLength) throws IllegalArgumentException {
        super(id);
        if (leftLanes < 1 && rightLanes < 1) {
            LOG.log_Error("Number of lane given is ", leftLanes + rightLanes, ".");
            throw new IllegalArgumentException("Number of lanes on road is 0! A road must have at least a lane.");
        }
        if (roadLength < 0.5) {
            LOG.log_Error("Length of the road is too small ('", roadLength, "'m).");
            throw new IllegalArgumentException("Length of the road is too small.");
        }
        this.leftLanesFromStart = leftLanes;
        this.rightLanesFromStart = rightLanes;
    }

    /**
     * Gets the number of lanes on the left side from the perspective of the road's start boundary
     *
     * @return Number of lanes
     */
    public int getNumOf_lLanes_atStartView() {
        return this.leftLanesFromStart;
    }

    /**
     * Gets the number of lanes on the right side from the perspective of the road's start boundary
     *
     * @return Number of lanes
     */
    public int getNumOf_rLanes_atStartView() {
        return this.rightLanesFromStart;
    }

    /**
     * Gets the number of lanes on the left side from the perspective of the road's end boundary
     *
     * @return Number of lanes
     */
    public int getNumOf_lLanes_atEndView() {
        return getNumOf_rLanes_atStartView();
    }

    /**
     * Gets the number of lanes on the right side from the perspective of the road's end boundary
     *
     * @return Number of lanes
     */
    public int getNumOf_rLanes_atEndView() {
        return getNumOf_lLanes_atStartView();
    }

    @Override
    public void tick(SimulationTick tick) {

    }
}
