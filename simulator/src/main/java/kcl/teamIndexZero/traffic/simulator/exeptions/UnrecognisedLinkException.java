package kcl.teamIndexZero.traffic.simulator.exeptions;

/**
 * Created by Es on 01/03/2016.
 * Exception for when a link/link description does not match loaded features
 */
public class UnrecognisedLinkException extends Exception {
    public UnrecognisedLinkException() {
    }

    public UnrecognisedLinkException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnrecognisedLinkException(String message) {
        super(message);
    }
}
