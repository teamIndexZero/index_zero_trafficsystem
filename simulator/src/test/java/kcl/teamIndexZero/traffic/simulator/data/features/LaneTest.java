package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Es on 01/03/2016.
 */
public class LaneTest {

    @Test
    public void testGetWidth() throws Exception {
        RoadSpecs rs = new RoadSpecs();
        rs.width = 8.5;
        Lane l = new Lane(new ID("LaneTest"), rs, mock(DirectedLanes.class));
        assertEquals(8.5, l.getWidth());
    }

    @Test
    public void testGetLength() throws Exception {
        RoadSpecs rs = new RoadSpecs();
        rs.length = 10;
        Lane l = new Lane(new ID("LaneTest"), rs, mock(DirectedLanes.class));
        assertEquals(10f, l.getLength(), 0.0001);
    }

    @Test
    public void testGetID() throws Exception {
        RoadSpecs rs = new RoadSpecs();
        ID testID = new ID("LaneTest");
        Lane l = new Lane(testID, rs, mock(DirectedLanes.class));
        assertEquals(testID, l.getID());
    }

    @Test
    public void testGetRoadID() throws Exception {
        RoadSpecs rs = new RoadSpecs();
        DirectedLanes mocked_dl = mock(DirectedLanes.class);
        Lane l = new Lane(new ID("LaneTest"), rs, mocked_dl);
        when(mocked_dl.getRoad()).thenReturn(new Road(new ID("TestRoad"), 2, 2, 2000, new GeoPolyline(), "Awesome Rd."));
        assertEquals("TestRoad", l.getRoadID().toString());
    }

    @Test
    public void testGetRoad() throws Exception {
        RoadSpecs rs = new RoadSpecs();
        DirectedLanes mocked_dl = mock(DirectedLanes.class);
        Lane l = new Lane(new ID("LaneTest"), rs, mocked_dl);
        when(mocked_dl.getRoad()).thenReturn(new Road(new ID("TestRoad"), 2, 2, 2000, new GeoPolyline(), "Awesome Rd."));
        assertEquals("Awesome Rd.", l.getRoad().getName());
    }

    @Test
    public void testGetNextLink() throws Exception {
        RoadSpecs rs = new RoadSpecs();
        DirectedLanes mocked_dl = mock(DirectedLanes.class);
        Lane l = new Lane(new ID("LaneTest"), rs, mocked_dl);
        Link link = new Link(new ID("LinkID"));
        l.connectNext(link);
        assertEquals(link, l.getNextLink());
    }
}