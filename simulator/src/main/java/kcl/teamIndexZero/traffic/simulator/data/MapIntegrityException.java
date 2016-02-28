package kcl.teamIndexZero.traffic.simulator.data;

/**
 * Created by Es on 28/02/2016.
 * Exception for when a graph of the map features's integrity is bad
 */
public class MapIntegrityException extends Exception {
    public MapIntegrityException() {
    }

    public MapIntegrityException(String message) {
        super(message);
    }
}
