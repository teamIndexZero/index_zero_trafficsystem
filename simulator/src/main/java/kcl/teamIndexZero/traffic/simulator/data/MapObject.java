package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;

import java.awt.*;
import java.io.Serializable;

/**
 * An object (whether passive or active) belonging to map. For now it has color and position, and it is abstract (
 * subclasses needs to implement actual {@link ISimulationAware} interface).
 * <p>
 * Map object is an abstraction over more complex entities (like vehicles and obstacles etc). {@link SimulationMap}
 * inserts itself into the {@link MapObject} when that one gets added to the map, so that
 */
public abstract class MapObject implements ISimulationAware, Serializable {

    protected static Logger_Interface LOG = Logger.getLoggerInstance(MapObject.class.getSimpleName());

    /* Random color selection*/
    protected static final Color[] COLORS = {
            Color.RED,
            Color.GREEN,
            Color.BLACK,
            Color.BLUE,
            Color.MAGENTA,
            Color.DARK_GRAY,
            Color.CYAN
    };

    private final Color color;
    protected String name;
    protected MapPosition position;
    protected SimulationMap map;

    /**
     * Map object constructor - not meant to be actually called from outside, only by subclasses as super(). Anyway as
     * the class is abstract they will have to.
     *
     * @param name     name of the object (to be shown in simulation maps)
     * @param position position of the object on map.
     */
    public MapObject(String name, MapPosition position) {
        this.name = name;
        this.position = position;
        this.color = getRandomColor();
    }

    /**
     * Getting random color from the list.
     *
     * @return one of the {@link Color} instances to draw
     */
    private Color getRandomColor() {
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

    public void setPosition(MapPosition position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
