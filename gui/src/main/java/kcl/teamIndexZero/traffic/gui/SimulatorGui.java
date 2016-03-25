package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.gui.components.ChooserDialog;
import kcl.teamIndexZero.traffic.gui.components.SimulationWindow;
import kcl.teamIndexZero.traffic.gui.mvc.GuiController;
import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.Simulator;
import kcl.teamIndexZero.traffic.simulator.data.GraphConstructor;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationParams;
import kcl.teamIndexZero.traffic.simulator.exceptions.MapIntegrityException;
import kcl.teamIndexZero.traffic.simulator.osm.OsmParseResult;

import java.time.LocalDateTime;
import java.util.Arrays;


/**
 * Main class (entry point for simulator Graphical User Interface).
 */
public class SimulatorGui {

    protected static Logger_Interface LOG = Logger.getLoggerInstance(SimulatorGui.class.getSimpleName());

    private GuiModel model;

    /**
     * Entry point.
     *
     * @param args CLI parameters
     */
    public static void main(String[] args) {
        startOver();
    }

    /**
     * Start from the beginning - with the chooser, etc.
     */
    public static void startOver() {
        ChooserDialog.showForOSMLoadResult(new SimulatorGui()::startSimulatorWindow);
    }

    private void startSimulatorWindow(OsmParseResult result) {
        try {
            LOG.log("Parsed OSM xml file, got ", result.roadDescriptions.size(), " roads and ", result.junctionDescriptions.size(), " junctions");
            GraphConstructor graph = new GraphConstructor(
                    result.junctionDescriptions,
                    result.roadDescriptions);
            SimulationMap map = new SimulationMap(4, 400, graph);
            map.widthMeters = result.boundingBox.end.xMeters;
            map.heightMeters = result.boundingBox.end.yMeters;

            // that's where we reset model into default state - before the simulation is started.
            model = new GuiModel(map);

            SimulationImageProducer imageProducer = new SimulationImageProducer(map, model);

            GuiController controller = new GuiController(model, () -> {
                SimulationDelay delay = new SimulationDelay(model);
                return new Simulator(
                        new SimulationParams(LocalDateTime.now(), 0.15, 10000),
                        Arrays.asList(
                                map,
                                delay,
                                model)
                );
            });

            SimulationWindow window = new SimulationWindow(model, controller);
            imageProducer.setImageConsumer(window.getMapPanel());
            window.setVisible(true);
        } catch (MapIntegrityException e) {
            LOG.log_Fatal("Map integrity compromised.");
            LOG.log_Exception(e);
        }
    }

}
