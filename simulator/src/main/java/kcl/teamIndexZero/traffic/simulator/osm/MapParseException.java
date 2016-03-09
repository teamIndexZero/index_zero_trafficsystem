package kcl.teamIndexZero.traffic.simulator.osm;

/**
 */
public class MapParseException extends Exception {
    public MapParseException(String message) {
        super(message);
    }

    public MapParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapParseException(Throwable cause) {
        super(cause);
    }

    public MapParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
