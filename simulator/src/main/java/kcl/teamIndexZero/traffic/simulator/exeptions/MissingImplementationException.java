package kcl.teamIndexZero.traffic.simulator.exeptions;

/**
 * Created by Es on 02/03/2016.
 * Exception for when an implementation for a enum case type is missing.
 */
public class MissingImplementationException extends Exception {
    public MissingImplementationException() {
    }

    public MissingImplementationException(String message) {
        super(message);
    }
}
