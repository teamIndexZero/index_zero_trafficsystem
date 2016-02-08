package kcl.teamIndexZero.traffic.simulator.data;

/**
 * Created by lexaux on 07/02/2016.
 */
public class MapPosition {
    public int x;
    public int y;
    public int width;
    public int height;

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
