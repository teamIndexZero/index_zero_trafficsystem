package kcl.teamIndexZero.traffic.simulator.data;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.features.Feature;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.MapObject;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.MapPosition;
import kcl.teamIndexZero.traffic.simulator.exeptions.EmptySimMapException;
import kcl.teamIndexZero.traffic.simulator.exeptions.MapIntegrityException;
import kcl.teamIndexZero.traffic.simulator.exeptions.OrphanFeatureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class SimulationMap implements ISimulationAware {

    public double lonStart;
    public double latStart;
    public double lonEnd;
    public double latEnd;

    private static Logger_Interface LOG = Logger.getLoggerInstance(SimulationMap.class.getSimpleName());
    private Map<ID, Feature> mapFeatures;
    private Map<ID, Link> mapLinks;
    private final int width;
    private final int height;
    private List<MapObject> objectsOnSurface = new ArrayList<>();

    public Map<ID, Feature> getMapFeatures() {
        return mapFeatures;
    }

    public Map<ID, Link> getMapLinks() {
        return mapLinks;
    }

    /**
     * Constructor.
     *
     * @param width  map width
     * @param height map height
     * @throws EmptySimMapException   when there are no features
     * @throws OrphanFeatureException when there is 1+ unconnected features
     */
    public SimulationMap(int width, int height, GraphConstructor graph_constructor) throws MapIntegrityException {
        this.width = width;
        this.height = height;
        this.mapFeatures = graph_constructor.getFeatures();
        this.mapLinks = graph_constructor.getLinks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick timeStep) {
        mapFeatures.forEach(
                (id, feature) -> feature.tick(timeStep)
        );
        mapLinks.forEach(
                (id, link) -> link.tick(timeStep)
        );
        objectsOnSurface.forEach(
                object -> object.tick(timeStep)
        );
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
        objectsOnSurface.add(mapObject);
        mapObject.setMap(this);
    }

    /**
     * Try moving object from one position to another (it may be impossible - i.e. occupied). Old position will be freed
     * while the new one will be occupied if it goes successfully.
     *
     * @param object an object to add
     * @param pos    position to move to.
     */
    public void moveObject(MapObject object, MapPosition pos) {
        // TODO check rules of physics
        // TODO  check that we don't have overlaps
        MapPosition oldPos = object.getPosition();
        object.setPosition(pos);
    }

    /**
     * TODO - bad example, we're exposing the objects from map. It should be encapsulated.
     *
     * @return objects on map.
     */
    public List<MapObject> getObjectsOnSurface() {
        return objectsOnSurface;
    }
}
