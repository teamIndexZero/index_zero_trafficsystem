package kcl.teamIndexZero.traffic.simulator.exeptions;

/**
 * Created by Es on 28/02/2016.
 * Exception for when a simulation map is empty of features
 */
public class EmptySimMapException extends Exception {
    public EmptySimMapException() {
    }

    public EmptySimMapException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptySimMapException(String message) {
        super(message);
    }
}
