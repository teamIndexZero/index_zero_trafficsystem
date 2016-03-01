package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.gui.components.MainToolbar;
import kcl.teamIndexZero.traffic.gui.components.MapPanel;
import kcl.teamIndexZero.traffic.gui.mvc.GuiController;
import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.Simulator;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationParams;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.MapPosition;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle;
import kcl.teamIndexZero.traffic.simulator.exeptions.AlreadyExistsException;
import kcl.teamIndexZero.traffic.simulator.exeptions.EmptySimMapException;
import kcl.teamIndexZero.traffic.simulator.exeptions.MapIntegrityException;
import kcl.teamIndexZero.traffic.simulator.exeptions.OrphanFeatureException;
import kcl.teamIndexZero.traffic.simulator.mapSetup.MapFactory;
import kcl.teamIndexZero.traffic.simulator.mapSetup.mapFeatureType;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Arrays;


/**
 * Main class (entry poing for simulator Graphical User Interface).
 */
public class SimulatorGui {

    protected static Logger_Interface LOG = Logger.getLoggerInstance(SimulatorGui.class.getSimpleName());

    private final JFrame frame;
    private final MainToolbar mainToolBar;
    private final GuiController controller;
    private final GuiModel model;
    private final MapPanel mapPanel;

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
        try {
            MapFactory ezFactory = new MapFactory();
            ezFactory.newFeature(mapFeatureType.SIMPLE_TWO_WAY_ROAD, new ID("road1"));
            SimulationMap map = new SimulationMap(4, 400, ezFactory.getFeatures(), ezFactory.getLinks());
            model = new GuiModel();
            controller = new GuiController(model, () -> {
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
            mainToolBar = new MainToolbar(model, controller);

            mapPanel = new MapPanel(model);

            frame = new JFrame("Simulation - One Road 2 DirectedLanes Each Way");
            frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.getContentPane().setLayout(new BorderLayout());
            frame.add(mainToolBar, BorderLayout.PAGE_START);
            frame.add(mapPanel, BorderLayout.CENTER);
            frame.pack();
            frame.setVisible(true);

            // that's where we reset model into default state - before the simulation is started.
            model.reset();
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
            LOG.log_Fatal("Map integrity compromised.");
            LOG.log_Exception(e);
            e.printStackTrace();
        } finally {
            throw new RuntimeException();
        }
    }

}
