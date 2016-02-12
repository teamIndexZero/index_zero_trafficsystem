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
 * An implementation of {@link ISimulationAware} which outputs a buffered image of the map size in respose to every
 * tick. This image can then be either packed into the video stream or directly shown on the window frame to show
 * live representation.
 */
public class SimulationImageProducer implements ISimulationAware {

    protected static Logger_Interface LOG = Logger.getLoggerInstance(SimulationImageProducer.class.getSimpleName());
    private SimulationMap map;
    private Consumer<BufferedImage> imageConsumer;
    private final BufferedImage image;
    private final Graphics2D graphics;

    /**
     * Constructor
     *
     * @param map           map to draw
     * @param imageConsumer consumer which is actually going to consume the image to use.
     */
    public SimulationImageProducer(SimulationMap map, Consumer<BufferedImage> imageConsumer) {
        this.map = map;
        this.imageConsumer = imageConsumer;
        image = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_INT_RGB);
        graphics = (Graphics2D) image.getGraphics();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
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
        
        imageConsumer.accept(image);
    }
}