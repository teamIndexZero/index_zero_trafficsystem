package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.Feature;

public class Link implements ISimulationAware {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Link.class.getSimpleName());
    public Feature one;
    public Feature two;
    private ID id;

    /**
     * Constructor
     * @param id Link ID tag
     */
    public Link( ID id ) {
        this.id = id;
    }

    /**
     * Get the Link's ID tag
     * @return ID tag
     */
    public ID getID() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
    }
}
