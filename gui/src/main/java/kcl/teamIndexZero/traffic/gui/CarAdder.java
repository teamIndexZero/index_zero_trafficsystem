package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.MapObject;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.MapPosition;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A traffic generator for adding cars into the map basing on some statistical approach.
 */
public class CarAdder implements ISimulationAware {

    int counter = 1;
    private final SimulationMap map;

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

    private Road getRandomRoad() {
        return roads.get((int) (Math.random() * roads.size() - 1));
    }

    @Override
    public void tick(SimulationTick tick) {
        if (Math.random() > 0.7) {

            // speed somehow varies between 0.5 and 1 m/s
            double speed = Math.random() * 0.5 + 0.5;

            // some cars should accelerate
            double acceleration = Math.random() > 0.6 ? 0.04 : 0;
            Color carColor = null;
            String name = null;
            if (acceleration == 0) {
                carColor = MapObject.COLORS[0];
                name = "SLOW";
            } else {
                carColor = MapObject.COLORS[1];
                name = "FAST";
            }

            Vehicle v = new Vehicle(name + counter++, new MapPosition(0, 1, 2, 1), getRandomRoad(), speed, acceleration);
            v.setColor(carColor);
            map.addMapObject(v);
        }
    }
}