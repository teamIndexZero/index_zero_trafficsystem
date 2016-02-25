package kcl.teamIndexZero.traffic.log.outputs;

import kcl.teamIndexZero.traffic.log.Log_Levels;
import kcl.teamIndexZero.traffic.log.Log_TimeStamp;
import org.junit.Test;

import java.time.LocalDateTime;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Es on 08/02/2016.
 */
public class Formatter_CSVTest {

    @Test
    public void testFormat() throws Exception {
        Formatter_CSV formatter = new Formatter_CSV();
        LocalDateTime now = LocalDateTime.now();
        Log_TimeStamp ts = new Log_TimeStamp(now);
        String expected = "100;" + ts.getDate() + ";" + ts.getTime() + ";" + Log_Levels.csvLevels[3] + ";Formatter_CSVTest;a description message with ¬." + System.lineSeparator();
        String returned = formatter.format("Formatter_CSVTest", 3, new Long(100), ts, "a description message with ;.");
        assertEquals(expected, returned);
    }

    @Test
    public void testFormat_Exception() throws Exception {
        Formatter_CSV formatter = new Formatter_CSV();
        LocalDateTime now = LocalDateTime.now();
        Log_TimeStamp ts = new Log_TimeStamp(now);
        Exception e = new IndexOutOfBoundsException("Exception message with ; inside.");
        String expected = "100;" + ts.getDate() + ";" + ts.getTime() + ";EXCEPTION;Formatter_CSVTest;java.lang.IndexOutOfBoundsException: Exception message with ¬ inside.";
        String returned = formatter.format("Formatter_CSVTest", ts, new Long(100), e);
        assertEquals(expected, returned);
    }
}