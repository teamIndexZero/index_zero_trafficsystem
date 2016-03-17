package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.links.JunctionLink;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.data.trafficBehaviour.TrafficBehaviour;
import kcl.teamIndexZero.traffic.simulator.exceptions.AlreadyExistsException;
import kcl.teamIndexZero.traffic.simulator.exceptions.JunctionPathException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Junction feature that connects roads and their links to direct traffic into each others
 * Usage: Add all roads connected to the junction with {@link #addRoad(Road road, JunctionDescription.RoadDirection direction) addRoad}
 * then compute the paths with {@link #computeAllPaths() computeAllPaths}
 */
public class Junction extends Feature {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Junction.class.getSimpleName());
    private final GeoPoint geoPoint;
    private TrafficBehaviour behaviour;
    private List<Feature> connectedFeatures;
    private Map<ID, JunctionLink> inflowLinks;
    private Map<ID, JunctionLink> outflowLinks;
    private boolean trafficLight_flag;
    private int usage = 0;

    /**
     * Constructor
     *
     * @param id                    Feature ID tag
     * @param requiresTrafficLights States whether the junction uses traffic lights or not
     */
    public Junction(ID id, boolean requiresTrafficLights, GeoPoint geoPoint) {
        super(id);
        this.geoPoint = geoPoint;
        this.connectedFeatures = new ArrayList<>();
        this.inflowLinks = new HashMap<>();
        this.outflowLinks = new HashMap<>();
        this.trafficLight_flag = requiresTrafficLights;
        this.behaviour = new TrafficBehaviour(this);
        LOG.log("Junction '", this.getID(), "' created.");
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    /**
     * Connects a road to the junction
     *
     * @param road      Road to connectNext
     * @param direction Direction of the road at connection point with the junction
     */
    public void addRoad(Road road, JunctionDescription.RoadDirection direction) throws AlreadyExistsException {
        if (connectedFeatures.contains(road)) {
            LOG.log_Error("Road '", road.getName(), "' (ID: ", road.getID(), ") already exists in the list of connected features.");
            throw new AlreadyExistsException("Road already in connected features of Junction.");
        }
        connectedFeatures.add(road);
        List<Lane> incoming;
        List<Lane> outgoing;
        if (direction == JunctionDescription.RoadDirection.INCOMING) {
            LOG.log("Adding road '", road.getName(), "' (ID: ", road.getID(), ") to Junction '", this.getID(), "' - INCOMING direction.");
            incoming = road.getForwardSide().getLanes();
            outgoing = road.getBackwardSide().getLanes();
        } else {
            LOG.log_Debug("Adding road '", road.getName(), "' (ID: ", road.getID(), ") to Junction '", this.getID(), "' - OUTGOING direction.");
            incoming = road.getBackwardSide().getLanes();
            outgoing = road.getForwardSide().getLanes();
        }
        if (!incoming.isEmpty()) {
            for (Lane l : incoming) {
                ID link_ID = new ID(this.getID() + "<-" + l.getID());
                if (!inflowLinks.containsKey(link_ID)) {
                    LOG.log("Adding inflow link '", link_ID, "' to Junction.");
                    JunctionLink link = new JunctionLink(link_ID, road, this, geoPoint, JunctionLink.LinkType.INFLOW);
                    link.in = l;
                    link.out = this;
                    l.connectNext(link);
                    inflowLinks.put(link_ID, link);
                }
                if (outflowLinks.containsKey(link_ID))
                    LOG.log_Warning("Inflow link '", link_ID, "' already exists in Junction as an outflow link.");
            }
        }
        if (!outgoing.isEmpty()) {
            for (Lane l : outgoing) {
                ID link_ID = new ID(this.getID() + "->" + l.getID());
                if (!outflowLinks.containsKey(link_ID)) {
                    LOG.log("Adding outflow link '", link_ID, "' to Junction.");
                    JunctionLink link = new JunctionLink(link_ID, road, this, geoPoint, JunctionLink.LinkType.OUTFLOW);
                    link.in = this;
                    link.out = l;
                    l.connectPrevious(link);
                    outflowLinks.put(link_ID, link);
                }
                if (inflowLinks.containsKey(link_ID))
                    LOG.log_Warning("Outflow link '", link_ID, "' already exists in Junction as an inflow link.");
            }
        }
    }

    /**
     * Adds a TrafficGenerator for receiving traffic at the junction
     *
     * @return The inflow link created
     */
    public JunctionLink addTrafficGenerator(ID id) {
        TrafficGenerator tg = new TrafficGenerator(id, this.getGeoPoint());
        this.connectedFeatures.add(tg);
        ID link_ID = new ID(this.getID() + "->" + tg.getID());
        JunctionLink link = new JunctionLink(link_ID, tg, this, geoPoint, JunctionLink.LinkType.OUTFLOW);
        link.in = this;
        link.out = tg;
        tg.addJunctionLinks(link);
        return link;
    }

    /**
     * From the current list of Links attached to the junction, computes paths.
     * Note: paths doing U-Turns on the same road are *not* added
     */
    public void computeAllPaths() {
        inflowLinks.values().forEach((in) ->
                outflowLinks.values().forEach((out) -> {
                    if (in.getRoadID() != out.getRoadID()) {
                        LOG.log_Debug("Creating Path {", in.getID(), " --> ", out.getID(), "}");
                        behaviour.addPath(in.getID(), out.getID());
                    }
                })
        );
    }

    /**
     * Gets the Features connected to the junction
     *
     * @return List of connected Features
     */
    public List<Feature> getConnectedFeatures() {
        return this.connectedFeatures;
    }

    /**
     * Gets all the possible exit point for an inflow link
     *
     * @param inflowLinkID ID of the inflow link
     * @return List of possible exit points
     * @throws JunctionPathException when a path problem is raised
     */
    public List<Link> getNextLinks(ID inflowLinkID) throws JunctionPathException {
        List<Link> links = new ArrayList<>();
        try {
            behaviour.getAllPossibleExitPoints(inflowLinkID).forEach(
                    (outID) -> {
                        links.add(this.outflowLinks.get(outID));
                    }
            );
            return links;
        } catch (JunctionPathException e) {
            LOG.log_Error("Junction[ ", this.getID(), " ] Either the inflow link ID doesn't exist or the there are no outflow links bound to it.");
            throw new JunctionPathException("Either the inflow link ID doesn't exist or the there are no outflow links bound to it.", e);
        }
    }

    /**
     * Gets the number of inflow links
     *
     * @return Inflow link count
     */
    public int getInflowCount() {
        return this.inflowLinks.size();
    }

    /**
     * Gets the number of outflow links
     *
     * @return Outflow link count
     */
    public int getOutflowCount() {
        return this.outflowLinks.size();
    }

    /**
     * Gets the Traffic Light flag for the junction
     *
     * @return Traffic light usage
     */
    public boolean hasTrafficLights() {
        return this.trafficLight_flag;
    }

    /**
     * Add one to the usage profile - basically car counter.
     */
    public void incrementUsage() {
        this.usage++;
    }

    /**
     * Get amount of cars passed through this junction.
     *
     * @return usage.
     */
    public int getUsage() {
        return usage;
    }

    /**
     * toString method
     *
     * @return Description of the junction
     */
    public String toString() {
        return this.getID() + "{ features: " + this.connectedFeatures.size() + " | "
                + ", inflow: " + this.inflowLinks.size() + ", outflow: " + this.outflowLinks.size() + " }";
    }
}
