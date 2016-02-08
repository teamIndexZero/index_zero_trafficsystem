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
 * Created by Es on 07/02/2016.
 */
public class OutputTest {
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
        Output out = new Output("Output_Name", GlobalOutputTypes.TERMINAL);
        out.setName("New_Ouptut_Name");
        assertEquals("New_Output_Name", out.getOutputName());
    }

    @Test
    public void testGetOutputName() throws Exception {
        Output out = new Output("Output_Name", GlobalOutputTypes.TERMINAL);
        assertEquals("Output_Name", out.getOutputName());
    }

    @Test
    public void testGetOutputType() throws Exception {
        Output out = new Output("Output_Name", GlobalOutputTypes.TERMINAL);
        assertEquals(GlobalOutputTypes.TERMINAL, out.getOutputType());
    }

    @Test
    public void testOutput() throws Exception {
        Output out = new Output("Output_Name", GlobalOutputTypes.TERMINAL);
        LocalDateTime ltd = LocalDateTime.now();
        Log_TimeStamp ts = new Log_TimeStamp(ltd);
        out.output("OutputTest", 4, new Long(100), ts, "Description message.");
        String expected = "[100] " + ts.getDate() + " - " + ts.getTime() + " " + Log_Levels.txtLevels[4] + " [OutputTest] Please use specific output child class to see the message..." + System.lineSeparator();
        assertEquals(outContent.toString(), expected);
    }

    @Test
    public void testOutput_Exception() throws Exception {
        Output out = new Output("Output_Name", GlobalOutputTypes.TERMINAL);
        LocalDateTime ltd = LocalDateTime.now();
        Log_TimeStamp ts = new Log_TimeStamp(ltd);
        Exception e = new IOException("Some exception message.");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        out.output("OutputTest", ts, new Long(100), e);
        String expected = "[100]\t===Exception raised in [OutputTest] at " + ts.getDate() + " - " + ts.getTime() + "===" + System.lineSeparator() + sw.toString() + System.lineSeparator() + System.lineSeparator();
        assertEquals(outContent.toString(), expected);
    }
}