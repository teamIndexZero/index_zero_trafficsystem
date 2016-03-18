package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

/**
 * Feature is a basic road network graph concept. Feature is a thing which belongs to a map and represent part of the
 * static setting of simulation. It can be something like road, individual lane, traffic generator, traffic receiver,
 * junction etc.
 */
public abstract class Feature implements ISimulationAware {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Feature.class.getSimpleName());
    private ID id;
    private SimulationMap map;

    /**
     * Constructor
     *
     * @param id Feature ID tag
     */
    public Feature(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feature feature = (Feature) o;

        return id != null ? id.equals(feature.id) : feature.id == null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * Gets the Feature's ID tag
     *
     * @return ID tag
     */
    public ID getID() {
        return this.id;
    }

    /**
     * Gets the SimulationMap the feature belongs to
     *
     * @return SimulationMap
     */
    public synchronized SimulationMap getMap() {
        return this.map;
    }

    /**
     * Sets the simulation map the feature belongs to
     *
     * @param map Simulation map
     */
    public void setMap(SimulationMap map) {
        this.map = map;
    }

    /**
     * By default, do nothing
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {

    }
}
