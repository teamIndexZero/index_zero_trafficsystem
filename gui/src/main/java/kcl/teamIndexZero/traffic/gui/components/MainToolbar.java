package kcl.teamIndexZero.traffic.gui.components;

import kcl.teamIndexZero.traffic.gui.Callback;
import kcl.teamIndexZero.traffic.gui.SimulatorGui;
import kcl.teamIndexZero.traffic.gui.mvc.GuiController;
import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * Main toolbar of the application - is also an MVC subscriber.
 */
public class MainToolbar extends JToolBar implements GuiModel.ChangeListener {
    protected static Logger_Interface LOG = Logger.getLoggerInstance(MainToolbar.class.getSimpleName());


    private final GuiModel model;
    private final GuiController controller;
    private JButton playButton;
    private JButton stopButton;
    private JButton pauseButton;
    private JTextField tickDetailsField;


    public MainToolbar(GuiModel model, GuiController controller) {
        super("MainToolBar");
        this.model = model;
        this.controller = controller;
        setFloatable(false);
        addButtons();

        model.addChangeListener(this);
    }

    @Override
    public void onModelChanged() {
        // updating button status
        pauseButton.setEnabled(model.getStatus() == GuiModel.SimulationStatus.INPROGRESS);
        stopButton.setEnabled(model.getStatus() == GuiModel.SimulationStatus.INPROGRESS
                || model.getStatus() == GuiModel.SimulationStatus.PAUSED);
        playButton.setEnabled(model.getStatus() == GuiModel.SimulationStatus.OFF
                || model.getStatus() == GuiModel.SimulationStatus.PAUSED);

        // updating tick details
        if (model.getTick() != null) {
            tickDetailsField.setText(model.getTick().toString());
        } else {
            tickDetailsField.setText("<NONE>");
        }
    }

    protected void addButtons() {
        playButton = makeButton(
                "play",
                "Start the simulation",
                "Start",
                controller::start);
        add(playButton);

        pauseButton = makeButton(
                "pause",
                "Temporarily pause the simulation",
                "Pause",
                controller::pause
        );
        add(pauseButton);

        stopButton = makeButton(
                "stop",
                "Stop the simulation",
                "Stop",
                controller::stop);
        add(stopButton);

        tickDetailsField = new JTextField(16);
        add(tickDetailsField);
    }


    protected JButton makeButton(String imageName,
                                 String toolTipText,
                                 String altText,
                                 Callback actionListener) {
        //Look for the image.
        String imgLocation = "/icons24/"
                + imageName
                + ".png";
        URL imageURL = SimulatorGui.class.getResource(imgLocation);

        //Create and initialize the button.
        JButton button = new JButton();

        button.setToolTipText(toolTipText);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    actionListener.call();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    LOG.log_Error("Got an exception invoking " + e.getSource());
                    LOG.log_Exception(e1);
                }
            }
        });

        if (imageURL != null) {                      //image found
            button.setIcon(new ImageIcon(imageURL, altText));
        } else {                                     //no image found
            button.setText(altText);
            System.err.println("Resource not found: " + imgLocation);
        }

        return button;
    }
}
