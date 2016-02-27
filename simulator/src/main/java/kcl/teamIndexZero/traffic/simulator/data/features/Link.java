package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

public class Link implements ISimulationAware {
    public Feature one;
    public Feature two;
    private ID id;

    /**
     * Constructor
     * @param id Link ID tag
     */
    Link( ID id ) {
        this.id = id;
    }

    @Override
    public void tick(SimulationTick tick) {

    }
}
