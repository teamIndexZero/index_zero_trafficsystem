package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.ID;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Es on 01/03/2016.
 */
public class DirectedLanes {
    private static Logger_Interface LOG = Logger.getLoggerInstance(DirectedLanes.class.getSimpleName());
    private ID id;
    List<Lane> lanes = new ArrayList<>();
    RoadSpecs roadSpecs;
    private final Road road;

    /**
     * Constructor
     *
     * @param id              ID tag
     * @param number_of_lanes Number of lanes on this direction
     * @param road_specs      Road specifications
     */
    public DirectedLanes(ID id, int number_of_lanes, RoadSpecs road_specs, Road road) {
        this.id = id;
        this.roadSpecs = road_specs;
        this.road = road;
        for (int i = 0; i < number_of_lanes; i++) {
            lanes.add(new Lane(new ID(id, Integer.toString(i)), roadSpecs, this));
        }
    }

    /**
     * Gets the number of lanes in the direction
     *
     * @return Number of lanes
     */
    public int getNumberOfLanes() {
        return this.lanes.size();
    }

    /**
     * Gets a particular lane
     *
     * @param lane_index Index of the lane
     * @return Lane
     * @throws ArrayIndexOutOfBoundsException when trying to access a non existent lane in the list
     */
    public ID getLaneID(int lane_index) throws ArrayIndexOutOfBoundsException {
        if (lane_index >= this.lanes.size()) {
            LOG.log_Error("Lane index '", lane_index, "' is not in the lanes available ('", this.lanes.size(), "').");
            throw new ArrayIndexOutOfBoundsException("Lane index not in group of Lanes.");
        }
        return lanes.get(lane_index).getID();
    }

    /**
     * Gets the road the directed lanes belong to
     *
     * @return Parent road
     */
    public Road getRoad() {
        return road;
    }

    /**
     * Gets the lanes inside the group of directed lanes
     *
     * @return List of lanes
     */
    public List<Lane> getLanes() {
        return lanes;
    }
}
