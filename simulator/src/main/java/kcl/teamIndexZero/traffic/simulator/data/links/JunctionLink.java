package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.Junction;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;

import java.util.List;

/**
 * Special Lane level link for Junction enabling access to Junction's behaviour
 */
public class JunctionLink extends Link {
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
        return junction.getLinks();
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
    public void tick(SimulationTick tick) {
        super.tick(tick);
    }
}
