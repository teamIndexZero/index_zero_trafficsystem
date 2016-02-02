package kcl.teamIndexZero.traffic.log;

/**
 * Created by Es on 27/01/2016.
 */
public interface Logger_Interface {
    /**
     * Logs a standard message
     *
     * @param objects description
     */
    void log(Object... objects);

    /**
     * Logs a fatal message
     *
     * @param objects description
     */
    void log_Fatal(Object... objects);

    /**
     * Logs an error message
     *
     * @param objects description
     */
    void log_Error(Object... objects);

    /**
     * Logs a warning message
     *
     * @param objects description
     */
    void log_Warning(Object... objects);

    /**
     * Logs a debug message
     *
     * @param objects description
     */
    void log_Debug(Object... objects);

    /**
     * Logs a trace message
     *
     * @param objects description
     */
    void log_Trace(Object... objects);
}
