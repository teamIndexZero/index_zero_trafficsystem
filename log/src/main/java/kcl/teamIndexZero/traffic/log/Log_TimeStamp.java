package kcl.teamIndexZero.traffic.log;

import kcl.teamIndexZero.traffic.log.microLogger.MicroLogger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Log_TimeStamp class
 */
public class Log_TimeStamp {
    private LocalDateTime now;

    /**
     * Constructor
     */
    public Log_TimeStamp() {
        this.now = LocalDateTime.now();
    }

    /**
     * Constructor
     *
     * @param now The LocalDateTIme to be used
     */
    public Log_TimeStamp(LocalDateTime now) {
        this.now = now;
    }

    /**
     * Gets the date of the time stamp as a String formatted as dd/mm/yy
     *
     * @return date of time stamp
     */
    public String getDate() {
        return now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Gets the time of the time stamp as a String formatted as hh:mm:ss:ms
     *
     * @return time of time stamp
     */
    public String getTime() {
        return now.format(DateTimeFormatter.ofPattern("HH:mm:ss.S"));
    }

    /**
     * Gets the stamp formatted with the custom formatter given
     *
     * @param formatter Formatter for the stamp
     *                  If stamp is invalid raises an error msg to System.err and
     *                  returns date/time formatted as "yyyyMMddHHmmss"
     * @return Formatted stamp
     */
    public String getCustomStamp(String formatter) {
        try {
            return now.format(DateTimeFormatter.ofPattern(formatter));
        } catch (IllegalArgumentException e) {
            MicroLogger.INSTANCE.log_Error("IllegalArgumentException raised in [Log_TimeStamp.getCustomStamp( ", formatter, " )]");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            try {
                return now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            } catch (IllegalArgumentException ee) {
                MicroLogger.INSTANCE.log_Error("IllegalArgumentException raised in [Log_TimeStamp.getCustomStamp( \'yyyyMMddHHmmss\' )] <-Default fallback value has failed.");
                MicroLogger.INSTANCE.log_ExceptionMsg(e);
                return "";
            }
        }
    }

    /**
     * Gets the string representation of the object
     *
     * @return String time stamp
     */
    public String toString() {
        return this.now.toString();
    }
}
