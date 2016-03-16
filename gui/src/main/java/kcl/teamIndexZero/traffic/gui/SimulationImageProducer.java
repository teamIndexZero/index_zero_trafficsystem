package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.features.Feature;
import kcl.teamIndexZero.traffic.simulator.data.features.Junction;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;
import kcl.teamIndexZero.traffic.simulator.data.features.TrafficGenerator;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoSegment;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * An implementation of {@link ISimulationAware} which outputs a buffered image of the map size in respose to every
 * tick. This image can then be either packed into the video stream or directly shown on the window frame to show
 * live representation.
 */
public class SimulationImageProducer {

    protected static Logger_Interface LOG = Logger.getLoggerInstance(SimulationImageProducer.class.getSimpleName());
    private final SimulationMap map;
    private final GuiModel model;
    private final Primitives primitives;
    private Consumer<BufferedImage> imageConsumer;


    private BufferedImage image = null;
    private Graphics2D graphics;

    //helper variable used to cycle colors when debugging road - helpful to spot issues in distribution.
    private int debugRoadsColorCounter;

    /**
     * Constructor
     *
     * @param map map to draw
     */
    public SimulationImageProducer(SimulationMap map, GuiModel model) {
        this.map = map;
        this.model = model;
        this.primitives = new Primitives();

        model.addChangeListener(() -> {
            setPixelSize(model.getViewport().getMapPanelWidthPixels(), model.getViewport().getMapPanelHeightPixels());
            redraw();
        });
    }

    public void setImageConsumer(Consumer<BufferedImage> imageConsumer) {
        this.imageConsumer = imageConsumer;
    }

    private void setPixelSize(int width, int height) {
        if (width == 0 && height == 0) {
            return;
        }

        if (image != null && width == image.getWidth() && height == image.getHeight()) {
            return;
        }

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        graphics = (Graphics2D) image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public void redraw() {
        if (image == null) {
            return;
        }

        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, image.getWidth(), image.getHeight());

        drawAllRoads();
        drawAllJunctions();
        drawAllTrafficGenerators();
        drawAllMapObjects();

        if (imageConsumer == null) {
            LOG.log_Fatal("Image consumer not present. Can not draw.");
        } else {
            imageConsumer.accept(image);
        }
    }

    private void drawAllTrafficGenerators() {
        if (!model.isShowTrafficGenerators()) {
            return;
        }
        map.getMapFeatures().values().forEach(feature -> {
            if (feature instanceof TrafficGenerator) {
                TrafficGenerator trafficGen = (TrafficGenerator) feature;
                primitives.drawCircle(trafficGen.getGeoPoint(), 7, Color.BLUE);
                primitives.drawText(trafficGen.getGeoPoint(), trafficGen.toString(), Color.BLUE);
            }
        });
    }

    public void drawAllMapObjects() {
        map.getObjectsOnSurface().forEach(object -> {
            GeoPoint point = object.getPositionOnMap();
            if (point == null) {
                return;
            }

            primitives.drawCircle(point, 4, object.getColor(), true);

            if (object.equals(model.getSelectedMapObject())) {
                primitives.drawCircle(point, 15, object.getColor());
                primitives.drawText(point, object.getNameAndRoad(), object.getColor());
            }
        });
    }

    public void drawAllJunctions() {
        if (!model.isShowJunctions()) {
            return;
        }
        map.getMapFeatures().values().forEach(feature -> {
            if (feature instanceof Junction) {
                Junction junction = (Junction) feature;
                primitives.drawCircle(junction.getGeoPoint(), 7, Color.GRAY);
                Color color = junction.getConnectedFeatures().size() < 2 ? Color.RED : Color.black;
                primitives.drawText(
                        junction.getGeoPoint(),
                        String.format(
                                "%dF, %dP",
                                junction.getConnectedFeatures().size(),
                                junction.getUsage()),
                        color);
            }
        });
    }

    public void drawAllRoads() {
        debugRoadsColorCounter = 0;
        map.getMapFeatures().values().forEach(feature -> {
            if (feature instanceof Road) {
                Road road = ((Road) feature);
                debugRoadsColorCounter++;
                road.getPolyline().getSegments().forEach(segment -> {
                    primitives.drawSegment(
                            segment,
                            model.debugRoads() ? Feature.COLORS[debugRoadsColorCounter % Feature.COLORS.length] :
                                    getColorForLayer(road.getLayer()),
                            new BasicStroke(
                                    (int) Math.floor(model.getViewport().getPixelsInMeter() * road.getRoadWidth() * 0.6),
                                    BasicStroke.CAP_ROUND,
                                    BasicStroke.JOIN_ROUND
                            )
                    );
                });

                if (model.debugRoads()) {
                    // for debug purposes, we want to draw road segment start/ends.
                    if (road.getPolyline().getSegments().size() > 0) {
                        GeoPoint startPoint = road.getPolyline().getSegments().get(0).start;
                        GeoPoint endPoint = road.getPolyline().getSegments().get(road.getPolyline().getSegments().size() - 1).end;
                        primitives.drawCross(startPoint, Color.GREEN);
                        primitives.drawCross(endPoint, Color.RED);
                    }
                }
            }
        });
    }

