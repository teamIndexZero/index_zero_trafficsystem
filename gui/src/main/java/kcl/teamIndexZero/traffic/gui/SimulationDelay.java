package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;


/**
 * Extremely simple class which delays simulation for a given period of time on each tick.
 */
public class SimulationDelay implements ISimulationAware {

    private int sleepTimeMillis;

    /**
     * Constructor.
     *
     * @param sleepTimeMillis how long should we sleep each tick.
     */
    public SimulationDelay(int sleepTimeMillis) {
        this.sleepTimeMillis = sleepTimeMillis;
    }


    @Override
    public void tick(SimulationTick tick) {
        try {
            Thread.sleep(sleepTimeMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
