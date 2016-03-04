package kcl.teamIndexZero.traffic.gui.components;

import kcl.teamIndexZero.traffic.gui.mvc.GuiController;
import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by lexaux on 03/03/2016.
 */
public class MainWindow extends JFrame {
    private final GuiModel model;
    private final GuiController controller;
    private final MapPanel mapPanel;

    public MainWindow(GuiModel model, GuiController controller) {
        super("Simulation - One Road 2 DirectedLanes Each Way");

        this.model = model;
        this.controller = controller;

        mapPanel = new MapPanel(model);

        MainToolbar mainToolBar = new MainToolbar(model, controller);

        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        add(mainToolBar, BorderLayout.PAGE_START);
        add(mapPanel, BorderLayout.CENTER);
        add(new SimulationDetailsPanel(model, controller), BorderLayout.WEST);
        pack();

        mapPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                model.setMapPanelSize(e.getComponent().getWidth(), e.getComponent().getHeight());
            }
        });
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }
}
