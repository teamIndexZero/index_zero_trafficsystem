package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

/**
 * Created by lexaux on 17/02/2016.
 */
public class Link implements ISimulationAware {
    public Feature one;
    public Feature two;

    @Override
    public void tick(SimulationTick tick) {

    }
}
