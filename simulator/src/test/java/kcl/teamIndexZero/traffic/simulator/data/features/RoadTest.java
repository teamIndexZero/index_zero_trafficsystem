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
    private GeoPolyline mockedPolyLine;

    @Before
    public void setUp() throws Exception {
        mockedPolyLine = mock(GeoPolyline.class);
        r1 = new Road(new ID("RoadTest"), 3, 5, 12000, mockedPolyLine, "Road1");
        r2 = new Road(new ID("RoadTest"), 10, 0, 10000, mockedPolyLine, "Road2");
    }

    @After
    public void tearDown() throws Exception {
        r1 = null;
        r2 = null;
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("Road1", r1.getName());
    }

    @Test
    public void testGetIncomingLaneCount() throws Exception {
        assertEquals(5, r1.getIncomingLaneCount());
        assertEquals(0, r2.getIncomingLaneCount());
    }

    @Test
    public void testGetOutgoingLaneCount() throws Exception {
        assertEquals(3, r1.getOutgoingLaneCount());
        assertEquals(10, r2.getOutgoingLaneCount());
    }

    @Test
    public void testGetIncomingSide() throws Exception {
        assertEquals(5, r1.getIncomingSide().getLanes().size());
        assertEquals(0, r2.getIncomingSide().getLanes().size());
    }

    @Test
    public void testGetOutgoingSide() throws Exception {
        assertEquals(3, r1.getOutgoingSide().getLanes().size());
        assertEquals(10, r2.getOutgoingSide().getLanes().size());
    }

    @Test
    public void testGetPolyline() throws Exception {
        assertEquals(mockedPolyLine, r1.getPolyline());
    }

    @Test
    public void testGetRoadLength() throws Exception {
        assertEquals(12000, r1.getRoadLength(), 0);
        assertEquals(10000, r2.getRoadLength(), 0);
    }
}