package kcl.teamIndexZero.traffic.simulator;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.SimulationParams;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by lexaux on 07/02/2016.
 */
public class Simulator {

    private static Logger_Interface LOG = Logger.getLoggerInstance(Simulator.class.getSimpleName());

    private final SimulationParams simParams;
    private final List<ISimulationAware> simulationMap;
    private Instant startedSimulationTimestamp = null;
    private SimulationTick currentTick;


    public Simulator(SimulationParams params, List<ISimulationAware> simulationMap) {
        this.simParams = params;
        this.simulationMap = simulationMap;
        LOG.log("Constructed new simulator with no parameters");
    }


    public void start() {
        LOG.log(">>>>> Started simulation");
        startedSimulationTimestamp = Instant.now();

        for (int i = 0; i < simParams.durationInTicks; i++) {
            SimulationTick tick = nextTick();
            LOG.log(String.format(">>> %s Started", tick));
            simulationMap.forEach(simulationAware -> simulationAware.tick(tick));
            LOG.log(String.format(">>> %s done\n", tick));
        }
    }

    /*
    * Won't be able to use this till we get to threading
    */
    public void pause() {
        LOG.log(">>>>> Paused simulation");
    }

    /*
     * Won't be able to use this till we get to threading
     */
    public void stop() {
        LOG.log(">>>>> Stopped simulation");
        onSimulationFinish();
    }

    private void onSimulationFinish() {
        LOG.log(">>>>> Simulation has finished, real time: ", Duration.between(startedSimulationTimestamp, Instant.now()),
                "; simulation time: ", Duration.between(simParams.simulatedTimeStart, currentTick.simulatedTime));
    }

    private SimulationTick nextTick() {
        if (currentTick == null) {
            currentTick = new SimulationTick(0, simParams.simulatedTimeStart, simParams.tickSeconds);
        } else {
            currentTick = new SimulationTick(
                    currentTick.tickNumber + 1,
                    currentTick.simulatedTime.plus(simParams.tickSeconds, ChronoUnit.SECONDS),
                    simParams.tickSeconds);
        }
        return currentTick;
    }
}
