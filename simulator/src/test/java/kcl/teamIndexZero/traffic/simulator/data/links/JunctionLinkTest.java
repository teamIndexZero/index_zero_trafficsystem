package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.features.Junction;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
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
        jl = new JunctionLink(new ID("JunctionLinkTest"), mockedRoad, mockedJunction, new GeoPoint(0, 0));
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
}