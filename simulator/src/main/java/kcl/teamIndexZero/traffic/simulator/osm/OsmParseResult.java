package kcl.teamIndexZero.traffic.simulator.osm;

import kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.LinkDescription;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoSegment;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for passing parse result
 */
public class OsmParseResult {
    public List<RoadDescription> roadDescriptions = new ArrayList<>();
    public List<JunctionDescription> junctionDescriptions = new ArrayList<>();
    public List<LinkDescription> linkDescriptions = new ArrayList<>();
    public GeoSegment boundingBox;
}
