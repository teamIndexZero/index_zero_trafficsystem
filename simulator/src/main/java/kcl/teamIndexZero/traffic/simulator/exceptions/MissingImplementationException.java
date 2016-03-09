package kcl.teamIndexZero.traffic.simulator.exceptions;

/**
 * Created by Es on 02/03/2016.
 * Exception for when an implementation for a enum case type is missing.
 */
public class MissingImplementationException extends Exception {
    public MissingImplementationException() {
    }

    public MissingImplementationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingImplementationException(String message) {
        super(message);
    }
}
