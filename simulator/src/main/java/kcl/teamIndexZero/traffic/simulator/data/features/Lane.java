package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoSegment;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Lane within a set of directed lanes within a road
 */
public class Lane extends Feature {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Lane.class.getSimpleName());
    private final DirectedLanes lanes;
    private final int indexInDirectedLanes;
    private GeoPolyline polyline;
    private RoadSpecs roadSpecs;
    private Color color;
    private Link nextLink;
    private Link previousLink;
    private Set<Vehicle> carsOnThisLane = new HashSet<>();

    /**
     * Constructor
     *
     * @param road_specs Road specifications
     */
    public Lane(ID id, RoadSpecs road_specs, DirectedLanes parent, int indexInDirectedLanes) {
        super(id);
        this.lanes = parent;
        this.roadSpecs = road_specs;
        this.indexInDirectedLanes = indexInDirectedLanes;

        LOG.log("New lane (", id.toString(), ") created: length=", this.roadSpecs.length, "m, width=", this.roadSpecs.width, "m.");
    }

    public Color getColor() {
        return color;
    }

    public int getIndexInDirectedLanes() {
        return indexInDirectedLanes;
    }

    public GeoPolyline getPolyline() {
        return polyline;
    }

    /**
     * Externally called by road constructor to create a polyline. We need to have all lanes created at this moment.
     */
    void constructPolyline() {
        double totalRoadWidth = lanes.getRoad().getRoadWidth();
        double laneWidth = getWidth();
        int laneInRoadPosition = isForwardLane()
                ? getRoad().getForwardSide().getLanes().indexOf(this)
                : getRoad().getForwardSide().getLanes().size() + getRoad().getBackwardSide().getLanes().indexOf(this);
        double metersOffsetAlongNormalVector = -totalRoadWidth / 2 + laneWidth / 2 + laneInRoadPosition * laneWidth;

        List<GeoSegment> segmentList = getRoad().getPolyline().getSegments().stream().map(segment -> {
            double normalAngle = segment.getAngleToEastRadians() - Math.toRadians(90);
            double offsetX = metersOffsetAlongNormalVector * Math.cos(normalAngle);
            double offsetY = metersOffsetAlongNormalVector * Math.sin(normalAngle);
            return new GeoSegment(
                    new GeoPoint(
                            segment.start.xMeters + offsetX,
                            segment.start.yMeters + offsetY),
                    new GeoPoint(
                            segment.end.xMeters + offsetX,
                            segment.end.yMeters + offsetY));

        }).collect(Collectors.toList());
        polyline = new GeoPolyline(segmentList);

        // set color
        if (isForwardLane()) {
            // forward side
            int strength = 150 + (20 * getRoad().getForwardSide().getLanes().indexOf(this)) % 105;
            color = new Color(strength, strength, 40);
        } else {
            int strength = 150 + (20 * getRoad().getBackwardSide().getLanes().indexOf(this)) % 105;
            color = new Color(strength, strength, strength);
        }
    }

    /**
     * Gets the width of the lane
     *
     * @return Width in meters
     */
    public double getWidth() {
        return this.roadSpecs.width;
    }

    /**
     * Gets the length of the lane
     *
     * @return Length in meters
     */
    public double getLength() {
        return this.roadSpecs.length;
    }

    @Override
    public String toHTMLString() {
        return toString();
    }

    /**
     * Gets the Lane's ID
     *
     * @return ID tag for the lane
     */
    public ID getID() {
        return super.getID();
    }

    /**
     * Gets the Road ID tag the lane belongs to
     *
     * @return Road's ID tag
     */
    public ID getRoadID() {
        return this.lanes.getRoad().getID();
    }

    /**
     * Get directed lanes for that lane.
     *
     * @return directed lanes this one belongs to
     */
    public DirectedLanes getLanes() {
        return lanes;
    }

    /**
     * Gets the Parent road it belongs to
     *
     * @return Road
     */
    public Road getRoad() {
        return this.lanes.getRoad();
    }

    /**
     * Gets the next link in the direction of the lane
     *
     * @return Next link
     */
    public Link getNextLink() {
        return this.nextLink;
    }

    /**
     * Gets the previous link in the direction of the lane
     *
     * @return Previous link
     */
    public Link getPreviousLink() {
        return this.previousLink;
    }

    /**
     * Connects the directed end of the lane to a link
     *
     * @param link Link to connectNext to
     */
    public void connectNext(Link link) {
        this.nextLink = link;
    }

    /**
     * Connects the directed beginning of the lane to a link
     *
     * @param link Link to connectPrevious to
     */
    public void connectPrevious(Link link) {
        this.previousLink = link;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {

    }

    /**
     * {@inheritDoc
     */
    @Override
    public String toString() {
        return String.format("{Lane in %s}", lanes);
    }

    /**
     * Try to get a lane which is ot the outer side of current one.
     *
     * @return null if none, {@link Lane} if there is one to the outer side.
     */
    public Lane tryGetLaneToOuterSide() {
        if (getLanes().getNumberOfLanes() > indexInDirectedLanes + 1) {
            return getLanes().getLanes().get(indexInDirectedLanes + 1);
        }
        return null;
    }

    /**
     * Try to get lane which is to the inner side of the road.
     *
     * @return null if none, {@link Lane} if there is one to the left (in UK).
     */
    public Lane tryGetLaneToInnerSide() {
        if (indexInDirectedLanes - 1 >= 0) {
            return getLanes().getLanes().get(indexInDirectedLanes - 1);
        }
        return null;
    }

    /**
     * According to the road direction, is this lane forward lane?
     *
     * @return true if corresponding to the road direction.
     */
    public boolean isForwardLane() {
        return getRoad().getForwardSide().getLanes().contains(this);
    }

    /**
     * True if according to the road direction, this one goes reverse.
     *
     * @return true if the direction of lane is opposite to the road direction.
     */
    public boolean isBackwardLane() {
        return getRoad().getBackwardSide().getLanes().contains(this);
    }

    /**
     * Add a vehicle to the lane.
     *
     * @param v vehicle to add.
     */
    public void addVehicle(Vehicle v) {
        carsOnThisLane.add(v);
    }

    /**
     * Remove vehicle from the lane, ex. when it goes to junction or traffic receiver.
     *
     * @param v vehicle to remove.
     */
    public void removeVehicle(Vehicle v) {
        carsOnThisLane.remove(v);
    }

    /**
     * Given start and end parameters, which are distance from lane start, give a subset of vehicles belonging to this
     * lane which fall in between start and end.
     * <p>
     * One can mix and swap start and end.
     *
     * @param start segment start
     * @param end   segment end
     * @return set of vehicles on that segment.
     */
    public Collection<Vehicle> getVehiclesAtSegment(double start, double end) {
        // just in case the start and end are swapped, swap them back.
        if (start > end) {
            double t = end;
            end = start;
            start = t;
        }
        final double s = start;
        final double e = end;
        return carsOnThisLane
                .stream()
                .filter(vehicle -> vehicle.getPositionOnLane() < e && vehicle.getPositionOnLane() > s)
                .collect(Collectors.toSet());
    }

    /**
     * For a vehicle, check if it is clear ahead for a specific amount of meters.
     *
     * @param position    vehicle position.
     * @param metersAhead how far ahead to look.
     * @return true if there is no other vehicle
     */
    public boolean isClearAhead(double position, double metersAhead) {
        double endSegment = position + (isForwardLane() ? 1 : -1) * metersAhead;
        return getVehiclesAtSegment(position, endSegment).isEmpty();
    }

    /**
     * Look back, check if it is clear behind there for a specific amount of meters.
     *
     * @param position     vehicle position.
     * @param metersBehind meters to check behind
     * @return true if no one is there behind.
     */
    public boolean isClearBehind(double position, double metersBehind) {
        double endSegment = position + (isForwardLane() ? -1 : 1) * metersBehind;
        return getVehiclesAtSegment(position, endSegment).isEmpty();
    }
}
