package kcl.teamIndexZero.traffic.simulator.data;

import java.time.LocalDateTime;

/**
 * Created by lexaux on 07/02/2016.
 */

public class SimulationParams {


    public LocalDateTime simulatedTimeStart;
    public int tickSeconds;
    public int durationInTicks;

    public SimulationParams(LocalDateTime simulatedTimeStart, int tickSeconds, int durationInTicks) {
        this.simulatedTimeStart = simulatedTimeStart;
        this.tickSeconds = tickSeconds;
        this.durationInTicks = durationInTicks;
    }
}
