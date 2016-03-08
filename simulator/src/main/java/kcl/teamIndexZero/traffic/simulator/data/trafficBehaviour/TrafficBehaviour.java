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
    private Map<ID, Integer> outflowPathIndexes; //keep track of the current out link to use in the list for an inflow link
    private Map<ID, TrafficQuota> outflowLinksQuotas;


    /**
     * Constructor
     */
    public TrafficBehaviour(Junction junction) {
        this.junction = junction;
        this.outflowPaths = new HashMap<>();
        this.outflowLinksQuotas = new HashMap<>();
        this.outflowPathIndexes = new HashMap<>();
    }

    /**
     * Maps a traffic shaping weight value to a link
     *
     * @param linkID Link to add a weight to
     * @param weight Traffic shaping weight
     */
    public void addLinkWeight(ID linkID, int weight) {
        outflowLinksQuotas.putIfAbsent(linkID, new TrafficQuota(weight));
    }

    /**
     * Adds a viable path from a junction link to another
     *
     * @param originLinkID      Link of origin
     * @param destinationLinkID Destination link
     */
    public void addPath(ID originLinkID, ID destinationLinkID) {
        outflowPaths.putIfAbsent(originLinkID, new LinkedList<>());
        if (outflowPaths.get(originLinkID).isEmpty()) {
            outflowPathIndexes.putIfAbsent(originLinkID, 0);
        }
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
     * [DEPRECIATED] Gets the ID of the next outflow link for a inflow link based on a weighted round-robin distribution
     *
     * @param from Inflow link's ID tag
     * @return Outflow ID
     * @throws JunctionPathException when the inflow link doesn't exist or there are no exit paths available for an inflow link
     */
    public ID getNextDestinationLinkID(ID from) throws JunctionPathException {
        if (!this.outflowPaths.containsKey(from)) {
            LOG.log_Error("Inflow Link ID '", from, "' does not exist in the TrafficBehaviour's path map.");
            throw new JunctionPathException("Inflow Link ID does not exist in the TrafficBehaviour's path map.");
        }
        if (this.outflowPaths.get(from).isEmpty()) {
            LOG.log_Error("There are no exit path available for '", from, "'.");
            throw new JunctionPathException("No exit path(s) available for inflow link.");
        }
        List<ID> outflowLinks = this.outflowPaths.get(from);
        int outListIndex = this.outflowPathIndexes.get(from);
        int outputPathsChecked = 0;
        //Iterate through the list of outflow links until a unfulfilled quota is found
        while (outputPathsChecked < outflowLinks.size()) {
            if (this.outflowLinksQuotas.get(outflowLinks.get(outListIndex)).incrementCounter()) {
                this.outflowPathIndexes.put(from, getNextIndex(outListIndex, outflowLinks));
                return outflowLinks.get(outListIndex);
            }
            setIndexOfPathList(from, getNextIndex(outListIndex, outflowLinks));
            outputPathsChecked++;
        } //Done a full round of the list
        resetAllQuotasInPaths(from);
        outListIndex = this.outflowPathIndexes.get(from);
        this.outflowLinksQuotas.get(outflowLinks.get(outListIndex)).incrementCounter();
        setIndexOfPathList(from, getNextIndex(outListIndex, outflowLinks));
        return outflowLinks.get(outListIndex);
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
     * Resets all quotas for an inflow link's outflow path links
     *
     * @param inflowLinkID Inflow link
     */
    private void resetAllQuotasInPaths(ID inflowLinkID) {
        for (ID id : this.outflowPaths.get(inflowLinkID)) {
            this.outflowLinksQuotas.get(id).reset();
        }
    }

    /**
     * Sets the current index of the outflow path List of a link
     *
     * @param inflowLinkID Inflow link ID tag
     * @param index        Index to set
     */
    private void setIndexOfPathList(ID inflowLinkID, int index) {
        this.outflowPathIndexes.put(inflowLinkID, index);
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
        String quotas = this.outflowLinksQuotas.entrySet().toString();
        String s = "TrafficBehaviour for <" + getJunctionID() + "> { Paths:" + paths + ", Quotas:" + quotas + " }";
        return s;
    }
}