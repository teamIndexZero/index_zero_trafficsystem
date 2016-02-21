package kcl.teamIndexZero.traffic.simulator;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.SimulationParams;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simulator is an abstraction of simulation control (nothing more). It is the class which has main time loop and
 * advances time, notifying all the dependent classes on time change as soon as they're looped into Simulator and implement
 * {@link ISimulationAware}.
 * <p>
 * For the time being Simulator runs on the main thread, however it may be a good idea to move it to a separate thread so
 * that we can control its flow (pause, terminate) by listening to external events (CLI, GUI).
 * <p>
 * Important feature - Simulator guarantees to run the {@link ISimulationAware} in the order they were passed to ther params.
 */
public class Simulator {

    private static Logger_Interface LOG = Logger.getLoggerInstance(Simulator.class.getSimpleName());
    private final SimulationParams simParams;
    private final List<ISimulationAware> iSimulationAwares;
    private SimulationTick currentTick;
    private Instant startedSimulationTimestamp = null;
    private AtomicBoolean paused = new AtomicBoolean(false);
    private AtomicBoolean stopped = new AtomicBoolean(false);

    /**
     * Creates a new simulator with the desired params and {@link List<ISimulationAware>} of instances listening to
     * simulation events.
     *
     * @param params              {@link SimulationParams} generic parameters for simulation (time, duration etc)
     * @param simulationListeners an ordered list of {@link ISimulationAware} implementing classes which will get notified
     *                            when the time ticks
     */
    public Simulator(SimulationParams params, List<ISimulationAware> simulationListeners) {
        this.simParams = params;
        this.iSimulationAwares = simulationListeners;
        LOG.log("Constructed new simulator with no parameters");
    }


    /**
     * Start the simulation with the parameters provided before.
     */
    public synchronized void start() {
        try {
            LOG.log(">>>>> Started simulation");
            startedSimulationTimestamp = Instant.now();

            for (int i = 0; i < simParams.durationInTicks; i++) {
                if (paused.get()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        LOG.log_Exception(e);
                    }
                }

                if (stopped.get()) {
                    return;
                }

                SimulationTick tick = nextTick();
                LOG.log(String.format(">>> %s Started", tick));
                iSimulationAwares.forEach(simulationAware -> simulationAware.tick(tick));
                LOG.log(String.format(">>> %s done\n", tick));
            }
        } finally {
            onSimulationFinish();
        }
    }

    /**
     * Temporarily pause the simulation.
     * <p>
     * TODO: as we move this to two threads at least it will start working. For now it is useless
     */
    public void pause() {
        paused.set(true);
    }

    /**
     * Terminate the simulation prematurely.
     * <p>
     * TODO:as we add two threads, should have this working. For now useless.
     */
    public void stop() {
        stopped.set(true);
    }

    /**
     * Resumes execution.
     */
    public synchronized void resume() {
        paused.set(false);
        notify();
    }

    /*
    Called by simulator when the simulation it is finished (callback method). Outputs some stats for the simulation.
     */
    private void onSimulationFinish() {
        LOG.log(">>>>> Simulation has finished, real time: ", Duration.between(startedSimulationTimestamp, Instant.now()),
                "; simulation time: ", Duration.between(simParams.simulatedTimeStart, currentTick.getSimulatedTime()));
    }

    /*
    Internal loop of the simulator.

    Within the loop, we are:
    1. Creating a next Tick object with the number=number+1, simulatedTime = prevSimulatedTime+(How many one tick means
        seconds)
    2. Run the tick aware listeners one by one (whatever they are).
     */
    private SimulationTick nextTick() {
        if (currentTick == null) {
            currentTick = new SimulationTick(0, simParams.simulatedTimeStart, simParams.tickSeconds);
        } else {
            currentTick = new SimulationTick(
                    currentTick.getTickNumber() + 1,
                    currentTick.getSimulatedTime().plus(simParams.tickSeconds, ChronoUnit.SECONDS),
                    simParams.tickSeconds);
        }
        return currentTick;
    }

}
