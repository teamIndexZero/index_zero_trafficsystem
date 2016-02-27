package kcl.teamIndexZero.traffic.simulator.data.features;

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
     * @param id Identification tag
     */
    ID( String id ) {
        this.id = id;
    }

    /**
     * Gets the identification tag
     * @return ID tag
     */
    public String getId() {
        return this.id;
    }
}
