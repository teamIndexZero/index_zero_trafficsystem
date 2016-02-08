package kcl.teamIndexZero.traffic.log.microLogger;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by Es on 07/02/2016.
 */
public class MicroLoggerTest {

    @Test
    public void testSetFileName() throws Exception {

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
    public void testLog() throws Exception {

    }

    @Test
    public void testLog_Debug() throws Exception {

    }

    @Test
    public void testLog_ExceptionMsg() throws Exception {
        MicroLogger.INSTANCE.log_Fatal( "Some fatal stuff happened.." );
        MicroLogger.INSTANCE.log_ExceptionMsg( new IOException( "Bad stuff" ) );
    }
}