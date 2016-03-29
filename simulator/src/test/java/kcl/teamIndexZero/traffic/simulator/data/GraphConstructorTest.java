package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by Es on 09/03/2016.
 */
@Ignore
public class GraphConstructorTest {
    private RoadDescription rd1, rd2, rd3, rd4, rd5, rd6, rd7, rd8, rd9, rd10, rd11;
    private JunctionDescription j1, j2, j3, j4;
    private GraphConstructor graphConstructor;

    @Before
    public void setUp() throws Exception {
        //Roads
        rd1 = new RoadDescription(new ID("rd1"), "Road1", mock(GeoPolyline.class), 1, 1, 0);
        rd2 = new RoadDescription(new ID("rd2"), "Road2", mock(GeoPolyline.class), 1, 1, 0);
        rd3 = new RoadDescription(new ID("rd3"), "Road3", mock(GeoPolyline.class), 1, 1, 0);
        rd4 = new RoadDescription(new ID("rd4"), "Road4", mock(GeoPolyline.class), 1, 1, 0);
        rd5 = new RoadDescription(new ID("rd5"), "Road5", mock(GeoPolyline.class), 1, 1, 0);
        rd6 = new RoadDescription(new ID("rd6"), "Road6", mock(GeoPolyline.class), 1, 1, 0);
        rd7 = new RoadDescription(new ID("rd7"), "Road7", mock(GeoPolyline.class), 1, 1, 0);
        rd8 = new RoadDescription(new ID("rd8"), "Road8", mock(GeoPolyline.class), 1, 1, 0);
        rd9 = new RoadDescription(new ID("rd9"), "Road9", mock(GeoPolyline.class), 1, 1, 0);
        rd10 = new RoadDescription(new ID("rd10"), "Road10", mock(GeoPolyline.class), 1, 1, 0);
        rd11 = new RoadDescription(new ID("rd11"), "Road11", mock(GeoPolyline.class), 1, 1, 0);
        //Junction 1
        Map<ID, JunctionDescription.RoadDirection> j1rd = new HashMap<>();
        j1rd.put(rd1.getId(), JunctionDescription.RoadDirection.OUTGOING);
        j1rd.put(rd2.getId(), JunctionDescription.RoadDirection.OUTGOING);
        j1rd.put(rd3.getId(), JunctionDescription.RoadDirection.OUTGOING);
        j1 = new JunctionDescription(new ID("j1"), j1rd, false, new GeoPoint(0, 0));
        //Junction 2
        Map<ID, JunctionDescription.RoadDirection> j2rd = new HashMap<>();
        j2rd.put(rd2.getId(), JunctionDescription.RoadDirection.INCOMING);
        j2rd.put(rd5.getId(), JunctionDescription.RoadDirection.OUTGOING);
        j2rd.put(rd6.getId(), JunctionDescription.RoadDirection.OUTGOING);
        j2 = new JunctionDescription(new ID("j2"), j2rd, false, new GeoPoint(0, 0));
        //Junction 3
        Map<ID, JunctionDescription.RoadDirection> j3rd = new HashMap<>();
        j3rd.put(rd4.getId(), JunctionDescription.RoadDirection.INCOMING);
        j3rd.put(rd5.getId(), JunctionDescription.RoadDirection.INCOMING);
        j3rd.put(rd8.getId(), JunctionDescription.RoadDirection.OUTGOING);
        j3 = new JunctionDescription(new ID("j3"), j3rd, false, new GeoPoint(0, 0));
        //Junction 4
        Map<ID, JunctionDescription.RoadDirection> j4rd = new HashMap<>();
        j4rd.put(rd3.getId(), JunctionDescription.RoadDirection.INCOMING);
        j4rd.put(rd4.getId(), JunctionDescription.RoadDirection.OUTGOING);
        j4rd.put(rd7.getId(), JunctionDescription.RoadDirection.OUTGOING);
        j4 = new JunctionDescription(new ID("j4"), j4rd, false, new GeoPoint(0, 0));
        //List of descriptions
        List<RoadDescription> roadDescriptionList = new ArrayList<>();
        roadDescriptionList.add(rd1);
        roadDescriptionList.add(rd2);
        roadDescriptionList.add(rd3);
        roadDescriptionList.add(rd4);
        roadDescriptionList.add(rd5);
        roadDescriptionList.add(rd6);
        roadDescriptionList.add(rd7);
        roadDescriptionList.add(rd8);
        roadDescriptionList.add(rd9);
        roadDescriptionList.add(rd10);
        roadDescriptionList.add(rd11);
        List<JunctionDescription> junctionDescriptionList = new ArrayList<>();
        junctionDescriptionList.add(j1);
        junctionDescriptionList.add(j2);
        junctionDescriptionList.add(j3);
        junctionDescriptionList.add(j4);
        //Graph
        graphConstructor = new GraphConstructor(junctionDescriptionList, roadDescriptionList);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetFeatures() throws Exception {
        assertEquals(47, graphConstructor.getFeatures().size());
    }

    @Test
    public void testGetLinks() throws Exception {
        assertEquals(20, graphConstructor.getLinks().size());
    }
}