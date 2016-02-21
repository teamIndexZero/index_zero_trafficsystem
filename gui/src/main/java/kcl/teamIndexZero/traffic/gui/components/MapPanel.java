package kcl.teamIndexZero.traffic.gui.components;

import kcl.teamIndexZero.traffic.gui.mvc.GuiController;
import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 *
 */
public class MapPanel extends JPanel {
    private final GuiModel model;
    private final GuiController controller;

    public MapPanel(GuiModel model, GuiController controller) {
        this.model = model;
        this.controller = controller;
        model.addChangeListener(this::repaint);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (model.getLastImage() == null) {
            String messageNotRunning = "Simulation not running.";
            g.drawChars(messageNotRunning.toCharArray(), 0, messageNotRunning.length()-1, 30,30);
            return;
        }
        g.drawImage(
                model.getLastImage(),
                100,
                20,
                model.getLastImage().getWidth() * 4,
                model.getLastImage().getHeight() * 4,
                null);
    }
}
