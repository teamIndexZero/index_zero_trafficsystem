package kcl.teamIndexZero.traffic.simulator.data.mapObjects;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.Simulator;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.Lane;

/**
 * An Obstacle is a non-moving map object. Think of it as of fallen tree or a stone or roadworks something.
 */
public class Obstacle extends MapObject {

    private static Logger_Interface LOG = Logger.getLoggerInstance(Simulator.class.getSimpleName());

    /**
     * Constructor. See constructor in {@link MapObject}
     */
    public Obstacle(String name, Lane lane, float positionInLane) {
        super(name, lane);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
        LOG.log(this.toString(), " is doing nothing as it is static");
    }

    @Override
    public String toString() {
        return String.format("{Obstacle \"%s\"}", name);
    }
}
