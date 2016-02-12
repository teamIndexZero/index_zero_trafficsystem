package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An umbrella object containing map deails for the simulation. Its responsibilities are clearly divided with Simulator:
 * {@link kcl.teamIndexZero.traffic.simulator.Simulator} is a control interface whilst the {@link SimulationMap} is a
 * collection of objects relating to the map
 * <p>
 * It will contain individual roads, lanes, crossings, objects, etc. etc. For the time being it only contains
 * <p>
 * For now SimulationMap has coordinates as integers, but this is not really relevant. We will need to get to (in terms of
 * coordinate systems) to:
 * <p>
 * 1) coordinates being a scalar (position on the lane)
 * 2) lanes belonging to roads
 * 3) roads being connected to a physical map (i.e. car would not have xy coords, instead it woudl just say 'I am in
 * position 23.442 on the lane Z, and lane Z would be physically located somewhere (and so we can tell the car position
 * from these coordinate system composition.
 */
public class SimulationMap implements ISimulationAware {

    private final int w;
    private final int h;
    private List<MapObject> objectsOnMap = new ArrayList<>();

    public SimulationMap(int width, int height) {
        this.w = width;
        this.h = height;
    }

    @Override
    public void tick(SimulationTick timeStep) {
        objectsOnMap.forEach(
                object -> {
                    object.tick(timeStep);
                }
        );
    }

    public int getW() {
        return w;

    }

    public int getH() {
        return h;
    }

    public void addMapObject(MapObject mapObject) {
        //todo check if this really does not occupy some other object's space on map
        objectsOnMap.add(mapObject);
        mapObject.setMap(this);
    }

    public void moveObject(MapObject object, MapPosition pos) {
        // TODO check rules of physics
        // TODO  check that we don't have overlaps
        MapPosition oldPos = object.getPosition();
        object.setPosition(pos);
    }

    public List<MapObject> getObjectsOnMap() {
        return objectsOnMap;
    }
}
