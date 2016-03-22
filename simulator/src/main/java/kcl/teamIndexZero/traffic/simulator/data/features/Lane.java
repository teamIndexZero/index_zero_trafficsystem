package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoSegment;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;

import java.awt.*;
import java.util.List;
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
        int laneInRoadPosition = getRoad().getForwardSide().getLanes().contains(this)
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
        if (getRoad().getForwardSide().getLanes().contains(this)) {
            // forward side
            int strength = 150 + (20 * getRoad().getForwardSide().getLanes().indexOf(this)) % 105;
            color = new Color(strength, strength, 40);
        } else {
            int strength = 150 + (20 * getRoad().getBackwardSide().getLanes().indexOf(this)) % 105;
            color = new Color(strength, 40, strength);
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
}
