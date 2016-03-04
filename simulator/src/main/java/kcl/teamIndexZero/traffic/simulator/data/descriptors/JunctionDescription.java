package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the all information we can actually get from OSM.
 */
public class JunctionDescription {
    private ID id;
    private List<ID> connectedRoadIDs = new ArrayList<>();
    private boolean hasTrafficLight;
}
