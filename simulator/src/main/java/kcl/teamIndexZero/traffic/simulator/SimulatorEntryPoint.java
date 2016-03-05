package kcl.teamIndexZero.traffic.simulator;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.GraphConstructor;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationParams;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.LinkDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import kcl.teamIndexZero.traffic.simulator.data.features.Junction;
import kcl.teamIndexZero.traffic.simulator.exceptions.AlreadyExistsException;
import kcl.teamIndexZero.traffic.simulator.exceptions.MapIntegrityException;
import kcl.teamIndexZero.traffic.simulator.mapSetup.MapFactory;
import kcl.teamIndexZero.traffic.simulator.mapSetup.mapFeatureType;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
            List<Junction> junctions = new LinkedList<>();
            List<LinkDescription> links = new LinkedList<LinkDescription>();
            List<RoadDescription> roads = new LinkedList<>();
            GraphConstructor graph = new GraphConstructor(junctions, roads, links); //TODO temp stuff. need to take care of the exceptions too
            SimulationMap map = new SimulationMap(4, 400, graph);


//            map.addMapObject(new Obstacle("Fallen tree 1", new MapPosition(0, 0, 2, 4)));
//            map.addMapObject(new Obstacle("Stone 1", new MapPosition(230, 2, 1, 1)));
//            map.addMapObject(new Vehicle("Ferrari ES3 4FF", new MapPosition(0, 0, 1, 2), 0.05f, 0));
//            map.addMapObject(new Vehicle("Land Rover RRT 2YG", new MapPosition(0, 1, 1, 2), 0.01f, 0.00002f));

            Simulator simulator = new Simulator(
                    new SimulationParams(LocalDateTime.now(), 20, 100),
                    Collections.singletonList(map)
            );

            simulator.start();
            simulator.stop();
        } catch (AlreadyExistsException e) {
            LOG.log_Error("Tying to use an ID tag that's already been used before for a new Feature!");
            LOG.log_Exception(e);
        } catch (MapIntegrityException e) {
            LOG.log_Fatal("Map integrity is compromised.");
            LOG.log_Exception(e);
            e.printStackTrace();
        }
    }
}
