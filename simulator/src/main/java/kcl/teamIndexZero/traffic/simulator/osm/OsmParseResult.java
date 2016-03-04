package kcl.teamIndexZero.traffic.simulator.osm;

import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoSegment;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class OsmParseResult {
    public List<RoadDescription> descriptionList = new ArrayList<>();
    public GeoSegment boundingBox;
}