    private Color getColorForLayer(int layer) {
        return Feature.COLORS[Math.abs(layer + 3) % Feature.COLORS.length];
    }


    /**
     * Inner class for primitive drawing.
     * Should expand it with deeper semantics later.
     */
    private class Primitives {
        public final BasicStroke THIN_STROKE = new BasicStroke(1);
        public final BasicStroke CAR_STROKE = new BasicStroke(3);

        /**
         * Segment, draws a geo segment - line between two points.
         *
         * @param segment segment to draw
         * @param color   color to draw with
         */
        public void drawSegment(GeoSegment segment, Color color, Stroke stroke) {
            graphics.setStroke(stroke);
            graphics.setColor(color);
            graphics.drawLine(
                    model.getViewport().convertXMetersToPixels(segment.start.xMeters),
                    model.getViewport().convertYMetersToPixels(segment.start.yMeters),
                    model.getViewport().convertXMetersToPixels(segment.end.xMeters),
                    model.getViewport().convertYMetersToPixels(segment.end.yMeters)
            );
        }

        /**
         * Draw a little cross. Defaults to thin stroke.
         *
         * @param startPoint where
         * @param color      in which color
         */
        public void drawCross(GeoPoint startPoint, Color color) {
            drawCross(startPoint, color, THIN_STROKE);
        }

        /**
         * Draw a little cross with a stroke provided.
         *
         * @param startPoint where
         * @param color      in which color
         * @param stroke     what stroke
         */
        public void drawCross(GeoPoint startPoint, Color color, Stroke stroke) {
            int size = 10;
            graphics.setColor(color);
            graphics.setStroke(stroke);
            int x = model.getViewport().convertXMetersToPixels(startPoint.xMeters);
            int y = model.getViewport().convertYMetersToPixels(startPoint.yMeters);
            graphics.drawLine(x - size / 2, y, x + size / 2, y);
            graphics.drawLine(x, y - size / 2, x, y + size / 2);
        }

        public void drawCircle(GeoPoint startPoint, int pixelRadius, Color color) {
            drawCircle(startPoint, pixelRadius, color, false);
        }

        /**
         * Draw a little circle around a point.
         *
         * @param startPoint  center
         * @param pixelRadius radius
         * @param color       color
         * @param fill        whether to fill the cirle
         */
        public void drawCircle(GeoPoint startPoint, int pixelRadius, Color color, boolean fill) {
            if (startPoint == null) {
                LOG.log_Error("Got null trying to draw geo point. Debug me.");
                return;
            }
            graphics.setStroke(THIN_STROKE);
            graphics.setColor(color);

            int x = model.getViewport().convertXMetersToPixels(startPoint.xMeters);
            int y = model.getViewport().convertYMetersToPixels(startPoint.yMeters);

            if (fill) {
                graphics.fillOval(x - pixelRadius, y - pixelRadius, pixelRadius * 2, pixelRadius * 2);
            } else {
                graphics.drawOval(x - pixelRadius, y - pixelRadius, pixelRadius * 2, pixelRadius * 2);
            }
        }

        /**
         * Draw a text to the bottom-right of a given point
         *
         * @param startPoint anchor point
         * @param text       string to draw
         * @param color      color
         */
        public void drawText(GeoPoint startPoint, String text, Color color) {
            if (startPoint == null) {
                LOG.log_Error("Got null trying to draw geo point. Debug me.");
                return;
            }

            graphics.setStroke(THIN_STROKE);
            graphics.setColor(color);

            int x = model.getViewport().convertXMetersToPixels(startPoint.xMeters);
            int y = model.getViewport().convertYMetersToPixels(startPoint.yMeters);
            graphics.drawChars(text.toCharArray(), 0, text.length(), x + 15, y + 15);
        }

        /**
         * Knowing a map bounds and current coordinate transformation (check out {@link kcl.teamIndexZero.traffic.gui.mvc.ViewportModel}),
         * draw a green rectangle bounding the map area (for ease of identification).
         */
        private void drawMapBounds() {
            graphics.setStroke(THIN_STROKE);
            graphics.setColor(Color.GREEN);
            int xStart = model.getViewport().convertXMetersToPixels(0);
            int yStart = model.getViewport().convertYMetersToPixels(0);
            int xEnd = model.getViewport().convertXMetersToPixels(map.widthMeters);
            int yEnd = model.getViewport().convertYMetersToPixels(map.heightMeters);

            graphics.drawLine(xStart, yStart, xStart, yEnd);
            graphics.drawLine(xStart, yStart, xEnd, yStart);
            graphics.drawLine(xEnd, yEnd, xStart, yEnd);
            graphics.drawLine(xEnd, yEnd, xEnd, yStart);

        }
    }
}
