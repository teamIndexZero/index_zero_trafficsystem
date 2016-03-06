package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;

/**
 * ID class for simulation entities
 */
public class ID {
    private static Logger_Interface LOG = Logger.getLoggerInstance(ID.class.getSimpleName());
    private String id;

    /**
     * Constructor
     *
     * @param id           ID tag to copy
     * @param discriminant Discriminant to append at the end of the copied tag
     */
    public ID(ID id, String discriminant) {
        this.id = id.getId() + ":" + discriminant;
    }

    /**
     * Constructor
     *
     * @param id           ID tag to copy
     * @param discriminant Discriminant to add in from of the copied tag
     */
    public ID(String discriminant, ID id) {
        this.id = discriminant + ":" + id.getId();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ID id1 = (ID) o;

        return id != null ? id.equals(id1.id) : id1.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
