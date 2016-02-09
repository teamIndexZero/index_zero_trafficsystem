package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;

import java.awt.*;

/**
 * Created by lexaux on 07/02/2016.
 */
public abstract class MapObject implements ISimulationAware {
    protected static Logger_Interface LOG = Logger.getLoggerInstance(MapObject.class.getSimpleName());
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

    public MapObject(String name, MapPosition position) {
        this.name = name;
        this.position = position;
        this.color = createRandomDarkColor();
    }

    private Color createRandomDarkColor() {
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
