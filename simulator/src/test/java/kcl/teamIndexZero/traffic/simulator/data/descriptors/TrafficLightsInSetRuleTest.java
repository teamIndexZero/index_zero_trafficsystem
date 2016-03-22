package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightController;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightInSet;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightSet;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by JK on 20-03-16.
 */
public class TrafficLightsInSetRuleTest {

    private TrafficLightsInSetRule model;
    private TrafficLightInSet trafficLightInSetA;
    private TrafficLightInSet trafficLightInSetB;
    private TrafficLightSet trafficLightSet;
    private TrafficLightController controller;
    private boolean flagA = false;
    private boolean flagB = false;

    @Before
    public void setUp() throws Exception {
        model = new TrafficLightsInSetRule();
        controller= new TrafficLightController();
        trafficLightInSetA = new TrafficLightInSet(new ID("TrafficLightInSetTest"));
        trafficLightInSetB = new TrafficLightInSet(new ID("TrafficLightInSetTest"));
        trafficLightSet = new TrafficLightSet(new ID("TrafficLightSetTest"));
    }

    @After
    public void tearDown() throws Exception {
        model = null;
        controller = null;
        trafficLightInSetA = null;
        trafficLightInSetB = null;
    }

    @Test
    public void testChangeStateofSet() throws Exception {

        trafficLightSet.addTrafficlight(trafficLightInSetA, 'A');
        trafficLightSet.addTrafficlight(trafficLightInSetB, 'B');
        model.changeStateofSet(trafficLightSet.InteriorListA); //Set A starts from GREEN so after change has RED
        model.changeStateofSet(trafficLightSet.InteriorListB); // The opposite from above


        for (TrafficLightInSet tf : trafficLightSet.InteriorListA) {
            if (tf.currentState == TrafficLightState.RED) {
                flagA = true;
            };
        }

        for (TrafficLightInSet tf : trafficLightSet.InteriorListB) {
            if (tf.currentState == TrafficLightState.GREEN) {
                flagB = true;
            };
        }
        if (flagB == flagA) {
            assertEquals(flagA, true);
        } else {
            assertEquals(false, true);
        }
    }
}