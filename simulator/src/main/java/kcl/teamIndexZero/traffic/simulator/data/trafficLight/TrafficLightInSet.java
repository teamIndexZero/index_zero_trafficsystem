package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightState;

/**
 * Traffic Light part of a synchronous set
 */
public class TrafficLightInSet {

    private ID id;
    public long TrafficLightInSetDelay;
    public TrafficLightState currentState;

    /**
     * Constructor
     *
     * @param id TrafficLightInSet ID tag
     */
    public TrafficLightInSet(ID id) {

        this.id=id;
        this.currentState = TrafficLightState.GREEN;

    }

    /**
     *  Returns the current state of the TrafficLightInSet
     */
    public TrafficLightState getState(){
        return this.currentState;
    }

    /**
     *  Returns the current state of the TrafficLightInSet
     */
    public ID getID() { return this.id;}

    //@Override
    public void tick(SimulationTick tick) {

    }
}
