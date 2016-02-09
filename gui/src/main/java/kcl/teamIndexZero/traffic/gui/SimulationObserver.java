package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.MapPosition;
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
    private final BufferedImage image;

    public SimulationObserver(SimulationMap map, Consumer<BufferedImage> imageConsumer) {
        this.map = map;
        this.imageConsumer = imageConsumer;
        image = new BufferedImage(map.getW(), map.getH(), BufferedImage.TYPE_INT_RGB);

    }

    @Override
    public void tick(SimulationTick tick) {
        Graphics2D graphics = (Graphics2D) image.getGraphics();

        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, image.getWidth(), image.getHeight());

        map.getObjectsOnMap().forEach(object -> {
            graphics.setColor(object.getColor());
            MapPosition pos = object.getPosition();
            graphics.fillRect(
                    pos.y,
                    pos.x,
                    pos.height,
                    pos.width
            );
        });

        graphics.dispose();

        imageConsumer.accept(image);

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
