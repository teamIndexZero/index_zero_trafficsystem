package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.features.Feature;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.data.links.LinkType;
import kcl.teamIndexZero.traffic.simulator.exeptions.EmptySimMapException;
import kcl.teamIndexZero.traffic.simulator.exeptions.MapIntegrityException;
import kcl.teamIndexZero.traffic.simulator.exeptions.OrphanFeatureException;
import kcl.teamIndexZero.traffic.simulator.exeptions.UnrecognisedLinkException;

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
    private Map<ID, Feature> mapFeatures;
    private Map<ID, Link> mapLinks;

    public GraphConstructor(Map<ID, Feature> features, List<LinkDescription> link_descriptions) throws EmptySimMapException, OrphanFeatureException, MapIntegrityException {
        if (features.isEmpty()) {
            LOG.log_Error("No Features were passed to the GraphConstructor.");
            throw new EmptySimMapException("No features were passed to the GraphConstructor.");
        }
        if (features.size() > 1 && link_descriptions.isEmpty()) {
            LOG.log_Error(features.size(), " features are present but no Link descriptions were passed to the GraphConstructor.");
            throw new OrphanFeatureException("Orphaned features exist in the GraphConstructor.");
        }
        this.mapFeatures = features;
        try {
            createGraph(link_descriptions);
            checkGraphIntegrity();
        } catch (UnrecognisedLinkException e) {
            LOG.log_Error("A LinkDescription describes one or more features that do not appear in the loaded collection.");
            LOG.log_Exception(e);
            throw new MapIntegrityException("Description of map doesn't match reality!");
        } catch (OrphanFeatureException e) {
            LOG.log_Error("A feature with no links to anything has been found.");
            LOG.log_Exception(e);
            throw e;
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
     * Creates the map graph by making links and connecting the relevant features to them
     *
     * @param node_vertices Description of the map links
     * @throws UnrecognisedLinkException when a link description points to a feature not loaded into the featureMap
     */
    private void createGraph(List<LinkDescription> node_vertices) throws UnrecognisedLinkException {
        for (LinkDescription l : node_vertices) {
            //TODO what if it's a one way street with nothing at one end (no feature) <- adapt the code for that!
            Feature feature_one = mapFeatures.get(l.fromID);
            Feature feature_two = mapFeatures.get(l.toID);
            if (feature_one == null) {
                LOG.log_Error("ID: '", l.fromID.toString(), "' not in loaded features.");
                throw new UnrecognisedLinkException("ID pointing to a Feature that is not loaded.");
            }
            if (feature_two == null) {
                LOG.log_Error("ID: '", l.toID.toString(), "' not in loaded feature.");
                throw new UnrecognisedLinkException("ID pointing to a Feature that is not loaded.");
            }
            Link link = createLink(l.type, l.linkID);
            feature_one.addLink(link);
            feature_two.addLink(link);
            link.one = feature_one;
            link.two = feature_two;
        }
    }

    /**
     * Checks the integrity of the graph currently held in this GraphConstructor
     *
     * @throws OrphanFeatureException when a feature with no connection to anything is found
     * @throws MapIntegrityException  when the graph integrity is compromised
     */
    private void checkGraphIntegrity() throws OrphanFeatureException, MapIntegrityException {
        //TODO check the integrity of the graph (no orphan features and no infinite directed loops with no exit  -o)
    }

    /**
     * Creates a specific link of a given type
     *
     * @param type   Type of the link to create
     * @param linkID ID tag of the new link
     * @return New link
     */
    private Link createLink(LinkType type, ID linkID) {
        //TODO switch for the LinkType and return relevant new link object
        return new Link(linkID);
    }
}
