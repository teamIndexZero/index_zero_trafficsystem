package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.features.Feature;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.exeptions.EmptySimMapException;
import kcl.teamIndexZero.traffic.simulator.exeptions.MapIntegrityException;
import kcl.teamIndexZero.traffic.simulator.exeptions.OrphanFeatureException;
import kcl.teamIndexZero.traffic.simulator.exeptions.UnrecognisedLinkException;

import java.util.List;
import java.util.Map;

/**
 * Created by Es on 01/03/2016.
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
            LOG.log_Error("Integrity of the map created from the features and link descriptions given is inconsistent.");
            LOG.log_Exception(e);
        } catch (MapIntegrityException e) {
            LOG.log_Error("Integrity of the map created from the features and link descriptions given is inconsistent.");
            LOG.log_Exception(e);
            this.mapFeatures.clear();
            this.mapLinks.clear();
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


    private void createGraph(List<LinkDescription> node_vertices) throws UnrecognisedLinkException {
        for (LinkDescription l : node_vertices) {
            //TODO
        }
    }

    private void checkGraphIntegrity() throws OrphanFeatureException, MapIntegrityException {

    }
}
