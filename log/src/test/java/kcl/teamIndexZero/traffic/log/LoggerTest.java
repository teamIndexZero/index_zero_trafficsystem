package kcl.teamIndexZero.traffic.log;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Es on 01/02/2016.
 */
public class LoggerTest {

    @Test
    public void testGetLoggerInstance() throws Exception {
        Logger log = Logger.getLoggerInstance("Logger_UnitTest");
        Logger log_copy = Logger.getLoggerInstance("Logger_UnitTest");
        assertEquals(log, log_copy);
    }

    @Test
    public void testLog() throws Exception {
        Logger mocked_log = mock(Logger.class);
        mocked_log.log("Testing .log ", 1, ", ", 2, ", ", 3);
        verify(mocked_log).log("Testing .log ", 1, ", ", 2, ", ", 3);
    }

    @Test
    public void testLog_Fatal() throws Exception {
        Logger mocked_log = mock(Logger.class);
        mocked_log.log_Fatal("Testing .log_Fatal ", 1, ", ", 2, ", ", 3);
        verify(mocked_log).log_Fatal("Testing .log_Fatal ", 1, ", ", 2, ", ", 3);
    }

    @Test
    public void testLog_Error() throws Exception {
        Logger mocked_log = mock(Logger.class);
        mocked_log.log_Error("Testing .log_Error ", 1, ", ", 2, ", ", 3);
        verify(mocked_log).log_Error("Testing .log_Error ", 1, ", ", 2, ", ", 3);
    }

    @Test
    public void testLog_Warning() throws Exception {
        Logger mocked_log = mock(Logger.class);
        mocked_log.log_Warning("Testing .log_Warning ", 1, ", ", 2, ", ", 3);
        verify(mocked_log).log_Warning("Testing .log_Warning ", 1, ", ", 2, ", ", 3);
    }

    @Test
    public void testLog_Debug() throws Exception {
        Logger mocked_log = mock(Logger.class);
        mocked_log.log_Debug("Testing .log_Debug ", 1, ", ", 2, ", ", 3);
        verify(mocked_log).log_Debug("Testing .log_Debug ", 1, ", ", 2, ", ", 3);
    }

    @Test
    public void testLog_Trace() throws Exception {
        Logger mocked_log = mock(Logger.class);
        mocked_log.log_Trace("Testing .log_Trace ", 1, ", ", 2, ", ", 3);
        verify(mocked_log).log_Trace("Testing .log_Trace ", 1, ", ", 2, ", ", 3);
    }
}