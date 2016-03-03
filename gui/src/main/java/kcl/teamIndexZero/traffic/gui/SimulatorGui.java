package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.gui.components.MainToolbar;
import kcl.teamIndexZero.traffic.gui.components.MapPanel;
import kcl.teamIndexZero.traffic.gui.mvc.GuiController;
import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.gui.simulationChooserDialog.ChooserDialog;
import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.Simulator;
import kcl.teamIndexZero.traffic.simulator.data.GraphConstructor;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationParams;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.LinkDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import kcl.teamIndexZero.traffic.simulator.data.features.Junction;
import kcl.teamIndexZero.traffic.simulator.data.links.LinkType;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.MapPosition;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle;
import kcl.teamIndexZero.traffic.simulator.exeptions.MapIntegrityException;
import kcl.teamIndexZero.traffic.simulator.osm.OsmParseResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
     * Entry point.
     *
     * @param args CLI parameters
     */
    public static void main(String[] args) {
        new SimulatorGui();
    }

    /**
     * Default constructor.
     */
    public SimulatorGui() {
        ChooserDialog.showForOSMLoadResult(result -> {
            startSimulatorWindow(result);
        });
    }

    private void startSimulatorWindow(OsmParseResult result) {
        try {
            //TODO factory then pass the stuff below to graph constructor
            java.util.List<Junction> junctions = new ArrayList<>();
            java.util.List<LinkDescription> links = new ArrayList<>();
            java.util.List<RoadDescription> roads = result.descriptionList;

            links.add(new LinkDescription(roads.get(0).getId(), roads.get(1).getId(), LinkType.SYNC_TL, new ID("Link1")));
            junctions.add(new Junction(new ID("Junction1")));

            GraphConstructor graph = new GraphConstructor(junctions, roads, links); //TODO temp stuff. need to take care of the exceptions too

            SimulationMap map = new SimulationMap(4, 400, graph);
            map.latStart = result.boundingBox.start.latitude;
            map.latEnd = result.boundingBox.end.latitude;
            map.lonStart = result.boundingBox.start.longitude;
            map.lonEnd = result.boundingBox.end.longitude;

            model = new GuiModel();

            SimulationImageProducer imageProducer = new SimulationImageProducer(
                    map,
                    (image, tick) -> {
                        // here, careful! We are working in another thread, but we want to update UI. In this case, we
                        // need a carfeul message passing mechanism to update UI thread. We'd rather invoke model
                        // update in UI thread, since it will then natively fire the UI redraw.
                        SwingUtilities.invokeLater(() -> {
                            model.setLastSimulationTickAndImage(image, tick);
                        });
                    }
            );

            GuiController controller = new GuiController(model, () -> {

                SimulationDelay delay = new SimulationDelay(50);
                CarAdder adder = new CarAdder(map);
                CarRemover remover = new CarRemover(map);

                map.addMapObject(new Vehicle("Ferrari ES3 4FF", new MapPosition(0, 0, 2, 1), 0.05f, 0));
                map.addMapObject(new Vehicle("Taxi TT1", new MapPosition(400, 5, 2, 1), -0.1f, 0));

                return new Simulator(
                        new SimulationParams(LocalDateTime.now(), 10, 1000),
                        Arrays.asList(
                                map,
                                imageProducer,
                                adder,
                                remover,
                                delay)
                );
            });
            MainToolbar mainToolBar = new MainToolbar(model, controller);

            MapPanel mapPanel = new MapPanel(model);

            JFrame frame = new JFrame("Simulation - One Road 2 DirectedLanes Each Way");
            frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.getContentPane().setLayout(new BorderLayout());
            frame.add(mainToolBar, BorderLayout.PAGE_START);
            frame.add(mapPanel, BorderLayout.CENTER);
            frame.pack();
            frame.setVisible(true);

            mapPanel.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    imageProducer.setPixelSize(e.getComponent().getWidth(), e.getComponent().getHeight());
                }
            });

            // that's where we reset model into default state - before the simulation is started.
            model.reset();
        } catch (MapIntegrityException e) {
            LOG.log_Fatal("Map integrity compromised.");
            LOG.log_Exception(e);
        }
    }

}
