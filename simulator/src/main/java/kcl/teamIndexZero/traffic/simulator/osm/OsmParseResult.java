package kcl.teamIndexZero.traffic.simulator.osm;

import kcl.teamIndexZero.traffic.simulator.data.GeoSegment;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class OsmParseResult {
    public List<RoadDescription> descriptionList = new ArrayList<>();
    public GeoSegment boundingBox;
}
