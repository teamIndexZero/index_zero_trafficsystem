package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.features.Junction;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;
import kcl.teamIndexZero.traffic.simulator.data.features.TrafficGenerator;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoSegment;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * An implementation of {@link ISimulationAware} which outputs a buffered image of the map size in respose to every
 * tick. This image can then be either packed into the video stream or directly shown on the window frame to show
 * live representation.
 * <p>
 * This version of ImageProducer has some important optimizations, explicitly:
 * <p>
 * 1. It skips frames in case the last frame was produced recently (frame rate limit)
 * 2. It caches static image of map in another image, and uses it to stamp to resulting image. The cached image is
 * invalidated and re-created when some UI parameters change (scroll, zoom, selected an object, enabled debug, etc.)
 */
public class SimulationImageProducer {

    public static final String THIRTY_METERS_TEXT = "30m";
    public static final int MAX_FRAME_RATE = 25;
    /* Random color selection*/
    public static final Color[] COLORS = {
            new Color(140, 200, 200),
            new Color(140, 200, 140),
            new Color(140, 140, 200),
            new Color(170, 170, 170),
            new Color(200, 140, 140),
            new Color(200, 200, 140),
    };
    private static final Stroke BASIC_STROKE = new BasicStroke(1);
    protected static Logger_Interface LOG = Logger.getLoggerInstance(SimulationImageProducer.class.getSimpleName());
    private final SimulationMap map;
    private final GuiModel model;
    private final Primitives primitives;
    Map<Integer, Stroke> strokeCache = new HashMap<>();
    private Consumer<BufferedImage> imageConsumer;
    private BufferedImage image = null;
    private BufferedImage roadsImage = null;
    private Graphics2D graphics;

    //helper variable used to cycle colors when debugging road - helpful to spot issues in distribution.
    private int debugRoadsColorCounter;

    // performance optimization.
    private int lastViewportHashcode = 0;
    private long lastDrawnTimestamp = 0;

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

    /**
     * Sets whom to send the ready image to
     *
     * @param imageConsumer addressee of the created image.
     */
    public void setImageConsumer(Consumer<BufferedImage> imageConsumer) {
        this.imageConsumer = imageConsumer;
    }

    /**
     * Sets the size of the drawing pane in pixels. Should be invoked from resizeSomething method. We need to know it to
     * create image of appropriate width and height.
     *
     * @param width  pixel width of target area
     * @param height pixel height of target area.
     */
    private void setPixelSize(int width, int height) {
        if (width == 0 && height == 0) {
            return;
        }

        if (image != null && width == image.getWidth() && height == image.getHeight()) {
            return;
        }

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        roadsImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        graphics = (Graphics2D) image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    /**
     * Produce a new image.
     */
    public void redraw() {
        // we don't want to redraw the screen more often that at MAX_FRAME_RATE - framerate optimization.
        if (System.currentTimeMillis() - lastDrawnTimestamp < 1000 / MAX_FRAME_RATE) {
            LOG.log_Warning("Received frame too early, skipping drawing because of FPS limitation.");
        }
        lastDrawnTimestamp = System.currentTimeMillis();
        if (image == null) {
            return;
        }

        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, image.getWidth(), image.getHeight());

        // draw all static objects on map (junctions, roads, traffic gens etc). It will try to cache its work.
        drawAllStaticObjects();
        drawAllDynamicObjects();

        if (imageConsumer == null) {
            LOG.log_Fatal("Image consumer not present. Can not draw.");
        } else {
            imageConsumer.accept(image);
        }
    }

    /**
     * Draws all static objects on screen onto resulting image. Worth noting, this method will try to cache its work by
     * using another image buffer, which saves static objects, and if things didnt' change from static object standpoints
     * (zoom, scale, etc) it will just copy over that cached image to result.
     * <p>
     * However, if things have changed, it will draw that anew.
     */
    private void drawAllStaticObjects() {
        if (lastViewportHashcode != model.getViewHashCode()) {
            lastViewportHashcode = model.getViewHashCode();
            Graphics2D roadGraphics = (Graphics2D) roadsImage.getGraphics();

            roadGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            roadGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            roadGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            roadGraphics.setBackground(Color.WHITE);
            roadGraphics.clearRect(0, 0, image.getWidth(), image.getHeight());

            drawAllRoads(roadGraphics);
            drawAllJunctions(roadGraphics);
            drawAllTrafficGenerators(roadGraphics);
            primitives.drawScaleOf30Meters(roadGraphics);
        }
        graphics.drawImage(roadsImage, 0, 0, null);
    }

