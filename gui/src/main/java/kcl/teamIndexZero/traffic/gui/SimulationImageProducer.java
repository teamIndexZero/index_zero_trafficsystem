package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.GeoSegment;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;

/**
 * An implementation of {@link ISimulationAware} which outputs a buffered image of the map size in respose to every
 * tick. This image can then be either packed into the video stream or directly shown on the window frame to show
 * live representation.
 */
public class SimulationImageProducer implements ISimulationAware {

    protected static Logger_Interface LOG = Logger.getLoggerInstance(SimulationImageProducer.class.getSimpleName());
    private SimulationMap map;
    private BiConsumer<BufferedImage, SimulationTick> imageConsumer;

    private int pixelWidth;
    private int pixelHeight;

    private BufferedImage image;
    private Graphics2D graphics;

    /**
     * Constructor
     *
     * @param map           map to draw
     * @param imageConsumer consumer which is actually going to consume the image to use.
     */
    public SimulationImageProducer(SimulationMap map, BiConsumer<BufferedImage, SimulationTick> imageConsumer) {
        this.map = map;
        this.imageConsumer = imageConsumer;
    }

    public int convertLatToY(double lat) {
        double scaleY = pixelHeight / (map.latEnd - map.latStart);
        return (int) Math.round(pixelHeight - (lat - map.latStart) * scaleY);
    }

    public int convertLonToX(double lon) {
        double scaleX = pixelWidth / (map.lonEnd - map.lonStart);
        return (int) Math.round((lon - map.lonStart) * scaleX);
    }

    public void setPixelSize(int width, int height) {
        this.pixelWidth = width;
        this.pixelHeight = height;
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);
        graphics = (Graphics2D) image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, image.getWidth(), image.getHeight());

        map.getMapFeatures().values().forEach(feature -> {
            if (feature instanceof Road) {
                graphics.setColor(Color.BLACK);
                GeoSegment segment = ((Road) feature).getSegment();
                graphics.setStroke(new BasicStroke(segment.pixelWidthTemporaryVariable));
                graphics.drawLine(
                        convertLonToX(segment.start.longitude),
                        convertLatToY(segment.start.latitude),
                        convertLonToX(segment.end.longitude),
                        convertLatToY(segment.end.latitude)
                );

            }
        });

//        map.getObjectsOnMap().forEach(object -> {
//            graphics.setColor(object.getColor());
//            MapPosition pos = object.getPosition();
//            graphics.fillRect(
//                    pos.x,
//                    pos.y,
//                    pos.pixelWidth,
//                    pos.pixelHeight
//            );
//        });

        imageConsumer.accept(image, tick);
    }
}
