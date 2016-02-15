package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.Simulator;
import kcl.teamIndexZero.traffic.simulator.data.*;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Main class (entry poing for simulator Graphical User Interface).
 */
public class SimulatorGui {

    protected static Logger_Interface LOG = Logger.getLoggerInstance(SimulatorGui.class.getSimpleName());

    private final JFrame frame;

    /**
     * Entry point.
     *
     * @param args CLI parameters
     */
    public static void main(String[] args) {
        new SimulatorGui().startSimulation();
    }

    /**
     * Default constructor.
     */
    public SimulatorGui() {
        frame = new JFrame("Simulation - One Road 2 Lanes Each Way");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(200 + 6 * 6, 300 * 2 + 100);
        frame.setVisible(true);
    }

    /**
     * Start a simulation. Once an object is constructed, we should call this method to run a simulation.
     */
    private void startSimulation() {
        SimulationMap map = new SimulationMap(6, 300);
        SimulationImageProducer imageProducer = new SimulationImageProducer(
                map,
                bufferedImage -> {
                    frame.getGraphics().drawImage(
                            bufferedImage,
                            100,
                            50,
                            bufferedImage.getWidth() * 6,
                            bufferedImage.getHeight() * 2,
                            null);
                }
        );
        SimulationDelay delay = new SimulationDelay(50);
        CarAdder adder = new CarAdder(map);
        CarRemover remover = new CarRemover(map);

        map.addMapObject(new Vehicle("Ferrari ES3 4FF", new MapPosition(0, 0, 2, 1), 0.05f, 0));
        map.addMapObject(new Vehicle("Taxi TT1", new MapPosition(400, 5, 2, 1), -0.1f, 0));

        Simulator simulator = new Simulator(
                new SimulationParams(LocalDateTime.now(), 10, 1000),
                Arrays.asList(
                        map,
                        imageProducer,
                        adder,
                        remover,
                        delay)
        );

        simulator.start();
        simulator.stop();
    }
}
