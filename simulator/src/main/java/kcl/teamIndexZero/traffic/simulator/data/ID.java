package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;

/**
 * Created by Es on 25/02/2016.
 */
public class ID {
    private static Logger_Interface LOG = Logger.getLoggerInstance(ID.class.getSimpleName());
    private String id;

    /**
     * Constructor
     *
     * @param id           ID tag to copy
     * @param discriminant Discriminant to add to the copied tag
     */
    public ID(ID id, String discriminant) {
        this.id = id.getId() + ":" + discriminant;
    }

    /**
     * Constructor
     *
     * @param id Identification tag
     */
    public ID(String id) {
        this.id = id;
    }

    /**
     * Gets the identification tag
     *
     * @return ID tag
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the String of the ID tag
     *
     * @return ID tag as a String
     */
    public String toString() {
        return this.id;
    }
}
