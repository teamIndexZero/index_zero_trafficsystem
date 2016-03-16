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
     *  Returns the current state of the TrafficLight object
     */
    public TrafficLightState getState(){
        return this.currentState;
    }


    /**
     * Returns the ID of the current TrafficLight object
     */
    public ID getTrafficLightID() { return this.id;}


    //@Override
    public void tick(SimulationTick tick) {

    }
}
