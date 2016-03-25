package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.features.Lane;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Es on 21/03/2016.
 */
public class LinkTest {
    private Link link;
    private GeoPoint gp;

    @Before
    public void setUp() throws Exception {
        gp = mock(GeoPoint.class);
        link = new Link(new ID("Link test"), gp);
    }

    @After
    public void tearDown() throws Exception {
        link = null;
    }

    @Test
    public void testGetGeoPoint() throws Exception {
        when(gp.getId()).thenReturn(new ID("GeoPoint 0000"));
        assertEquals(new ID("GeoPoint 0000"), link.getGeoPoint().getId());
    }

    @Test
    public void testGetID() throws Exception {
        assertEquals(new ID("Link test"), link.getID());
    }

    @Test
    public void testIsDeadEnd() throws Exception {
        assertTrue(link.isDeadEnd());
        link.in = mock(Lane.class);
        assertTrue(link.isDeadEnd());
        link.out = mock(Lane.class);
        assertFalse(link.isDeadEnd());
    }

    @Test
    public void testGetNextFeature() throws Exception {
        Lane n = mock(Lane.class);
        when(n.getID()).thenReturn(new ID("next lane"));
        link.out = n;
        assertEquals(new ID("next lane"), link.getNextFeature().getID());
    }

    @Test
    public void testGetPreviousFeature() throws Exception {
        Lane p = mock(Lane.class);
        when(p.getID()).thenReturn(new ID("previous lane"));
        link.in = p;
        assertEquals(new ID("previous lane"), link.getPreviousFeature().getID());
    }
}