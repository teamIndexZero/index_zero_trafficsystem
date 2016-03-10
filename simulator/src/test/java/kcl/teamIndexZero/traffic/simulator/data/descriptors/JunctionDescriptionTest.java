package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Es on 04/03/2016.
 */
public class JunctionDescriptionTest {
    private ID id;
    private Map<ID, JunctionDescription.RoadDirection> list;
    private boolean flag;
    private JunctionDescription jd;

    @Before
    public void setUp() throws Exception {
        this.id = new ID("JunctionDescriptionTest");
        this.list = new HashMap<>();
        this.list.put(new ID("road1"), JunctionDescription.RoadDirection.INCOMING);
        this.list.put(new ID("road2"), JunctionDescription.RoadDirection.INCOMING);
        this.list.put(new ID("road3"), JunctionDescription.RoadDirection.OUTGOING);
        this.flag = true;
        this.jd = new JunctionDescription(this.id, this.list, this.flag, new GeoPoint(0, 0));
    }

    @After
    public void tearDown() throws Exception {
        this.id = null;
        this.list.clear();
    }

    @Test
    public void testGetID() throws Exception {
        assertEquals(this.id, jd.getID());
    }

    @Test
    public void testGetConnectedIDs() throws Exception {
        assertEquals(list, jd.getConnectedIDs());
    }

    @Test
    public void testAsTrafficLight() throws Exception {
        assertTrue(jd.hasTrafficLight());
    }


}