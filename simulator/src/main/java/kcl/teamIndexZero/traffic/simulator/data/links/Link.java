package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.Lane;

public class Link implements ISimulationAware {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Link.class.getSimpleName());
    public Lane in;
    public Lane out;
    private ID id;

    /**
     * Constructor
     *
     * @param id Link ID tag
     */
    public Link(ID id) {
        this.id = id;
    }

    /**
     * Get the Link's ID tag
     *
     * @return ID tag
     */
    public ID getID() {
        return this.id;
    }

    /**
     * Checks of the link is connected to only in feature
     *
     * @return Dead end state
     */
    public boolean isDeadEnd() {
        return (in == null || out == null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Link)) return false;

        Link link = (Link) o;

        if (in != null ? !in.equals(link.in) : link.in != null) return false;
        if (out != null ? !out.equals(link.out) : link.out != null) return false;
        return id != null ? id.equals(link.id) : link.id == null;
    }

    @Override
    public int hashCode() {
        int result = in != null ? in.hashCode() : 0;
        result = 31 * result + (out != null ? out.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void tick(SimulationTick tick) {
    }
}
