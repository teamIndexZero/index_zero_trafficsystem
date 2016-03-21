package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.links.LinkType;

/**
 * Class to contain the specifications of a link
 */
public class LinkDescription {
    public ID fromID;
    public ID toID;
    public LinkType type;
    public ID linkID;

    /**
     * Constructor
     *
     * @param from   From ID tag
     * @param to     To ID tag
     * @param type   Link type
     * @param linkID Link's ID tag
     */
    public LinkDescription(ID from, ID to, LinkType type, ID linkID) {
        this.fromID = from;
        this.toID = to;
        this.type = type;
        this.linkID = linkID;
    }

    /**
     * toString method
     *
     * @return String with the LinkDescription's info
     */
    public String toString() {
        return this.linkID + "[ " + fromID + " -> " + toID + " ]";
    }
}
