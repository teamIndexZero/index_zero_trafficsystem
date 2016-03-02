package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

/**
 * Created by Es on 28/02/2016.
 */
public class Lane extends Feature {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Lane.class.getSimpleName());
    private RoadSpecs roadSpecs;
    private final DirectedLanes lanes;

    /**
     * Constructor
     *
     * @param road_specs Road specifications
     */
    public Lane(ID id, RoadSpecs road_specs, DirectedLanes parent) {
        super(id);
        this.lanes = parent;
        this.roadSpecs = road_specs;
        LOG.log("New lane (", id.toString(), ") created: length=", this.roadSpecs.length, "m, width=", this.roadSpecs.width, "m.");
    }

    /**
     * Gets the width of the lane
     *
     * @return Width in meters
     */
    public double getWidth() {
        return this.roadSpecs.width;
    }

    /**
     * Gets the length of the lane
     *
     * @return Length in meters
     */
    public int getLength() {
        return this.roadSpecs.length;
    }

    /**
     * Gets the Lane's ID
     *
     * @return ID tag for the lane
     */
    public ID getID() {
        return super.getID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {

    }
}
