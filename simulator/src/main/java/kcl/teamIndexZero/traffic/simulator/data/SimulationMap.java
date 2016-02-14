package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lexaux on 07/02/2016.
 */
public class SimulationMap implements ISimulationAware, Serializable {


    private final boolean map[][];
    private final int w;
    private final int h;

    private List<MapObject> objectsOnMap = new ArrayList<>();

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public SimulationMap(int w, int h) {
        this.w = w;
        this.h = h;
        map = new boolean[h][];
        for (int i = 0; i < h; i++) {
            map[i] = new boolean[w];
            Arrays.fill(map[i], false);
        }
    }


    @Override
    public void tick(SimulationTick timeStep) {
        objectsOnMap.forEach(
                object -> {
                    object.tick(timeStep);
                }
        );
    }

    public boolean[][] getMap() {
        return map;
    }

    public void addMapObject(MapObject mapObject) {
        //todo check if this really does not occupy some other object's space on map
        objectsOnMap.add(mapObject);
        mapObject.setMap(this);
    }

    public void moveObject(MapObject object, MapPosition pos) {
        // check rules of physics
        // check that we don't have overlaps
        MapPosition oldPos = object.getPosition();
        object.setPosition(pos);

        mapFillWithBusy(oldPos, false);
        mapFillWithBusy(pos, true);
    }

    private void mapFillWithBusy(MapPosition pos, boolean value) {
        int maxX = Math.min(pos.x + pos.width, getH() - 1);
        int maxY = Math.min(pos.y + pos.height, getW() - 1);
        for (int x = pos.x; x <= maxX; x++) {
            int xPos = Math.max(0, x);
            int yPos = Math.max(0, pos.y);
            Arrays.fill(map[xPos], yPos, maxY, value);
        }
    }

    public List<MapObject> getObjectsOnMap() {
        return objectsOnMap;
    }
}
