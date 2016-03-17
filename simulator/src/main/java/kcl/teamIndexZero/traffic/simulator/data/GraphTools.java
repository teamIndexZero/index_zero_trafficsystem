package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.features.DirectedLanes;
import kcl.teamIndexZero.traffic.simulator.data.features.Lane;
import kcl.teamIndexZero.traffic.simulator.exceptions.MapIntegrityException;

import java.util.Collection;

/**
 * Helper tools mostly for Graph creation
 */
public class GraphTools {
    private static Logger_Interface LOG = Logger.getLoggerInstance(GraphTools.class.getSimpleName());

    /**
     * Checks if all or none of the lanes in a directed group within a Road are connected at the front to links
     *
     * @param lanes DirectedLanes group
     * @return Full connection state
     * @throws MapIntegrityException when the lanes in DirectedLanes group have partly implemented links
     */
    public boolean checkFwdLinksPresent(DirectedLanes lanes) throws MapIntegrityException {
        LOG.log_Trace("Checking the connections at the end of ", lanes);
        int link_count = 0;
        for (Lane l : lanes.getLanes()) {
            if (l.getNextLink() != null)
                link_count++;
        }
        if (link_count == 0) {
            LOG.log_Trace("-->X '", lanes.getID(), "' has no next link(s).");
            return false;
        } else if (link_count == lanes.getNumberOfLanes()) {
            return true;
        } else {
            LOG.log_Error("Road '", lanes.getRoad().getID(), "' has group of directed lanes with partly implemented Links (Fwd). ", link_count, "/", lanes.getNumberOfLanes(), " Lanes connected to a link.");
            throw new MapIntegrityException("Road has a group of directed lanes with partly implemented links.");
        }
    }

    /**
     * Checks if all or none of the lanes in a directed group within a Road are connected at the back to links
     *
     * @param lanes DirectedLanes group
     * @return Full connection state
     * @throws MapIntegrityException when the lanes in DirectedLanes group have partly implemented links
     */
    public boolean checkBckLinksPresent(DirectedLanes lanes) throws MapIntegrityException {
        LOG.log_Trace("Checking the connections at the end of ", lanes);
        int link_count = 0;
        for (Lane l : lanes.getLanes()) {
            if (l.getPreviousLink() != null)
                link_count++;
        }
        if (link_count == 0) {
            LOG.log_Trace("X--> '", lanes.getID(), "' has no previous link(s).");
            return false;
        } else if (link_count == lanes.getNumberOfLanes()) {
            return true;
        } else {
            LOG.log_Error("Road '", lanes.getRoad().getID(), "' has group of directed lanes with partly implemented Links (Back). ", link_count, "/", lanes.getNumberOfLanes(), " Lanes connected to a link.");
            throw new MapIntegrityException("Road has a group of directed lanes with partly implemented links.");
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
