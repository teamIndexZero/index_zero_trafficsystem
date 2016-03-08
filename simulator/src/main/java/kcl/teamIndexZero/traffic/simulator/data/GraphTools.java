package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.features.DirectedLanes;
import kcl.teamIndexZero.traffic.simulator.data.features.Lane;

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
     */
    public boolean checkForwardLinks(DirectedLanes lanes) {
        int link_count = 0;
        for (Lane l : lanes.getLanes()) {
            if (l.getNextLink() != null)
                link_count++;
        }
        if (link_count == lanes.getNumberOfLanes())
            return true;
        if (link_count != 0)
            LOG.log_Error("Road '", lanes.getRoad().getID(), "' has group of directed lanes with partly implemented Links. ", link_count, "/", lanes.getNumberOfLanes(), " Lanes connected to a link.");
        return false;
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
