package kcl.teamIndexZero.traffic.simulator.exeptions;

/**
 * Created by Es on 05/03/2016.
 * Exception for when a junction path is non existent
 */
public class JunctionPathException extends Exception {
    public JunctionPathException() {
    }

    public JunctionPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public JunctionPathException(String message) {
        super(message);
    }
}