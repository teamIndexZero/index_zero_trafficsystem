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

        final int[] i = {0};

        trafficLightList.forEach(TrafficLight -> {
            if (i[0] % 2 == 0){
                TrafficLight.currentState = (TrafficLight.currentState == TrafficLightState.RED) ? TrafficLightState.GREEN : TrafficLightState.RED;
                i[0]++;
            }
           else
                i[0]++;
        });
    }
}
