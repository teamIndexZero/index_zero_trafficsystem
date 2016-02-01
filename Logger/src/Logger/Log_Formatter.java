package Logger;

import Logger.Outputs.GlobalOutputTypes;

/**
 * Created by Es on 28/01/2016.
 */
public class Log_Formatter {
    public Log_Formatter() {}

    /**
     * Formats message information based on the output type
     * @param global_output Output type
     * @param origin_name Class/Package origin name
     * @param log_number Message number in log session
     * @param time_stamp Time stamp on the message
     * @param objects Message details
     * @return Formatted string
     */
    public String format(GlobalOutputTypes global_output, int log_level, String origin_name, Long log_number, Log_TimeStamp time_stamp,
                         Object... objects ) {
        switch ( global_output ) {
            case TXT:
                return formatTXT(origin_name, log_level, log_number, time_stamp, objects);
            case CSV:
                return formatCSV(origin_name, log_level, log_number, time_stamp, objects);
            case TERMINAL:
                return formatCONSOLE(origin_name, log_level, log_number, time_stamp, objects);
            default:
                return formatTXT(origin_name, log_level, log_number, time_stamp, objects);
        }
    }

    /**
     * Formats message information for console output
     * @param origin_name Name of the origin class
     * @param log_number Message number in log session
     * @param time_stamp Time stamp of the message
     * @param objects Message details
     * @return Formatted string
     */
    private String formatCONSOLE( String origin_name, int log_level, Long log_number, Log_TimeStamp time_stamp, Object... objects ) {
        return "[" + log_number.toString() + "] " + time_stamp.getDate() + " - " + time_stamp.getTime() + " " + Log_Levels.txtLevels[log_level] + " [" + origin_name + "] " + objects + "\n";
    }
    private String formatTXT( String origin_name, int log_level, Long log_number, Log_TimeStamp time_stamp, Object... objects ) {
        return "[" + log_number.toString() + "] " + time_stamp.getDate() + " - " + time_stamp.getTime() + " "+ Log_Levels.txtLevels[log_level] + " [" + origin_name + "] " + objects + "\n";
    }
    private String formatCSV( String origin_name, int log_level, Long log_number, Log_TimeStamp time_stamp, Object... objects ) {
        return log_number.toString() + ";" + time_stamp.getDate() + ";" + time_stamp.getTime() + ";" + Log_Levels.csvLevels[log_level] + ";" + origin_name + "; " + objects + "\n";
    }
}
