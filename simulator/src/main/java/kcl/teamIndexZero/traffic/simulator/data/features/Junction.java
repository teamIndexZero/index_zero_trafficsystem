package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoSegment;
import kcl.teamIndexZero.traffic.simulator.data.links.JunctionLink;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.data.trafficBehaviour.TrafficBehaviour;
import kcl.teamIndexZero.traffic.simulator.exceptions.AlreadyExistsException;
import kcl.teamIndexZero.traffic.simulator.exceptions.JunctionPathException;

import java.util.*;

/**
 * Junction feature that connects roads and their links to direct traffic into each others
 * Usage: Add all roads connected to the junction with {@link #addRoad(Road road, JunctionDescription.RoadDirection direction) addRoad}
 * then compute the paths with {@link #computeAllPaths() computeAllPaths}
 */
public class Junction extends Feature {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Junction.class.getSimpleName());
    private final GeoPoint geoPoint;
    private TrafficBehaviour behaviour;
    private Map<Feature, JunctionDescription.RoadDirection> connectedFeatures;
    private Map<ID, JunctionLink> inflowLinks;
    private Map<ID, JunctionLink> outflowLinks;
    private boolean trafficLight_flag;
    private int usage = 0;

    /**
     * Constructor
     *
     * @param id                    Feature ID tag
     * @param requiresTrafficLights States whether the junction uses traffic lights or not
     * @param geoPoint              GeoPoint of the Junction
     */
    public Junction(ID id, boolean requiresTrafficLights, GeoPoint geoPoint) {
        super(id);
        this.geoPoint = geoPoint;
        this.connectedFeatures = new HashMap<>();
        this.inflowLinks = new HashMap<>();
        this.outflowLinks = new HashMap<>();
        this.trafficLight_flag = requiresTrafficLights;
        this.behaviour = new TrafficBehaviour(this);
        LOG.log("Junction '", this.getID(), "' created.");
    }

    /**
     * Gets the GeoPoint for the Junction
     *
     * @return GeoPoint
     */
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
        if (connectedFeatures.keySet().contains(road)) {
            LOG.log_Error("Road '", road.getName(), "' (ID: ", road.getID(), ") already exists in the list of connected features.");
            throw new AlreadyExistsException("Road already in connected features of Junction.");
        }
        connectedFeatures.put(road, direction);
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
     * Adds a TrafficGenerator to the junction
     *
     * @param tg TrafficGenerator to add
     */
    public void addTrafficGenerator(TrafficGenerator tg) {
        List<Link> tgOutLinks = tg.getOutgoingLinks();
        List<Link> tgInLinks = tg.getIncomingLinks();
        tgOutLinks.forEach(link -> {
            if (link instanceof JunctionLink)
                this.addInflowLink((JunctionLink) link);
            else
                LOG.log_Error("Trying to add a Link ('", link.getID(), "') to the Junction '", this.getID(), "' from a TrafficGenerator ('", tg.getID(), "') - Link not a JunctionLink!");
        });
        tgInLinks.forEach(link -> {
            if (link instanceof JunctionLink)
                this.addOutflowLink((JunctionLink) link);
            else
                LOG.log_Error("Trying to add a Link ('", link.getID(), "') to the Junction '", this.getID(), "' from a TrafficGenerator ('", tg.getID(), "') - Link not a JunctionLink!");
        });
        this.connectedFeatures.put(tg, JunctionDescription.RoadDirection.OUTGOING);
    }

    /**
     * Adds an inflow link to the junction
     *
     * @param link Inflow link
     */
    private void addInflowLink(JunctionLink link) {
        LOG.log_Trace("Adding inflow link '", link.getID(), "'.");
        this.inflowLinks.put(link.getID(), link);
    }

    /**
     * Adds an outflow link to the junction
     *
     * @param link Outflow link
     */
    private void addOutflowLink(JunctionLink link) {
        LOG.log_Trace("Adding outflow link '", link.getID(), "'.");
        this.outflowLinks.put(link.getID(), link);
    }


    /**
     * From the current list of Links attached to the junction, computes paths.
     * Note: paths doing U-Turns on the same road are *not* added
     */
    public void computeAllPaths() {
        behaviour.clearAllPaths();
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
    public Collection<Feature> getConnectedFeatures() {
        return this.connectedFeatures.keySet();
    }

    /**
     * Gets the bearing for a feature - that is, on which angle it connects to a junction.
     *
     * @return incoming or outgoing.
     */
    public double getBearingForFeature(Feature f) {
        JunctionDescription.RoadDirection direction = connectedFeatures.get(f);
        if (!(f instanceof Road)) {
            return 0;
        }
        Road r = (Road) f;
        if (direction == JunctionDescription.RoadDirection.INCOMING) {
            // reverse the last segment since we are incoming.
            GeoSegment lastSegment = r.getPolyline().getLastSegment();
            return new GeoSegment(lastSegment.end, lastSegment.start).getAngleToEastRadians();
        } else {
            return r.getPolyline().getFirstSegment().getAngleToEastRadians();
        }
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
     * Gets a single random exit point for an inflow link
     *
     * @param inflowLinkID ID of the inflow link
     * @return Single random link
     * @throws JunctionPathException when a path problem is raised
     */
    public Link getRandomLink(ID inflowLinkID) throws JunctionPathException {
        List<Link> links = getNextLinks(inflowLinkID);
        return links.get((int) (Math.random() * links.size() - 1));
    }

    /**
     * Gets a list of all inflow links currently on the junction
     *
     * @return List of inflow links
     */
    public List<JunctionLink> getInflowLinks() {
        return Collections.unmodifiableList(new ArrayList<>(this.inflowLinks.values()));
    }

    /**
     * Gets a list of all outflow links currently on the junction
     *
     * @return List of outflow links
     */
    public List<JunctionLink> getOutflowLinks() {
        return Collections.unmodifiableList(new ArrayList<>(this.outflowLinks.values()));
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

    /**
     * Check if this junction is a dead end - i.e. if there is no way to go somewhere after it is there.
     *
     * @return true if no outbound links.
     */
    public boolean isDeadEnd() {
        return outflowLinks.size() == 0;
    }
}
