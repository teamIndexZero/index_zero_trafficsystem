package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.TrafficLightRule;
import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLightSet;

import java.util.Date;

/**
 * Created by Es on 02/03/2016.
 */
public abstract class TrafficLightInSet implements ISimulationAware {
    private TrafficLight model;
    private TrafficLightSet modelSet;
    long CurrentDate = (new Date().getTime())/1000;
    long lastChange = CurrentDate;
    long timer;



    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
        CurrentDate = (new Date().getTime())/1000;

        if (CurrentDate - lastChange> timer) {
            modelSet.changeColour(model, model.currentState);
            lastChange = CurrentDate;
        }
    }
}
