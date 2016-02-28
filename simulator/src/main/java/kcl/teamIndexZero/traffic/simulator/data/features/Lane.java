package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;

/**
 * Created by Es on 28/02/2016.
 */
public class Lane {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Lane.class.getSimpleName());
    private double length;
    private double width = 3.2; //Default

    /**
     * Constructor (with default width = 3.2m)
     *
     * @param length Length of the lane in meters
     */
    public Lane(double length) {
        LOG.log("New lane created: length=", length, "m, width=", width, "m.");
        this.length = length;
    }

    /**
     * Constructor
     *
     * @param length Length of the lane in meters
     * @param width  Width of the lane in meters
     */
    public Lane(double length, double width) {
        LOG.log("New lane created: length=", length, "m, width=", width, "m.");
        this.length = length;
        this.width = width;
    }

    /**
     * Gets the width of the lane
     *
     * @return Width in meters
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * Gets the length of the lane
     *
     * @return Length in meters
     */
    public double getLength() {
        return this.length;
    }


}
