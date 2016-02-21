package kcl.teamIndexZero.traffic.log.outputs;

import kcl.teamIndexZero.traffic.log.Log_Levels;
import kcl.teamIndexZero.traffic.log.Log_TimeStamp;
import kcl.teamIndexZero.traffic.log.fileIO.FileOutput;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Es on 21/02/2016.
 */
public class Output_CSVTest {
    private Output_CSV out = null;
    FileOutput mocked_file_out = null;

    @Before
    public void setUp() throws Exception {
        mocked_file_out = mock(FileOutput.class);
        out = new Output_CSV(mocked_file_out);
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
        assertEquals(out.getOutputType(), GlobalOutputTypes.CSV);
    }

    @Test
    public void testOutput() throws Exception {
        LocalDateTime ltd = LocalDateTime.now();
        Log_TimeStamp ts = new Log_TimeStamp(ltd);
        String expected = "100;" + ts.getDate() + ";" + ts.getTime() + ";" + Log_Levels.csvLevels[4] + ";Output_CSVTest;Description message." + System.lineSeparator();
        out.output("Output_CSVTest", 4, new Long(100), ts, "Description message.");
        verify(mocked_file_out).appendString(eq(expected));
    }

    @Test
    public void testOutputException() throws Exception {
        LocalDateTime ltd = LocalDateTime.now();
        Log_TimeStamp ts = new Log_TimeStamp(ltd);
        Exception e = new IOException("Some exception message.");
        out.output("Output_CSVTest", ts, new Long(100), e);
        String expected = "100;" + ts.getDate() + ";" + ts.getTime() + ";EXCEPTION;Output_CSVTest;java.io.IOException: Some exception message.";
        verify(mocked_file_out).appendString(eq(expected));
    }
}