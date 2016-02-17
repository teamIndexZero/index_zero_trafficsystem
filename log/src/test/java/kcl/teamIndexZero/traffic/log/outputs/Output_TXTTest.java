package kcl.teamIndexZero.traffic.log.outputs;

import kcl.teamIndexZero.traffic.log.Log_Levels;
import kcl.teamIndexZero.traffic.log.Log_TimeStamp;
import kcl.teamIndexZero.traffic.log.fileIO.FileOutput;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Alwlyan on 17/02/2016.
 */
public class Output_TXTTest {
    private Output_TXT out = null;
    FileOutput mocked_file_out = null;

    @Before
    public void setUp() throws Exception {
        mocked_file_out = mock(FileOutput.class);
        out = new Output_TXT(mocked_file_out);
    }

    @After
    public void tearDown() throws Exception {
        out = null;
    }

    @Test
    public void testSetName() throws Exception {
        out.setName("a_file_name.txt");
        assertEquals(out.getOutputName(), "a_file_name.txt");
    }

    @Test
    public void testGetOutputName() throws Exception {
        out.setName("a_file_name.txt");
        assertEquals(out.getOutputName(), "a_file_name.txt");
    }

    @Test
    public void testGetOutputType() throws Exception {
        assertEquals(out.getOutputType(), GlobalOutputTypes.TXT);
    }

    @Test
    public void testOutput() throws Exception {
        LocalDateTime ltd = LocalDateTime.now();
        Log_TimeStamp ts = new Log_TimeStamp(ltd);
        String expected = "[100] " + ts.getDate() + " - " + ts.getTime() + " " + Log_Levels.txtLevels[4] + " [Output_TERMTest] Description message." + System.lineSeparator();
        out.output("Output_TERMTest", 4, new Long(100), ts, "Description message.");
        verify(mocked_file_out).appendString(eq(expected));
    }

    @Test
    public void testOutputException() throws Exception {
        LocalDateTime ltd = LocalDateTime.now();
        Log_TimeStamp ts = new Log_TimeStamp(ltd);
        Exception e = new IOException("Some exception message.");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        out.output("OutputTest", ts, new Long(100), e);
        String expected = "[100]\t===Exception raised in [OutputTest] at " + ts.getDate() + " - " + ts.getTime() + "===" + System.lineSeparator() + "\t" + sw.toString() + System.lineSeparator();
        verify(mocked_file_out).appendString(eq(expected));
    }
}