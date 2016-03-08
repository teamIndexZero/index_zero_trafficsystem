package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.features.DirectedLanes;
import kcl.teamIndexZero.traffic.simulator.data.features.Lane;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.data.links.LinkType;
import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLight;
import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLightInSet;
import kcl.teamIndexZero.traffic.simulator.exceptions.MapIntegrityException;
import kcl.teamIndexZero.traffic.simulator.exceptions.MissingImplementationException;

import java.util.Collection;

/**
 * Helper tools mostly for Graph creation
 */
public class GraphTools {
    private static Logger_Interface LOG = Logger.getLoggerInstance(GraphTools.class.getSimpleName());

    /**
     * Checks of all or none of the lanes in a directed group within a Road are connected to links
     *
     * @param lanes DirectedLanes group
     * @return Full connection state
     * @throws MapIntegrityException when the lanes in DirectedLanes group have partly implemented links
     */
    public boolean checkForwardLinks(DirectedLanes lanes) throws MapIntegrityException {
        int link_count = 0;
        for (Lane l : lanes.getLanes()) {
            if (l.getNextLink() != null)
                link_count++;
        }
        if (link_count == 0)
            return false;
        else if (link_count == lanes.getNumberOfLanes())
            return true;
        else {
            LOG.log_Error("Road '", lanes.getRoad().getID(), "' has group of directed lanes with partly implemented Links. ", link_count, "/", lanes.getNumberOfLanes(), " Lanes connected to a link.");
            throw new MapIntegrityException("Road has a group of directed lanes with partly implemented links.");
        }
    }

    /**
     * Creates a specific link of a given type
     *
     * @param type   Type of the link to create
     * @param linkID ID tag of the new link
     * @return New link
     */
    public Link createLink(LinkType type, ID linkID) throws MissingImplementationException {
        switch (type) {
            case GENERIC:
                return new Link(linkID);
            case AUTONOMOUS_TL:
                //TODO maybe add the TrafficLight to the tfcontroller?
                return new TrafficLight(linkID);
            case SYNC_TL:
                //TODO definitely add the TrafficLight to the TFcontroller!
                return new TrafficLightInSet(linkID);
            default:
                LOG.log_Error("LinkType not implemented in .createLink(..)!");
                throw new MissingImplementationException("LinkType not implemented!");
        }
    }

    /**
     * Checks if collections is/are empty
     *
     * @param collections Collections to check
     * @return Status: 1+ Collections is empty
     */
    public boolean checkEmpty(Collection<?>... collections) {
        boolean empty_flag = false;
        for (Collection<?> c : collections) {
            if (c.isEmpty()) {
                empty_flag = true;
            }
        }
        return empty_flag;
    }
}
