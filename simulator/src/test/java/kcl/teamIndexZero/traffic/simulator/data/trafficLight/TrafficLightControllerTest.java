package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import kcl.teamIndexZero.traffic.simulator.data.ID;
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
    private TrafficLightInSet trafficLightInSet;
    private TrafficLightSet trafficLightSet;
    private boolean flag = false;
    private Date date;


    @Before
    public void setUp() throws Exception {
        model = new TrafficLightController();
        rule = new TrafficLightRule();
        setRule = new TrafficLightsInSetRule();
        trafficLightSet = new TrafficLightSet(new ID("TrafficLightSetTest"));
        trafficLight = new TrafficLight(new ID("TrafficLightTest"));
        trafficLightInSet= new TrafficLightInSet(new ID("TrafficLightInSetTest"));
    }

    @After
    public void tearDown() throws Exception {
        model = null;
        rule = null;
        setRule = null;
        trafficLightSet = null;
        trafficLight = null;
        trafficLightInSet = null;
    }

    @Test
    public void testGetSingleSet() {
        Assert.assertEquals(model.getSingleSet(), model.TrafficLightSinglesList);
    }

    @Test
    public void testAddRuleSingleTF() {
        model.addTrafficlight(trafficLight);//At the beginning the State was GREEN
        model.addRule(rule); //should be RED now

        for (TrafficLight tf : model.TrafficLightSinglesList) {
            if (tf.currentState == TrafficLightState.RED) {
                flag = true;
            }
            ;
        }
        Assert.assertTrue(flag == true);
    }
}