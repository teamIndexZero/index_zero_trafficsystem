package kcl.teamIndexZero.traffic.simulator.exceptions;

/**
 * Created by Es on 08/03/2016.
 * Exception for when a feature doesn't have a link
 */
public class DeadEndFeatureException extends Exception {
    public DeadEndFeatureException() {
        super();
    }

    public DeadEndFeatureException(String message) {
        super(message);
    }

    public DeadEndFeatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeadEndFeatureException(Throwable cause) {
        super(cause);
    }
}
