package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.simulator.Simulator;
import kcl.teamIndexZero.traffic.simulator.data.*;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Consumer;

public class SimulatorGui implements Consumer<BufferedImage> {

    private final JFrame frame;

    public static void main(String[] args) {
        new SimulatorGui().startSimulation();
    }

    public SimulatorGui() {
        frame = new JFrame("FrameDemo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 600);
        frame.setVisible(true);
    }

    private void startSimulation() {
        SimulationMap map = new SimulationMap(4, 400);
        SimulationObserver observer = new SimulationObserver(map, this);

        map.addMapObject(new Obstacle("Fallen tree 1", new MapPosition(330, 0, 7, 1)));
        map.addMapObject(new Obstacle("Fallen tree 1", new MapPosition(20, 2, 7, 2)));
        map.addMapObject(new Obstacle("Stone 1", new MapPosition(230, 2, 1, 1)));
        map.addMapObject(new Vehicle("Ferrari ES3 4FF", new MapPosition(0, 0, 1, 2), 0.05f, 0));
        map.addMapObject(new Vehicle("Land Rover RRT 2YG", new MapPosition(0, 1, 1, 2), 0.01f, 0.00002f));

        Simulator simulator = new Simulator(
                new SimulationParams(LocalDateTime.now(), 20, 500),
                Arrays.asList(map, observer)
        );

        simulator.start();
        simulator.stop();

    }

    @Override
    public void accept(BufferedImage bufferedImage) {
        frame.getGraphics().drawImage(bufferedImage, 200, 100, null);
    }
}
