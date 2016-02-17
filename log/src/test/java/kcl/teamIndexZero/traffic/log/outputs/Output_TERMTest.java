package kcl.teamIndexZero.traffic.log.outputs;

import kcl.teamIndexZero.traffic.log.Log_Levels;
import kcl.teamIndexZero.traffic.log.Log_TimeStamp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * Created by Es on 17/02/2016.
 */
public class Output_TERMTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }

    @Test
    public void testSetName() throws Exception {
        Output_TERM out = new Output_TERM("output name");
        out.setName("new name for output");
        assertEquals(out.getOutputName(), "new name for output");
    }

    @Test
    public void testGetOutputName() throws Exception {
        Output_TERM out = new Output_TERM("output name");
        assertEquals(out.getOutputName(), "output name");
    }

    @Test
    public void testGetOutputType() throws Exception {
        Output_TERM out = new Output_TERM("output_name");
        assertEquals(out.getOutputType(), GlobalOutputTypes.TERMINAL);
    }

    @Test
    public void testOutput() throws Exception {
        Output_TERM out = new Output_TERM("Output_Name");
        LocalDateTime ltd = LocalDateTime.now();
        Log_TimeStamp ts = new Log_TimeStamp(ltd);
        out.output("Output_TERMTest", 4, new Long(100), ts, "Description message.");
        String expected = "[ 100] " + ts.getDate() + " - " + ts.getTime() + " " + Log_Levels.txtLevels[4] + " + [Output_TERMTest] Description message." + System.lineSeparator();
        assertEquals(outContent.toString(), expected);
    }

    @Test
    public void testOutputException() throws Exception {
        Output_TERM out = new Output_TERM("Output_Name");
        LocalDateTime ltd = LocalDateTime.now();
        Log_TimeStamp ts = new Log_TimeStamp(ltd);
        Exception e = new IOException("Some exception message.");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        out.output("Output_TERMTest", ts, new Long(100), e);
        String expected = "[ 100]\t===Exception raised in [Output_TERMTest]===" + System.lineSeparator() + "\t" + sw.toString() + System.lineSeparator() + System.lineSeparator();
        assertEquals(outContent.toString(), expected);
    }
}