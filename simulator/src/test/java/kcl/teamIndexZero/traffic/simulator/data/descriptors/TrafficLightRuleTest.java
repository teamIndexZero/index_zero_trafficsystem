package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;
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
    private TrafficLight trafficLight;
    private TrafficLightController controller;
    private boolean flag = false;


    @Before
    public void setUp() throws Exception {
        model = new TrafficLightRule();
        controller= new TrafficLightController();
        trafficLight = new TrafficLight(new ID("TrafficLightTest"));
    }

    @After
    public void tearDown() throws Exception {
        model = null;
        controller = null;
        trafficLight = null;
    }

    @Test
    public void testChangeStateofSingleTrafficLights() throws Exception {

        controller.addTrafficlight(trafficLight);
        model.changeStateofSingleTrafficLights(controller.TrafficLightSinglesList);

        for(TrafficLight tf : controller.TrafficLightSinglesList ) {
            if(tf.currentState == TrafficLightState.RED){
                flag = true;
            };
        }
        assertEquals(flag, true);

    }
}