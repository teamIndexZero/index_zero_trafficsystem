package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.LinkDescription;
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
     * @param link_descriptions     List of link description
     * @throws MapIntegrityException when simMap integrity is compromised
     *                               <p>
     *                               //TODO doc update
     */
    public GraphConstructor(List<JunctionDescription> junction_descriptions, List<RoadDescription> road_descriptions, List<LinkDescription> link_descriptions) throws MapIntegrityException {
        if (tools.checkEmpty(road_descriptions)) {
            LOG.log_Error("No road descriptions were passed to the GraphConstructor.");
            throw new MapIntegrityException("No road descriptions were passed to the GraphConstructor.");
        }
        try {
            createGraph(junction_descriptions, road_descriptions, link_descriptions);
        } catch (MissingImplementationException e) {
            LOG.log_Error("Implementation for a case is missing in the code base!");
            throw new MapIntegrityException("Implementation for a case is missing!", e);
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
     * @param link_descriptions     Description of the links
     * @throws MapIntegrityException          when issues have come up during construction
     * @throws MissingImplementationException when a LinkType has not been implemented in the code
     */
    private void createGraph(List<JunctionDescription> junction_descriptions, List<RoadDescription> road_descriptions, List<LinkDescription> link_descriptions) throws MapIntegrityException, MissingImplementationException {
        try {
            createRoadFeatures(road_descriptions); //DONE
            if (!tools.checkEmpty(junction_descriptions))
                createJunctionsFeatures(junction_descriptions); //DONE
            if (!tools.checkEmpty(link_descriptions))
                linkFeatures(link_descriptions); //DONE - CHECK
            //addTrafficGenerators(); //DONE - CHECK
            checkGraphIntegrity(); //TODO checkGraphIntegrity() method implementation
        } catch (MapIntegrityException e) {
            LOG.log_Error("Graph integrity is compromised. Aborting construction...");
            throw e;
        } catch (BadLinkException e) {
            LOG.log_Error("The graph has a bad link.");
            throw new MapIntegrityException("Bad link detected in the graph..", e);
        } catch (OrphanFeatureException e) {
            LOG.log_Error("An orphan feature has been detected in the graph.");
            throw new MapIntegrityException("An orphan feature has been found in the graph.", e);
        } catch (MissingImplementationException e) {
            LOG.log_Fatal("An implementation is missing for a case.");
            throw e;
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
     * Creates Links from descriptions
     *
     * @param linkDescriptions List of LinkDescriptions
     * @throws BadLinkException               when a link description is erroneous or something bad happens with a link
     * @throws MissingImplementationException when the implementation for a LinkType has not been done
     * @throws MapIntegrityException          when partly implemented links were found in a directed group of lanes
     */
    private void linkFeatures(List<LinkDescription> linkDescriptions) throws BadLinkException, MissingImplementationException, MapIntegrityException {
        try {
            for (LinkDescription desc : linkDescriptions) {
                Feature feature_from = mapFeatures.get(desc.fromID);
                Feature feature_to = mapFeatures.get(desc.toID);
                //FIXME check this, looks to break on the test
                LOG.log_Error(String.format("Going to create link %s for roads %s and %s",
                        desc.linkID,
                        feature_from.getID(),
                        feature_to.getID()));
            }
            for (LinkDescription desc : linkDescriptions) {
                Feature feature_from = mapFeatures.get(desc.fromID);
                Feature feature_to = mapFeatures.get(desc.toID);

                if (feature_from == null) {
                    LOG.log_Error("ID '", desc.fromID, "' not in loaded features.");
                    throw new BadLinkException("ID in LinkDescription points to a Feature that is not loaded.");
                }
                if (feature_to == null) {
                    LOG.log_Error("ID '", desc.toID, "' not in loaded features.");
                    throw new BadLinkException("ID in LinkDescription points to a Feature that is not loaded.");
                }
                if (!(feature_from instanceof Road)) {
                    LOG.log_Error("Trying to link a Feature ('", feature_from.getID(), "') that is not an instance of Road.");
                    throw new BadLinkException("LinkDescription points to something other than a Road object.");
                }
                if (!(feature_to instanceof Road)) {
                    LOG.log_Error("Trying to link a Feature ('", feature_to.getID(), "') that is not an instance of Road.");
                    throw new BadLinkException("LinkDescription points to something other than a Road object.");
                }
                List<Lane> forwardLanes_atFrom;
                List<Lane> backwardLanes_atFrom;
                List<Lane> forwardLanes_atTo;
                List<Lane> backwardLanes_atTo;

                if (!tools.checkFwdLinksPresent(((Road) feature_from).getForwardSide())
                        && !tools.checkFwdLinksPresent(((Road) feature_to).getBackwardSide())) {
                    //from->to links free
                    forwardLanes_atFrom = ((Road) feature_from).getForwardSide().getLanes();
                    backwardLanes_atFrom = ((Road) feature_from).getBackwardSide().getLanes();
                    forwardLanes_atTo = ((Road) feature_to).getForwardSide().getLanes();
                    backwardLanes_atTo = ((Road) feature_to).getBackwardSide().getLanes();
                } else if (!tools.checkFwdLinksPresent(((Road) feature_from).getBackwardSide())
                        && !tools.checkFwdLinksPresent(((Road) feature_to).getForwardSide())) {
                    forwardLanes_atFrom = ((Road) feature_from).getBackwardSide().getLanes();
                    backwardLanes_atFrom = ((Road) feature_to).getForwardSide().getLanes();
                    forwardLanes_atTo = ((Road) feature_to).getBackwardSide().getLanes();
                    backwardLanes_atTo = ((Road) feature_from).getForwardSide().getLanes();
                } else {
                    LOG.log_Error("Detected attempt to cris-cross the lanes on Roads '", feature_from.getID(), "' and '", feature_to.getID(), "' in the link.");
                    throw new BadLinkException("Road is already linked on the both sides.");
                }
                //Do the lanes match on both roads?
                if (!(forwardLanes_atFrom.size() == forwardLanes_atTo.size()
                        && backwardLanes_atFrom.size() == backwardLanes_atTo.size())) {
                    LOG.log_Error("Directed linking '", feature_from.getID(), "' to '", feature_to.getID(), "' is not possible as the number of lanes do not match.");
                    throw new BadLinkException("Mismatch in the lanes of the directed Roads to be linked.");
                }

                //Linking time for the LaneS inside the Road!
                for (int i = 0; i < forwardLanes_atFrom.size(); i++) {
                    try {
                        ID id = new ID(desc.linkID + "F:" + Integer.toString(i));
                        Link link = tools.createLink(desc.type, id, desc.geoPoint);
                        Lane from = forwardLanes_atFrom.get(i);
                        Lane to = forwardLanes_atTo.get(i);
                        link.in = from;
                        link.out = to;
                        from.connectNext(link);
                        this.mapLinks.put(link.getID(), link);
                        LOG.log("Linking Lane(s) '", from.getID(), "' -> '", to.getID(), "'");
                    } catch (MissingImplementationException e) {
                        LOG.log_Fatal("Link type for LinkDescription '", desc.linkID, "' has not been implemented in createLink().");
                        throw e;
                    }
                }
                for (int i = 0; i < backwardLanes_atTo.size(); i++) {
                    try {
                        ID id = new ID(desc.linkID + "B:" + Integer.toString(i));
                        Link link = tools.createLink(desc.type, id, desc.geoPoint);
                        Lane from = backwardLanes_atTo.get(i);
                        Lane to = backwardLanes_atFrom.get(i);
                        link.in = from;
                        link.out = to;
                        from.connectNext(link);
                        this.mapLinks.put(link.getID(), link);
                        LOG.log("Linking Lane(s) '", from.getID(), "' -> '", to.getID(), "'");
                    } catch (MissingImplementationException e) {
                        LOG.log_Fatal("Link type for LinkDescription '", desc.linkID, "' has not been implemented in createLink().");
                        throw e;
                    }
                }
            }
        } catch (MapIntegrityException e) {
            LOG.log_Error("Partly implemented links on group of directed lanes found. Map integrity is compromised");
            throw e;
        }
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
