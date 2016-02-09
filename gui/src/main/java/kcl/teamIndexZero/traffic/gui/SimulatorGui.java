package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.Simulator;
import kcl.teamIndexZero.traffic.simulator.data.*;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Consumer;

public class SimulatorGui implements Consumer<BufferedImage> {

    protected static Logger_Interface LOG = Logger.getLoggerInstance(SimulatorGui.class.getSimpleName());


    private final JFrame frame;

    public static void main(String[] args) {
        new SimulatorGui().startSimulation();
    }

    public SimulatorGui() {
        frame = new JFrame("Simulation - One Road 2 Lanes Each Way");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(200 + 6 * 6, 300 * 2 + 100);
        frame.setVisible(true);
    }

    private void startSimulation() {
        SimulationMap map = new SimulationMap(6, 300);
        SimulationObserver observer = new SimulationObserver(map, this);


        class CarAdder implements ISimulationAware {
            int counter = 1;
            boolean addedCarOnPrevStep = false;

            private void addForwardCar() {
                if (Math.random() < 0.5) {
                    map.addMapObject(new Vehicle("Forward TRUCK " + counter++, new MapPosition(300 - 4, 5, 4, 1), -0.1f, 0));
                } else {
                    map.addMapObject(new Vehicle("Forward CAR " + counter++, new MapPosition(300 - 2, 4, 2, 1), -0.24f, 0));
                }
            }

            private void addBackwardCar() {
                if (Math.random() < 0.5) {
                    map.addMapObject(new Vehicle("Backward TRUCK " + counter++, new MapPosition(2, 0, 4, 1), 0.05f, 0));
                } else {
                    map.addMapObject(new Vehicle("Backward CAR " + counter++, new MapPosition(0, 1, 2, 1), 0.2f, 0));
                }
            }

            @Override
            public void tick(SimulationTick tick) {
                if (Math.random() < 0.2 && !addedCarOnPrevStep) {
                    addedCarOnPrevStep = true;
                    if (tick.tickNumber % 2 == 1) {
                        addForwardCar();
                    } else {
                        addBackwardCar();
                    }
                } else {
                    addedCarOnPrevStep = false;
                }
            }
        }

        class CarRemover implements ISimulationAware {
            @Override
            public void tick(SimulationTick tick) {
                //todo probably we just want to skip that form simulation, or at least move to crossings/outlets, hm?
                map.getObjectsOnMap().removeIf(object -> object.getPosition().x < 0 || object.getPosition().x > map.getH());
            }
        }


        map.addMapObject(new Vehicle("Ferrari ES3 4FF", new MapPosition(0, 0, 2, 1), 0.05f, 0));
        map.addMapObject(new Vehicle("Taxi TT1", new MapPosition(400, 5, 2, 1), -0.1f, 0));

        Simulator simulator = new Simulator(
                new SimulationParams(LocalDateTime.now(), 10, 1000),
                Arrays.asList(map, observer, new CarAdder(), new CarRemover())
        );


        simulator.start();
        simulator.stop();

    }

    @Override
    public void accept(BufferedImage bufferedImage) {
        frame.getGraphics().drawImage(bufferedImage, 100, 50, bufferedImage.getWidth() * 6, bufferedImage.getHeight() * 2, null);
    }
}
