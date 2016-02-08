package kcl.teamIndexZero.traffic.log;

import kcl.teamIndexZero.traffic.log.outputs.Output;

/**
 * Created by Es on 27/01/2016.
 * Log engine
 */
public class Log_Engine {
    private Log_Config global_config;
    private Long session_msg_number;
    private static Log_Engine INSTANCE;

    /**
     * Constructor
     */
    private Log_Engine() {
        global_config = new Log_Config();
        session_msg_number = new Long(0);
    }

    /**
     * Gets the instance of the Log_Engine
     *
     * @return Log_Engine instance
     */
    public static Log_Engine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Log_Engine();
        }
        return INSTANCE;
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
    protected void processException(Log_TimeStamp time_stamp, String class_origin, Exception e ) {
        if (global_config.getGlobalLogLevel() > 0 && global_config.getLogExceptionFlag() ) {
            for (Output out : global_config.getOutputs()) {
                out.output(class_origin, time_stamp, this.session_msg_number, e );
            }
        }
        session_msg_number++;
    }
}
