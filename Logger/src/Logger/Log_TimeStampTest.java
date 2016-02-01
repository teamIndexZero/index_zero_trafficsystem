package Logger;

import junit.framework.TestCase;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Created by Es on 28/01/2016.
 */
public class Log_TimeStampTest extends TestCase {

    @Test
    public void testGetDate() throws Exception {
        LocalDateTime date_time = LocalDateTime.now();
        String day, month, year;
        Log_TimeStamp timeStamp_test = new Log_TimeStamp( date_time );
        String date_stamp = timeStamp_test.getDate();
        day = String.format( "%02d", date_time.getDayOfMonth() );
        month = String.format( "%02d", date_time.getMonthValue() );
        year = String.format( "%04d", date_time.getYear() );
        String date = day + "/" + month + "/" + year;
        assertEquals( timeStamp_test.getDate(), date );
    }

    @Test
    public void testGetTime() throws Exception {
        LocalDateTime date_time = LocalDateTime.now();
        String hours, mns, secs, nano;
        Log_TimeStamp timeStamp_test = new Log_TimeStamp( date_time );
        String time_stamp = timeStamp_test.getTime();
        hours = String.format( "%02d", date_time.getHour() );
        mns = String.format( "%02d", date_time.getMinute() );
        secs = String.format( "%02d", date_time.getSecond() );
        nano = Integer.toString( date_time.getNano() / 1 ).substring( 0, 1 );
        String time = hours + ":" + mns + ":" + secs + "." + nano;
        assertEquals( timeStamp_test.getTime(), time );
    }

    @Test
    public void testGetCustomStamp() throws Exception {
        LocalDateTime date_time = LocalDateTime.now();
        String year, month, day, hours, mns, secs, nano;
        day = String.format( "%02d", date_time.getDayOfMonth() );
        month = String.format( "%02d", date_time.getMonthValue() );
        year = String.format( "%04d", date_time.getYear() );
        hours = String.format( "%02d", date_time.getHour() );
        mns = String.format( "%02d", date_time.getMinute() );
        secs = String.format( "%02d", date_time.getSecond() );
        nano = Integer.toString( date_time.getNano() ).substring( 0, 1 );
        Log_TimeStamp timeStamp_test = new Log_TimeStamp( date_time );
        //Valid formatter
        String valid_formatter = "yyyy MM dd hh mm ss S";
        String time_stamp = timeStamp_test.getCustomStamp( valid_formatter );
        String comparator = year + " " + month + " " + day + " " + hours + " " + mns + " " + secs + " " + nano;
        assertEquals( time_stamp, comparator );
        //Invalid formatter
        String invalid_formatter = "YyJkjhkjf87y53kj";
        String default_time_stamp = timeStamp_test.getCustomStamp( invalid_formatter ); //yyyyMMddhhmmss
        String default_comparator = year + month + day + hours + mns + secs;
        assertEquals( default_time_stamp, default_comparator );
    }
}