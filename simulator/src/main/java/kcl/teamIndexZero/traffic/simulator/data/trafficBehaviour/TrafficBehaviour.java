package kcl.teamIndexZero.traffic.simulator.data.trafficBehaviour;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.features.Junction;
import kcl.teamIndexZero.traffic.simulator.exceptions.JunctionPathException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Traffic Behaviour class describing the inner flow of a Junction
 */
public class TrafficBehaviour {
    private static Logger_Interface LOG = Logger.getLoggerInstance(TrafficBehaviour.class.getSimpleName());
    private Junction junction;
    private Map<ID, List<ID>> outflowPaths;

    /**
     * Constructor
     *
     * @param junction a junction this behavior controls
     */
    public TrafficBehaviour(Junction junction) {
        this.junction = junction;
        this.outflowPaths = new HashMap<>();
    }

    /**
     * Adds a viable path from a junction link to another
     *
     * @param originLinkID      Link of origin
     * @param destinationLinkID Destination link
     */
    public void addPath(ID originLinkID, ID destinationLinkID) {
        outflowPaths.putIfAbsent(originLinkID, new LinkedList<>());
        outflowPaths.get(originLinkID).add(destinationLinkID);
    }

    /**
     * Checks if a path exists between an inflow and ouflow link
     *
     * @param originLinkID      Inflow link ID tag
     * @param destinationLinkID Outflow link ID tag
     * @return Path existence
     */
    public boolean pathExists(ID originLinkID, ID destinationLinkID) {
        return this.outflowPaths.containsKey(originLinkID) && this.outflowPaths.get(originLinkID).contains(destinationLinkID);
    }

    /**
     * Gets all possible exit Link IDs for a entry link
     *
     * @param from Inflow link's IF tag
     * @return List of exit Link ID tag
     * @throws JunctionPathException when the inflow link doesn't exist or there are no exit paths available for an inflow link
     */
    public List<ID> getAllPossibleExitPoints(ID from) throws JunctionPathException {
        if (!this.outflowPaths.containsKey(from)) {
            LOG.log_Error("Inflow Link ID '", from, "' does not exist in the TrafficBehaviour's path map.");
            throw new JunctionPathException("Inflow Link ID does not exist in the TrafficBehaviour's path map.");
        }
        if (this.outflowPaths.get(from).isEmpty()) {
            LOG.log_Error("There are no exit path available for '", from, "'.");
            throw new JunctionPathException("No exit path(s) available for inflow link.");
        }
        return this.outflowPaths.get(from);
    }

    /**
     * Clears all paths
     */
    public void clearAllPaths() {
        this.outflowPaths.clear();
    }

    /**
     * Gets the ID of the junction TrafficBehaviour is associated with
     *
     * @return Junction ID tag
     */
    public ID getJunctionID() {
        return junction.getID();
    }

    /**
     * Gets the junction that the TrafficBehaviour is associated with
     *
     * @return Associated Junction
     */
    public Junction getJunction() {
        return this.junction;
    }

    /**
     * Gets the next index in a list (circular iteration)
     *
     * @param currentIndex Current index on the list
     * @param list         List to iterate across
     * @return Next index in the loop
     */
    private int getNextIndex(int currentIndex, List<ID> list) {
        if (currentIndex < list.size() - 1) return ++currentIndex;
        else return 0;
    }

    /**
     * toString method
     *
     * @return String describing the TrafficBehaviour object
     */
    public String toString() {
        String paths = this.outflowPaths.entrySet().toString();
        return "TrafficBehaviour for <" + getJunctionID() + "> { Paths:" + paths + " }";
    }
}