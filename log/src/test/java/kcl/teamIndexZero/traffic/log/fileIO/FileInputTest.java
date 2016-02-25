package kcl.teamIndexZero.traffic.log.fileIO;

import org.junit.After;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Es on 06/02/2016.
 */
public class FileInputTest {
    private FileInput in = null;

    @After
    public void tearDown() throws Exception {
        if (in.isOpen()) in.closeReader();
        in.deleteFile();
        in = null;
    }

    @Test
    public void testRead() throws Exception {
        FileOutput out = new FileOutput("", "testRead.test");
        out.appendString("String 1" + System.lineSeparator());
        out.appendString("String 2" + System.lineSeparator());
        out.appendString("String 3");
        out.closeWriter();
        in = new FileInput("", "testRead.test");
        List<String> list = in.read();
        assertEquals(list.size(), 3);
        assertEquals(list.get(0), "String 1");
        assertEquals(list.get(1), "String 2");
        assertEquals(list.get(2), "String 3");
        assertTrue(out.deleteFile());
    }

    @Test
    public void testCloseReader() throws Exception {
        FileOutput out = new FileOutput("", "testCloseReader.test");
        out.closeWriter();
        in = new FileInput("", "testCloseReader.test");
        assertTrue(in.isOpen());
        in.closeReader();
        assertFalse(in.isOpen());
        out.deleteFile();
    }

    @Test
    public void testDeleteFile() throws Exception {
        FileOutput out = new FileOutput("", "testDeleteFile.test");
        out.closeWriter();
        in = new FileInput("", "testDeleteFile.test");
        assertTrue(in.deleteFile());
    }

    @Test
    public void testIsOpen() throws Exception {
        FileOutput out = new FileOutput("", "testIsOpen.test");
        out.closeWriter();
        in = new FileInput("", "testIsOpen.test");
        assertTrue(in.isOpen());
        in.closeReader();
        assertFalse(in.isOpen());
    }
}