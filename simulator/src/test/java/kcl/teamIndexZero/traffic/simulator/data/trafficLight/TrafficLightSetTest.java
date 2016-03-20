package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by JK on 20-03-16.
 */
public class TrafficLightSetTest {

    private TrafficLightSet model;
    private TrafficLightInSet trafficLightInSetA;
    private TrafficLightInSet trafficLightInSetB;
    private boolean flagA = false;
    private boolean flagB = false;
    @Before
    public void setUp() throws Exception {
        model = new TrafficLightSet(new ID("TrafficLightSetTest"));
        trafficLightInSetA = new TrafficLightInSet(new ID("TrafficLightInSetTest"));
        trafficLightInSetB = new TrafficLightInSet(new ID("TrafficLightInSetTest"));
    }

    @After
    public void tearDown() throws Exception {
        model = null;
        trafficLightInSetA = null;
        trafficLightInSetB = null;
    }

    @Test
    public void testAddTrafficlight() throws Exception {
        model.addTrafficlight(trafficLightInSetA, 'A');
        model.addTrafficlight(trafficLightInSetB, 'B');

        for (TrafficLightInSet tf : model.InteriorListA) {
            if (tf.currentState == TrafficLightState.GREEN) { //should fulfill this statement as initial state is GREEN in this group
                flagA = true;
            };
        }

        for (TrafficLightInSet tf : model.InteriorListB) {
            if (tf.currentState == TrafficLightState.RED) { //should fulfill this statement as initial state is GREEN in this group
                flagB = true;
            };
        }

        if (!flagA){ //flagA or flagB should not remain false!
            assertEquals(false, true);
        };
        if (!flagB){
            assertEquals(false, true);
        };
        if (flagB == flagA) {
            assertEquals(flagA, true);
        }
    }

    @Test
    public void testGetSet() throws Exception {
        assertEquals(model.getSet(new ID("TrafficLightSetTest")), model.TrafficLightSetList);
    }

    @Test
    public void testGetID() throws Exception {
        assertEquals(model.getID(), new ID("TrafficLightSetTest"));
    }
}