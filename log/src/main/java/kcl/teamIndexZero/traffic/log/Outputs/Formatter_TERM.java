package kcl.teamIndexZero.traffic.log.Outputs;

import kcl.teamIndexZero.traffic.log.Log_Levels;
import kcl.teamIndexZero.traffic.log.Log_TimeStamp;

/**
 * Created by Es on 29/01/2016.
 */
public class Formatter_TERM implements Formatter_Interface {
    protected Formatter_TERM() {
    }

    ;

    /**
     * Formats message information for console output
     *
     * @param origin_name Name of the message's origin
     * @param log_level   Log level
     * @param log_number  Message number in log session
     * @param time_stamp  Time stamp of the message
     * @param objects     Message details
     * @return Formatted string
     */
    @Override
    public String format(String origin_name, int log_level, Long log_number, Log_TimeStamp time_stamp, Object... objects) {
        String s = "[" + log_number.toString() + "] " + time_stamp.getDate() + " - " + time_stamp.getTime() + " " + Log_Levels.txtLevels[log_level] + " [" + origin_name + "] ";
        for (Object o : objects) {
            s += o.toString();
        }
        return s + "\n";
    }
}
