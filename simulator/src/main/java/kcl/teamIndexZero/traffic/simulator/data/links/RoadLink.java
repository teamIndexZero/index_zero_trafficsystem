package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.Feature;

import java.util.List;

/**
 * Created by Es on 02/03/2016.
 * RoadLink class
 * <p>Class describing the relation between two macro-features such as roads and junctions.
 * Holds the micro-feature (such as Lanes) links as directed inflow lists.</p>
 */
public class RoadLink implements ISimulationAware {
    public Feature a;
    public Feature b;
    public List<Link> a_inflow;
    public List<Link> b_inflow;

    /**
     * Constructor
     *
     * @param a             Feature A to link
     * @param inflow_from_a Inflow lanes from feature A
     * @param b             Feature B to link
     * @param inflow_from_b Inflow lanes from feature B
     */
    public RoadLink(Feature a, List<Link> inflow_from_a, Feature b, List<Link> inflow_from_b) {
        this.a = a;
        this.b = b;
        this.a_inflow = inflow_from_a;
        this.b_inflow = inflow_from_b;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
        //TODO call the ticks inside the inflows?--check!
    }
}
