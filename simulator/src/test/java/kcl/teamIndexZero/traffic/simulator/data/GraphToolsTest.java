package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.simulator.data.features.Lane;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.exceptions.MapIntegrityException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by Es on 08/03/2016.
 */
public class GraphToolsTest {
    private GraphTools gt;

    @Before
    public void setUp() throws Exception {
        gt = new GraphTools();
    }

    @After
    public void tearDown() throws Exception {
        gt = null;
    }

    @Test
    public void testCheckFwdLinksPresent() throws Exception {
        GeoPolyline mockedPolyLine = mock(GeoPolyline.class);
        Road r = new Road(new ID("testCheckFwdLinksPresent"), 2, 3, 1000, mockedPolyLine, "Road1");
        assertFalse(gt.checkFwdLinksPresent(r.getForwardSide()));
        assertFalse(gt.checkFwdLinksPresent(r.getBackwardSide()));
        for (Lane l : r.getForwardSide().getLanes()) {
            ID id = new ID(l.getID() + "->" + "Link");
            Link link = new Link(id, new GeoPoint(0, 0));
            link.in = l;
            l.connectNext(link);
        }
        assertTrue(gt.checkFwdLinksPresent(r.getForwardSide()));
        assertFalse(gt.checkFwdLinksPresent(r.getBackwardSide()));
    }

    @Test(expected = MapIntegrityException.class)
    public void testCheckFwdLinksPresent_Exception() throws Exception {
        GeoPolyline mockedPolyLine = mock(GeoPolyline.class);
        Road r = new Road(new ID("testCheckFwdLinksPresent"), 2, 3, 1000, mockedPolyLine, "Road1");
        assertFalse(gt.checkFwdLinksPresent(r.getForwardSide()));
        assertFalse(gt.checkFwdLinksPresent(r.getBackwardSide()));
        ID id = new ID("LaneB0" + "->" + "Link");
        Link link = new Link(id, new GeoPoint(0, 0));
        r.getBackwardSide().getLanes().get(0).connectNext(link);
        gt.checkFwdLinksPresent(r.getBackwardSide());
    }

    @Test
    public void testCheckEmpty() throws Exception {
        List<String> list1 = new LinkedList<>();
        List<Integer> list2 = new ArrayList<>();
        assertTrue(gt.checkEmpty(list1, list2));
        list1.add("one");
        assertTrue(gt.checkEmpty(list1, list2));
        list2.add(5);
        assertFalse(gt.checkEmpty(list1, list2));
    }

    @Test
    public void testCheckBckLinksPresent() throws Exception {
        GeoPolyline mockedPolyLine = mock(GeoPolyline.class);
        Road r = new Road(new ID("testCheckBckLinksPresent"), 2, 3, 1000, mockedPolyLine, "Road1");
        assertFalse(gt.checkBckLinksPresent(r.getForwardSide()));
        assertFalse(gt.checkBckLinksPresent(r.getBackwardSide()));
        for (Lane l : r.getForwardSide().getLanes()) {
            ID id = new ID(l.getID() + "<-" + "Link");
            Link link = new Link(id, new GeoPoint(0, 0));
            link.in = l;
            l.connectPrevious(link);
        }
        assertTrue(gt.checkBckLinksPresent(r.getForwardSide()));
        assertFalse(gt.checkBckLinksPresent(r.getBackwardSide()));
    }

    @Test(expected = MapIntegrityException.class)
    public void testCheckBckLinksPresent_Exception() throws Exception {
        GeoPolyline mockedPolyLine = mock(GeoPolyline.class);
        Road r = new Road(new ID("testCheckBckLinksPresent"), 2, 3, 1000, mockedPolyLine, "Road1");
        assertFalse(gt.checkBckLinksPresent(r.getForwardSide()));
        assertFalse(gt.checkBckLinksPresent(r.getBackwardSide()));
        ID id = new ID("LaneB0" + "<-" + "Link");
        Link link = new Link(id, new GeoPoint(0, 0));
        r.getBackwardSide().getLanes().get(0).connectPrevious(link);
        gt.checkBckLinksPresent(r.getBackwardSide());
    }
}