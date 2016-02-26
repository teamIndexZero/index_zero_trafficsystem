package kcl.teamIndexZero.traffic.simulator;

/**
 * Factory for simulator object.
 */
public interface SimulatorFactory {
    /**
     * Create a new, or get an existing one, simulator.
     *
     * @return simulator instance.
     */
    Simulator createSimulator();
}
