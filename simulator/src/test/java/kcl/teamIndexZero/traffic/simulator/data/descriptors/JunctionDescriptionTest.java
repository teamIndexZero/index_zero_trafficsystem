package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Es on 04/03/2016.
 */
public class JunctionDescriptionTest {
    private ID id;
    private List<ID> list;
    private boolean flag;
    private JunctionDescription jd;

    @Before
    public void setUp() throws Exception {
        this.id = new ID("JunctionDescriptionTest");
        this.list = new ArrayList<ID>();
        this.list.add(new ID("road1"));
        this.list.add(new ID("road2"));
        this.list.add(new ID("road3"));
        this.flag = true;
        this.jd = new JunctionDescription(this.id, this.list, this.flag);
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