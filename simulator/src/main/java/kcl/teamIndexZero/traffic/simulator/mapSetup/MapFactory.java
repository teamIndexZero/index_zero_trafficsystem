package kcl.teamIndexZero.traffic.simulator.mapSetup;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.features.Feature;
import kcl.teamIndexZero.traffic.simulator.data.features.ID;
import kcl.teamIndexZero.traffic.simulator.data.features.Link;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Es on 27/02/2016.
 * Factory for creating Features and Links to those
 */
public class MapFactory {
    private static Logger_Interface LOG = Logger.getLoggerInstance(MapFactory.class.getSimpleName());
    private Map<ID, Feature> features = new HashMap<>();
    private Map<ID, Link> links = new HashMap<>();

    /**
     * Creates a new feature
     *
     * @param type Type of feature wanted
     * @param id   ID tag of the new feature
     * @throws AlreadyExistsException when an ID already exists
     */
    public void newFeature(mapFeatureType type, ID id) throws AlreadyExistsException {
        if (features.containsKey(id)) {
            LOG.log_Error("Feature's ID tag ('", id, "') is already used.");
            throw new AlreadyExistsException("Feature ID tag already exists.");
        }
        switch (type) {
            case SIMPLE_ONE_WAY_ROAD:
                //TODO
                break;
            case SIMPLE_TWO_WAY_ROAD:
                features.put(id, new Road(id, 1, 1, 1000));
                //TODO
                break;
            case DUAL_CARRIAGEWAY:
                //TODO
                break;
            case T_JUNCTION:
                //TODO
                break;
            case X_JUNCTION:
                //TODO
                break;
            default:
                LOG.log_Error("Feature with ID '", id, "' has a type that has not been programmed in the MapFactory computer.");
                break;
        }
    }

    /**
     * Creates a new feature out of an OSM feature
     *
     * @param osm_feature OSM feature
     */
    public void newFeature(OSMFeature osm_feature) {
        //extract OSM details and create
    }

    /**
     * Creates link to 2 features
     *
     * @param id1 ID tag of first feature
     * @param id2 ID tag of second feature
     */
    public void linkFeatures(ID id1, ID id2) {
        //TODO maybe have an OSM tag so that adaptive linkage is turned off?
        //TODO for manual graph creation maybe do adaptive linkage:
        //     e.g. when a simple road and a dual carriage way link up on T junction
        //     auto-magically create the appropriate T junction that fits the road specs

    }

    /**
     * Gets the factory stock of features
     *
     * @return Features created
     */
    public Map<ID, Feature> getFeatures() {
        return this.features;
    }

    /**
     * Gets the factory stock of feature links
     *
     * @return Links created
     */
    public Map<ID, Link> getLinks() {
        return this.links;
    }
}
