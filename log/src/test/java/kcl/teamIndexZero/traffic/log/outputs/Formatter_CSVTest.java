package kcl.teamIndexZero.traffic.log.outputs;

import kcl.teamIndexZero.traffic.log.Log_Levels;
import kcl.teamIndexZero.traffic.log.Log_TimeStamp;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Es on 08/02/2016.
 */
public class Formatter_CSVTest {

    @Test
    public void testFormat() throws Exception {
        Formatter_CSV formatter = new Formatter_CSV();
        LocalDateTime now = LocalDateTime.now();
        Log_TimeStamp ts = new Log_TimeStamp(now);
        String expected = "100;" + ts.getDate() + ";" + ts.getTime() + ";" + Log_Levels.csvLevels[3] + ";Formatter_CSVTest;a description message." + System.lineSeparator();
        String returned = formatter.format("Formatter_CSVTest", 3, new Long(100), ts, "a description message.");
        assertEquals(returned, expected);
    }

    @Test
    @Ignore
    public void testFormat_Exception() throws Exception {
        assertTrue( false ); //TODO Need to work out how to clean & parse the exception details for cvs
    }
}