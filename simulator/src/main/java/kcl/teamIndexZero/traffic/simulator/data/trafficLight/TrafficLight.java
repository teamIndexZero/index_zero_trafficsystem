package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;


/**
 * Created by Es on 02/03/2016.
 */
public class TrafficLight {

    private ID id;

    public enum State {
        RED, GREEN
    }

    public State currentState;

    /**
     * Constructor
     *
     * @param id TrafficLight ID tag
     */
    public TrafficLight(ID id) {

        this.id=id;
        this.currentState = TrafficLight.State.GREEN;

    }

    /**
     *  getState - method returns the current state of the TrafficLights
     *
     */
    public State getState(){
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
