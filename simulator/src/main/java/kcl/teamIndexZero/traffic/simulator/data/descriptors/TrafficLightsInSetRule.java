package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLight;
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

        int i = 0;

        for(TrafficLightInSet tf : trafficLightList ) {
            if (i % 2 == 0){
                tf.currentState = (tf.currentState == TrafficLightState.RED) ? TrafficLightState.GREEN : TrafficLightState.RED;
                i++;
            }
            else
                i++;
        }
    }
}
