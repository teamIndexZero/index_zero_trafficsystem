package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.TrafficLightRule;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLightSet;
import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLightInSet;



/**
 * Created by Es on 02/03/2016.
 */
public class TrafficLight extends Link {


    public enum State {
        RED, GREEN
    }

    public State currentState;


    /**
     * Constructor
     *
     * @param id Link ID tag
     */
    public TrafficLight(ID id, GeoPoint point) {
       super(id, point);
       this.currentState = TrafficLight.State.RED;

    }

    /**
     * showState - method returns the current state of the Lights
     *
     * @param id Link ID tag
     */
    public State showState(ID id){
        return this.currentState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {

    }
}
