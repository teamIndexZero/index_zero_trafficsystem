package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

import java.awt.*;

public class Feature implements ISimulationAware {
    /* Random color selection*/
    public static final Color[] COLORS = {
            new Color(140, 200, 200),
            new Color(140, 200, 140),
            new Color(140, 140, 200),
            new Color(170, 170, 170),
            new Color(200, 140, 140),
            new Color(200, 200, 140),
    };
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

    /**
     * Gets the Feature's ID tag
     *
     * @return ID tag
     */
    public ID getID() {
        return this.id;
    }

    /**
     * By default, do nothing
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {

    }

    public void setMap(SimulationMap map) {
        this.map = map;
    }
}
