package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.Feature;
import kcl.teamIndexZero.traffic.simulator.data.features.Junction;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;
import kcl.teamIndexZero.traffic.simulator.exceptions.JunctionPathException;

import java.util.ArrayList;
import java.util.List;

/**
 * Special Lane level link for Junction enabling access to Junction's behaviour
 * Note: use *only* for Junctions!
 */
public class JunctionLink extends Link {
    private static Logger_Interface LOG = Logger.getLoggerInstance(JunctionLink.class.getSimpleName());
    private Junction junction;
    private Road road;

    /**
     * Constructor
     *
     * @param id       Link ID tag
     * @param junction Junction the link belongs to
     */
    public JunctionLink(ID id, Road road, Junction junction) {
        super(id);
        this.road = road;
        this.junction = junction;
    }

    /**
     * gets the possible exit links on a junction
     *
     * @return exit links
     */
    public List<Link> getLinks() {
        try {
            return junction.getNextLinks(super.getID());
        } catch (JunctionPathException e) {
            LOG.log_Fatal("No exit link from the junction were found! i.e.: Car is stuck!");
            return new ArrayList<>();
        }
    }

    /**
     * Gets the ID tag of the road the lane belongs to
     *
     * @return Parent road ID tag
     */
    public ID getRoadID() {
        return this.road.getID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Feature getNextFeature() {
        return super.out;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
        super.tick(tick);
    }
}
