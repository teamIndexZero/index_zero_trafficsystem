package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is all information we can actually get from OSM.
 */
public class JunctionDescription {
    private ID id;
    private Map<ID, RoadDirection> connectedRoadIDs = new HashMap<>();
    private boolean trafficLightFlag;

    /**
     * Constructor
     *
     * @param id                Junction's ID tag
     * @param connectedRoadIDs  Connected road IDs
     * @param trafficLight_flag Traffic light on the junction flag
     */
    public JunctionDescription(ID id, Map<ID, RoadDirection> connectedRoadIDs, boolean trafficLight_flag) {
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
    public Map<ID, RoadDirection> getConnectedIDs() {
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

    public enum RoadDirection {
        INCOMING,
        OUTGOING
    }
}
