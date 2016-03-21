package kcl.teamIndexZero.traffic.simulator.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Es on 01/03/2016.
 */
public class IDTest {
    private ID id1;
    private ID id2;

    @Before
    public void setUp() throws Exception {
        id1 = new ID("IDTest");
        id2 = new ID(id1, "discriminant");
    }

    @After
    public void tearDown() throws Exception {
        id1 = null;
        id2 = null;
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals("IDTest", id1.getId());
        assertEquals("IDTest:discriminant", id2.getId());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("IDTest", id1.toString());
        assertEquals("IDTest:discriminant", id2.toString());
    }
}