package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.data.features.*;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoSegment;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle;

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
     * @param map   map to draw
     * @param model gui model
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
            model.setImage(image);
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
        drawAllJunctions(graphics);
        drawAllTrafficGenerators(graphics);
        drawFeatureSelection(graphics);

        // draw all cars.
        map.getObjectsOnSurface().forEach(mapObject -> {
            Vehicle v = (Vehicle) mapObject;
            GeoPoint point = v.getPositionOnMap();
            if (point == null) {
                return;
            }

            if (model.getViewport().getPixelsInMeter() < 1) {
                primitives.drawCircle(graphics, point, 2, v.getColor(), true);
            } else {
                double bearing = v.getBearing();
                primitives.drawSegment(graphics,
                        new GeoSegment(
                                new GeoPoint(
                                        v.getPositionOnMap().xMeters - Math.cos(bearing) * v.getLengthMeters(),
                                        v.getPositionOnMap().yMeters - Math.sin(bearing) * v.getLengthMeters()
                                ),
                                v.getPositionOnMap()
                        ),
                        v.getColor(),
                        getStrokeByWidthMeters(v.getWidthMeters())
                );
                primitives.drawCircle(graphics, point, (int) (Math.floor(v.getWidthMeters() * model.getViewport().getPixelsInMeter())), Color.YELLOW, true);
            }

            if (v.equals(model.getSelectedMapObject())) {
                int radius = (int) (model.getViewport().getPixelsInMeter()
                        * ((Vehicle) model.getSelectedMapObject()).getDistanceToKeepToNextObject());
                primitives.drawCircle(graphics,
                        point,
                        radius,
                        v.getColor());
                primitives.drawText(graphics, point, v.getNameAndRoad(), v.getColor());
                primitives.drawAngleVector(graphics, point, v.getBearing(), radius, 1, v.getColor(), true, false);
            }
        });
    }

    private void drawFeatureSelection(Graphics2D graphics) {
        if (model.getSelectedFeature() == null) {
            return;
        }
        Feature f = model.getSelectedFeature();
        if (f instanceof Junction) {
            Junction j = (Junction) f;
            primitives.drawCircle(graphics, j.getGeoPoint(), 8, Color.WHITE, true);
            primitives.drawCircle(graphics, j.getGeoPoint(), 9, Color.BLACK);
            primitives.drawCircle(graphics, j.getGeoPoint(), 2, Color.BLACK, true);

            j.getConnectedFeatures().stream().filter(feature -> feature instanceof Road).forEach(feature -> {
                Road r = (Road) feature;
                for (Lane lane : r.getForwardSide().getLanes()) {
                    drawJunctionConnectionVector(graphics, j, lane);

                }
                for (Lane lane : r.getBackwardSide().getLanes()) {
                    drawJunctionConnectionVector(graphics, j, lane);
                }
            });
            return;
        }

        if (f instanceof TrafficGenerator) {
            TrafficGenerator j = (TrafficGenerator) f;
            primitives.drawCircle(graphics, j.getGeoPoint(), 30, Color.BLUE);
            return;
        }

        if (f instanceof Road) {
            Road r = (Road) f;
            double minx = Double.MAX_VALUE, miny = Double.MAX_VALUE, maxx = Double.MIN_VALUE, maxy = Double.MIN_VALUE;
            for (GeoSegment segment : r.getPolyline().getSegments()) {
                minx = Math.min(segment.start.xMeters, Math.min(minx, segment.end.xMeters));
                miny = Math.min(segment.start.yMeters, Math.min(miny, segment.end.yMeters));
                maxx = Math.max(segment.start.xMeters, Math.max(maxx, segment.end.xMeters));
                maxy = Math.max(segment.start.yMeters, Math.max(maxy, segment.end.yMeters));
            }
            GeoPoint center = new GeoPoint(
                    minx + (maxx - minx) / 2,
                    miny + (maxy - miny) / 2);
            double radiusMeters = Math.max(maxx - minx, maxy - miny) / 2;
            primitives.drawCircle(graphics, center, 15 + (int) (model.getViewport().getPixelsInMeter() * radiusMeters), Color.GREEN);
        }
    }

    private void drawJunctionConnectionVector(Graphics2D graphics, Junction j, Lane lane) {
        Road r = lane.getRoad();

        boolean isLaneOutgoing =
                (j.getDirectionForFeature(r) == JunctionDescription.RoadDirection.INCOMING && lane.isBackwardLane())
                        || (j.getDirectionForFeature(r) == JunctionDescription.RoadDirection.OUTGOING && lane.isForwardLane());

        primitives.drawAngleVector(
                graphics,
                j.getGeoPoint(),
                j.getBearingForLane(lane),
                140,
                2,
                isLaneOutgoing ? Color.RED : Color.BLUE,
                isLaneOutgoing,
                true);
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
                                getStrokeByWidthMeters(lane.getWidth())
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
                                getStrokeByWidthMeters(lane.getWidth())
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
     * @param strokeWidthMeters road width in meters.
     * @return cached, or newly created stroke.
     */
    private Stroke getStrokeByWidthMeters(double strokeWidthMeters) {
        int width = (int) Math.ceil(model.getViewport().getPixelsInMeter() * strokeWidthMeters);
        return getStrokeByWidthPixels(width);
    }

    private Stroke getStrokeByWidthPixels(int width) {
        if (!strokeCache.containsKey(width)) {
            strokeCache.put(width,
                    new BasicStroke(
                            width,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    ));
        }
        return strokeCache.get(width);
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
            graphics.drawChars(text.toCharArray(), 0, text.length(), x + 18, y + 18);
        }

        /**
         * Draws a vector coming out (or going into) the point in meters. Vector is specified by radius and bearing to
         * 1, 0 base vector. Please take special care of units - some in meters, some in pixels here.
         *
         * @param graphics     graphics to draw on
         * @param point        pivot point (start or end depending on isOutgoing) of the vector
         * @param bearing      angle to 1,0 base vector (t east)
         * @param radiusPixels radius in pixels
         * @param strokeWidth  width of the stroke
         * @param isOutgoing   whether the vector is going out, or going into the point
         */
        public void drawAngleVector(Graphics2D graphics,
                                    GeoPoint point,
                                    double bearing,
                                    int radiusPixels,
                                    int strokeWidth,
                                    Color color,
                                    boolean isOutgoing,
                                    boolean drawAngleText) {
            int startX = model.getViewport().convertXMetersToPixels(point.xMeters);
            int startY = model.getViewport().convertYMetersToPixels(point.yMeters);

            String angleDetails = String.format("%.1f\u0176", Math.toDegrees(bearing));
            graphics.setStroke(getStrokeByWidthPixels(strokeWidth));
            graphics.setColor(color);

            if (isOutgoing) {
                angleDetails = "OUT: " + angleDetails;
                int xEnd = (int) (startX + Math.cos(bearing) * radiusPixels);
                int yEnd = (int) (startY - Math.sin(bearing) * radiusPixels);
                graphics.drawLine(startX,
                        startY,
                        xEnd,
                        yEnd);
                if (drawAngleText) {
                    graphics.drawChars(
                            angleDetails.toCharArray(),
                            0,
                            angleDetails.length(),
                            xEnd - 7,
                            yEnd - 7

                    );
                }
            } else {
                int xEnd = (int) (startX - Math.cos(bearing) * radiusPixels);
                int yEnd = (int) (startY + Math.sin(bearing) * radiusPixels);
                angleDetails = "IN: " + angleDetails;
                graphics.drawLine(
                        xEnd,
                        yEnd,
                        startX,
                        startY);
                if (drawAngleText) {
                    graphics.drawChars(
                            angleDetails.toCharArray(),
                            0,
                            angleDetails.length(),
                            xEnd + 7,
                            yEnd + 7
                    );
                }
            }
        }
    }
}
