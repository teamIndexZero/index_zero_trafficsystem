package kcl.teamIndexZero.traffic.simulator.exceptions;

/**
 * Created by Es on 01/03/2016.
 * Exception for when a link/link description does not match loaded features
 */
public class BadLinkException extends Exception {
    public BadLinkException() {
    }

    public BadLinkException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadLinkException(String message) {
        super(message);
    }
}
