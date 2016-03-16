package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.Lane;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * A traffic generator for adding cars into the map basing on some statistical approach.
 */
public class CarAdder implements ISimulationAware {

    private final SimulationMap map;
    int counter = 1;
    private List<Road> roads = new ArrayList<>();

    /**
     * Constructor with map.
     *
     * @param map {@link SimulationMap} to add vehicles to.
     */
    public CarAdder(SimulationMap map) {
        this.map = map;
        map.getMapFeatures().values().forEach(feature -> {
            if (feature instanceof Road) {
                roads.add((Road) feature);
            }
        });
    }

    private Lane getRandomLane() {
        return roads.get((int) (Math.random() * roads.size() - 1)).getForwardSide().getLanes().get(0);
    }

    @Override
    public void tick(SimulationTick tick) {
        if (Math.random() > 0.07) {
            return;
        }

        Vehicle v = new Vehicle("Car " + counter++, getRandomLane());

        synchronized (map) {
            map.addMapObject(v);
        }
    }
}