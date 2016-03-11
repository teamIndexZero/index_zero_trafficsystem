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
    private static final int DELAY_INITIAL = 50;
    private final ViewportModel viewport;
    private final SimulationMap map;
    private int delayBetweenTicks = DELAY_INITIAL;
    private boolean showSegmentEnds;
    private MapObject selectedMapObject;
    private SimulationTick tick;
    private SimulationStatus status;
    private SimulationParams params;
    private List<ChangeListener> listeners = new ArrayList<>();
    private boolean showJunctions;


    /**
     * Default constructor.
     *
     * @param map simulation map factory; can provide/create a map on demand.
     */
    public GuiModel(SimulationMap map) {
        this.map = map;
        this.viewport = new ViewportModel(this);
        reset();

    }

    /**
     * Get a Viewport related to current simulation. Viewport is a part of the model basically related to the coordinates,
     * their conversion, window size, proportions, zoom in/out etc.
     * <p>
     * It could have been placed here, but due to the extensive amount of specific logic looks like it deserves a
     * special class.
     *
     * @return current viewport model.
     */
    public ViewportModel getViewport() {
        return viewport;
    }

    @Override
    public void tick(SimulationTick tick) {
        this.tick = tick;
        SwingUtilities.invokeLater(this::fireChangeEvent);
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

    /**
     * Whether to display little crossings at segment ends - green at start and red at end
     *
     * @return current setting.
     */
    public boolean isShowSegmentEnds() {
        return this.showSegmentEnds;
    }

    /**
     * Setter for showSegmentEnds
     *
     * @param showSegmentEnds set
     */
    public void setShowSegmentEnds(boolean showSegmentEnds) {
        this.showSegmentEnds = showSegmentEnds;
        fireChangeEvent();
    }

    /**
     * Returns an object which is currently selected (either on map, or in the list). On map this object should be drawn
     * with the special symbol to designate it as selected.
     *
     * @return selected object
     */
    public MapObject getSelectedMapObject() {
        if (map.getObjectsOnSurface().contains(selectedMapObject)) {
            return selectedMapObject;
        }
        return null;
    }

    public void setSelectedMapObject(MapObject selectedMapObject) {
        this.selectedMapObject = selectedMapObject;
        fireChangeEvent();
    }

    public int getDelayBetweenTicks() {
        return delayBetweenTicks;
    }

    public void setDelayBetweenTicks(int delayBetweenTicks) {
        this.delayBetweenTicks = delayBetweenTicks;
    }

    public SimulationMap getMap() {
        return map;
    }

    public SimulationTick getTick() {
        return tick;
    }

    public SimulationStatus getStatus() {
        return status;
    }

    public void setStatus(SimulationStatus status) {
        this.status = status;
        fireChangeEvent();
    }

    public void setParams(SimulationParams params) {
        this.params = params;
        fireChangeEvent();
    }

    /**
     * Listeners will get notified as soon as there is a change in model to report to outside world.
     *
     * @param listener an instance of listeren to add.
     */
    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    /*
    Notify all listeners: something has changed.
     */
    void fireChangeEvent() {
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
        if (delayBetweenTicks != guiModel.delayBetweenTicks) return false;
        if (selectedMapObject != null ? !selectedMapObject.equals(guiModel.selectedMapObject) : guiModel.selectedMapObject != null)
            return false;
        if (tick != null ? !tick.equals(guiModel.tick) : guiModel.tick != null) return false;
        if (status != guiModel.status) return false;
        return params != null ? params.equals(guiModel.params) : guiModel.params == null;

    }

    @Override
    public int hashCode() {
        int result = (showSegmentEnds ? 1 : 0);
        result = 31 * result + (selectedMapObject != null ? selectedMapObject.hashCode() : 0);
        result = 31 * result + delayBetweenTicks;
        result = 31 * result + (tick != null ? tick.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        return result;
    }

    public boolean isShowJunctions() {
        return showJunctions;
    }

    public void setShowJunctions(boolean showJunctions) {
        this.showJunctions = showJunctions;
        fireChangeEvent();
    }

    /**
     * Status of the simulation process.
     */
    public enum SimulationStatus {
        OFF,
        PAUSED,
        INPROGRESS
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
}
