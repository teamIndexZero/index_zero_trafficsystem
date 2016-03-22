package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.features.Junction;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by Es on 06/03/2016.
 */
public class JunctionLinkTest {
    private Road mockedRoad;
    private Junction mockedJunction;
    private JunctionLink jl;

    @Before
    public void setUp() throws Exception {
        mockedRoad = mock(Road.class);
        mockedJunction = mock(Junction.class);
        jl = new JunctionLink(new ID("JunctionLinkTest"), mockedRoad, mockedJunction, new GeoPoint(0, 0), JunctionLink.LinkType.INFLOW);
    }

    @After
    public void tearDown() throws Exception {
        jl = null;
        mockedJunction = null;
        mockedRoad = null;
    }

    @Test
    public void testGetLinks() throws Exception {
        when(mockedJunction.getNextLinks(jl.getID())).thenReturn(new ArrayList<Link>());
        jl.getLinks();
        verify(mockedJunction).getNextLinks(jl.getID());
    }

    @Test
    public void testGetRoadID() throws Exception {
        when(mockedRoad.getID()).thenReturn(new ID("Awesome Rd."));
        assertEquals("Awesome Rd.", jl.getRoadID().toString());
        verify(mockedRoad).getID();
    }

    @Test
    public void testGetGeoPoint() throws Exception {
        assertEquals(new GeoPoint(0, 0), jl.getGeoPoint());
    }

    @Test
    public void testGetID() throws Exception {
        assertEquals(new ID("JunctionLinkTest"), jl.getID());
    }

    @Test
    public void testGetJunctionID() throws Exception {
        Junction j = mock(Junction.class);
        when(j.getID()).thenReturn(new ID("JunctionID tag"));
        JunctionLink jl1 = new JunctionLink(new ID("dead-end link"), mock(Road.class), j, new GeoPoint(0, 0), JunctionLink.LinkType.INFLOW);
        assertEquals(new ID("JunctionID tag"), jl1.getJunctionID());
    }

    @Test
    public void testIsOutflowLink() throws Exception {
        JunctionLink jl1 = new JunctionLink(new ID("dead-end link"), mock(Road.class), mock(Junction.class), new GeoPoint(0, 0), JunctionLink.LinkType.INFLOW);
        JunctionLink jl2 = new JunctionLink(new ID("dead-end link"), mock(Road.class), mock(Junction.class), new GeoPoint(0, 0), JunctionLink.LinkType.OUTFLOW);
        assertFalse(jl1.isOutflowLink());
        assertTrue(jl2.isOutflowLink());
    }

    @Test
    public void testIsInflowLink() throws Exception {
        JunctionLink jl1 = new JunctionLink(new ID("dead-end link"), mock(Road.class), mock(Junction.class), new GeoPoint(0, 0), JunctionLink.LinkType.INFLOW);
        JunctionLink jl2 = new JunctionLink(new ID("dead-end link"), mock(Road.class), mock(Junction.class), new GeoPoint(0, 0), JunctionLink.LinkType.OUTFLOW);
        assertTrue(jl1.isInflowLink());
        assertFalse(jl2.isInflowLink());
    }
}