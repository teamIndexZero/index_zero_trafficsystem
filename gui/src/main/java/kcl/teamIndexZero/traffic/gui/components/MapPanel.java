package kcl.teamIndexZero.traffic.gui.components;

import kcl.teamIndexZero.traffic.gui.mvc.GuiController;
import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 *
 */
public class MapPanel extends JPanel implements GuiModel.ChangeListener {
    private final GuiModel model;
    private final GuiController controller;

    public MapPanel(GuiModel model, GuiController controller) {
        this.model = model;
        this.controller = controller;
        model.addChangeListener(this);
    }

    @Override
    public void onModelChanged() {
        if (model.getLastImage() == null) {
            return;
        }
        getGraphics().drawImage(
                model.getLastImage(),
                100,
                20,
                model.getLastImage().getWidth() * 6,
                model.getLastImage().getHeight() * 2,
                null);
    }

}
