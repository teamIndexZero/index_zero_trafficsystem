package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * An implementation of {@link ISimulationAware} which outputs a buffered image of the map size in respose to every
 * tick. This image can then be either packed into the video stream or directly shown on the window frame to show
 * live representation.
 */
public class SimulationImageProducer {

    public static final BasicStroke THIN_STROKE = new BasicStroke(1);
    public static final BasicStroke CAR_STROKE = new BasicStroke(3);
    protected static Logger_Interface LOG = Logger.getLoggerInstance(SimulationImageProducer.class.getSimpleName());
    private final SimulationMap map;
    private final GuiModel model;
    private Consumer<BufferedImage> imageConsumer;

    private int pixelWidth;
    private int pixelHeight;

    private BufferedImage image = null;
    private Graphics2D graphics;

    /**
     * Constructor
     *
     * @param map map to draw
     */
    public SimulationImageProducer(SimulationMap map, GuiModel model) {
        this.map = map;
        this.model = model;

        model.addChangeListener(() -> {
            setPixelSize(model.getMapPanelWidthPixels(), model.getMapPanelHeightPixels());
            redraw();
        });
    }

    public void setImageConsumer(Consumer<BufferedImage> imageConsumer) {
        this.imageConsumer = imageConsumer;
    }

    private double getScale() {
        double scaleX = pixelWidth / map.widthMeters;
        double scaleY = pixelHeight / map.heightMeters;
        return Math.min(scaleX, scaleY);
    }

    public int convertYMetersToPixles(double yMeters) {
        return (int) Math.round(pixelHeight - yMeters * getScale());
    }

    public int convertXMetersToPixels(double xMeters) {
        return (int) Math.round(xMeters * getScale());
    }

    private void setPixelSize(int width, int height) {

        if (width == 0 && height == 0) {
            return;
        }

        if (width == this.pixelWidth && height == this.pixelHeight) {
            return;
        }

        this.pixelWidth = width;
        this.pixelHeight = height;
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);
        graphics = (Graphics2D) image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public void redraw() {
        if (image == null) {
            return;
        }

        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, image.getWidth(), image.getHeight());

        map.getMapFeatures().values().forEach(feature -> {
            if (feature instanceof Road) {
                Road road = ((Road) feature);

                graphics.setStroke(THIN_STROKE);
                graphics.setColor(Color.BLACK);

                road.getPolyline().getSegments().forEach(segment -> {
                    graphics.drawLine(
                            convertXMetersToPixels(segment.start.xMeters),
                            convertYMetersToPixles(segment.start.yMeters),
                            convertXMetersToPixels(segment.end.xMeters),
                            convertYMetersToPixles(segment.end.yMeters)
                    );
                });

                if (model.isShowSegmentEnds()) {
                    // for debug purposes, we want to draw road segment start/ends.
                    if (road.getPolyline().getSegments().size() > 0) {
                        GeoPoint startPoint = road.getPolyline().getSegments().get(0).start;
                        GeoPoint endPoint = road.getPolyline().getSegments().get(road.getPolyline().getSegments().size() - 1).end;
                        drawCross(startPoint, Color.GREEN, graphics, null);
                        drawCross(endPoint, Color.RED, graphics, null);
                    }
                }
            }
        });

        map.getObjectsOnSurface().forEach(object -> {
            GeoPoint point = object.getPositionOnMap();
            if (point == null) {
                return;
            }

            drawCross(point, object.getColor(), graphics, CAR_STROKE);

            if (object.equals(model.getSelectedMapObject())) {
                drawCircle(point, 15, object.getColor(), graphics);
                drawText(point, object.getNameAndRoad(), object.getColor(), graphics);
            }
        });

        if (imageConsumer == null) {
            LOG.log_Fatal("Image consumer not present. Can not draw.");
        }
        imageConsumer.accept(image);

    }

    private void drawCross(GeoPoint startPoint, Color color, Graphics2D graphics, Stroke stroke) {
        if (stroke == null) {
            stroke = THIN_STROKE;
        }
        int size = 10;
        graphics.setColor(color);
        graphics.setStroke(stroke);
        int x = convertXMetersToPixels(startPoint.xMeters);
        int y = convertYMetersToPixles(startPoint.yMeters);
        graphics.drawLine(x - size / 2, y, x + size / 2, y);
        graphics.drawLine(x, y - size / 2, x, y + size / 2);
    }

    private void drawCircle(GeoPoint startPoint, int pixelRadius, Color color, Graphics2D graphics) {
        graphics.setStroke(THIN_STROKE);
        graphics.setColor(color);

        int x = convertXMetersToPixels(startPoint.xMeters);
        int y = convertYMetersToPixles(startPoint.yMeters);

        graphics.drawOval(x - pixelRadius, y - pixelRadius, pixelRadius * 2, pixelRadius * 2);
    }

    private void drawText(GeoPoint startPoint, String text, Color color, Graphics2D graphics) {
        graphics.setStroke(THIN_STROKE);
        graphics.setColor(color);

        int x = convertXMetersToPixels(startPoint.xMeters);
        int y = convertYMetersToPixles(startPoint.yMeters);
        graphics.drawChars(text.toCharArray(), 0, text.length(), x + 15, y + 15);
    }
}
