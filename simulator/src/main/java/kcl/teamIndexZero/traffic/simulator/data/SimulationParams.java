package kcl.teamIndexZero.traffic.simulator.data;

import java.time.LocalDateTime;

/**
 * Parameters for simulation.
 */
public class SimulationParams {

    // no need to encapsulate these - they're going to be rather simplistic uses for these fields
    public LocalDateTime simulatedTimeStart;
    public int tickSeconds;
    public int durationInTicks;

    /**
     * Parameters for the simulation.
     *
     * @param simulatedTimeStart simulated time, not real time. I.e. we start our 'simulated' time on 12th of Feb 2013 18:00
     * @param tickSeconds        how many seconds simulated time doe s one tick correspond to
     * @param durationInTicks    how many ticks should the simulation be run for
     */
    public SimulationParams(LocalDateTime simulatedTimeStart, int tickSeconds, int durationInTicks) {
        this.simulatedTimeStart = simulatedTimeStart;
        this.tickSeconds = tickSeconds;
        this.durationInTicks = durationInTicks;
    }
}
