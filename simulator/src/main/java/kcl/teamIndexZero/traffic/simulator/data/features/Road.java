package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;

/**
 * Created by Es on 28/02/2016.
 */
public class Road extends Feature {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Road.class.getSimpleName());
    private final String name;
    public DirectedLanes rightSide;
    public DirectedLanes leftSide;
    private ID id;
    private RoadSpecs roadSpecs = new RoadSpecs();
    private GeoPolyline polyline;

    /**
     * Constructor
     *
     * @param id              Feature ID tag
     * @param leftLanesCount  Number of ongoing lanes from the start viewpoint of the road
     * @param rightLanesCount Number of upcoming lanes from the start viewpoint of the road
     * @param roadLength      Length of the road
     * @throws IllegalArgumentException when the number of lanes given is less than 1 or the road length is < 0.5
     */
    public Road(ID id, int leftLanesCount, int rightLanesCount, double roadLength, GeoPolyline polyline, String name) throws IllegalArgumentException {
        super(id);
        this.polyline = polyline;
        this.name = name;
        if (leftLanesCount < 1 && rightLanesCount < 1) {
            LOG.log_Error("Total number of lanes given is ", leftLanesCount + rightLanesCount, ".");
            throw new IllegalArgumentException("Number of lanes on road is 0! A road must have at least 1 lane.");
        }

        this.id = id;
        this.roadSpecs.length = roadLength;
        this.rightSide = new DirectedLanes(new ID(id, "R"), rightLanesCount, roadSpecs, this);
        this.leftSide = new DirectedLanes(new ID(id, "L"), leftLanesCount, roadSpecs, this);
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the length of the road
     *
     * @return Length of road
     */
    public double getRoadLength() {
        return this.roadSpecs.length;
    }

    /**
     * Gets the number of lanes on the left from the start view
     *
     * @return Number of left lanes
     */
    public int getLeftLaneCount() {
        return this.leftSide.getNumberOfLanes();
    }

    /**
     * Gets the number of lanes on the right from the start view
     *
     * @return Number of right lanes
     */
    public int getRightLaneCount() {
        return this.rightSide.getNumberOfLanes();
    }

    public DirectedLanes getLeftSide() {
        return leftSide;
    } //FIXME Why when the road contructor creates the lanes automagically?

    public DirectedLanes getRightSide() {
        return rightSide;
    } //FIXME Why when the road contructor creates the lanes automagically?

    public GeoPolyline getPolyline() {
        return polyline;
    }
}
