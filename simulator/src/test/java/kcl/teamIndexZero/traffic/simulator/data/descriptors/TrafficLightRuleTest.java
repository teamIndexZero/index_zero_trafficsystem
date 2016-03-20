package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLight;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightController;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by JK on 20-03-16.
 */
public class TrafficLightRuleTest {

    private TrafficLightRule model;
    public List<TrafficLight> trafficLightSinglesList;
    boolean flag = false;

    @Before
    public void setUp() throws Exception {
        model = new TrafficLightRule();
        trafficLightSinglesList = new ArrayList<TrafficLight>();
    }


    @After
    public void tearDown() throws Exception {
        model = null;
    }

    @Test
    public void testChangeStateofSingleTrafficLights() throws Exception {

        model.changeStateofSingleTrafficLights(trafficLightSinglesList);

        for(TrafficLight tf : trafficLightSinglesList ) {
            if(tf.currentState == TrafficLightState.GREEN){
                flag = true;
            };
        }
        assertEquals(flag, false);

    }
}