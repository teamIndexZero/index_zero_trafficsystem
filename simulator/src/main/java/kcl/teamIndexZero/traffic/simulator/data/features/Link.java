package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

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

    @Override
    public void tick(SimulationTick tick) {
    }
}
