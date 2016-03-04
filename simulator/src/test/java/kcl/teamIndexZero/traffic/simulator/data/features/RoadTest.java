package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by Es on 01/03/2016.
 */
public class RoadTest {
    private Road r1;
    private Road r2;

    @Before
    public void setUp() throws Exception {
        r1 = new Road(new ID("RoadTest"), 3, 5, 12000, mock(GeoPolyline.class), "Road1");
        r2 = new Road(new ID("RoadTest"), 10, 0, 10000, mock(GeoPolyline.class), "Road2");
    }

    @After
    public void tearDown() throws Exception {
        r1 = null;
        r2 = null;
    }

    @Test
    public void testGetRoadLength() throws Exception {
        assertEquals(12000, r1.getRoadLength(), 0);
        assertEquals(10000, r2.getRoadLength(), 0);
    }

    @Test
    public void testGetLeftLaneCount() throws Exception {
        assertEquals(3, r1.getLeftLaneCount());
        assertEquals(10, r2.getLeftLaneCount());
    }

    @Test
    public void testGetRightLaneCount() throws Exception {
        assertEquals(5, r1.getRightLaneCount());
        assertEquals(0, r2.getRightLaneCount());
    }
}