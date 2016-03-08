package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.LinkDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import kcl.teamIndexZero.traffic.simulator.data.features.*;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.exceptions.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Es on 01/03/2016.
 * GraphConstructor class
 * <p>Creates the underlining graph of the road network for the simulation from the link
 * descriptions pointing to features to link via their IDs and the features themselves.</p>
 */
public class GraphConstructor {
    private static Logger_Interface LOG = Logger.getLoggerInstance(GraphConstructor.class.getSimpleName());
    private List<TrafficGenerator> trafficGenerators = new LinkedList<>();
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
    public GraphConstructor(SimulationMap map, List<JunctionDescription> junction_descriptions, List<RoadDescription> road_descriptions, List<LinkDescription> link_descriptions) throws MapIntegrityException {
        //TODO make it so that a lone single feature can be passed to the sim
        if (tools.checkEmpty(junction_descriptions, road_descriptions, link_descriptions)) {
            LOG.log_Error("Nothing was passed to the GraphConstructor.");
            throw new MapIntegrityException("Nothing was passed to the GraphConstructor.");
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
            createJunctionsFeatures(junction_descriptions); //DONE
            linkFeatures(link_descriptions); //DONE - CHECK
            addTrafficGenerators(); //TODO addTrafficGenerators() method implementation
            checkGraphIntegrity(); //TODO checkGraphIntegrity() method implementation
        } catch (MapIntegrityException e) { //TODO sort out the exceptions with log calls and forwarding them as appropriate

        } catch (BadLinkException e) {

        } catch (OrphanFeatureException e) {

        } catch (MissingImplementationException e) {

        }
    }

    /**
     * Checks the integrity of the graph currently held in this GraphConstructor
     *
     * @throws OrphanFeatureException when a feature with no connection to anything is found
     * @throws MapIntegrityException  when the graph integrity is compromised
     */
    private void checkGraphIntegrity() throws OrphanFeatureException, MapIntegrityException {
        //check for infinite loop
        //TODO count infinite loops
        //TODO check the integrity of the graph (no orphan features and no infinite directed loops with no exit  -o)
    }

    /**
     * Creates the Road Features
     *
     * @param roadDescriptions RoadDescription object
     */
    private void createRoadFeatures(List<RoadDescription> roadDescriptions) {
        roadDescriptions.forEach(rd -> {
            Road r = new Road(rd.getId(), rd.getLaneCountForward(), rd.getLaneCountBackward(), rd.getLength(), rd.getGeoPolyline(), rd.getRoadName());
            mapFeatures.put(r.getID(), r);
            r.getForwardSide().getLanes().forEach(lane -> {
                mapFeatures.put(lane.getID(), lane);
            });
            r.getBackwardSide().getLanes().forEach(lane -> {
                mapFeatures.put(lane.getID(), lane);
            });
            LOG.log("<GRAPH> Created Road '", rd.getId(), "' [Lanes{F:", rd.getLaneCountForward(), ", B:", rd.getLaneCountBackward(), "}.");
        });
    }

    /**
     * Creates a Junction from a description
     *
     * @param junction_descriptions Description of the all the Junctions to create
     */
    private void createJunctionsFeatures(List<JunctionDescription> junction_descriptions) {
        junction_descriptions.forEach(junctionDescription -> {
            Junction junction = new Junction(junctionDescription.getID(), junctionDescription.hasTrafficLight());
            junctionDescription.getConnectedIDs().forEach((id, roadDirection) -> {
                try {
                    junction.addRoad((Road) this.mapFeatures.get(id), roadDirection);
                    LOG.log("<GRAPH> Added Road '", id, "' to Junction '", junction.getID(), "'.");
                } catch (AlreadyExistsException e) {
                    LOG.log_Warning("<GRAPH> Trying to add Road '", id, "' to Junction '", junction.getID(), "' failed as it's already connected.");
                }
            });
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
                int f1_F = ((Road) feature_from).getForwardLaneCount();
                int f1_B = ((Road) feature_from).getBackwardLaneCount();
                int f2_F = ((Road) feature_to).getForwardLaneCount();
                int f2_B = ((Road) feature_to).getBackwardLaneCount();
                if (f1_F != f2_F || f1_B != f2_B) {
                    LOG.log_Error("Directed linking '", feature_from.getID(), "' to '", feature_to.getID(), "' is not possible as the number of lanes do not match (FWD: ", f1_F, "->", f2_F, ", BCK: ", f1_B, "->", f2_B, " ).");
                    throw new BadLinkException("Mismatch in the lanes of the directed Roads to be linked.");
                }
                if (tools.checkForwardLinks(((Road) feature_from).getForwardSide())) {
                    LOG.log_Error("Road '", feature_from.getID(), "' to link is already linked on the forward side.");
                    throw new BadLinkException("Road is already linked on the forward side.");
                }
                if (tools.checkForwardLinks(((Road) feature_from).getBackwardSide())) {
                    LOG.log_Error("Road '", feature_from.getID(), "' to link is already linked on the backward side.");
                    throw new BadLinkException("Road is already linked on the backward side.");
                }
                //Linking time for the LaneS inside the Road!
                for (int i = 0; i < ((Road) feature_from).getForwardLaneCount(); i++) {
                    try {
                        ID id = new ID(desc.linkID + "F:" + Integer.toString(i));
                        Link link = tools.createLink(desc.type, id);
                        Lane from = ((Road) feature_from).getForwardSide().getLanes().get(i);
                        Lane to = ((Road) feature_to).getForwardSide().getLanes().get(i);
                        link.in = from;
                        link.out = to;
                        from.connect(link);
                        this.mapLinks.put(link.getID(), link);
                        LOG.log("<GRAPH> Linking Lane '", from.getID(), "' to '", to.getID(), "'");
                    } catch (MissingImplementationException e) {
                        LOG.log_Fatal("Link type for LinkDescription '", desc.linkID, "' has not been implemented in createLink().");
                        throw e;
                    }
                }
                for (int i = 0; i < ((Road) feature_to).getBackwardLaneCount(); i++) {
                    try {
                        ID id = new ID(desc.linkID + "B:" + Integer.toString(i));
                        Link link = tools.createLink(desc.type, id);
                        Lane from = ((Road) feature_to).getBackwardSide().getLanes().get(i);
                        Lane to = ((Road) feature_from).getBackwardSide().getLanes().get(i);
                        link.in = from;
                        link.out = to;
                        from.connect(link);
                        this.mapLinks.put(link.getID(), link);
                        LOG.log("<GRAPH> Linking Lane '", from.getID(), "' to '", to.getID(), "'");
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
                if (!tools.checkForwardLinks(((Road) road).getForwardSide()) || !tools.checkForwardLinks(((Road) road).getBackwardSide())) {
                    TrafficGenerator tg = new TrafficGenerator(new ID("TrafficGenerator:" + this.trafficGenerators.size()));
                    tg.linkRoad((Road) road);
                    this.trafficGenerators.add(tg);
                    this.mapFeatures.put(tg.getID(), tg);
                    LOG.log("<GRAPH> Created a TrafficGenerator ('", tg.getID(), "').");
                }
            }
        }
    }
}
