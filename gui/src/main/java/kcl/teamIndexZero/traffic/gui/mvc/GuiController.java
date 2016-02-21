package kcl.teamIndexZero.traffic.gui.mvc;

import kcl.teamIndexZero.traffic.gui.CarAdder;
import kcl.teamIndexZero.traffic.gui.CarRemover;
import kcl.teamIndexZero.traffic.gui.SimulationDelay;
import kcl.teamIndexZero.traffic.gui.SimulationImageProducer;
import kcl.teamIndexZero.traffic.simulator.Simulator;
import kcl.teamIndexZero.traffic.simulator.data.MapPosition;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationParams;
import kcl.teamIndexZero.traffic.simulator.data.Vehicle;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Created by lexaux on 20/02/2016.
 */
public class GuiController {

    private GuiModel model;
    private Simulator simulator;
    private Thread simulatorThread;

    public GuiController(GuiModel model) {

        this.model = model;
    }

    public void pause() {
        model.setStatus(GuiModel.SimulationStatus.PAUSED);
        simulator.pause();
    }

    public void stop() {
        assert (simulator != null);
        assert (simulatorThread != null);
        if (model.getStatus() == GuiModel.SimulationStatus.PAUSED) {
            simulator.resume();
        }
        simulator.stop();
        model.reset();
    }

    public void start() {
        if (model.getStatus() == GuiModel.SimulationStatus.PAUSED) {
            simulator.resume();
        } else {
            SimulationMap map = new SimulationMap(6, 300);
            SimulationImageProducer imageProducer = new SimulationImageProducer(
                    map,
                    (image, tick) -> {
                        SwingUtilities.invokeLater(() -> {
                            model.setLastSimulationTickAndImage(image, tick);
                        });
                    }
            );
            SimulationDelay delay = new SimulationDelay(50);
            CarAdder adder = new CarAdder(map);
            CarRemover remover = new CarRemover(map);

            map.addMapObject(new Vehicle("Ferrari ES3 4FF", new MapPosition(0, 0, 2, 1), 0.05f, 0));
            map.addMapObject(new Vehicle("Taxi TT1", new MapPosition(400, 5, 2, 1), -0.1f, 0));

            simulator = new Simulator(
                    new SimulationParams(LocalDateTime.now(), 10, 1000),
                    Arrays.asList(
                            map,
                            imageProducer,
                            adder,
                            remover,
                            delay)
            );
            simulatorThread = new Thread(() -> {
                simulator.start();
            }, "SimulatorThread");
            simulatorThread.start();
        }
        model.setStatus(GuiModel.SimulationStatus.INPROGRESS);

    }
}
