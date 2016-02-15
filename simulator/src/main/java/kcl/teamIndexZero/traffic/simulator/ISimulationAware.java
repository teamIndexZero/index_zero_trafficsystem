package kcl.teamIndexZero.traffic.simulator;

import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

/**
 * Basic interface for everything which lives in simulator and has a notion of 'next-time'.
 * <p>
 * If a class implements ISimulationAware it can handle simulation event - tick. Class may be everything, examples:
 * - Model:
 * - Car
 * - Obstacle
 * - Traffic Light
 * - Pedestrian
 * - Technical
 * - Map serializer (thing which saves map state to the file)
 * - Traffic generator (thing which adds cars
 * - GUI drawing panel.
 */
public interface ISimulationAware {

    /**
     * Callback method called by simulator when there is a next tick due.
     *
     * @param tick tick we are working on. It has simulated time, sequence number at least
     */
    void tick(SimulationTick tick);
}
