package Logger.Outputs;

import Logger.Log_TimeStamp;

/**
 * Created by Es on 29/01/2016.
 */
public interface Formatter_Interface {
    //TODO try object to string for all implemetnations but if fail cut off description and replace with why it doesn't show + create new message with that errror
    String format(String origin_name, int log_level, Long log_number, Log_TimeStamp time_stamp, Object... objects );
}
