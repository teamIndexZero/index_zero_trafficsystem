package kcl.teamIndexZero.traffic.gui.components;

import kcl.teamIndexZero.traffic.gui.mvc.GuiController;
import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Window containing actual simulation process. It has multiple panels, that is - actual map panel responsible for
 * drawing the map with all the features and settings/information panel.
 */
public class SimulationWindow extends JFrame {
    private static SimulationWindow INSTANCE = null;
    private final GuiModel model;
    private final GuiController controller;
    private final MapPanel mapPanel;

    /**
     * Default constructor with model and controller.
     *
     * @param model      model to look at
     * @param controller controller to invoke actions on
     */
    public SimulationWindow(GuiModel model, GuiController controller) {
        super("Simulation - One Road 2 DirectedLanes Each Way");
        if (INSTANCE != null) {
            throw new IllegalStateException("Another simulation window running");
        }
        INSTANCE = this;

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
                model.getViewport().setMapPanelSize(e.getComponent().getWidth(), e.getComponent().getHeight());
            }
        });
    }

    /**
     * A static close method is used to re-start the whole simulation. Not the best design choice, but easiest at the
     * time being.
     * <p>
     * By this we also ensure that there is only one window of the app open at any time.
     */
    public static void close() {
        if (INSTANCE == null) {
            return;
        }
        INSTANCE.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        INSTANCE.setVisible(false);
        INSTANCE.dispose();
        INSTANCE = null;
    }

    /**
     * Exposes the map panel.
     *
     * @return actual MapPanel instance
     */
    public MapPanel getMapPanel() {
        return mapPanel;
    }
}
