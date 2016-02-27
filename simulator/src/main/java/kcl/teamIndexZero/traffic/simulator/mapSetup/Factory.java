package kcl.teamIndexZero.traffic.simulator.mapSetup;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.features.ID;

/**
 * Created by Es on 27/02/2016.
 */
public class Factory {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Factory.class.getSimpleName());

    public void newFeature( mapFeatureType type, ID id ) {
        //TODO check ID not already used

    }

    public void newFeature(OSMFeature osm_feature) {
        //extract OSM details and create
    }

    public void linkFeatures(ID id1, ID id2) {
        //TODO maybe have an OSM tag so that adaptive linkage is turned off?
        //TODO for manual graph creation maybe do adaptive linkage:
        //     e.g. when a simple road and a dual carriage way link up on T junction
        //     auto-magically create the appropriate T junction that fits the road specs

    }
}
