package kcl.teamIndexZero.traffic.log;

import kcl.teamIndexZero.traffic.log.Outputs.Output;

/**
 * Created by Es on 27/01/2016.
 * Log engine
 */
public class Log_Engine {
    private static Log_Engine INSTANCE = new Log_Engine();
    private Log_Config global_config = new Log_Config();
    private Long session_msg_number = new Long(0);

    /**
     * Constructor
     */
    private Log_Engine() {
    }

    /**
     * Gets the instance of the Log_Engine
     *
     * @return Log_Engine instance
     */
    public static Log_Engine getInstance() {
        return INSTANCE;
    }

    /**
     * Queues the log messages
     *
     * @param class_origin Name of the message's origin
     * @param time_stamp   Time stamp of the message
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
}
