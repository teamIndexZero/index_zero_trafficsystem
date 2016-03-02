package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.gui.components.MainToolbar;
import kcl.teamIndexZero.traffic.gui.components.MapPanel;
import kcl.teamIndexZero.traffic.gui.mvc.GuiController;
import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.Simulator;
import kcl.teamIndexZero.traffic.simulator.data.MapPosition;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationParams;
import kcl.teamIndexZero.traffic.simulator.data.Vehicle;

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
        model = new GuiModel();
        controller = new GuiController(model, () -> {
            SimulationMap map = new SimulationMap(300, 300);
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

        frame = new JFrame("Simulation - One Road 2 Lanes Each Way");
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.add(mainToolBar, BorderLayout.PAGE_START);
        frame.add(mapPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        // that's where we reset model into default state - before the simulation is started.
        model.reset();
    }

}
