package kcl.teamIndexZero.traffic.simulator.data;

import java.io.Serializable;

/**
 * Class encapsulating position of the object on the map. For now it is simplistic - just a pair of int coordinates and
 * width/height, but we are going to extend it to the lane/float later.
 * <p>
 * TODO: modify coordinate system of the whole thing to accomodate for floating point coordinates and object belonging to a lane.
 */
public class MapPosition implements Serializable {
    public int x;
    public int y;
    public int width;
    public int height;

    /**
     * Constructor
     *
     * @param x      coordinate
     * @param y      coordinate
     * @param width  object width
     * @param height object height
     */
    public MapPosition(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return String.format("{Pos xywh: %d,%d,%d,%d}", x, y, width, height);
    }
}
