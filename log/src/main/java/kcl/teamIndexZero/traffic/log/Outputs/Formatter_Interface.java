package kcl.teamIndexZero.traffic.log.outputs;

import kcl.teamIndexZero.traffic.log.Log_TimeStamp;

/**
 * Created by Es on 29/01/2016.
 */
public interface Formatter_Interface {
    String format(String origin_name, int log_level, Long log_number, Log_TimeStamp time_stamp, Object... objects);
    String format(String origin_name, Log_TimeStamp time_stamp, Exception e );
}
