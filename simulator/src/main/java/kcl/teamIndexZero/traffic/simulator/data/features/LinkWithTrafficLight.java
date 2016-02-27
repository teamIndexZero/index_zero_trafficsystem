package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.simulator.data.trafficlights.TrafficLight;

/**
 * Created by lexaux on 17/02/2016.
 * Altered by JK on 27/02/2016.
 */
public class LinkWithTrafficLight extends Link {
    TrafficLight light= new TrafficLight( TrafficLight.State.GREEN, 4, 500 );
}
