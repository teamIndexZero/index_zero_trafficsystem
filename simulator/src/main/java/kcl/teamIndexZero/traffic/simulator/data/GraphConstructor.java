package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.LinkDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import kcl.teamIndexZero.traffic.simulator.data.features.Feature;
import kcl.teamIndexZero.traffic.simulator.data.features.Junction;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.data.links.LinkType;
import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLight;
import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLightInSet;
import kcl.teamIndexZero.traffic.simulator.exeptions.MapIntegrityException;
import kcl.teamIndexZero.traffic.simulator.exeptions.MissingImplementationException;
import kcl.teamIndexZero.traffic.simulator.exeptions.OrphanFeatureException;
import kcl.teamIndexZero.traffic.simulator.exeptions.UnrecognisedLinkException;

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
    private Map<ID, Feature> mapFeatures = new HashMap<>();
    private Map<ID, Link> mapLinks = new HashMap<>();

    public GraphConstructor(List<Junction> junction_list, List<RoadDescription> road_descriptions, List<LinkDescription> link_descriptions) throws MapIntegrityException {
        //TODO make it so that a lone single feature can be passed to the sim
        if (checkEmpty(junction_list, road_descriptions, link_descriptions)) {
            LOG.log_Error("Nothing was passed to the GraphConstructor.");
            throw new MapIntegrityException("Nothing was passed to the GraphConstructor.");
        }

        try {
            createFeatures(road_descriptions, junction_list);
            createGraph(link_descriptions);
            checkGraphIntegrity();
        } catch (UnrecognisedLinkException e) {
            LOG.log_Error("A LinkDescription describes one or more features that do not appear in the loaded collection.");
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

    private void createFeatures(List<RoadDescription> roadDescriptions, List<Junction> junctions) {
        roadDescriptions.forEach(rd -> {
            Road r = new Road(rd.getId(),
                    rd.getLaneCountA(),
                    rd.getLaneCountA(),
                    rd.getLength(),
                    rd.getGeoPolyline(),
                    rd.getRoadName());
            mapFeatures.put(r.getID(), r);
            r.getRightSide().getLanes().forEach(lane -> {
                mapFeatures.put(lane.getID(), lane);
            });
            r.getLeftSide().getLanes().forEach(lane -> {
                mapFeatures.put(lane.getID(), lane);
            });
        });
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
     * Creates the map graph by making links and connecting the relevant features to them
     *
     * @param node_vertices Description of the map links
     * @throws UnrecognisedLinkException when a link description points to a feature not loaded into the featureMap
     */

    private void createGraph(List<LinkDescription> node_vertices) throws UnrecognisedLinkException, MissingImplementationException {
        for (LinkDescription l : node_vertices) {
            Feature feature_one = mapFeatures.get(l.fromID);
            Feature feature_two = mapFeatures.get(l.toID);
            if (feature_one == null && feature_two == null) {
                LOG.log_Error("IDs '", l.fromID.toString(), "' and '", l.toID, "' not in loaded features.");
                throw new UnrecognisedLinkException("ID pointing to a Feature that is not loaded.");
            }
            try {
                Link link = createLink(l.type, l.linkID);
                if (feature_one != null) {
                    feature_one.addLink(link);
                    link.one = feature_one;
                } else {
                    LOG.log_Warning("Link description's fromID '", l.fromID, "' is null. Must be a one-way path.");
                    //TODO maybe put traffic generators on the null ends of links ?
                }
                if (feature_two != null) {
                    feature_two.addLink(link);
                    link.two = feature_two;
                } else {
                    LOG.log_Warning("link description's toID '", l.toID, "' is null. Must be a one-way path.");
                    //TODO maybe put traffic generators on the null ends of links ?
                }
            } catch (MissingImplementationException e) {
                LOG.log_Fatal("Creating Link failed.");
                LOG.log_Exception(e);
                throw e;
            }
        }
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
