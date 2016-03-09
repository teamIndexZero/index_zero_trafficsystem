package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import kcl.teamIndexZero.traffic.simulator.data.ID;


/**
 * Stand-alone independent Traffic Light
 */
public class TrafficLight {
    private ID id;
    private TrafficLightState currentState;


    /**
     * Constructor
     *
     * @param id Link ID tag
     */
    public TrafficLight(ID id) {
        this.id = id;
        this.currentState = TrafficLightState.GREEN;
    }

    /**
     * Gets the current Traffic Light state
     */
    public TrafficLightState getState() {
        return this.currentState;
    }
}
