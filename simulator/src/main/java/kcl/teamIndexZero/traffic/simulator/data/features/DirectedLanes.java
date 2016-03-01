package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.simulator.data.ID;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Es on 01/03/2016.
 */
public class DirectedLanes {
    private ID id;
    List<Lane> lanes = new ArrayList<>();
    RoadSpecs roadSpecs;
    private int numberOfLanes;

    /**
     * Constructor
     */
    public DirectedLanes(ID id) {
        this.id = id;
        numberOfLanes = 0;
    }

    /**
     * Constructor
     *
     * @param id              ID tag
     * @param number_of_lanes Number of lanes on this direction
     * @param road_specs      Road specifications
     */
    public DirectedLanes(ID id, int number_of_lanes, RoadSpecs road_specs) {
        this.id = id;
        this.roadSpecs = road_specs;
        for (int i = 0; i < number_of_lanes; i++) {
            lanes.add(new Lane(new ID(this.id, Integer.toString(i)), roadSpecs));
        }
    }

    /**
     * Gets the number of lanes in the direction
     *
     * @return Number of lanes
     */
    public int getNumberOfLanes() {
        return numberOfLanes;
    }
}
