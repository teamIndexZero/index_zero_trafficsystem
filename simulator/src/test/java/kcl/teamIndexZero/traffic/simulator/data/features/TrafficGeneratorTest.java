package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.MapObject;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle;
import kcl.teamIndexZero.traffic.simulator.exceptions.JunctionPathException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by Es on 19/03/2016.
 */
public class TrafficGeneratorTest {
    GeoPoint point;
    Junction junction;
    TrafficGenerator tg;
    Road r1;
    Road r2;

    @Before
    public void setUp() throws Exception {
        point = mock(GeoPoint.class);
        GeoPolyline mockedPolyLine = mock(GeoPolyline.class);
        r1 = new Road(new ID("Road 1"), 2, 1, 12000, mockedPolyLine, "Road1");
        r2 = new Road(new ID("Road 2"), 0, 3, 10000, mockedPolyLine, "Road2");
        junction = new Junction(new ID("Junction"), false, point);
        tg = new TrafficGenerator(new ID("TrafficGenerator_Test"), point);
    }

    @After
    public void tearDown() throws Exception {
        tg = null;
    }

    @Test
    public void testGetGeoPoint() throws Exception {
        assertEquals(this.point, tg.getGeoPoint());
    }

    @Test
    public void testLinkRoad() throws Exception {
        //Check empty
        assertEquals(0, tg.getIncomingLinks().size());
        assertEquals(0, tg.getOutgoingLinks().size());
        //Check tg is added at Head
        tg.linkRoad(r1);
        assertEquals(2, tg.getIncomingLinks().size());
        assertEquals(1, tg.getOutgoingLinks().size());
        //Check tg is added at Tail (wrap around)
        tg.linkRoad(r1);
        assertEquals(3, tg.getIncomingLinks().size());
        assertEquals(3, tg.getOutgoingLinks().size());
    }

    @Test
    public void testLinkJunction() throws Exception {
        junction.addRoad(r1, JunctionDescription.RoadDirection.INCOMING);
        junction.addRoad(r2, JunctionDescription.RoadDirection.INCOMING);
        assertEquals(2, junction.getInflowCount());
        assertEquals(4, junction.getOutflowCount());
        tg.linkJunction(junction, 10, 20);
        assertEquals(10, tg.getIncomingLinks().size());
        assertEquals(20, tg.getOutgoingLinks().size());
        junction.addTrafficGenerator(tg);
        junction.computeAllPaths();
        assertEquals(22, junction.getInflowCount());
        assertEquals(14, junction.getOutflowCount());
    }

    @Test
    public void testGetIncomingLinks() throws Exception {
        tg.linkJunction(junction, 5, 10);
        assertEquals(5, tg.getIncomingLinks().size());
    }

    @Test
    public void testGetOutgoingLinks() throws Exception {
        tg.linkJunction(junction, 5, 10);
        assertEquals(10, tg.getOutgoingLinks().size());
    }

    @Test
    public void testTerminateTravel() throws Exception {
        Vehicle v = mock(Vehicle.class);
        tg.linkRoad(r1);
        assertEquals("TrafficGenerator_Test[IN: 0, OUT: 0]", tg.toString());
        tg.terminateTravel(v);
        assertEquals("TrafficGenerator_Test[IN: 1, OUT: 0]", tg.toString());
    }

    @Test
    public void testGetRandomLane() throws Exception {
        junction.addRoad(r1, JunctionDescription.RoadDirection.INCOMING);
        tg.linkJunction(junction, 5, 10);
        junction.addTrafficGenerator(tg);
        junction.computeAllPaths();
        tg.getRandomLane();
    }

    @Test(expected = JunctionPathException.class)
    public void testGetRandomLane_Exception1() throws Exception {
        tg.linkJunction(junction, 5, 10);
        tg.getRandomLane();
    }

    @Test
    public void testTick() throws Exception {
        SimulationMap map = mock(SimulationMap.class);
        tg.setMap(map);
        List<MapObject> list = new ArrayList<>();
        when(map.getObjectsOnSurface()).thenReturn(list);
        ArgumentCaptor<Vehicle> argument = ArgumentCaptor.forClass(Vehicle.class);
        tg.linkRoad(r1);
        SimulationTick tick = mock(SimulationTick.class);
        for (int i = 0; i < 50; i++) tg.tick(tick);
        verify(map, atLeastOnce()).addMapObject(argument.capture());
    }

    @Test
    public void testGetReceiptCounter() throws Exception {
        assertEquals(0, tg.getReceiptCounter());
        Vehicle v = mock(Vehicle.class);
        tg.terminateTravel(v);
        assertEquals(1, tg.getReceiptCounter());
    }

    @Test
    public void testGetThisGeneratorCreationCounter() throws Exception {
        SimulationMap map = mock(SimulationMap.class);
        tg.setMap(map);
        List<MapObject> list = new ArrayList<>();
        when(map.getObjectsOnSurface()).thenReturn(list);
        tg.linkRoad(r1);
        SimulationTick tick = mock(SimulationTick.class);
        for (int i = 0; i < 50; i++) {
            tg.tick(tick);
            assertTrue(tg.getThisGeneratorCreationCounter() >= 0);
        }
    }
}