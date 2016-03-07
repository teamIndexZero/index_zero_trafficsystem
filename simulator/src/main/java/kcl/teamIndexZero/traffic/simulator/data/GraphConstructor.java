package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.LinkDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import kcl.teamIndexZero.traffic.simulator.data.features.Feature;
import kcl.teamIndexZero.traffic.simulator.data.features.Junction;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;
import kcl.teamIndexZero.traffic.simulator.data.features.TrafficGenerator;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.data.links.LinkType;
import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLight;
import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLightInSet;
import kcl.teamIndexZero.traffic.simulator.exceptions.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Es on 01/03/2016.
 * GraphConstructor class
 * <p>Creates the underlining graph of the road network for the simulation from the link
 * descriptions pointing to features to link via their IDs and the features themselves.</p>
 */
public class GraphConstructor {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Link.class.getSimpleName());
    private Map<ID, TrafficGenerator> trafficGenerator = new HashMap<>();
    private Map<ID, Feature> mapFeatures = new HashMap<>();
    private Map<ID, Link> mapLinks = new HashMap<>();

    /**
     * Constructor
     *
     * @param junction_descriptions List of junction description
     * @param road_descriptions     List of road description
     * @param link_descriptions     List of link description
     * @throws MapIntegrityException when map integrity is compromised
     */
    public GraphConstructor(List<JunctionDescription> junction_descriptions, List<RoadDescription> road_descriptions, List<LinkDescription> link_descriptions) throws MapIntegrityException {
        //TODO make it so that a lone single feature can be passed to the sim
        if (checkEmpty(junction_descriptions, road_descriptions, link_descriptions)) {
            LOG.log_Error("Nothing was passed to the GraphConstructor.");
            throw new MapIntegrityException("Nothing was passed to the GraphConstructor.");
        }
        try {
            createGraph(junction_descriptions, road_descriptions, link_descriptions);
            checkGraphIntegrity();
        } catch (UnrecognisedLinkException e) {
            LOG.log_Error("A LinkDescription describes in or more features that do not appear in the loaded collection.");
            LOG.log_Exception(e);
            throw new MapIntegrityException("Description of map doesn't match reality!", e);
        } catch (MissingImplementationException e) {
            LOG.log_Error("Implementation for a case is missing in the code base!");
            throw new MapIntegrityException("Implementation for a case is missing!", e);
        } catch (OrphanFeatureException e) {
            LOG.log_Error("A feature with no links to anything has been found.");
            LOG.log_Exception(e);
            throw new MapIntegrityException("Orphan feature", e);
        } catch (MapIntegrityException e) {
            LOG.log_Error("Integrity of the map created from the features and link descriptions given is inconsistent.");
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
     * Creates the map graph by making the features and linking them
     *
     * @param junction_descriptions Descriptions of the junctions
     * @param road_descriptions     Descriptions of the roads
     * @param link_descriptions     Description of the links
     * @throws UnrecognisedLinkException      when a link description points to a feature not loaded into the featureMap
     * @throws MissingImplementationException when a LinkType has not been implemented in the code
     */
    private void createGraph(List<JunctionDescription> junction_descriptions, List<RoadDescription> road_descriptions, List<LinkDescription> link_descriptions) throws UnrecognisedLinkException, MissingImplementationException {
        createRoadFeatures(road_descriptions);
        createJunctionsFeatures(junction_descriptions);
        for (LinkDescription l : link_descriptions) {
            Feature feature_one = mapFeatures.get(l.fromID);
            Feature feature_two = mapFeatures.get(l.toID);
            if (feature_one == null) {
                LOG.log_Error("ID '", l.fromID, "' not in loaded features.");
                throw new UnrecognisedLinkException("ID pointing to a Feature that is not loaded.");
            }
            if (feature_two == null) {
                LOG.log_Error("ID '", l.toID, "' not in loaded features.");
                throw new UnrecognisedLinkException("ID pointing to a Feature that is not loaded.");
            }
            //TODO check that the features one/two are of Road type! otherwise throw exception
            //TODO work out which ends of the feature to connect based on their properties
            //TODO > if other end(s) of road(s) already connected check the remaining ends match (for lanes)
            //TODO > if both ends are free then match correct ends to each other
            //TODO create the needed numbers of links matching lanes getting connected (of correct LinkTypes)
            //TODO > if LinkType is traffic light in set then add to set too
            //TODO -----> Perhaps pass a t.light description with the the two feature being linked for ref later?? Let's see.
            //TODO connect all lanes

        }
        addTrafficGenerators();
    }

    /**
     * Checks the integrity of the graph currently held in this GraphConstructor
     *
     * @throws OrphanFeatureException when a feature with no connection to anything is found
     * @throws MapIntegrityException  when the graph integrity is compromised
     */
    private void checkGraphIntegrity() throws OrphanFeatureException, MapIntegrityException {
        //TODO count dead-ends
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
        });
    }

    /**
     * Creates a specific link of a given type
     *
     * @param type   Type of the link to create
     * @param linkID ID tag of the new link
     * @return New link
     */
    private Link createLink(LinkType type, ID linkID) throws MissingImplementationException {
        switch (type) {
            case GENERIC:
                return new Link(linkID);
            case AUTONOMOUS_TL:
                return new TrafficLight(linkID);
            case SYNC_TL:
                return new TrafficLightInSet(linkID);
            default:
                LOG.log_Error("LinkType not implemented in .createLink(..)!");
                throw new MissingImplementationException("LinkType not implemented!");
        }
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
                } catch (AlreadyExistsException e) {
                    LOG.log_Error("Trying to add Road '", id, "' to Junction '", junction.getID(), "' failed as it's already connected.");
                    LOG.log_Exception(e);
                }
            });
            this.mapFeatures.put(junction.getID(), junction);
        });
    }

    /**
     * Adds TrafficGenerator features to all dead-ends in the map.
     */
    private void addTrafficGenerators() {
        //TODO go through a completed graph and find dead ends to add the TrafficGenerators to.
    }

    /**
     * Checks if collections is/are empty
     *
     * @param collections Collections to check
     * @return Status: 1+ Collections is empty
     */
    private boolean checkEmpty(Collection<?>... collections) {
        boolean empty_flag = false;
        for (Collection<?> c : collections) {
            if (c.isEmpty()) {
                empty_flag = true;
            }
        }
        return empty_flag;
    }
}
