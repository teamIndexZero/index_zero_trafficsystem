package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.gui.components.ChooserDialog;
import kcl.teamIndexZero.traffic.gui.components.MainWindow;
import kcl.teamIndexZero.traffic.gui.mvc.GuiController;
import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.Simulator;
import kcl.teamIndexZero.traffic.simulator.data.GraphConstructor;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationParams;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.LinkDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import kcl.teamIndexZero.traffic.simulator.data.links.LinkType;
import kcl.teamIndexZero.traffic.simulator.exceptions.MapIntegrityException;
import kcl.teamIndexZero.traffic.simulator.osm.OsmParseResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Main class (entry poing for simulator Graphical User Interface).
 */
public class SimulatorGui {

    protected static Logger_Interface LOG = Logger.getLoggerInstance(SimulatorGui.class.getSimpleName());

    private GuiModel model;

    /**
     * Default constructor.
     */
    public SimulatorGui() {
        ChooserDialog.showForOSMLoadResult(this::startSimulatorWindow);
    }

    /**
     * Entry point.
     *
     * @param args CLI parameters
     */
    public static void main(String[] args) {
        new SimulatorGui();
    }

    private void startSimulatorWindow(OsmParseResult result) {
        try {
            //TODO factory then pass the stuff below to graph constructor
            java.util.List<JunctionDescription> junctionDescriptions = new ArrayList<>();
            java.util.List<LinkDescription> links = new ArrayList<>();
            java.util.List<RoadDescription> roads = result.descriptionList;

            links.add(new LinkDescription(roads.get(0).getId(), roads.get(0).getId(), LinkType.SYNC_TL, new ID("Link1")));

            GraphConstructor graph = new GraphConstructor(junctionDescriptions, roads, links); //TODO temp stuff. need to take care of the exceptions too

            SimulationMap map = new SimulationMap(4, 400, graph);
            map.widthMeters = result.boundingBox.end.xMeters;
            map.heightMeters = result.boundingBox.end.yMeters;

            model = new GuiModel(map);

            SimulationImageProducer imageProducer = new SimulationImageProducer(map, model);

            GuiController controller = new GuiController(model, () -> {

                SimulationDelay delay = new SimulationDelay(model);
                CarAdder adder = new CarAdder(map);
                CarRemover remover = new CarRemover(map);

                return new Simulator(
                        new SimulationParams(LocalDateTime.now(), 1, 1000),
                        Arrays.asList(
                                map,
                                adder,
                                remover,
                                delay,
                                model)
                );
            });

            // that's where we reset model into default state - before the simulation is started.

            model.reset();
            MainWindow window = new MainWindow(model, controller);
            imageProducer.setImageConsumer(window.getMapPanel());
            window.setVisible(true);
        } catch (MapIntegrityException e) {
            LOG.log_Fatal("Map integrity compromised.");
            LOG.log_Exception(e);
        }
    }

}