    /**
     * Maybe draw all traffic generators. Switch is in model (UI checkbox).
     *
     * @param graphics graphics to draw on
     */
    private void drawAllTrafficGenerators(Graphics2D graphics) {
        if (!model.isShowTrafficGenerators()) {
            return;
        }
        map.getMapFeatures().values().forEach(feature -> {
            if (feature instanceof TrafficGenerator) {
                TrafficGenerator trafficGen = (TrafficGenerator) feature;
                primitives.drawCircle(graphics, trafficGen.getGeoPoint(), 7, Color.BLUE);
                primitives.drawText(graphics, trafficGen.getGeoPoint(), trafficGen.toString(), Color.BLUE);
            }
        });
    }

    /**
     * Draw all dynamic objects - vehicles in this case.
     */
    public void drawAllDynamicObjects() {
        map.getObjectsOnSurface().forEach(mapObject -> {
            GeoPoint point = mapObject.getPositionOnMap();
            if (point == null) {
                return;
            }

            primitives.drawCircle(graphics, point, 4, mapObject.getColor(), true);

            if (mapObject.equals(model.getSelectedMapObject())) {
                primitives.drawCircle(graphics, point, 15, mapObject.getColor());
                primitives.drawText(graphics, point, mapObject.getNameAndRoad(), mapObject.getColor());
            }
        });
    }

    /**
     * Draw all junctions (if enabled in UI as a checkbox).
     *
     * @param graphics graphics to draw on
     */
    public void drawAllJunctions(Graphics2D graphics) {
        if (!model.isShowJunctions()) {
            return;
        }
        map.getMapFeatures().values().forEach(feature -> {
            if (feature instanceof Junction) {
                Junction junction = (Junction) feature;
                primitives.drawCircle(graphics, junction.getGeoPoint(), 7, Color.GRAY);
                Color color = (junction.isDeadEnd() || junction.getConnectedFeatures().size() < 2) ? Color.RED : Color.black;
                primitives.drawText(graphics,
                        junction.getGeoPoint(),
                        String.format(
                                "%dF, %dP" + (junction.isDeadEnd() ? " DEAD END" : ""),
                                junction.getConnectedFeatures().size(),
                                junction.getUsage()),
                        color);
            }
        });
    }

    /**
     * Draw all roads.
     *
     * @param graphics graphics to draw on.
     */
    public void drawAllRoads(Graphics2D graphics) {
        debugRoadsColorCounter = 0;
        map.getMapFeatures().values().forEach(feature -> {
            if (feature instanceof Road) {
                Road road = ((Road) feature);
                road.getForwardSide().getLanes().forEach(lane -> {
                    debugRoadsColorCounter++;
                    lane.getPolyline().getSegments().forEach(segment -> {
                        primitives.drawSegment(
                                graphics,
                                segment,
                                model.debugRoads()
                                        ? COLORS[debugRoadsColorCounter % COLORS.length]
                                        : lane.getColor(),
                                getStrokeByWidth(lane.getWidth())
                        );
                    });
                });
                road.getBackwardSide().getLanes().forEach(lane -> {
                    debugRoadsColorCounter++;
                    lane.getPolyline().getSegments().forEach(segment -> {
                        primitives.drawSegment(
                                graphics,
                                segment,
                                model.debugRoads()
                                        ? COLORS[debugRoadsColorCounter % COLORS.length]
                                        : lane.getColor(),
                                getStrokeByWidth(lane.getWidth())
                        );
                    });
                });


                if (model.debugRoads()) {
                    // for debug purposes, we want to draw road segment start/ends.
                    if (road.getPolyline().getSegments().size() > 0) {
                        GeoPoint startPoint = road.getPolyline().getSegments().get(0).start;
                        GeoPoint endPoint = road.getPolyline().getSegments().get(road.getPolyline().getSegments().size() - 1).end;
                        primitives.drawCross(graphics, startPoint, Color.GREEN);
                        primitives.drawCross(graphics, endPoint, Color.RED);
                    }
                    road.getPolyline().getSegments().forEach(segment -> {
                        primitives.drawSegment(
                                graphics,
                                segment,
                                Color.BLACK,
                                BASIC_STROKE);
                    });

                }
            }
        });
    }

