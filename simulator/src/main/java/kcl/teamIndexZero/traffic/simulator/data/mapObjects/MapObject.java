package kcl.teamIndexZero.traffic.simulator.data.mapObjects;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.features.Lane;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;

import java.awt.*;

/**
 * An object (whether passive or active) belonging to map. For now it has color and position, and it is abstract (
 * subclasses needs to implement actual {@link ISimulationAware} interface).
 * <p>
 * Map object is an abstraction over more complex entities (like vehicles and obstacles etc). {@link SimulationMap}
 * inserts itself into the {@link MapObject} when that in gets added to the map, so that
 */
public abstract class MapObject implements ISimulationAware {

    protected static Logger_Interface LOG = Logger.getLoggerInstance(MapObject.class.getSimpleName());
    protected Lane lane;
    protected String name;
    protected double positionOnLane = 0;
    protected SimulationMap map;
    private Color color;
    private ID id;

    /**
     * Map object constructor - not meant to be actually called from outside, only by subclasses as super(). Anyway as
     * the class is abstract they will have to.
     *
     * @param name name of the object (to be shown in simulation maps)
     * @param lane lane to stay on initially
     */
    public MapObject(ID id, String name, Lane lane) {
        this.name = name;
        this.lane = lane;
        this.id = id;
        this.color = Color.BLACK;
    }

    public ID getID() {
        return this.id;
    }

    public Lane getLane() {
        return lane;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setMap(SimulationMap map) {
        this.map = map;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAndRoad() {
        return name + " " + lane.getRoad().getName();
    }

    public GeoPoint getPositionOnMap() {
        return lane.getPolyline().getGeoPointAtDistanceFromStart(positionOnLane);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapObject mapObject = (MapObject) o;

        return id != null ? id.equals(mapObject.id) : mapObject.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public double getPositionOnLane() {
        return positionOnLane;
    }
}
