package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.Feature;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;

public class Link implements ISimulationAware {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Link.class.getSimpleName());
    public Feature in;
    public Feature out;
    private ID id;
    private GeoPoint geoPoint;
    //TODO add private traffic lights + shebang when module is available

    /**
     * Constructor
     *
     * @param id Link ID tag
     */
    public Link(ID id, GeoPoint geoPoint) {
        this.id = id;
        this.geoPoint = geoPoint;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
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

    /**
     * Gets the next feature in the direction
     *
     * @return Next Feature
     */
    public Feature getNextFeature() {
        return this.out;
    }

    /**
     * Gets the previous feature in the direction
     *
     * @return Previous feature
     */
    public Feature getPreviousFeature() {
        return this.in;
    }

    /**
     * Checks if two Link objects are the same
     *
     * @param o Object to compare to
     * @return Equality
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Link)) return false;

        Link link = (Link) o;

        if (in != null ? !in.equals(link.in) : link.in != null) return false;
        if (out != null ? !out.equals(link.out) : link.out != null) return false;
        return id.equals(link.id);

    }

    /**
     * Hashcode function for the Link object
     *
     * @return Hashcode
     */
    @Override
    public int hashCode() {
        int result = in != null ? in.hashCode() : 0;
        result = 31 * result + (out != null ? out.hashCode() : 0);
        result = 31 * result + id.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
    }
}
