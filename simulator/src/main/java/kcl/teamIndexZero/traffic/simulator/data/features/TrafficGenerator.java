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
    private List<Vehicle> vehiclesToDelete = new ArrayList<>();

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
        GeoPoint placement;
        int fwdCount = road.getForwardLaneCount();
        int bckCount = road.getBackwardLaneCount();
        if ((fwdCount > 0 && !tools.checkFwdLinksPresent(road.getForwardSide()))
                || (bckCount > 0 && !tools.checkBckLinksPresent(road.getBackwardSide()))) { //head
            LOG.log_Trace("TG '", this.getID(), "' will link at the head of the road [FWD: ", road.getForwardLaneCount(), ", BCK: ", road.getBackwardLaneCount(), "]");
            incoming = road.getForwardSide();
            outgoing = road.getBackwardSide();
            placement = road.getPolyline().getFinishPoint();
        } else if ((bckCount > 0 && !tools.checkFwdLinksPresent(road.getBackwardSide()))
                || (fwdCount > 0 && !tools.checkBckLinksPresent(road.getForwardSide()))) { //tail
            LOG.log_Trace("TG '", this.getID(), "' will link at the tail of the road [FWD: ", road.getBackwardLaneCount(), ", BCK: ", road.getForwardLaneCount(), "]");
            incoming = road.getBackwardSide();
            outgoing = road.getForwardSide();
            placement = road.getPolyline().getStartPoint();
        } else {
            LOG.log_Warning("Detected attempt to link a fully linked road ('", road.getID(), "') to a traffic generator ('", this.getID(), "'). Nothing done.");
            return;
        }
        //Linking time!
        for (Lane lane : incoming.getLanes()) { //entering the generator
            ID id = new ID(lane.getID() + "->" + this.getID());
            Link link = new Link(id, placement);
            link.in = lane;
            link.out = this;
            lane.connectNext(link);
            this.incoming.add(link);
            LOG.log("Linked: '", link.getID(), "'.");
        }
        for (Lane lane : outgoing.getLanes()) { //leaving the generator
            ID id = new ID(lane.getID() + "<-" + this.getID());
            Link link = new Link(id, placement);
            link.in = this;
            link.out = lane;
            lane.connectPrevious(link);
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
        vehicle.setOutOfScopeFlag();
        this.vehiclesToDelete.add(vehicle);
        this.receiptCounter++;
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
        if (!this.vehiclesToDelete.isEmpty()) {
            this.vehiclesToDelete.forEach(vehicle -> {
                super.getMap().removeMapObject(vehicle.getID());
            });
            this.vehiclesToDelete.clear();
        }

        if (this.getOutgoingLinks().size() > 0) {
            if (Math.random() > 0.04) {
                return;
            }
            Vehicle v = new Vehicle(new ID("Vehicle::" + this.getID() + "::" + creationCounter), "Vehicle " + creationCounter, getRandomLane());
            super.getMap().addMapObject(v);
            creationCounter++;
            LOG.log_Trace("TrafficGenerator '", this.getID(), "' created '", v.getName(), "'.");
        }
    }

    /**
     * toString method
     *
     * @return Description of the TrafficGenerator state
     */
    public String toString() {
        boolean inflowExists = this.getIncomingLinks().size() > 0;
        boolean outflowExists = this.getOutgoingLinks().size() > 0;
        String in = inflowExists ? "IN: " + Integer.toString(this.receiptCounter) : "";
        String out = outflowExists ? "OUT: " + Integer.toString(this.creationCounter) : "";
        String separator = inflowExists && outflowExists ? ", " : "";
        return this.getID() + "[" + in + separator + out + "]";
    }
}
