package kcl.teamIndexZero.traffic.gui.mvc;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationParams;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.MapObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI model of our UI application. Basically it is different from simulation model (map, vehicles, etc) since it contains
 * only things relevant on the abstraction level of UI. For example: as soon as we are only drawing cars as dots on the
 * map we don't need to care about their velocity, color or driving strategy (whether they are cautious or not), we only
 * care about their position. More generally, we don't care about map either - we're good to have a new image to draw which
 * is derived from map.
 */
public class GuiModel implements ISimulationAware {

    public static final int DELAY_MINIMAL = 10;
    public static final int DELAY_MAXIMAL = 100;

    private boolean showSegmentEnds;
    private final SimulationMap map;
    private MapObject selectedMapObject;
    private int delayBetweenTicks = 50;

    public boolean isShowSegmentEnds() {
        return this.showSegmentEnds;
    }

    public void setShowSegmentEnds(boolean showSegmentEnds) {
        this.showSegmentEnds = showSegmentEnds;
        fireChangeEvent();
    }

    public MapObject getSelectedMapObject() {
        if (map.getObjectsOnSurface().contains(selectedMapObject)) {
            return selectedMapObject;
        }
        return null;
    }

    @Override
    public void tick(SimulationTick tick) {
        this.tick = tick;
        SwingUtilities.invokeLater(this::fireChangeEvent);
    }

    public int getDelayBetweenTicks() {
        return delayBetweenTicks;
    }

    public void setDelayBetweenTicks(int delayBetweenTicks) {
        this.delayBetweenTicks = delayBetweenTicks;
    }

    /**
     * Model update interface. Other components want to implement it in order to receive updates from model when something
     * important changes.
     */
    @FunctionalInterface
    public interface ChangeListener {
        /**
         * Callback interface, invoked by model when its internals change.
         */
        void onModelChanged();
    }

    /**
     * Status of the simulation process.
     */
    public enum SimulationStatus {
        OFF,
        PAUSED,
        INPROGRESS
    }

    private List<ChangeListener> listeners = new ArrayList<>();

    private SimulationTick tick;
    private SimulationStatus status;
    private SimulationParams params;

    private int mapPanelWidthPixels;
    private int mapPanelHeightPixels;


    public SimulationMap getMap() {
        return map;
    }

    /**
     * Default constructor.
     *
     * @param map
     */
    public GuiModel(SimulationMap map) {
        this.map = map;
        reset();

    }

    /**
     * Reset method gets model to the default state (same as it was right after running constructor.
     */
    public void reset() {
        tick = null;
        status = SimulationStatus.OFF;
        params = null;
        fireChangeEvent();
    }

    public void setMapPanelSize(int width, int height) {
        this.mapPanelWidthPixels = width;
        this.mapPanelHeightPixels = height;
        fireChangeEvent();
    }

    public int getMapPanelWidthPixels() {
        return mapPanelWidthPixels;
    }

    public int getMapPanelHeightPixels() {
        return mapPanelHeightPixels;
    }

    public SimulationTick getTick() {
        return tick;
    }

    public SimulationStatus getStatus() {
        return status;
    }

    public void setSelectedMapObject(MapObject selectedMapObject) {
        this.selectedMapObject = selectedMapObject;
        fireChangeEvent();
    }

    public void setStatus(SimulationStatus status) {
        this.status = status;
        fireChangeEvent();
    }

    public SimulationParams getParams() {
        return params;
    }

    public void setParams(SimulationParams params) {
        this.params = params;
        fireChangeEvent();
    }

    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    private void fireChangeEvent() {
        synchronized (map) {
            listeners.forEach(ChangeListener::onModelChanged);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GuiModel guiModel = (GuiModel) o;

        if (showSegmentEnds != guiModel.showSegmentEnds) return false;
        if (mapPanelWidthPixels != guiModel.mapPanelWidthPixels) return false;
        if (mapPanelHeightPixels != guiModel.mapPanelHeightPixels) return false;
        if (map != null ? !map.equals(guiModel.map) : guiModel.map != null) return false;
        if (selectedMapObject != null ? !selectedMapObject.equals(guiModel.selectedMapObject) : guiModel.selectedMapObject != null)
            return false;
        if (tick != null ? !tick.equals(guiModel.tick) : guiModel.tick != null) return false;
        if (status != guiModel.status) return false;
        return params != null ? params.equals(guiModel.params) : guiModel.params == null;

    }

    @Override
    public int hashCode() {
        int result = (showSegmentEnds ? 1 : 0);
        result = 31 * result + (map != null ? map.hashCode() : 0);
        result = 31 * result + (selectedMapObject != null ? selectedMapObject.hashCode() : 0);
        result = 31 * result + (tick != null ? tick.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + mapPanelWidthPixels;
        result = 31 * result + mapPanelHeightPixels;
        return result;
    }
}
