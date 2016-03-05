package kcl.teamIndexZero.traffic.simulator.exceptions;

/**
 * Created by Es on 28/02/2016.
 * Exception for when a graph of the map features's integrity is bad
 */
public class MapIntegrityException extends Exception {
    public MapIntegrityException() {
    }

    public MapIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapIntegrityException(String message) {
        super(message);
    }
}
