package kcl.teamIndexZero.traffic.simulator;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.*;
import kcl.teamIndexZero.traffic.simulator.data.features.Feature;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.MapPosition;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Obstacle;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle;
import kcl.teamIndexZero.traffic.simulator.exeptions.AlreadyExistsException;
import kcl.teamIndexZero.traffic.simulator.exeptions.EmptySimMapException;
import kcl.teamIndexZero.traffic.simulator.exeptions.MapIntegrityException;
import kcl.teamIndexZero.traffic.simulator.exeptions.OrphanFeatureException;
import kcl.teamIndexZero.traffic.simulator.mapSetup.MapFactory;
import kcl.teamIndexZero.traffic.simulator.mapSetup.mapFeatureType;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Main class (entry point) for the Simulator command line interface.
 * <p>
 * TODO: add the CLI parameters parsing with commons-cli and set the params as such from CLI.
 */
public class SimulatorEntryPoint {
    private static Logger_Interface LOG = Logger.getLoggerInstance(SimulatorEntryPoint.class.getSimpleName());

    /**
     * Main method.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        try {
            MapFactory ezFactory = new MapFactory();
            ezFactory.newFeature(mapFeatureType.SIMPLE_TWO_WAY_ROAD, new ID("road1"));
            //TODO do some map entity creation
            Map<ID, Feature> featureMap = new HashMap<>();
            List<LinkDescription> links = new LinkedList<LinkDescription>();
            GraphConstructor graph = new GraphConstructor(featureMap, links); //TODO temp stuff. need to take care of the exceptions too
            SimulationMap map = new SimulationMap(4, 400, graph);


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
        } catch (AlreadyExistsException e) {
            LOG.log_Error("Tying to use an ID tag that's already been used before for a new Feature!");
            LOG.log_Exception(e);
        } catch (EmptySimMapException e) {
            LOG.log_Fatal("The map graph is empty of features!");
            LOG.log_Exception(e);
            e.printStackTrace();
        } catch (OrphanFeatureException e) {
            LOG.log_Fatal("There are orphan (unconnected) features in the map graph!");
            LOG.log_Exception(e);
            e.printStackTrace();
        } catch (MapIntegrityException e) {
            LOG.log_Fatal("Map integrity is compromised.");
            LOG.log_Exception(e);
            e.printStackTrace();
        }
    }
}
