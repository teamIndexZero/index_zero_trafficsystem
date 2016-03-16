package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;


/**
 * Created by Es on 02/03/2016.
 */
public class TrafficLight {
    private ID id;
    public TrafficLightState currentState;

    /**
     * Constructor
     *
     * @param id TrafficLight ID tag
     */
    public TrafficLight(ID id) {

        this.id=id;
        this.currentState = TrafficLightState.GREEN;

    }

    /**
     *  getState - method returns the current state of the TrafficLights
     *
     */
    public TrafficLightState getState(){
        return this.currentState;
    }


    /**
     * getTrafficLightID - method returns the ID of the  current object
     *
     */
    public ID getTrafficLightID() { return this.id;}


    //@Override
    public void tick(SimulationTick tick) {

    }
}
