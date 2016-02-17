package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

import java.util.List;

/**
 * Created by lexaux on 17/02/2016.
 */
public class Feature implements ISimulationAware{

    List<Link> links;

    @Override
    public void tick(SimulationTick tick) {

    }
}
