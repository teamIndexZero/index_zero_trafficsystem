package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.gui.components.MainToolbar;
import kcl.teamIndexZero.traffic.gui.components.MapPanel;
import kcl.teamIndexZero.traffic.gui.mvc.GuiController;
import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.Simulator;
import kcl.teamIndexZero.traffic.simulator.data.*;

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
        controller = new GuiController(model);
        mainToolBar = new MainToolbar(model, controller);

        mapPanel = new MapPanel(model, controller);

        frame = new JFrame("Simulation - One Road 2 Lanes Each Way");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.add(mainToolBar, BorderLayout.PAGE_START);
        frame.add(mapPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setSize(200 + 6 * 6, 300 * 2 + 100);
        frame.setVisible(true);
    }
}