    /**
     * Cache method for strokes.
     *
     * @param roadWidth road width in meters.
     * @return cached, or newly created stroke.
     */
    private Stroke getStrokeByWidth(double roadWidth) {
        int strokeWidth = (int) Math.ceil(model.getViewport().getPixelsInMeter() * roadWidth);
        if (!strokeCache.containsKey(strokeWidth)) {
            strokeCache.put(strokeWidth,
                    new BasicStroke(
                            strokeWidth,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    ));
        }
        return strokeCache.get(strokeWidth);
    }

    /**
     * Inner class for primitive drawing.
     * Should expand it with deeper semantics later.
     */
    private class Primitives {
        public final BasicStroke THIN_STROKE = new BasicStroke(1);

        /**
         * Segment, draws a geo segment - line between two points.
         *
         * @param segment segment to draw
         * @param color   color to draw with
         */
        public void drawSegment(Graphics2D graphics, GeoSegment segment, Color color, Stroke stroke) {
            graphics.setStroke(stroke);
            graphics.setColor(color);
            graphics.drawLine(
                    model.getViewport().convertXMetersToPixels(segment.start.xMeters),
                    model.getViewport().convertYMetersToPixels(segment.start.yMeters),
                    model.getViewport().convertXMetersToPixels(segment.end.xMeters),
                    model.getViewport().convertYMetersToPixels(segment.end.yMeters)
            );
        }

        public void drawScaleOf30Meters(Graphics2D graphics) {
            graphics.setStroke(BASIC_STROKE);
            graphics.setColor(Color.BLACK);
            int endCoord = (int) (10 + 30 * model.getViewport().getPixelsInMeter());
            graphics.drawLine(
                    10, 10,
                    (int) (10 + 30 * model.getViewport().getPixelsInMeter()),
                    10
            );
            graphics.drawLine(10, 8, 10, 12);
            graphics.drawLine(endCoord, 8, endCoord, 12);


            graphics.drawChars(THIRTY_METERS_TEXT.toCharArray(), 0, THIRTY_METERS_TEXT.length(), 10, 30);
        }

        /**
         * Draw a little cross. Defaults to thin stroke.
         *
         * @param startPoint where
         * @param color      in which color
         */
        public void drawCross(Graphics2D graphics, GeoPoint startPoint, Color color) {
            drawCross(graphics, startPoint, color, THIN_STROKE);
        }

        /**
         * Draw a little cross with a stroke provided.
         *
         * @param startPoint where
         * @param color      in which color
         * @param stroke     what stroke
         */
        public void drawCross(Graphics2D graphics, GeoPoint startPoint, Color color, Stroke stroke) {
            int size = 10;
            graphics.setColor(color);
            graphics.setStroke(stroke);
            int x = model.getViewport().convertXMetersToPixels(startPoint.xMeters);
            int y = model.getViewport().convertYMetersToPixels(startPoint.yMeters);
            graphics.drawLine(x - size / 2, y, x + size / 2, y);
            graphics.drawLine(x, y - size / 2, x, y + size / 2);
        }

        public void drawCircle(Graphics2D graphics, GeoPoint startPoint, int pixelRadius, Color color) {
            drawCircle(graphics, startPoint, pixelRadius, color, false);
        }

        /**
         * Draw a little circle around a point.
         *
         * @param startPoint  center
         * @param pixelRadius radius
         * @param color       color
         * @param fill        whether to fill the cirle
         */
        public void drawCircle(Graphics2D graphics, GeoPoint startPoint, int pixelRadius, Color color, boolean fill) {
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
        public void drawText(Graphics2D graphics, GeoPoint startPoint, String text, Color color) {
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
    }
}
