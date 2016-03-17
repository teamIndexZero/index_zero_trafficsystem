package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import kcl.teamIndexZero.traffic.simulator.data.features.*;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.exceptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Es on 01/03/2016.
 * GraphConstructor class
 * <p>Creates the underlining graph of the road network for the simulation from the link
 * descriptions pointing to features to link via their IDs and the features themselves.</p>
 */
public class GraphConstructor {
    private static Logger_Interface LOG = Logger.getLoggerInstance(GraphConstructor.class.getSimpleName());
    private List<TrafficGenerator> trafficGenerators = new ArrayList<>();
    private Map<ID, Feature> mapFeatures = new HashMap<>();
    private Map<ID, Link> mapLinks = new HashMap<>();
    private GraphTools tools = new GraphTools();

    /**
     * Constructor
     *
     * @param junction_descriptions List of junction description
     * @param road_descriptions     List of road description
     * @throws MapIntegrityException when simMap integrity is compromised
     */
    public GraphConstructor(List<JunctionDescription> junction_descriptions, List<RoadDescription> road_descriptions) throws MapIntegrityException {
        if (tools.checkEmpty(road_descriptions)) {
            LOG.log_Error("No road descriptions were passed to the GraphConstructor.");
            throw new MapIntegrityException("No road descriptions were passed to the GraphConstructor.");
        }
        try {
            createGraph(junction_descriptions, road_descriptions);
        } catch (MapIntegrityException e) {
            LOG.log_Error("Integrity of the simMap created from the features and link descriptions given is inconsistent.");
            LOG.log_Exception(e);
            throw e;
        }
    }

    /**
     * Gets the collection of Features
     *
     * @return Features
     */
    public Map<ID, Feature> getFeatures() {
        return this.mapFeatures;
    }

    /**
     * Gets the collection of Links
     *
     * @return Links
     */
    public Map<ID, Link> getLinks() {
        return this.mapLinks;
    }

    /**
     * Creates the simMap graph by making the features and linking them
     *
     * @param junction_descriptions Descriptions of the junctions
     * @param road_descriptions     Descriptions of the roads
     * @throws MapIntegrityException when issues have come up during construction
     */
    private void createGraph(List<JunctionDescription> junction_descriptions, List<RoadDescription> road_descriptions) throws MapIntegrityException {
        createRoadFeatures(road_descriptions);
        if (!tools.checkEmpty(junction_descriptions))
            createJunctionsFeatures(junction_descriptions);
        addTrafficGenerators(); //DONE - CHECK
    }

    /**
     * Creates the Road Features
     *
     * @param roadDescriptions RoadDescription object
     */
    private void createRoadFeatures(List<RoadDescription> roadDescriptions) {
        roadDescriptions.forEach(rd -> {
            Road r = new Road(rd.getId(), rd.getLaneCountForward(), rd.getLaneCountBackward(), rd.getLength(), rd.getGeoPolyline(), rd.getRoadName(), rd.getLayer());

            mapFeatures.putIfAbsent(r.getID(), r);
            r.getForwardSide().getLanes().forEach(lane -> {
                mapFeatures.putIfAbsent(lane.getID(), lane);
            });
            r.getBackwardSide().getLanes().forEach(lane -> {
                mapFeatures.putIfAbsent(lane.getID(), lane);
            });
            LOG.log("Created Road '", rd.getId(), "' [Lanes{F:", rd.getLaneCountForward(), ", B:", rd.getLaneCountBackward(), "}.");
        });
    }

