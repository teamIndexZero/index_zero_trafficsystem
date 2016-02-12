package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;


public class SimulationDelay implements ISimulationAware {

    private int sleepTimeMillis;

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
