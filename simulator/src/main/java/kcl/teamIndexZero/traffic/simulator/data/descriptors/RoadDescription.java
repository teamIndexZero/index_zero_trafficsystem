package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPolyline;

/**
 * Class to contain the specifications of a road
 * TODO add understanding of what is forward/backward.
 */
public class RoadDescription {
    private final ID id;
    private GeoPolyline geoPolyline;
    private String roadName;
    private int laneCountForward;
    private int laneCountBackward;

    /**
     * Constructor
     *
     * @param laneCountForward  Number of lanes on side A of the road
     * @param laneCountBackward Number of lanes on side B of the road
     * @param id                Road's ID tag
     */
    public RoadDescription(ID id, String roadName, GeoPolyline geoPolyline, int laneCountForward, int laneCountBackward) {
        this.laneCountForward = laneCountForward;
        this.laneCountBackward = laneCountBackward;
        this.id = id;
        this.geoPolyline = geoPolyline;
        this.roadName = roadName;
    }

    public GeoPolyline getGeoPolyline() {
        return geoPolyline;
    }

    /**
     * Gets the lane count for side A
     *
     * @return Lane count
     */
    public int getLaneCountForward() {
        return laneCountForward;
    }

    /**
     * Gets the lane count for side B
     *
     * @return Lane count
     */
    public int getLaneCountBackward() {
        return laneCountBackward;
    }

    /**
     * Gets the Road's ID tag
     *
     * @return Road ID tag
     */
    public ID getId() {
        return id;
    }

    /**
     * Gets the length of the road
     *
     * @return Length
     */
    public double getLength() {
        return geoPolyline.getPolylineLength();
    }

    /**
     * Gets the name of the road
     *
     * @return Road's name
     */
    public String getRoadName() {
        return roadName;
    }
}
