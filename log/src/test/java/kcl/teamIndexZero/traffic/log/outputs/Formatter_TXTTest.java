package kcl.teamIndexZero.traffic.log.outputs;

import kcl.teamIndexZero.traffic.log.Log_Levels;
import kcl.teamIndexZero.traffic.log.Log_TimeStamp;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import static junit.framework.TestCase.assertEquals;


/**
 * Created by Es on 08/02/2016.
 */
public class Formatter_TXTTest {

    @Test
    public void testFormat() throws Exception {
        Formatter_TXT formatter = new Formatter_TXT();
        LocalDateTime now = LocalDateTime.now();
        Log_TimeStamp ts = new Log_TimeStamp(now);
        String expected = "[100] " + ts.getDate() + " - " + ts.getTime() + " " + Log_Levels.txtLevels[3] + " [Formatter_TXTTest] a description message." + System.lineSeparator();
        String returned = formatter.format("Formatter_TXTTest", 3, new Long(100), ts, "a description message.");
        assertEquals(returned, expected);
    }

    @Test
    public void testFormat_Exception() throws Exception {
        Formatter_TXT formatter = new Formatter_TXT();
        LocalDateTime now = LocalDateTime.now();
        Log_TimeStamp ts = new Log_TimeStamp(now);
        Exception e = new IOException("Exception message");
        String expected = "[100]\t===Exception raised in [Formatter_TXTTest] at " + ts.getDate() + " - " + ts.getTime() + "===" + System.lineSeparator() + "\t";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        expected += sw.toString() + System.lineSeparator();
        String returned = formatter.format("Formatter_TXTTest", ts, new Long(100), e);
        assertEquals(returned, expected);
    }
}