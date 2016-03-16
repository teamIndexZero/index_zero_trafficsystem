package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightState;

/**
 * Traffic Light part of a synchronous set
 */
public class TrafficLightInSet {

    private ID id;

    public TrafficLightState currentState;

    /**
     * Constructor
     *
     * @param id TrafficLightInSet ID tag
     */
    public TrafficLightInSet(ID id) {

        this.id=id;
        this.currentState = currentState.GREEN;

    }

    /**
     *  getState - method returns the current state of the TrafficLightInSet
     *
     */
    public TrafficLightState getState(){
        return this.currentState;
    }

    /**
     *  getID - method returns the current state of the TrafficLightInSet
     *
     */
    public ID getID() { return this.id;}

    //@Override
    public void tick(SimulationTick tick) {

    }
}
