package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import javafx.scene.chart.StackedAreaChart;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.IDTest;
import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLight;
import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLight.State;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by JK on 14-03-16.
 */
public class TrafficLightTest {
    private TrafficLight modelTrafficLight;


    @Before
    public void setUp() throws Exception {
        modelTrafficLight = new TrafficLight(new ID("TrafficLightTest"));
    }

    @After
    public void tearDown() throws Exception {
        modelTrafficLight = null;
    }

    @Test
    public void getState()throws Exception {
        assertTrue(modelTrafficLight.getState() == State.GREEN);
    }
}