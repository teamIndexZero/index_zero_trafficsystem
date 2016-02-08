package kcl.teamIndexZero.traffic.simulator;

import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

/**
 * Basic interface for everything which lives in simulator and has a notion of 'next-time'.
 */
public interface ISimulationAware {
    void tick(SimulationTick tick);
}
