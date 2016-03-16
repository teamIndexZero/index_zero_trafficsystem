package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.GraphTools;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle;
import kcl.teamIndexZero.traffic.simulator.exceptions.MapIntegrityException;

import java.util.ArrayList;
import java.util.List;

/**
 * Traffic Generator Feature that can be linked to dead-ends on the map to make traffic
 */
public class TrafficGenerator extends Feature {
    private static Logger_Interface LOG = Logger.getLoggerInstance(TrafficGenerator.class.getSimpleName());
    private final GeoPoint geoPoint;
    private int creationCounter = 0;
    private int receiptCounter = 0;
    private java.util.List<Link> incoming = new ArrayList<>();
    private java.util.List<Link> outgoing = new ArrayList<>();

    /**
     * Constructor
     *
     * @param id Feature ID tag
     */
    public TrafficGenerator(ID id, GeoPoint geoPoint) {
        super(id);
        this.geoPoint = geoPoint;
    }

    /**
     * Gets the Geo point of the TrafficGenerator
     *
     * @return GeoPoint
     */
    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    /**
     * Links a Road to the TrafficGenerator
     *
     * @param road Road to link
     * @throws MapIntegrityException when the lanes in DirectedLanes group have partly implemented links
     */
    public void linkRoad(Road road) throws MapIntegrityException {
        GraphTools tools = new GraphTools();
        DirectedLanes incoming;
        DirectedLanes outgoing;
        if (!tools.checkFwdLinksPresent(road.getForwardSide())) {
            incoming = road.getForwardSide();
            outgoing = road.getBackwardSide();
        } else if (!tools.checkFwdLinksPresent(road.getBackwardSide())) {
            incoming = road.getBackwardSide();
            outgoing = road.getForwardSide();
        } else {
            LOG.log_Warning("Detected attempt to link a fully linked road ('", road.getID(), "') to a traffic generator ('", this.getID(), "'). Nothing done.");
            return;
        }

        //Linking time!
        for (Lane lane : incoming.getLanes()) { //entering the generator
            ID id = new ID(lane.getID() + "->" + this.getID());
            Link link = new Link(id, road.getPolyline().getStartPoint());
            link.in = lane;
            link.out = this;
            lane.connectNext(link);
            this.incoming.add(link);
            LOG.log("Linked: '", link.getID(), "'.");
        }
        for (Lane lane : outgoing.getLanes()) { //leaving the generator
            ID id = new ID(lane.getID() + "<-" + this.getID());
            Link link = new Link(id, road.getPolyline().getStartPoint());
            link.in = this;
            link.out = lane;
            this.outgoing.add(link);
            LOG.log("Linked: '", link.getID(), "'.");
        }
    }

    /**
     * Gets the list of all incoming links from the TrafficGenerator
     *
     * @return List of incoming links
     */
    public List<Link> getIncomingLinks() {
        return this.incoming;
    }

    /**
     * Gets the list of all outgoing links from the TrafficGenerator
     *
     * @return List of outgoing links
     */
    public List<Link> getOutgoingLinks() {
        return this.outgoing;
    }

    /**
     * Take receipt of incoming vehicle
     *
     * @param vehicle Vehicle to receive into the TrafficGenerator
     */
    public void terminateTravel(Vehicle vehicle) {
        LOG.log("TF[ '", this.getID(), "' ] Vehicle '", vehicle.getName(), "' terminated journey.");
        /*
        //TODO maybe a bit heavy as will be called every time a car exits..
        //super.getMap().getObjectsOnSurface().removeIf(MapObject::isPleaseRemoveMeFromSimulation);
        //TODO or maybe access via ID (means changing the surface object list to a map. quicker access time though and can still iterate)
        this.receiptCounter++;
        */
    }

    /**
     * Gets a random lane to place a vehicle onto
     *
     * @return Random Lane
     */
    public Lane getRandomLane() {
        return (Lane) outgoing.get((int) (Math.random() * outgoing.size() - 1)).out;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
        if (this.getOutgoingLinks().size() > 0) {
            if (Math.random() > 0.04) {
                return;
            }
            Vehicle v = new Vehicle("Vehicle " + creationCounter++, getRandomLane());
            super.getMap().addMapObject(v);
            LOG.log_Trace("TrafficGenerator '", this.getID(), "' created '", v.getName(), "'.");
        }
    }

    /**
     * toString method
     *
     * @return Description of the TrafficGenerator state
     */
    public String toString() {
        String in = this.getIncomingLinks().size() > 0 ? "IN: " + Integer.toString(this.receiptCounter) : "IN: -";
        String out = this.getOutgoingLinks().size() > 0 ? "OUT: " + Integer.toString(this.creationCounter) : "OUT: -";
        return this.getID() + "[" + in + ", " + out + "]";
    }
}