    /**
     * Creates a Junction from a description
     *
     * @param junction_descriptions Description of the all the Junctions to create
     */
    private void createJunctionsFeatures(List<JunctionDescription> junction_descriptions) {
        for (JunctionDescription desc : junction_descriptions) {
            Junction junction = new Junction(desc.getID(), desc.hasTrafficLight(), desc.getGeoPoint());
            desc.getConnectedIDs().forEach((id, roadDirection) -> {
                try {
                    junction.addRoad((Road) this.mapFeatures.get(id), roadDirection);
                } catch (AlreadyExistsException e) {
                    LOG.log_Warning("Trying to add Road '", id, "' to Junction '", junction.getID(), "' failed as it's already connected.");
                }
            });
            if (junction.getOutflowCount() == 0) { //we need a TG to provide and receive outflow
                TrafficGenerator tg = new TrafficGenerator(new ID("TF" + this.trafficGenerators.size()), desc.getGeoPoint());
                tg.linkJunction(junction, junction.getInflowCount(), 0);
                junction.addTrafficGenerator(tg);
                this.trafficGenerators.add(tg);
                LOG.log("TrafficGenerator '", tg.getID(), "' created for Junction '", junction.getID(), "' {TG.IN: ", tg.getIncomingLinks().size(), ", TG.OUT: ", tg.getOutgoingLinks().size(), "}");
            }
            junction.computeAllPaths();
            this.mapFeatures.put(junction.getID(), junction);
        }
    }

    /**
     * Adds TrafficGenerator features to all dead-ends in the simMap
     *
     * @throws MapIntegrityException when partly implemented links were found in a directed group of lanes
     */
    private void addTrafficGenerators() throws MapIntegrityException {
        int tgCounter = 0;
        for (Feature road : mapFeatures.values()) {
            if (road instanceof Road) {
                int fwdCount = ((Road) road).getForwardLaneCount();
                int bckCount = ((Road) road).getBackwardLaneCount();
                //Checking the head of the road
                if ((fwdCount > 0 && !tools.checkFwdLinksPresent(((Road) road).getForwardSide()))
                        || (bckCount > 0 && !tools.checkBckLinksPresent(((Road) road).getBackwardSide()))) {
                    LOG.log_Trace("Fwd links on '", road.getID(), "' [Forward] are not present. Adding TF.");
                    TrafficGenerator tg = new TrafficGenerator(
                            new ID("TF" + this.trafficGenerators.size()),
                            ((Road) road).getPolyline().getFinishPoint());
                    tg.linkRoad((Road) road);
                    this.trafficGenerators.add(tg);
                    tg.getIncomingLinks().forEach(link -> {
                        this.mapLinks.put(link.getID(), link);
                    });
                    tg.getOutgoingLinks().forEach(link -> {
                        this.mapLinks.put(link.getID(), link);
                    });
                    tgCounter++;
                    LOG.log("Created a TrafficGenerator ('", tg.getID(), "') on forward side of '", road.getID(), "'.");
                }
                //Checking the tail of the road
                if ((fwdCount > 0 && !tools.checkBckLinksPresent(((Road) road).getForwardSide()))
                        || (bckCount > 0 && !tools.checkFwdLinksPresent(((Road) road).getBackwardSide()))) {
                    LOG.log_Trace("Fwd links on '", road.getID(), "' [Backward] are not present. Adding TF.");
                    TrafficGenerator tg = new TrafficGenerator(
                            new ID("TF" + this.trafficGenerators.size()),
                            ((Road) road).getPolyline().getStartPoint());
                    tg.linkRoad((Road) road);
                    this.trafficGenerators.add(tg);
                    tg.getIncomingLinks().forEach(link -> {
                        this.mapLinks.put(link.getID(), link);
                    });
                    tg.getOutgoingLinks().forEach(link -> {
                        this.mapLinks.put(link.getID(), link);
                    });
                    tgCounter++;
                    LOG.log("Created a TrafficGenerator ('", tg.getID(), "') on backward side of '", road.getID(), "'.");
                }
            }
        }

        this.mapFeatures.putAll(trafficGenerators.stream().collect(
                Collectors.toMap(
                        TrafficGenerator::getID,
                        Function.identity()))
        );
        LOG.log_Debug("Created and placed ", tgCounter, " TrafficGenerator(s) on graph.");
    }
}
