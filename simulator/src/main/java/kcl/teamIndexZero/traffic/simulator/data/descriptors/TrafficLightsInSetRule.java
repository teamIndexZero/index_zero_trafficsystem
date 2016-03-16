package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightInSet;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightState;

import java.util.List;

/**
 * Traffic Light in synchronous set rule
 */
public class TrafficLightsInSetRule {

    /**
     * Changes colours at trafficLights in one set
     *
     * @param trafficLightList List of the traffic light in one set
     */
    public static void changeStateofSet(List<TrafficLightInSet> trafficLightList){

        trafficLightList.forEach(TrafficLight -> {
            TrafficLight.currentState = (TrafficLight.currentState == TrafficLightState.RED) ? TrafficLightState.GREEN : TrafficLightState.RED;
        });
    }
}
