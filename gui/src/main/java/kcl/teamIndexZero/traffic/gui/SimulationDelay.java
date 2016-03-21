package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;


/**
 * Extremely simple class which delays simulation for a given period of time on each tick.
 */
public class SimulationDelay implements ISimulationAware {

    private GuiModel model;

    /**
     * Constructor.
     */
    public SimulationDelay(GuiModel model) {
        this.model = model;
    }


    @Override
    public void tick(SimulationTick tick) {
        try {
            Thread.sleep(model.getDelayBetweenTicks());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
