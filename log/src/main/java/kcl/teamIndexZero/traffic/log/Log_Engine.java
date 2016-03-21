package kcl.teamIndexZero.traffic.log;

import kcl.teamIndexZero.traffic.log.microLogger.MicroLogger;
import kcl.teamIndexZero.traffic.log.outputs.Output;

/**
 * Log engine
 */
public class Log_Engine {
    private static Log_Engine instance;
    private Log_Config global_config;
    private Long session_msg_number;

    /**
     * Gets the Log_Engine instance
     *
     * @return Log_Engine instance
     * @throws NullPointerException when the instance is not initiated
     */
    public static Log_Engine getInstance() throws NullPointerException {
        if (instance == null) {
            MicroLogger.INSTANCE.log_Fatal("[Log_Engine.getInstance()] The static instance of the Log_Engine is null. Loader [.load( log_Engine locator ) was likely not called previous to [.getInstance()].");
            throw new NullPointerException();
        }
        return instance;
    }

    /**
     * Sets the Log_Engine instance
     *
     * @param locator new Log_Engine instance
     */
    public static void load(Log_Engine locator) {
        instance = locator;
    }

    /**
     * Constructor
     *
     * @param config LogConfig object to use
     */
    public Log_Engine(Log_Config config) {
        global_config = config;
        session_msg_number = new Long(0);
    }

    /**
     * Sends the log messages to outputs
     *
     * @param time_stamp   Time stamp of the message
     * @param level        Log level
     * @param class_origin Name of the message's origin
     * @param objects      Log message details
     */
    protected void processLogMsg(Log_TimeStamp time_stamp, int level, String class_origin, Object... objects) {
        if (level > Log_Levels.OFF && level <= global_config.getGlobalLogLevel()) {
            for (Output out : global_config.getOutputs()) {
                out.output(class_origin, level, this.session_msg_number, time_stamp, objects);
            }
        }
        session_msg_number++;
    }

    /**
     * Sends the exception details to outputs
     *
     * @param time_stamp   Time stamp of the message
     * @param class_origin Name of the message's origin
     * @param e            Exception raised
     */
    protected void processException(Log_TimeStamp time_stamp, String class_origin, Exception e) {
        if (global_config.getGlobalLogLevel() > Log_Levels.OFF && global_config.getLogExceptionFlag()) {
            for (Output out : global_config.getOutputs()) {
                out.output(class_origin, time_stamp, this.session_msg_number, e);
            }
        }
        session_msg_number++;
    }
}
