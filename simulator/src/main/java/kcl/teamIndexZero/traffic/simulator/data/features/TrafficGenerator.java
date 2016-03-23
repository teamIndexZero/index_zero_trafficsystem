package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.GraphTools;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.links.JunctionLink;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle;
import kcl.teamIndexZero.traffic.simulator.exceptions.DeadEndFeatureException;
import kcl.teamIndexZero.traffic.simulator.exceptions.JunctionPathException;
import kcl.teamIndexZero.traffic.simulator.exceptions.MapIntegrityException;

import java.util.ArrayList;
import java.util.List;

/**
 * Traffic Generator Feature that can be linked to dead-ends on the map to make traffic.
 */
public class TrafficGenerator extends Feature {
    private static final int MAX_CARS_ON_SCREEN = 500;
    private static Logger_Interface LOG = Logger.getLoggerInstance(TrafficGenerator.class.getSimpleName());
    // global counter, used to name the car properly
    private static int globalCreationCounter = 0;
    private final GeoPoint geoPoint;
    // local counter, used to know how many cars has this TG sent into the wild
    private int thisGeneratorCreationCounter = 0;
    private int receiptCounter = 0;
    private java.util.List<Link> incoming = new ArrayList<>();
    private java.util.List<Link> outgoing = new ArrayList<>();
    private List<Vehicle> vehiclesToDelete = new ArrayList<>();

    /**
     * Constructor
     *
     * @param id       Feature ID tag
     * @param geoPoint geographical location of TG
     */
    public TrafficGenerator(ID id, GeoPoint geoPoint) {
        super(id);
        this.geoPoint = geoPoint;
    }

    public int getReceiptCounter() {
        return receiptCounter;
    }

    public int getThisGeneratorCreationCounter() {
        return thisGeneratorCreationCounter;
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
     * Links a Junction to the TrafficGenerator
     *
     * @param junction       Junction to link to
     * @param tgInflowPaths  Number of traffic inflow links to the TrafficGenerator
     * @param tgOutflowPaths Number of traffic outflow links from the TrafficGenerator
     */
    public void linkJunction(Junction junction, int tgInflowPaths, int tgOutflowPaths) {
        int incoming_size = this.incoming.size();
        for (int i = 0; i < tgInflowPaths; i++) {
            ID link_ID = new ID(junction.getID() + "->" + this.getID() + ".in" + (incoming_size + i));
            JunctionLink link = new JunctionLink(link_ID, this, junction, geoPoint, JunctionLink.LinkType.OUTFLOW);
            link.in = junction;
            link.out = this;
            this.incoming.add(link);
        }
        int outgoing_size = this.outgoing.size();
        for (int i = 0; i < tgOutflowPaths; i++) {
            ID link_ID = new ID(junction.getID() + "<-" + this.getID() + ".out" + (outgoing_size + i));
            JunctionLink link = new JunctionLink(link_ID, this, junction, geoPoint, JunctionLink.LinkType.INFLOW);
            link.in = this;
            link.out = junction;
            this.outgoing.add(link);
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
        this.vehiclesToDelete.add(vehicle);
        this.receiptCounter++;
    }

    /**
     * Gets a random lane to place a vehicle onto
     *
     * @return Random Lane
     * @throws JunctionPathException   if there is no path from junction this TG is connected to
     * @throws DeadEndFeatureException in case we got to dead end.
     */
    public Lane getRandomLane() throws JunctionPathException, DeadEndFeatureException {
        Link outboundLink = outgoing.get((int) (Math.random() * outgoing.size() - 1));
        Feature f = outboundLink.out;
        if (f instanceof Lane)
            return (Lane) f;
        if (f instanceof Junction) {
            Feature fNextOnJunction = ((Junction) f).getRandomLink(outboundLink.getID()).out;
            if (fNextOnJunction instanceof Lane)
                return (Lane) fNextOnJunction;
            else {
                throw new DeadEndFeatureException("Could not get a lane out of the TrafficGenerator indirectly.");
            }
        }
        throw new DeadEndFeatureException("Could not get a lane out of the TrafficGenerator directly.");
    }

    @Override
    public String toHTMLString() {
        return String.format(
                "<html>" +
                        "<font color='blue'/><b>+</b></font>" +
                        " TrafficGenerator %s" +
                        "</html>", this.getID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
        // remove the cars which have 'arrived' into this TG
        if (!this.vehiclesToDelete.isEmpty()) {
            this.vehiclesToDelete.forEach(vehicle -> {
                vehicle.getLane().removeVehicle(vehicle);
                super.getMap().removeMapObject(vehicle.getID());
            });
            this.vehiclesToDelete.clear();
        }

        // if we are over hard limit for cars, stop generating (until some of them get removed)
        if (getMap().getObjectsOnSurface().size() > MAX_CARS_ON_SCREEN) {
            return;
        }

        // maybe create a car.
        if (this.getOutgoingLinks().size() > 0) {
            if (Math.random() > 0.04) {
                return;
            }
            try {
                Lane l = getRandomLane();
                boolean hasLaneFreeSpace = l.isClearAhead(null, l.isForwardLane() ? 0 : l.getLength(), 10);
                if (!hasLaneFreeSpace) {
                    return;
                }
                Vehicle v = Math.random() > 0.7
                        ? Vehicle.createTruck(new ID("Truck::" + this.getID() + "::" + globalCreationCounter), "Truck " + globalCreationCounter, getRandomLane())
                        : Vehicle.createPassengerCar(new ID("Car::" + this.getID() + "::" + globalCreationCounter), "Car " + globalCreationCounter, getRandomLane());

                super.getMap().addMapObject(v);
                globalCreationCounter++;
                thisGeneratorCreationCounter++;
                LOG.log_Trace("TrafficGenerator '", this.getID(), "' created '", v.getName(), "'.");

            } catch (JunctionPathException e) {
                LOG.log_Fatal("Trying to get a path to a lane to pass the new vehicle onto through the Junction failed.");
                LOG.log_Exception(e);
            } catch (DeadEndFeatureException e) {
                LOG.log_Fatal("Trying to get a lane to pass the new vehicle onto out of the TrafficGenerator ('", this.getID(), "') failed.");
                LOG.log_Exception(e);
            }
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
        String out = outflowExists ? "OUT: " + Integer.toString(this.thisGeneratorCreationCounter) : "";
        String separator = inflowExists && outflowExists ? ", " : "";
        return this.getID() + "[" + in + separator + out + "]";
    }
}
