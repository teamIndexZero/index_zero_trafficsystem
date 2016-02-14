package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.Simulator;

/**
 * An Obstacle is a non-moving map object. Think of it as of fallen tree or a stone or roadworks something.
 */
public class Obstacle extends MapObject {

    private static Logger_Interface LOG = Logger.getLoggerInstance(Simulator.class.getSimpleName());

    /**
     * Constructor. See constructor in {@link MapObject}
     */
    public Obstacle(String name, MapPosition position) {
        super(name, position);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
        LOG.log(this.toString(), " is doing nothing as it is static");
        map.moveObject(this, position);
    }

    @Override
    public String toString() {
        return String.format("{Obstacle \"%s\" at %s}", name, position.toString());
    }
}
