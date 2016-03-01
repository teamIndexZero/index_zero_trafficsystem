package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.simulator.data.links.LinkType;

/**
 * Created by Es on 01/03/2016.
 */
public class LinkDescription {
    ID fromID;
    ID toID;
    LinkType type;

    /**
     * Constructor
     *
     * @param from From ID tag
     * @param to   To ID tag
     * @param type Link type
     */
    LinkDescription(ID from, ID to, LinkType type) {
        this.fromID = from;
        this.toID = to;
        this.type = type;
    }
}
