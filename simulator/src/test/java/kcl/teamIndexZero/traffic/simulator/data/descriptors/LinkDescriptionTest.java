package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.links.LinkType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Es on 04/03/2016.
 */
public class LinkDescriptionTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testToString() throws Exception {
        LinkDescription ld = new LinkDescription(new ID("FromID"), new ID("ToID"), LinkType.GENERIC, new ID("LinkDescriptionTest"));
        assertEquals("LinkDescriptionTest[ FromID -> ToID ]", ld.toString());
    }
}