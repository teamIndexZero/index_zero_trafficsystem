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
        try {
            createRoadFeatures(road_descriptions); //DONE
            if (!tools.checkEmpty(junction_descriptions))
                createJunctionsFeatures(junction_descriptions); //DONE
            //addTrafficGenerators(); //DONE - CHECK
            checkGraphIntegrity(); //TODO checkGraphIntegrity() method implementation
        } catch (MapIntegrityException e) {
            LOG.log_Error("Graph integrity is compromised. Aborting construction...");
            throw e;
        } catch (OrphanFeatureException e) {
            LOG.log_Error("An orphan feature has been detected in the graph.");
            throw new MapIntegrityException("An orphan feature has been found in the graph.", e);
        }
    }

    /**
     * Checks the integrity of the graph currently held in this GraphConstructor
     *
     * @throws OrphanFeatureException when a feature with no connection to anything is found
     * @throws MapIntegrityException  when the graph integrity is compromised
     */
    private void checkGraphIntegrity() throws OrphanFeatureException, MapIntegrityException {
        Map<ID, Boolean> visitedFeatures = new HashMap<>();
        this.mapFeatures.keySet().forEach(id -> {
            visitedFeatures.put(id, Boolean.FALSE);
        });
        Map<ID, Boolean> visitedLinks = new HashMap<>();
        this.mapLinks.keySet().forEach(id -> {
            visitedLinks.put(id, Boolean.FALSE);
        });

        this.trafficGenerators.forEach(trafficGenerator -> {
            trafficGenerator.getOutgoingLinks().forEach(link -> {
                try {
                    if (_checkGraphIntegrity(visitedFeatures, visitedLinks, link)) {

                    } else {

                    }
                } catch (BadLinkException e) {
                    e.printStackTrace();
                } catch (DeadEndFeatureException e) {
                    e.printStackTrace();
                }
            });
        });

        //TODO check and count infinite loops
        //TODO check the integrity of the graph (no orphan features and no infinite directed loops with no exit  -o)
    }

    private boolean _checkGraphIntegrity(Map<ID, Boolean> visitedFeatures, Map<ID, Boolean> visitedLinks, Link nextLink) throws BadLinkException, DeadEndFeatureException {
        visitedLinks.put(nextLink.getID(), Boolean.TRUE);
        LOG.log_Debug("¬ Visited Link: '", nextLink.getID(), "'");
        if (nextLink.isDeadEnd()) {
            LOG.log_Error("Link '", nextLink.getID(), "' is a dead-end.");
            throw new BadLinkException("Dead end link found.");
        }
        if (visitedFeatures.get(nextLink.getNextFeature().getID()) == Boolean.TRUE) {
            return true;
        } else {
            LOG.log_Debug("¬ Visiting Feature: '", nextLink.getNextFeature().getID(), "'");
            if (nextLink.getNextFeature() instanceof Junction) {
                boolean flag = true;
                try {
                    for (Link l : ((Junction) nextLink.getNextFeature()).getNextLinks(nextLink.getID())) {
                        if (!_checkGraphIntegrity(visitedFeatures, visitedLinks, l))
                            flag = false;
                    }
                    return flag;
                } catch (JunctionPathException e) {
                    LOG.log_Error("Path problem found in '", nextLink.getNextFeature().getID(), "'.");
                    return false;
                }
            } else if (nextLink.getNextFeature() instanceof Lane) {
                visitedFeatures.put(nextLink.getNextFeature().getID(), Boolean.TRUE);
                if (!visitedFeatures.get(((Lane) nextLink.getNextFeature()).getRoadID()) == Boolean.TRUE) {
                    boolean flag = true;
                    for (Lane lane : ((Lane) nextLink.getNextFeature()).getRoad().getForwardSide().getLanes()) {
                        if (!visitedFeatures.get(lane.getID())) {
                            flag = false;
                        }
                    }
                    for (Lane lane : ((Lane) nextLink.getNextFeature()).getRoad().getBackwardSide().getLanes()) {
                        if (!visitedFeatures.get(lane.getID())) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        visitedFeatures.put(((Lane) nextLink.getNextFeature()).getRoadID(), Boolean.TRUE);
                    }
                }
                return _checkGraphIntegrity(visitedFeatures, visitedLinks, ((Lane) nextLink.getNextFeature()).getNextLink());
            } else if (nextLink.getNextFeature() instanceof TrafficGenerator) {
                if (visitedFeatures.get(nextLink.getNextFeature().getID()) == Boolean.FALSE)
                    visitedFeatures.put(nextLink.getNextFeature().getID(), Boolean.TRUE);
                return true;
            } else {
                LOG.log_Fatal("Found an unrecognised object in the map at '", nextLink.getNextFeature().getID(), "'.");
                return false;
            }
        }
    }

    /**
     * Creates the Road Features
     *
     * @param roadDescriptions RoadDescription object
     */
    private void createRoadFeatures(List<RoadDescription> roadDescriptions) {
        roadDescriptions.forEach(rd -> {
            Road r = new Road(rd.getId(), rd.getLaneCountForward(), rd.getLaneCountBackward(), rd.getLength(), rd.getGeoPolyline(), rd.getRoadName(), rd.getLayer());

            mapFeatures.put(r.getID(), r);
            r.getForwardSide().getLanes().forEach(lane -> {
                mapFeatures.put(lane.getID(), lane);
            });
            r.getBackwardSide().getLanes().forEach(lane -> {
                mapFeatures.put(lane.getID(), lane);
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
        junction_descriptions.forEach(junctionDescription -> {
            Junction junction = new Junction(junctionDescription.getID(), junctionDescription.hasTrafficLight(),
                    junctionDescription.getGeoPoint());
            junctionDescription.getConnectedIDs().forEach((id, roadDirection) -> {
                try {
                    junction.addRoad((Road) this.mapFeatures.get(id), roadDirection);
                    LOG.log("Added Road '", id, "' to Junction '", junction.getID(), "'.");
                } catch (AlreadyExistsException e) {
                    LOG.log_Warning("Trying to add Road '", id, "' to Junction '", junction.getID(), "' failed as it's already connected.");
                }
            });
            junction.computeAllPaths();
            this.mapFeatures.put(junction.getID(), junction);
        });
    }

    /**
     * Adds TrafficGenerator features to all dead-ends in the simMap
     *
     * @throws MissingImplementationException when the implementation for a LinkType has not been done
     * @throws MapIntegrityException          when partly implemented links were found in a directed group of lanes
     */
    private void addTrafficGenerators() throws MissingImplementationException, MapIntegrityException {
        for (Feature road : mapFeatures.values()) {
            if (road instanceof Road) {
                if (!tools.checkFwdLinksPresent(((Road) road).getForwardSide()) || !tools.checkFwdLinksPresent(((Road) road).getBackwardSide())) {
                    TrafficGenerator tg = new TrafficGenerator(
                            new ID("TrafficGenerator:" + this.trafficGenerators.size()),
                            ((Road) road).getPolyline().getStartPoint());
                    tg.linkRoad((Road) road);
                    this.trafficGenerators.add(tg);
                    LOG.log("Created a TrafficGenerator ('", tg.getID(), "').");
                }
            }
        }

        this.mapFeatures.putAll(trafficGenerators.stream().collect(
                Collectors.toMap(
                        TrafficGenerator::getID,
                        Function.identity()))
        );
    }
}
