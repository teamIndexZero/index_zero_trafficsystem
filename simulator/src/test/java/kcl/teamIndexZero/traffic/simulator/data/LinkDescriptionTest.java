package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.simulator.data.descriptors.LinkDescription;
import kcl.teamIndexZero.traffic.simulator.data.links.LinkType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Es on 01/03/2016.
 */
public class LinkDescriptionTest {

    @Test
    public void testToString() throws Exception {
        ID from = new ID("fromID");
        ID to = new ID("toID");
        ID linkID = new ID("linkID");
        LinkDescription ld = new LinkDescription(from, to, LinkType.GENERIC, linkID);
        assertEquals("linkID[ fromID -> toID ]", ld.toString());
    }
}