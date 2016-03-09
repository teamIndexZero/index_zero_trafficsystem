package kcl.teamIndexZero.traffic.gui.mvc;

import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;

/**
 * Model of the viewport, containing all the calculations on zoom, coordinate conversion, pixel to meters etc.
 * <p>
 * By default, we are taking the minimum of (x/metersX) and (y/metersY) - it allows us to fit the whole map on screen
 * initially while maintaining proportions. Next, zoom/offset can be applied to alter the viewport position, however
 * it can always be reset by 'resetZoom' method.
 */
public class ViewportModel {

    public static final int ZOOM_COEFFICIENT_INITIAL = 1;
    public static final double ZOOM_CHANGE_COEFFICIENT = 1.05;
    private int mapPanelWidthPixels;
    private int mapPanelHeightPixels;
    private double zoomCoefficient = ZOOM_COEFFICIENT_INITIAL;
    private double yMetersOffset = 0;
    private double xMetersOffset = 0;
    private GuiModel mainModel;

    /**
     * Construct an instsance of viewport with default parameters, linking to the original GuiModel.
     *
     * @param mainModel gui model to link to.
     */
    public ViewportModel(GuiModel mainModel) {
        this.mainModel = mainModel;
    }


    /**
     * Gets scale of 1 meter to pixels, taking into account current viewport zoom and map width/height.
     *
     * @return how many pixels are in one meter.
     */
    public double getPixelsInMeter() {
        double scaleX = mapPanelWidthPixels / mainModel.getMap().widthMeters;
        double scaleY = mapPanelHeightPixels / mainModel.getMap().heightMeters;
        return Math.min(scaleX, scaleY) * getUserZoomCoefficient();
    }

    /**
     * Adds an offset in meters (to the map). Can be thought as a 'move' operation - if you add -100, -100, map 'moves'
     * 100 meters west and 100 meters south.
     *
     * @param xMeters offset by X
     * @param yMeters offset by Y
     */
    public void addMetersOffset(double xMeters, double yMeters) {
        xMetersOffset += xMeters;
        yMetersOffset += yMeters;
        mainModel.fireChangeEvent();
    }

    /**
     * Resets zoom and anchor point to original value.
     */
    public void resetZoom() {
        xMetersOffset = 0;
        yMetersOffset = 0;
        zoomCoefficient = ZOOM_COEFFICIENT_INITIAL;
        mainModel.fireChangeEvent();
    }

    /**
     * Convert meters by Y into screen pixels (remember, screen coordinate height is top-to-bottom, while normal one
     * bottom to top.
     *
     * @param yMeters meters offset from zero point by Y axis
     * @return pixel position in screen coordinates (that is, from top left corner) ready to draw.
     */
    public int convertYMetersToPixels(double yMeters) {
        return (int) Math.round(mapPanelHeightPixels - (yMeters + yMetersOffset) * getPixelsInMeter());
    }

    /**
     * Converts meters by X axis into screen pixels.
     *
     * @param xMeters offset from map 0,0 point.
     * @return place on screen (x coord) corresponding to this position, with scale and offset in mind.
     */
    public int convertXMetersToPixels(double xMeters) {
        return (int) Math.round((xMeters + xMetersOffset) * getPixelsInMeter());
    }

    /**
     * Get simulation coordinates of some position on screen. For instance, you want to translate mouse click location to
     * the simulation coordinates (meters by x).
     *
     * @param xPixels on screen X coordinate.
     * @return meters offset from zero by X axis in the actual simulation map coordinate system
     */
    public double convertXPixelsToMeters(int xPixels) {
        return xPixels / getPixelsInMeter() - xMetersOffset;
    }

    /**
     * Get simulation coordinates of an on-screen position.
     *
     * @param yPixels - pixels in on-screen coordinates (as taken from java methods, no conversion needed).
     * @return meters offset from zero by Y axis in simulation map coordinate system.
     */
    public double convertYPixelsToMeters(int yPixels) {
        return (mapPanelHeightPixels - yPixels) / getPixelsInMeter() - yMetersOffset;
    }

    /**
     * Sets map panel draw size. Corresponds to containing panel size.
     *
     * @param width  horizontal
     * @param height vertical
     */
    public void setMapPanelSize(int width, int height) {
        this.mapPanelWidthPixels = width;
        this.mapPanelHeightPixels = height;
        mainModel.fireChangeEvent();
    }

    /**
     * Get width
     *
     * @return drawing panel width.
     */
    public int getMapPanelWidthPixels() {
        return mapPanelWidthPixels;
    }

    /**
     * Get height
     *
     * @return drawing panel height.
     */
    public int getMapPanelHeightPixels() {
        return mapPanelHeightPixels;
    }

    /**
     * User zoom coefficient - that is, how deep did we zoom in.
     *
     * @return current zoom coefficient.
     */
    public double getUserZoomCoefficient() {
        return zoomCoefficient;
    }

    /**
     * Zoom in (make things closer) around some center point.
     *
     * @param centerPoint zoom anchor point - this point will stay at same logical and physical position after the zoom.
     */
    public void zoomIn(GeoPoint centerPoint) {
        zoomCoefficient *= ZOOM_CHANGE_COEFFICIENT;
        xMetersOffset = (xMetersOffset + (1 - ZOOM_CHANGE_COEFFICIENT) * centerPoint.xMeters) / ZOOM_CHANGE_COEFFICIENT;
        yMetersOffset = (yMetersOffset + (1 - ZOOM_CHANGE_COEFFICIENT) * centerPoint.yMeters) / ZOOM_CHANGE_COEFFICIENT;
        mainModel.fireChangeEvent();
    }

    /**
     * Zoom out (make things more distant) around some center point.
     *
     * @param centerPoint zoom anchor point - this point will stay at same logical and physical position after the zoom.
     */
    public void zoomOut(GeoPoint centerPoint) {
        zoomCoefficient /= ZOOM_CHANGE_COEFFICIENT;
        xMetersOffset = ZOOM_CHANGE_COEFFICIENT * xMetersOffset + (ZOOM_CHANGE_COEFFICIENT - 1) * centerPoint.xMeters;
        yMetersOffset = ZOOM_CHANGE_COEFFICIENT * yMetersOffset + (ZOOM_CHANGE_COEFFICIENT - 1) * centerPoint.yMeters;
        mainModel.fireChangeEvent();
    }

}
