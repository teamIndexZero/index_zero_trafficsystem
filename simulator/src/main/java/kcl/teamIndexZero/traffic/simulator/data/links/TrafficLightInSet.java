package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.Simulator;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

import java.util.Date;

/**
 * Created by Es on 02/03/2016.
 */
public /*abstract*/ class TrafficLightInSet extends TrafficLight implements ISimulationAware {
    long CurrentTime;
    long lastChange;
    long timer;
    private TrafficLight model;
    private TrafficLightSet modelSet;

    public TrafficLightInSet(ID linkId) {
        super(linkId);
        //TODO Fix construction?
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
       // CurrentTime =

        if (CurrentTime - lastChange > timer) {
            if (modelSet != null) {
                modelSet.changeColour(model, model.currentState);
                lastChange = CurrentTime;
            }
        }
    }
}