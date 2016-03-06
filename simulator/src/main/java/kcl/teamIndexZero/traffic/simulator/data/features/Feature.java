package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

public class Feature implements ISimulationAware {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Feature.class.getSimpleName());
    private ID id;

    /**
     * Constructor
     *
     * @param id Feature ID tag
     */
    public Feature(ID id) {
        this.id = id;
    }

    /**
     * Gets the Feature's ID tag
     *
     * @return ID tag
     */
    public ID getID() {
        return this.id;
    }

    /**
     * By default, do nothing
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {

    }
}
