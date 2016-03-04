package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;

import java.util.ArrayList;
import java.util.List;

/**
 * This is all information we can actually get from OSM.
 */
public class JunctionDescription {
    private ID id;
    private List<ID> connectedRoadIDs = new ArrayList<>();
    private boolean trafficLightFlag;

    /**
     * Constructor
     *
     * @param id                Junction's ID tag
     * @param connectedRoadIDs  Connected road IDs
     * @param trafficLight_flag Traffic light on the junction flag
     */
    public JunctionDescription(ID id, List<ID> connectedRoadIDs, boolean trafficLight_flag) {
        this.id = id;
        this.connectedRoadIDs = connectedRoadIDs;
        this.trafficLightFlag = trafficLight_flag;
    }

    /**
     * Gets the Junction's ID tag
     *
     * @return ID tag
     */
    public ID getID() {
        return this.id;
    }

    /**
     * Gets the ID list of connected roads
     *
     * @return List of IDs
     */
    public List<ID> getConnectedIDs() {
        return this.connectedRoadIDs;
    }

    /**
     * Gets the traffic light flag on the junciton
     *
     * @return Traffic light flag
     */
    public boolean hasTrafficLight() {
        return this.trafficLightFlag;
    }
}
