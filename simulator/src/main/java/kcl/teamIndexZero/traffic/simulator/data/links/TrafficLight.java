package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.TrafficLightRule;
import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLightSet;
import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLightInSet;



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
     * @param id Link ID tag
     */
    public TrafficLight(ID id) {

       this.id=id;
       this.currentState = TrafficLight.State.RED;

    }

    /**
     *  getState - method returns the current state of the TrafficLights
     *
     * @param id Link ID tag
     */
    public State getState(ID id){
        return this.currentState;
    }


    /**
     * getTrafficLightID - method returns the ID of the  current object
     *
     * @param trafficLight an object of a TrafficLight class
     */
    public ID getTrafficLightID(TrafficLight trafficLight) { return trafficLight.id;}

    /**
     * {@inheritDoc}
     */
    //@Override
    public void tick(SimulationTick tick) {

    }
}
