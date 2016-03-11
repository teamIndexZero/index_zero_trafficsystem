package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.Lane;
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

    private Lane getRandomRoad() {
        return roads.get((int) (Math.random() * roads.size() - 1)).getForwardSide().getLanes().get(0);
    }

    @Override
    public void tick(SimulationTick tick) {
        if (Math.random() > 0.2) {
            return;
        }

        // speed somehow varies between 0.5 and 1 m/s
        double speed = 5;

        // some cars should accelerate
        double acceleration = 0;
        Color carColor = null;
        String name = null;
        if (acceleration == 0) {
            carColor = MapObject.getRandomColor();
            name = "SLOW";
        } else {
            carColor = MapObject.COLORS[1];
            name = "FAST";
        }

        Vehicle v = new Vehicle(name + counter++, new MapPosition(0, 1, 2, 1), getRandomRoad(), speed, acceleration);
        v.setColor(carColor);
        synchronized (map) {
            map.addMapObject(v);
        }
    }
}