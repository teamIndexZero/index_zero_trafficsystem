package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * Created by lexaux on 07/02/2016.
 */
public class SimulationObserver implements ISimulationAware {
    protected static Logger_Interface LOG = Logger.getLoggerInstance(SimulationObserver.class.getSimpleName());

    private SimulationMap map;

    private Consumer<BufferedImage> imageConsumer;

    public SimulationObserver(SimulationMap map, Consumer<BufferedImage> imageConsumer) {
        this.map = map;
        this.imageConsumer = imageConsumer;
    }

    @Override
    public void tick(SimulationTick tick) {
        boolean[][] currentState = map.getMap();
        BufferedImage image = new BufferedImage(map.getW(), map.getH(), BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < map.getW(); x++) {
            for (int y = 0; y < map.getH(); y++) {
                image.setRGB(x, y, (currentState[y][x] ? 0xFFFF0000 : 0xFFFFFFFF));
            }
        }
        imageConsumer.accept(image);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
