package kcl.teamIndexZero.traffic.simulator.data.mapObjects;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.*;
import kcl.teamIndexZero.traffic.simulator.data.links.JunctionLink;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Testing some aspects of vehicle movement behavior.
 */
public class VehicleMovementTest {

    private Lane lane;
    private Road road;
    private DirectedLanes forwardSide;
    private Vehicle vehicle;
    private SimulationTick tick;
    private SimulationMap map;
    private Map<ID, Feature> features;

    @Before
    public void setup() {
        ID roadId = new ID("Road");

        lane = mock(Lane.class);
        road = mock(Road.class);
        forwardSide = mock(DirectedLanes.class);
        when(lane.getRoad()).thenReturn(road);
        when(road.getForwardSide()).thenReturn(forwardSide);
        when(road.getID()).thenReturn(roadId);
        when(forwardSide.getLanes()).thenReturn(Collections.singletonList(lane));

        vehicle = spy(Vehicle.createPassengerCar(new ID("Test"), "TestName", lane));

        tick = new SimulationTick(10, LocalDateTime.now(), 1);

        map = mock(SimulationMap.class);
        features = new HashMap<>();
        features.put(roadId, road);
        when(map.getMapFeatures()).thenReturn(features);

        vehicle.setMap(map);
    }

    @Test
    public void shouldAccelerateOnStartOfTheLane() {
        // given
        JunctionLink outgoingLink = mock(JunctionLink.class);
        when(lane.getNextLink()).thenReturn(outgoingLink);
        when(lane.getLength()).thenReturn(10.0);

        // when
        vehicle.tick(tick);

        // then
        verify(vehicle, never()).driveOnJunction(any());
        verify(vehicle, never()).driveIntoTrafficGenerator(any());
        verify(vehicle, never()).driveOnGenericLink(any());

        assertThat(vehicle.getSpeedKph()).isGreaterThan(0);
        assertThat(vehicle.getAccelerationKphH()).isGreaterThan(0);
    }


    @Test
    public void shouldAccelerateIfJunctionIsForTwoRoadsOnly() {
        // given
        JunctionLink outgoingLink = mock(JunctionLink.class);
        Junction junction = mock(Junction.class);
        // next link leads us to a junciton which has two roads only.
        Road otherRoad = mock(Road.class);
        when(junction.getConnectedFeatures()).thenReturn(Arrays.asList(road, otherRoad));

        when(outgoingLink.getNextFeature()).thenReturn(junction);
        when(lane.getNextLink()).thenReturn(outgoingLink);
        when(lane.getLength()).thenReturn(10.0);

        // when
        vehicle.tick(tick);

        // then
        verify(vehicle, never()).driveOnJunction(any());
        verify(vehicle, never()).driveIntoTrafficGenerator(any());
        verify(vehicle, never()).driveOnGenericLink(any());

        assertThat(vehicle.getSpeedKph()).isGreaterThan(0);
        assertThat(vehicle.getAccelerationKphH()).isGreaterThan(0);
    }


    @Test
    public void shouldNotAccelerateIfJunctionIsForMoreThanTwoRoads() {
        // given
        JunctionLink outgoingLink = mock(JunctionLink.class);
        Junction junction = mock(Junction.class);
        // next link leads us to a junciton which has two roads only.
        Road otherRoad = mock(Road.class);
        Road otherRoad2 = mock(Road.class);
        when(junction.getConnectedFeatures()).thenReturn(Arrays.asList(road, otherRoad, otherRoad2));

        when(outgoingLink.getNextFeature()).thenReturn(junction);
        when(lane.getNextLink()).thenReturn(outgoingLink);
        when(lane.getLength()).thenReturn(10.0);

        // when
        vehicle.tick(tick);

        // then
        verify(vehicle, never()).driveOnJunction(any());
        verify(vehicle, never()).driveIntoTrafficGenerator(any());
        verify(vehicle, never()).driveOnGenericLink(any());

        assertThat(vehicle.getSpeedKph()).isGreaterThan(0);
        assertThat(vehicle.getAccelerationKphH()).isLessThanOrEqualTo(0);
    }
}
