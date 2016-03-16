package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLight;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightState;

/**
 * Stand-alone Traffic Light rule description
 */
public class TrafficLightRule {
    public TrafficLightState changeColour(TrafficLight trafficLight, TrafficLightState currentState) {
        return (currentState == TrafficLightState.RED) ? TrafficLightState.GREEN : TrafficLightState.RED;
    }

}