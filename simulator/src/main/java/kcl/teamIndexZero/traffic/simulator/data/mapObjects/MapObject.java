package kcl.teamIndexZero.traffic.simulator.data.mapObjects;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
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

    /* Random color selection*/
    public static final Color[] COLORS = {
            new Color(230, 0, 0),
            new Color(230, 150, 0),
            new Color(150, 0, 150),
            new Color(0, 150, 150),
            new Color(0, 160, 0),
            new Color(0, 0, 210)
    };

    protected static Logger_Interface LOG = Logger.getLoggerInstance(MapObject.class.getSimpleName());
    protected Lane lane;
    protected String name;
    protected double positionOnRoad = 0;
    protected SimulationMap map;
    protected boolean pleaseRemoveMeFromSimulation = false;
    private Color color;

    /**
     * Map object constructor - not meant to be actually called from outside, only by subclasses as super(). Anyway as
     * the class is abstract they will have to.
     *
     * @param name name of the object (to be shown in simulation maps)
     * @param lane lane to stay on initially
     */
    public MapObject(String name, Lane lane) {
        this.name = name;
        this.lane = lane;
        this.color = MapObject.getRandomColor();
    }

    /**
     * Getting random color from the list.
     *
     * @return in of the {@link Color} instances to draw
     */
    public static Color getRandomColor() {
        return COLORS[Math.min(
                COLORS.length - 1,
                (int) Math.round(Math.random() * COLORS.length))];
    }

    public boolean isPleaseRemoveMeFromSimulation() {
        return pleaseRemoveMeFromSimulation;
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
        //TODO: add appropriate width shift
        return lane.getRoad().getPolyline().getGeoPointAtDistanceFromStart(positionOnRoad);
    }
}
