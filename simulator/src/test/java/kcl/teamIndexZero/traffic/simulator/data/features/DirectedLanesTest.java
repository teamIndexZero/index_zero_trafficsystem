package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Created by Es on 01/03/2016.
 */
public class DirectedLanesTest {
    private RoadSpecs rs = new RoadSpecs();

    @Before
    public void setUp() throws Exception {
        this.rs.length = 5000;
    }

    @After
    public void tearDown() throws Exception {
        rs = null;
    }

    @Test
    public void testGetNumberOfLanes() throws Exception {
        DirectedLanes dl = new DirectedLanes(new ID("LaneGroup"), 5, this.rs);
        assertEquals(5, dl.getNumberOfLanes());
    }

    @Test
    public void testGetLaneID() throws Exception {
        DirectedLanes dl = new DirectedLanes(new ID("LaneGroup"), 5, this.rs);
        for (int i = 0; i < 5; i++) {
            ID expected = new ID(new ID("LaneGroup"), Integer.toString(i));
            assertEquals(expected.toString(), dl.getLaneID(i).toString());
        }
    }
}