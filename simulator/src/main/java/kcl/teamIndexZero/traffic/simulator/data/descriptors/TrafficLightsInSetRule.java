package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLight;

import java.util.List;

/**
 * Traffic Light in synchronous set rule
 */
public class TrafficLightsInSetRule {

    /**
     * Changes colours at trafficLights in one set
     *
     * @param trafficLight1List List of the traffic light in one set
     */
    public static void changeColourSet(List<TrafficLight> trafficLight1List){

        trafficLight1List.forEach(TrafficLight -> {
            TrafficLight.currentState = (TrafficLight.currentState == kcl.teamIndexZero.traffic.simulator.data.links.TrafficLight.State.RED) ?
                    kcl.teamIndexZero.traffic.simulator.data.links.TrafficLight.State.GREEN : kcl.teamIndexZero.traffic.simulator.data.links.TrafficLight.State.RED;
        });

    }
}
