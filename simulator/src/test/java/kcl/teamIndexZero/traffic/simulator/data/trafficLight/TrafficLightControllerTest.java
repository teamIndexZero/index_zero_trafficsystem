package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.TrafficLightRule;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.TrafficLightsInSetRule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.time.LocalDateTime;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by JK on 14-03-16.
 */
public class TrafficLightControllerTest {

    private TrafficLightController model;
    private TrafficLightRule rule;
    private TrafficLightsInSetRule setRule;
    private TrafficLight trafficLight;
    private TrafficLightInSet trafficLightInSetA;
    private TrafficLightInSet trafficLightInSetB;
    private TrafficLightSet trafficLightSet;
    private boolean flag;


    @Before
    public void setUp() throws Exception {
        model = new TrafficLightController();
        rule = new TrafficLightRule();
        setRule = new TrafficLightsInSetRule();
        trafficLightSet = new TrafficLightSet(new ID("TrafficLightSetTestA"));
        trafficLight = new TrafficLight(new ID("TrafficLightTest"));
        trafficLightInSetA = new TrafficLightInSet(new ID("TrafficLightInSetTestA"));
        trafficLightInSetB = new TrafficLightInSet(new ID("TrafficLightInSetTestB"));
    }

    @After
    public void tearDown() throws Exception {
        model = null;
        rule = null;
        setRule = null;
        trafficLightSet = null;
        trafficLight = null;
        trafficLightInSetA = null;
        trafficLightInSetB = null;
    }

    @Test
    public void testGetSingleSet() {
        Assert.assertEquals(model.getSingleSet(), model.TrafficLightSinglesList);
    }

    @Test
    public void testAddRuleSingleTF() {
        flag = false;
        model.addTrafficlight(trafficLight);//At the beginning the State was GREEN
        model.addRule(rule);                //Should be RED by now

        for (TrafficLight tf : model.TrafficLightSinglesList) {
            if (tf.currentState == TrafficLightState.RED) {
                flag = true;
            };
        }
        Assert.assertTrue(flag);
    }

    @Test
    public void testAddRuleSetA() {
        flag = false;
        trafficLightSet.addTrafficlight(trafficLightInSetA, 'A');           //initially the State was GREEN
        model.TrafficLightSetList = trafficLightSet.TrafficLightSetList;
        model.addRule(setRule);                                             // Now should be RED

        for (TrafficLightInSet tf : model.TrafficLightSetList) {
            if (tf.currentState == TrafficLightState.RED) {
                flag = true;
            };
        }
        Assert.assertTrue(flag);
    }

    @Test
    public void testAddRuleSetB() {
        flag = false;
        trafficLightSet.addTrafficlight(trafficLightInSetB, 'B');       //initially the State was RED
        model.TrafficLightSetList = trafficLightSet.TrafficLightSetList;
        model.addRule(setRule);                                          // Now should be GREEN

        for (TrafficLightInSet tf : model.TrafficLightSetList) {
            if (tf.currentState == TrafficLightState.GREEN) {
                flag = true;
            };
        }
        Assert.assertTrue(flag);
    }

    @Test
    public void testFormatTimeToLong() {
        LocalDateTime date = LocalDateTime.of(1984, 12, 16, 7, 45, 56); // 1 second = 1000 millisec. The "starting" date has 55 seconds in the end
        long diff;
        diff = model.formatTimeToLong(date);
        Assert.assertTrue(diff == 1000L);
    }
}