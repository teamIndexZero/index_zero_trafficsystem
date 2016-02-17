package kcl.teamIndexZero.traffic.simulator;

import kcl.teamIndexZero.traffic.simulator.data.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

/**
 * Main class (entry point) for the Simulator command line interface.
 * <p>
 * TODO: add the CLI parameters parsing with commons-cli and set the params as such from CLI.
 */
public class SimulatorEntryPoint {

    /**
     * Main method.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {

        SimulationMap map = new SimulationMap(4, 400);

        map.addMapObject(new Obstacle("Fallen tree 1", new MapPosition(0, 0, 2, 4)));
        map.addMapObject(new Obstacle("Stone 1", new MapPosition(230, 2, 1, 1)));
        map.addMapObject(new Vehicle("Ferrari ES3 4FF", new MapPosition(0, 0, 1, 2), 0.05f, 0));
        map.addMapObject(new Vehicle("Land Rover RRT 2YG", new MapPosition(0, 1, 1, 2), 0.01f, 0.00002f));

        Simulator simulator = new Simulator(
                new SimulationParams(LocalDateTime.now(), 20, 100),
                Collections.singletonList(map)
        );

        simulator.start();
        simulator.stop();
    }
}
