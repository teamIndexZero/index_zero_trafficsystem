package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.GraphTools;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.data.links.LinkType;
import kcl.teamIndexZero.traffic.simulator.exceptions.MapIntegrityException;
import kcl.teamIndexZero.traffic.simulator.exceptions.MissingImplementationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Traffic Generator Feature that can be linked to dead-ends on the map to make traffic
 */
public class TrafficGenerator extends Feature {
    private static Logger_Interface LOG = Logger.getLoggerInstance(TrafficGenerator.class.getSimpleName());
    private final GeoPoint geoPoint;
    private int counter = 1;
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

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    /**
     * Links a Road to the TrafficGenerator
     *
     * @param road Road to link
     * @throws MapIntegrityException          when the lanes in DirectedLanes group have partly implemented links
     * @throws MissingImplementationException when a LinkType construction is not implemented in the GraphTools.createLink(..) method
     */
    public void linkRoad(Road road) throws MapIntegrityException, MissingImplementationException {
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
            Link link = tools.createLink(LinkType.GENERIC, id, road.getPolyline().getStartPoint());
            link.in = lane;
            link.out = this;
            lane.connectNext(link);
            this.incoming.add(link);
            LOG.log("Linked: '", link.getID(), "'.");
        }
        for (Lane lane : outgoing.getLanes()) { //leaving the generator
            ID id = new ID(lane.getID() + "<-" + this.getID());
            Link link = tools.createLink(LinkType.GENERIC, id, road.getPolyline().getStartPoint());
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
        // test if it is working at all.
        LOG.log_Error("Got message from traffic generator.");
        /*
        //TODO Es: move that to sim map
        //TODO Alex: not so sure, I'd rather leave it here and figure out how to get rid of map link/ChickenEgg problem.
         // this code feels like it belongs here - traffic generator is a thing which creates new car and injects them
         // into the network...

        if (Math.random() > 0.7) {
            // speed somehow varies between 0.5 and 1 m/s
            double speed = Math.random() * 0.5 + 0.5;

            // some cars should accelerate
            double acceleration = Math.random() > 0.6 ? 0.04 : 0;
            Color carColor = null;
            String name = null;
            if (acceleration == 0) {
                carColor = MapObject.COLORS[0];
                name = "SLOW";
            } else {
                carColor = MapObject.COLORS[1];
                name = "FAST";
            }

            Vehicle v = new Vehicle(name + counter++, new MapPosition(0, 1, 2, 1), getRandomLane(), speed, acceleration);
            v.setColor(carColor);
            map.addMapObject(v);
        }
        */
    }
}
