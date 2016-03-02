package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by Es on 01/03/2016.
 */
public class LaneTest {

    @Test
    public void testGetWidth() throws Exception {
        RoadSpecs rs = new RoadSpecs();
        rs.width = 8.5;
        Lane l = new Lane(new ID("LaneTest"), rs, mock(DirectedLanes.class));
        assertEquals(8.5, l.getWidth());
    }

    @Test
    public void testGetLength() throws Exception {
        RoadSpecs rs = new RoadSpecs();
        rs.length = 10;
        Lane l = new Lane(new ID("LaneTest"), rs, mock(DirectedLanes.class));
        assertEquals(10, l.getLength());
    }

    @Test
    public void testGetID() throws Exception {
        RoadSpecs rs = new RoadSpecs();
        ID testID = new ID("LaneTest");
        Lane l = new Lane(testID, rs, mock(DirectedLanes.class));
        assertEquals(testID, l.getID());
    }

    @Test
    public void testTick() throws Exception {
        //TODO
    }
}