package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

/**
 * Created by Es on 02/03/2016.
 */
public class TrafficLight extends Link {
    /**
     * Constructor
     *
     * @param id Link ID tag
     */
    public TrafficLight(ID id) {
        super(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
        //TODO
    }
}