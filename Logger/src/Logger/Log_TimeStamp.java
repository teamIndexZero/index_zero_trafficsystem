package Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Es on 27/01/2016.
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
     * @param now The LocalDateTIme to be used
     */
    public Log_TimeStamp( LocalDateTime now ) {
        this.now = now;
    }
    /**
     * Gets the date of the time stamp as a String formatted as dd/mm/yy
     * @return date of time stamp
     */
    public String getDate() {
        return now.format( DateTimeFormatter.ofPattern( "dd/MM/yyyy" ) );
    }

    /**
     * Gets the time of the time stamp as a String formatted as hh:mm:ss:ms
     * @return time of time stamp
     */
    public String getTime() {
        return now.format( DateTimeFormatter.ofPattern( "hh:mm:ss.S" ) );
    }

    /**
     * Gets the stamp formatted with the custom formatter given
     * @param formatter Formatter for the stamp
     *                  If stamp is invalid raises an error msg to System.err and
     *                  returns date/time formatted as "yyyyMMddhhmmss"
     * @return Formatted stamp
     */
    public String getCustomStamp( String formatter ) {
        try {
            return now.format(DateTimeFormatter.ofPattern(formatter));
        } catch ( IllegalArgumentException e ) {
            System.err.println("Caught IllegalArgumentException in Log_TimeStamp.geCustomStamp(..): " + e.getMessage());
            return now.format( DateTimeFormatter.ofPattern( "yyyyMMddhhmmss" ) );
        }
    }
}
