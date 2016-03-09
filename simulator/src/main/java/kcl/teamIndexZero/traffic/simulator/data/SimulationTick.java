package kcl.teamIndexZero.traffic.simulator.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * It is an important data class encapsulating information about single tick - that is, single step of simulation.
 * For now we are mostly intersted in several things:
 * <p>
 * 1. What's the ordinal number of this tick?
 * 2. How long does this tick span for (in simulated time)? E.g. in tick may be equal 1 second or 1 minute, that is
 * our simulation granularity
 * 3. What is the tick's simulated moment in time? (it may be used in future - consider, for example, night time
 * approaching or dusk forcing drivers to lower the speed so that they can react, or a time dependency for traffic
 * generator - in the morning everyone commutes, during the day it's not the case.
 */
public class SimulationTick {

    private static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private int tickNumber;
    private LocalDateTime simulatedTime;
    private int tickDurationSeconds;

    /**
     * Constructor for tick.
     *
     * @param tickNumber          ordinal number in current simulation run.
     * @param simulatedTime       time this tick represents within a simulation run (i.e. date)
     * @param tickDurationSeconds duration of single tick in simulated time
     */
    public SimulationTick(int tickNumber, LocalDateTime simulatedTime, int tickDurationSeconds) {
        this.tickNumber = tickNumber;
        this.simulatedTime = simulatedTime;
        this.tickDurationSeconds = tickDurationSeconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("{Tick #%d %s}", tickNumber, simulatedTime.format(DATE_TIME_FORMAT));
    }

    public int getTickDurationSeconds() {
        return tickDurationSeconds;
    }

    public LocalDateTime getSimulatedTime() {
        return simulatedTime;
    }

    public int getTickNumber() {
        return tickNumber;
    }
}
