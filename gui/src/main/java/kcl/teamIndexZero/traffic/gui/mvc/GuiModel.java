package kcl.teamIndexZero.traffic.gui.mvc;

import kcl.teamIndexZero.traffic.simulator.data.SimulationParams;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GuiModel {

    @FunctionalInterface
    public interface ChangeListener {
        void onModelChanged();
    }

    public enum SimulationStatus {
        OFF,
        PAUSED,
        INPROGRESS
    }

    private List<ChangeListener> listeners = new ArrayList<>();

    private SimulationTick tick;
    private SimulationStatus status;
    private SimulationParams params;
    private BufferedImage lastImage;

    public BufferedImage getLastImage() {
        return lastImage;
    }

    public void setLastSimulationTickAndImage(BufferedImage lastImage, SimulationTick tick) {
        this.lastImage = lastImage;
        this.tick = tick;
        fireChangeEvent();
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
        listeners.forEach(ChangeListener::onModelChanged);
    }
}
