package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

import java.awt.*;

public class Feature implements ISimulationAware {
    /* Random color selection*/
    public static final Color[] COLORS = {
            new Color(0, 0, 0),
            new Color(120, 120, 120),
            new Color(150, 0, 60),
            new Color(0, 60, 150),
            new Color(60, 150, 0),
            new Color(150, 150, 0)
    };
    private static Logger_Interface LOG = Logger.getLoggerInstance(Feature.class.getSimpleName());
    private ID id;

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
}
