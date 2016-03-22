package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Es on 04/03/2016.
 */
public class RoadDescriptionTest {
    private RoadDescription rd;
    private ID id;
    private GeoPolyline pl;
    private String rname;
    private int fLanes;
    private int bLanes;

    @Before
    public void setUp() throws Exception {
        this.id = new ID("RoadDescriptionTest");
        this.rname = "Awesome rd";
        this.fLanes = 5;
        this.bLanes = 3;
        this.pl = new GeoPolyline();
        pl.addPoint(new GeoPoint(0, 0));
        pl.addPoint(new GeoPoint(0, 1));
        pl.addPoint(new GeoPoint(1, 1));
        this.rd = new RoadDescription(id, rname, pl, fLanes, bLanes, 0);
    }

    @After
    public void tearDown() throws Exception {
        this.rd = null;
        this.id = null;
        this.pl = null;
        this.rname = null;
    }

    @Test
    public void testGetGeoPolyline() throws Exception {
        assertEquals(this.pl, this.rd.getGeoPolyline());
    }

    @Test
    public void testGetLaneCountForward() throws Exception {
        assertEquals(this.fLanes, this.rd.getLaneCountForward());
    }

    @Test
    public void testGetLaneCountBackward() throws Exception {
        assertEquals(this.bLanes, this.rd.getLaneCountBackward());
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(this.id, this.rd.getId());
    }

    @Test
    public void testGetLength() throws Exception {
        assertEquals(2, this.rd.getLength(), 0);
    }

    @Test
    public void testGetRoadName() throws Exception {
        assertEquals(this.rname, this.rd.getRoadName());
    }

    @Test
    public void testGetLayer() throws Exception {
        assertEquals(0, rd.getLayer());
    }
}