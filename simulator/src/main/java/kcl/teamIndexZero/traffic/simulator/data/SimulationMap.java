package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.features.Feature;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.MapObject;
import kcl.teamIndexZero.traffic.simulator.exceptions.MapIntegrityException;

import java.io.Serializable;
import java.util.*;

/**
 * An umbrella object containing map details for the simulation. Its responsibilities are clearly divided with Simulator:
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
public class SimulationMap implements ISimulationAware, Serializable {

    private static Logger_Interface LOG = Logger.getLoggerInstance(SimulationMap.class.getSimpleName());
    private final int width;
    private final int height;
    public double widthMeters;
    public double heightMeters;
    private Map<ID, Feature> mapFeatures = new HashMap<>();
    private Map<ID, Link> mapLinks;
    private Map<ID, MapObject> objectsOnSurface = new HashMap<>();

    /**
     * Constructor.
     *
     * @param width             map width
     * @param height            map height
     * @param graph_constructor a graph constructor object which has features alread created
     * @throws MapIntegrityException when there is an unrecoverable error in the integrity of the map
     */
    public SimulationMap(int width, int height, GraphConstructor graph_constructor) throws MapIntegrityException {
        this.width = width;
        this.height = height;
        graph_constructor.getFeatures().forEach((id, feature) -> {
            addFeature(feature);
        });
        this.mapLinks = graph_constructor.getLinks();
    }

    /**
     * Adds features to the map
     *
     * @param feature Feature to add
     */
    private void addFeature(Feature feature) {
        LOG.log_Trace("Adding feature '", feature.getID(), "' to map.");
        feature.setMap(this);
        this.mapFeatures.putIfAbsent(feature.getID(), feature);
    }

    /**
     * Get the features on the map
     *
     * @return all features on the map
     */
    public Map<ID, Feature> getMapFeatures() {
        return mapFeatures;
    }

    /**
     * Gets all the links on the map
     *
     * @return Links of the map
     */
    public Map<ID, Link> getMapLinks() {
        return mapLinks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick timeStep) {
        synchronized (this) {
            mapFeatures.forEach(
                    (id, feature) -> feature.tick(timeStep)
            );
            mapLinks.forEach(
                    (id, link) -> link.tick(timeStep)
            );
            objectsOnSurface.forEach((id, mapObject) ->
                    mapObject.tick(timeStep)
            );
        }
    }

    /**
     * @return width of the map
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return height of the map
     */
    public int getHeight() {
        return height;
    }

    /**
     * Add an object to the map. Object will be added at its current position.
     *
     * @param mapObject map object to add.
     */
    public void addMapObject(MapObject mapObject) {
        //todo check if this really does not occupy some other object's space on map
        objectsOnSurface.put(mapObject.getID(), mapObject);
        mapObject.setMap(this);
    }

    /**
     * Removes a mapObject from the map
     *
     * @param id ID tag of map object
     */
    public void removeMapObject(ID id) {
        objectsOnSurface.remove(id);
    }

    /**
     * Gets all objects on the surface of the map
     *
     * @return Read-only collection of the objects
     */
    public List<MapObject> getObjectsOnSurface() {
        return Collections.unmodifiableList(new ArrayList<>(objectsOnSurface.values()));
    }

    /**
     * toString method
     *
     * @return Description of the map
     */
    public String toString() {
        return "Map (" + this.width + "x" + this.height + ") has " + this.getObjectsOnSurface().size() + " objects.";
    }
}
