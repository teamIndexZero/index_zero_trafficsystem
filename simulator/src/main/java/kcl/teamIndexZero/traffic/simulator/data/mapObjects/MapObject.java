package kcl.teamIndexZero.traffic.simulator.data.mapObjects;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;

import java.awt.*;

/**
 * An object (whether passive or active) belonging to map. For now it has color and position, and it is abstract (
 * subclasses needs to implement actual {@link ISimulationAware} interface).
 * <p>
 * Map object is an abstraction over more complex entities (like vehicles and obstacles etc). {@link SimulationMap}
 * inserts itself into the {@link MapObject} when that one gets added to the map, so that
 */
public abstract class MapObject implements ISimulationAware {

    protected static Logger_Interface LOG = Logger.getLoggerInstance(MapObject.class.getSimpleName());

    /* Random color selection*/
    public static final Color[] COLORS = {
            new Color(230, 0, 0),
            new Color(230, 150, 0),
            new Color(150, 0, 150),
            new Color(0, 150, 150),
            new Color(0, 160, 0),
            new Color(0, 0, 210)
    };

    private Color color;
    protected String name;
    protected MapPosition position;
    protected Road road;

    public Road getRoad() {
        return road;
    }

    protected double positionOnRoad = 0;
    protected SimulationMap map;

    /**
     * Map object constructor - not meant to be actually called from outside, only by subclasses as super(). Anyway as
     * the class is abstract they will have to.
     *
     * @param name     name of the object (to be shown in simulation maps)
     * @param position position of the object on map.
     */
    public MapObject(String name, MapPosition position, Road road) {
        this.name = name;
        this.position = position;
        this.road = road;
        this.color = MapObject.getRandomColor();
    }

    /**
     * Getting random color from the list.
     *
     * @return one of the {@link Color} instances to draw
     */
    public static Color getRandomColor() {
        return COLORS[Math.min(
                COLORS.length - 1,
                (int) Math.round(Math.random() * COLORS.length))];
    }

    public Color getColor() {
        return color;
    }

    public void setMap(SimulationMap map) {
        this.map = map;
    }

    public MapPosition getPosition() {
        return position;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setPosition(MapPosition position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public String getNameAndRoad() {
        String roadName = getRoad().getName();
        return name + " " + (roadName != null ? roadName : "NONAME ") + " at pos " + getPositionOnMap();
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getPositionOnMap() {
        return road.getPolyline().getGeoPointAtDistanceFromStart(positionOnRoad);
    }
}
