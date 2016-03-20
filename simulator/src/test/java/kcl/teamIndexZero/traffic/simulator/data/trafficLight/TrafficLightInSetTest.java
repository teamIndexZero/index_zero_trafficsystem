package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TrafficLightInSetTest {

    private TrafficLightInSet model;

    @Before
    public void setUp() throws Exception {
        model = new TrafficLightInSet(new ID("TrafficLightInSeTest"));
    }

    @After
    public void tearDown() throws Exception {
        model = null;
    }

    @Test
    public void testGetState() throws Exception {
        assertTrue(model.getState() == TrafficLightState.GREEN);
    }

    @Test
    public void testGetID() throws Exception {
        assertEquals(model.getID(), new ID("TrafficLightInSeTest"));
    }
}