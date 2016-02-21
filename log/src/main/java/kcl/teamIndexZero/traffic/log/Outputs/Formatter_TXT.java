package kcl.teamIndexZero.traffic.log.outputs;

import kcl.teamIndexZero.traffic.log.Log_Levels;
import kcl.teamIndexZero.traffic.log.Log_TimeStamp;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Es on 29/01/2016.
 * Formatter for '.txt' file export
 */
public class Formatter_TXT implements Formatter_Interface {
    /**
     * Constructor
     */
    protected Formatter_TXT() {
    }

    /**
     * Formats message information for Text File output
     *
     * @param origin_name Name of the message's origin
     * @param log_level   Log level
     * @param log_number  Message number in log session
     * @param time_stamp  Time stamp for the message
     * @param objects     Message details
     * @return Formatted String
     */
    @Override
    public String format(String origin_name, int log_level, Long log_number, Log_TimeStamp time_stamp, Object... objects) {
        String s = String.format( "[%5d] %s - %s %s [%s] ",
                log_number,
                time_stamp.getDate(),
                time_stamp.getTime(),
                Log_Levels.txtLevels[log_level],
                origin_name
        );
        for (Object o : objects) {
            s += o.toString();
        }
        return s + System.lineSeparator();
    }

    /**
     * Formats message information for Text File output
     *
     * @param origin_name Name of the message's origin
     * @param time_stamp  Time stamp for the message
     * @param log_number  Message number in session
     * @param e           Exception raised
     * @return Formatted String
     */
    @Override
    public String format(String origin_name, Log_TimeStamp time_stamp, Long log_number, Exception e) {
        String s = String.format("[%5d]\t===Exception raised in [%s] at %s - %s===",
                log_number,
                origin_name,
                time_stamp.getDate(),
                time_stamp.getTime()
        );
        s += System.lineSeparator() + "\t";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        s += sw.toString() + System.lineSeparator();
        return s;
    }
}
