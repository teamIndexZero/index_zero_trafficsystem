package kcl.teamIndexZero.traffic.log;

import org.junit.Test;

/**
 * Created by Es on 01/02/2016.
 */
public class LoggerTest {

    @Test
    public void testGetLoggerInstance() throws Exception {
        Logger log = Logger.getLoggerInstance("Logger_test");
        log.log("Testing ", 1, ", ", 2, ", ", 3, ", and some more...");
    }

    @Test
    public void testLog() throws Exception {

    }

    @Test
    public void testLog_Fatal() throws Exception {

    }

    @Test
    public void testLog_Error() throws Exception {

    }

    @Test
    public void testLog_Warning() throws Exception {

    }

    @Test
    public void testLog_Debug() throws Exception {

    }

    @Test
    public void testLog_Trace() throws Exception {

    }
}