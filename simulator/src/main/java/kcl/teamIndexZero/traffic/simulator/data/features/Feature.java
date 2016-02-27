package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

import java.util.ArrayList;
import java.util.List;

public class Feature implements ISimulationAware {
    private static Logger_Interface LOG = Logger.getLoggerInstance(Feature.class.getSimpleName());
    private List<Link> links = new ArrayList<>();
    private ID id;

    /**
     * Constructor
     * @param id Feature ID tag
     */
    public Feature( ID id ) {
        this.id = id;
    }

    /**
     * Adds a Link to the Feature
     * @param link Link to add
     */
    public void addLink( Link link ) {
        links.add( link );
    }

    /**
     * Gets the links connected to the Feature
     * @return List of links connected
     */
    public List<Link> getLinks() {
        return this.links;
    }

    /**
     * Gets the Feature's ID tag
     * @return ID tag
     */
    public ID getID() {
        return this.id;
    }

    @Override
    public void tick(SimulationTick tick) {

    }
}
