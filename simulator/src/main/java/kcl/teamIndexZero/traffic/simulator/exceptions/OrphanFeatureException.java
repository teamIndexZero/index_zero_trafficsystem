package kcl.teamIndexZero.traffic.simulator.exceptions;

/**
 * Created by Es on 28/02/2016.
 * Exception for when a simulation map has unconnected features
 */
public class OrphanFeatureException extends Exception {
    public OrphanFeatureException() {
    }

    public OrphanFeatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrphanFeatureException(String message) {
        super(message);
    }
}
