package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;

import java.util.HashMap;
import java.util.Map;

/**
 * This is all information we can actually get from OSM.
 */
public class JunctionDescription {
    private ID id;
    private Map<ID, RoadDirection> connectedRoadIDs = new HashMap<>();
    private boolean trafficLightFlag;
    private GeoPoint geoPoint;

    /**
     * Constructor
     *
     * @param id                Junction's ID tag
     * @param connectedRoadIDs  Connected road IDs
     * @param trafficLight_flag Traffic light on the junction flag
     * @param geoPoint          a geographical location of the center of junction
     */
    public JunctionDescription(ID id, Map<ID, RoadDirection> connectedRoadIDs, boolean trafficLight_flag, GeoPoint geoPoint) {
        this.id = id;
        this.connectedRoadIDs = connectedRoadIDs;
        this.trafficLightFlag = trafficLight_flag;
        this.geoPoint = geoPoint;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
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

    /**
     * Enum for the direction of the road to be connected to the junciton
     */
    public enum RoadDirection {
        INCOMING,
        OUTGOING
    }
}
