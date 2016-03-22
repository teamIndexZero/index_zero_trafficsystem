package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLight;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightState;

import java.util.List;

/**
 * Stand-alone Traffic Light rule description
 */
public class TrafficLightRule {

    /**
     * Changing single lights states
     *
     * @param trafficLightList - list with all single traffic lights
     */
    public static void changeStateofSingleTrafficLights(List<TrafficLight> trafficLightList) {

        trafficLightList.forEach(TrafficLight -> {
            TrafficLight.currentState = (TrafficLight.currentState == TrafficLightState.RED) ? TrafficLightState.GREEN : TrafficLightState.RED;
        });
    }
}