package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.MapPosition;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.Vehicle;

/**
 * A traffic generator for adding cars into the map basing on some statistical approach.
 */
public class CarAdder implements ISimulationAware {

    int counter = 1;
    boolean addedCarOnPrevStep = false;
    private final SimulationMap map;

    /**
     * Constructor with map.
     *
     * @param map {@link SimulationMap} to add vehicles to.
     */
    public CarAdder(SimulationMap map) {
        this.map = map;
    }

    private void addForwardCar() {
        if (Math.random() < 0.5) {
            map.addMapObject(new Vehicle("Forward TRUCK " + counter++, new MapPosition(300 - 4, 5, 4, 1), -0.1f, 0));
        } else {
            map.addMapObject(new Vehicle("Forward CAR " + counter++, new MapPosition(300 - 2, 4, 2, 1), -0.24f, 0));
        }
    }

    private void addBackwardCar() {
        if (Math.random() < 0.5) {
            map.addMapObject(new Vehicle("Backward TRUCK " + counter++, new MapPosition(2, 0, 4, 1), 0.05f, 0));
        } else {
            map.addMapObject(new Vehicle("Backward CAR " + counter++, new MapPosition(0, 1, 2, 1), 0.2f, 0));
        }
    }

    /**
     * Implements {@link ISimulationAware} tick method.
     * <p>
     * Here we just every ~1 of 5 times generate a car which happens to be either top to bottom or bottom to top car.
     *
     * @param tick tick we are working on. It has simulated time, sequence number at least
     */
    @Override
    public void tick(SimulationTick tick) {
        if (Math.random() < 0.2 && !addedCarOnPrevStep) {
            addedCarOnPrevStep = true;
            if (tick.getTickNumber() % 2 == 1) {
                addForwardCar();
            } else {
                addBackwardCar();
            }
        } else {
            addedCarOnPrevStep = false;
        }
    }
}