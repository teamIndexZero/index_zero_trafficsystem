package kcl.teamIndexZero.traffic.simulator.data.trafficBehaviour;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Es on 05/03/2016.
 */
public class TrafficQuotaTest {

    @Test
    public void testReset() throws Exception {
        TrafficQuota q = new TrafficQuota(10);
        for (int i = 0; i < 10; i++) {
            q.incrementCounter();
        }
        assertFalse(q.incrementCounter());
        q.reset();
        for (int i = 0; i < 10; i++) {
            assertTrue(q.incrementCounter());
        }
        assertFalse(q.incrementCounter());

    }

    @Test
    public void testIncrementCounter() throws Exception {
        TrafficQuota q = new TrafficQuota(10);
        for (int i = 0; i < 10; i++) {
            assertTrue(q.incrementCounter());
        }
        assertFalse(q.incrementCounter());
    }

    @Test
    public void testToString() throws Exception {
        TrafficQuota q = new TrafficQuota(10);
        assertEquals("(0/10)", q.toString());
        for (int i = 0; i < 10; i++) {
            assertTrue(q.incrementCounter());
            assertEquals("(" + (i + 1) + "/10)", q.toString());
        }
        assertFalse(q.incrementCounter());
        q.reset();
        assertEquals("(0/10)", q.toString());
    }
}