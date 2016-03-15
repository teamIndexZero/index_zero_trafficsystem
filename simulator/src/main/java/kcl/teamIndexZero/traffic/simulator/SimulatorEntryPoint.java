package kcl.teamIndexZero.traffic.simulator;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;

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
        /*
        try {
            List<JunctionDescription> junctionDescriptions = new LinkedList<>();
            List<RoadDescription> roads = new LinkedList<>();
            GraphConstructor graph = new GraphConstructor(junctionDescriptions, roads); //TODO temp stuff. need to take care of the exceptions too
            SimulationMap map = new SimulationMap(4, 400, graph);

            Simulator simulator = new Simulator(
                    new SimulationParams(LocalDateTime.now(), 20, 100),
                    Collections.singletonList(map)
            );

            simulator.start();
            simulator.stop();
        } catch (MapIntegrityException e) {
            LOG.log_Fatal("Map integrity is compromised.");
            LOG.log_Exception(e);
            e.printStackTrace();
        }
        */
    }
}
