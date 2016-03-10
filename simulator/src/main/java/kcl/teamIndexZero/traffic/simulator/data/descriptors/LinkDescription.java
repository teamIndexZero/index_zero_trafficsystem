package kcl.teamIndexZero.traffic.simulator.data.descriptors;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;
import kcl.teamIndexZero.traffic.simulator.data.links.LinkType;

/**
 * Class to contain the specifications of a link
 */
public class LinkDescription {
    public ID fromID;
    public ID toID;
    public LinkType type;
    public ID linkID;
    public GeoPoint geoPoint;

    /**
     * Constructor
     *
     * @param from   From ID tag
     * @param to     To ID tag
     * @param type   Link type
     * @param linkID Link's ID tag
     */
    public LinkDescription(ID from, ID to, LinkType type, ID linkID, GeoPoint geoPoint) {
        this.fromID = from;
        this.toID = to;
        this.type = type;
        this.linkID = linkID;
        this.geoPoint = geoPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LinkDescription that = (LinkDescription) o;

        if (fromID != null ? !fromID.equals(that.fromID) : that.fromID != null) return false;
        if (toID != null ? !toID.equals(that.toID) : that.toID != null) return false;
        if (type != that.type) return false;
        return linkID != null ? linkID.equals(that.linkID) : that.linkID == null;

    }

    @Override
    public int hashCode() {
        int result = fromID != null ? fromID.hashCode() : 0;
        result = 31 * result + (toID != null ? toID.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (linkID != null ? linkID.hashCode() : 0);
        return result;
    }

    /**
     * toString method
     *
     * @return String with the LinkDescription's info
     */
    public String toString() {
        return this.linkID + "[ " + fromID + " -> " + toID + " ]";
    }
}
