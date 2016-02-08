package kcl.teamIndexZero.traffic.simulator.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by lexaux on 07/02/2016.
 */
public class SimulationTick {
    private static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public SimulationTick(int tickNumber, LocalDateTime simulatedTime, int tickDurationSeconds) {
        this.tickNumber = tickNumber;
        this.simulatedTime = simulatedTime;
        this.tickDurationSeconds = tickDurationSeconds;
    }

    @Override
    public String toString() {
        return String.format("{Tick #%d %s}", tickNumber, simulatedTime.format(DATE_TIME_FORMAT));
    }

    public int tickNumber;
    public LocalDateTime simulatedTime;
    public int tickDurationSeconds;
}
