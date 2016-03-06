package kcl.teamIndexZero.traffic.simulator.data.trafficBehaviour;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.features.Junction;
import kcl.teamIndexZero.traffic.simulator.exceptions.JunctionPathException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Es on 05/03/2016.
 */
public class TrafficBehaviourTest {
    private Junction junction;
    private TrafficBehaviour tb;

    @Before
    public void setUp() throws Exception {
        junction = mock(Junction.class);
        when(junction.getID()).thenReturn(new ID("MockJunctionID"));
        tb = new TrafficBehaviour(junction);
    }

    @After
    public void tearDown() throws Exception {
        tb = null;
    }

    @Test
    public void testAddLinkWeight() throws Exception {
        ID in1 = new ID("In1");
        ID out1 = new ID("Out1");
        ID out2 = new ID("Out2");
        this.tb.addPath(in1, out1);
        this.tb.addPath(in1, out2);
        this.tb.addLinkWeight(out1, 2);
        this.tb.addLinkWeight(out2, 1);
        assertEquals(out1, this.tb.getNextDestinationLinkID(in1));
        assertEquals(out2, this.tb.getNextDestinationLinkID(in1));
        assertEquals(out1, this.tb.getNextDestinationLinkID(in1));
        assertEquals(out1, this.tb.getNextDestinationLinkID(in1));
        assertEquals(out2, this.tb.getNextDestinationLinkID(in1));
    }

    @Test
    public void testAddPath() throws Exception {
        ID in1 = new ID("In1");
        ID out1 = new ID("Out1");
        this.tb.addPath(in1, out1);
        assertTrue(tb.pathExists(in1, out1));
    }

    @Test(expected = JunctionPathException.class)
    public void testAddPath_Exception() throws JunctionPathException {
        ID in1 = new ID("In1");
        tb.getNextDestinationLinkID(in1);
    }

    @Test
    public void testPathExists() throws Exception {
        ID in1 = new ID("In1");
        ID in2 = new ID("In2");
        ID out1 = new ID("Out1");
        ID out2 = new ID("Out2");
        this.tb.addPath(in1, out1);
        assertTrue(tb.pathExists(in1, out1));
        assertFalse(tb.pathExists(in1, out2));
        assertFalse(tb.pathExists(in2, out1));
        assertFalse(tb.pathExists(in2, out2));
    }

    @Test
    public void testGetNextDestinationLinkID() throws Exception {
        ID in1 = new ID("In1");
        ID out1 = new ID("Out1");
        ID out2 = new ID("Out2");
        ID out3 = new ID("Out3");
        this.tb.addPath(in1, out1);
        this.tb.addPath(in1, out2);
        this.tb.addPath(in1, out3);
        this.tb.addLinkWeight(out1, 2);
        this.tb.addLinkWeight(out2, 2);
        this.tb.addLinkWeight(out3, 1);
        for (int i = 0; i < 100; i++) {
            assertEquals(out1, this.tb.getNextDestinationLinkID(in1));
            assertEquals(out2, this.tb.getNextDestinationLinkID(in1));
            assertEquals(out3, this.tb.getNextDestinationLinkID(in1));
            assertEquals(out1, this.tb.getNextDestinationLinkID(in1));
            assertEquals(out2, this.tb.getNextDestinationLinkID(in1));
        }
    }

    @Test
    public void testGetJunctionID() throws Exception {
        assertEquals("MockJunctionID", this.tb.getJunctionID().toString());
    }

    @Test
    public void testGetJunction() throws Exception {
        assertEquals(this.junction, tb.getJunction());
    }

    @Test
    public void testToString() throws Exception {
        ID in1 = new ID("In1");
        ID in2 = new ID("In2");
        ID out1 = new ID("Out1");
        ID out2 = new ID("Out2");
        this.tb.addPath(in1, out1);
        this.tb.addPath(in1, out2);
        this.tb.addPath(in2, out2);
        this.tb.addLinkWeight(out1, 2);
        this.tb.addLinkWeight(out2, 3);
        this.tb.getNextDestinationLinkID(in1);
        String expected = "TrafficBehaviour for <MockJunctionID> { Paths:[In2=[Out2], In1=[Out1, Out2]], Quotas:[Out2=(0/3), Out1=(1/2)] }";
        assertEquals(expected, tb.toString());
    }

    @Test
    public void testGetAllPossibleExitPoints() throws Exception {
        ID in1 = new ID("In1");
        ID in2 = new ID("In2");
        ID out1 = new ID("Out1");
        ID out2 = new ID("Out2");
        this.tb.addPath(in1, out1);
        this.tb.addPath(in1, out2);
        this.tb.addPath(in2, out2);
        assertTrue(this.tb.getAllPossibleExitPoints(in1).contains(out1));
        assertTrue(this.tb.getAllPossibleExitPoints(in1).contains(out2));
        assertEquals(2, this.tb.getAllPossibleExitPoints(in1).size());
        assertTrue(this.tb.getAllPossibleExitPoints(in2).contains(out2));
        assertEquals(1, this.tb.getAllPossibleExitPoints(in2).size());
    }
}