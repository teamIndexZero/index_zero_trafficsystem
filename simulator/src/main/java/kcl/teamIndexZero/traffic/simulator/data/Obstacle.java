package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.Simulator;

/**
 * Created by lexaux on 07/02/2016.
 */
public class Obstacle extends MapObject {

    private static Logger_Interface LOG = Logger.getLoggerInstance(Simulator.class.getSimpleName());

    public Obstacle(String name, MapPosition position) {
        super(name, position);
    }

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
