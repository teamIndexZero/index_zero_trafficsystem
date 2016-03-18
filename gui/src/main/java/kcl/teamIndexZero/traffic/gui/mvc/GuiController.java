package kcl.teamIndexZero.traffic.gui.mvc;

import kcl.teamIndexZero.traffic.gui.SimulatorGui;
import kcl.teamIndexZero.traffic.gui.components.SimulationWindow;
import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.Simulator;
import kcl.teamIndexZero.traffic.simulator.SimulatorFactory;

import javax.swing.*;

/**
 * Controller of our MVC model. Controller is generally responsible for 'business logic' - that is, actually doing the
 * heavy lifting of the task. For us, controller controls simulator and then updates model based on new events from
 * simulator or from the UI.
 */
public class GuiController {

    protected static Logger_Interface LOG = Logger.getLoggerInstance(GuiController.class.getSimpleName());

    private GuiModel model;
    private SimulatorFactory factory;
    private Simulator simulator;
    private Thread simulatorThread;

    /**
     * Constructor with model. Controller needs it since it actively changes model details.
     *
     * @param model GUI model
     */
    public GuiController(GuiModel model, SimulatorFactory factory) {
        this.model = model;
        this.factory = factory;
    }

    /**
     * We may need that for status updates and understanding how is simulation going on
     */
    public Thread getSimulatorThread() {
        return simulatorThread;
    }

    /**
     * Pause method - temporarily pause the execution until further commands. We can go to 'restart' or to 'continue' then.
     */
    public void pause() {
        model.setStatus(GuiModel.SimulationStatus.PAUSED);
        simulator.pause();
    }

    /**
     * Finish the simulation with erasing all the immediately visible results.
     * It then resets the model into initial state.
     */
    public void restart() {
        assert (simulator != null);

        if (model.getStatus() == GuiModel.SimulationStatus.PAUSED) {
            simulator.resume();
        }
        if (simulator != null) {
            simulator.stop();
        }
        model.reset();
        SimulatorGui.startOver();
        SimulationWindow.close();
    }

    /**
     * Start the simulation.
     */
    public void start() {
        if (model.getStatus() == GuiModel.SimulationStatus.PAUSED) {
            simulator.resume();
        } else {
            simulator = factory.createSimulator();
            simulatorThread = new Thread(() -> {
                try {
                    simulator.start();
                } catch (Exception e) {
                    LOG.log_Exception(e);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                                null,
                                "Error in simulation - please see logs for details.\n" + e.getClass().getCanonicalName() + "\n" + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
            }, "SimulatorThread");

            simulatorThread.start();
        }
        model.setStatus(GuiModel.SimulationStatus.INPROGRESS);
    }
}
