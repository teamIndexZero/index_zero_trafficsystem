package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

/**
 * A simple implementation of the {@link ISimulationAware} which simply deletes the objects which have moved (or happened
 * to be) off map.
 * <p>
 * TODO should be worth moving that into the inside of the map. For now looks like we're forced to expose map object due to this
 */
public class CarRemover implements ISimulationAware {

    private final SimulationMap map;

    /**
     * Constructor.
     *
     * @param map simulation map we are looking at to remove the objects from.
     */
    public CarRemover(SimulationMap map) {

        this.map = map;
    }

    @Override
    public void tick(SimulationTick tick) {
        //todo probably we just want to skip that form simulation, or at least move to crossings/outlets, hm?
        map.getObjectsOnMap().removeIf(object -> object.getPosition().x < 0 || object.getPosition().x > map.getWidth());
    }
}
