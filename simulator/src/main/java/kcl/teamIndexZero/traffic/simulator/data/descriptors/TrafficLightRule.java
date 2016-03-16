package kcl.teamIndexZero.traffic.simulator.data.descriptors;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLight;

/**
 * Stand-alone Traffic Light rule description
 */
public class TrafficLightRule {

    public static TrafficLight.State changeColour(TrafficLight trafficLight, TrafficLight.State currentState) {
        TrafficLight.State temp;
        temp = (currentState == TrafficLight.State.RED) ? TrafficLight.State.GREEN : TrafficLight.State.RED;
        return temp;
    }

}