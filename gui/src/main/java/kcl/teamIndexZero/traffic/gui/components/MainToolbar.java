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
 * <p>
 * Main Toolbar contains button related to actual simulation flow - this is a 'control interface' from architecture
 * diagram. For now buttons are Pause, Stop and Play, and also a textbox which shows the current simulation step details.
 */
public class MainToolbar extends JToolBar implements GuiModel.ChangeListener {
    protected static Logger_Interface LOG = Logger.getLoggerInstance(MainToolbar.class.getSimpleName());

    private final GuiModel model;
    private final GuiController controller;
    private JButton playButton;
    private JButton stopButton;
    private JButton pauseButton;
    private JTextField tickDetailsField;


    /**
     * Create toolbar off model and controller.
     *
     * @param model      GUI model
     * @param controller GUI controller. Does actual things. Toolbar only forwards the commands to controller.
     */
    public MainToolbar(GuiModel model, GuiController controller) {
        super("MainToolBar");
        this.model = model;
        this.controller = controller;
        setFloatable(false);
        addButtons();

        // subscribing to events after we are done creating UI.
        model.addChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
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

    /*
    Helper method to add buttons to the panel.
     */
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


    /**
     * Helper method to create button. We are using our own {@link Callback} interface within to ensure the flow is
     * transferred to where it should belong.
     */
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

        if (imageURL != null) {
            //image found
            button.setIcon(new ImageIcon(imageURL, altText));
        } else {
            //no image found
            button.setText(altText);
            LOG.log_Error("Could not find image ", imageURL);
        }

        return button;
    }
}
