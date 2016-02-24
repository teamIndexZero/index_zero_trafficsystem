package kcl.teamIndexZero.traffic.log.fileIO;

import org.junit.After;
import org.junit.Test;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.util.Vector;

import static org.junit.Assert.*;

/**
 * Created by Es on 06/02/2016.
 */
public class FileOutputTest {
    FileOutput out = null;

    @After
    public void tearDown() throws Exception {
        if (out.isOpen()) out.closeWriter();
        out.deleteFile();
        out = null;
    }

    @Test
    public void testReOpenWriter() throws Exception {
        if (out == null) out = new FileOutput("", "testReOpenWriter.test");
        if (out.isOpen()) {
            out.closeWriter();
            assertFalse(out.isOpen());
            out.reOpenWriter();
            assertTrue(out.isOpen());
        } else {
            out.reOpenWriter();
            assertTrue(out.isOpen());
        }
    }

    @Test
    public void testCloseWriter() throws Exception {
        if (out == null) out = new FileOutput("", "testCloseWriter.test");
        if (out.isOpen()) {
            out.closeWriter();
            assertFalse(out.isOpen());
        } else {
            out.reOpenWriter();
            assertTrue(out.isOpen());
            out.closeWriter();
            assertFalse(out.isOpen());
        }
    }

    @Test
    public void testAppendString() throws Exception {
        if (out == null) out = new FileOutput("", "testAppendString.test");
        if (!out.isOpen()) out.reOpenWriter();
        assertTrue(out.clearFileContent());

        out.appendString("String1");
        out.appendString("String2");
        out.appendString(System.lineSeparator() + "String3");

        BufferedReader reader = Files.newBufferedReader(out.getFilePath());
        Vector<String> file_content = new Vector<String>();
        String line = null;
        while ((line = reader.readLine()) != null) {
            file_content.add(line);
        }

        assertEquals(file_content.elementAt(0), "String1String2");
        assertEquals(file_content.elementAt(1), "String3");
        assertEquals(file_content.size(), 2);
    }

    @Test
    public void testClearFileContent() throws Exception {
        if (out == null) out = new FileOutput("", "testClearFileContent.test");
        assertTrue(out.clearFileContent());
        if (!out.isOpen()) out.reOpenWriter();
        out.appendString("String1" + System.lineSeparator());
        out.appendString("String2" + System.lineSeparator());
        out.appendString("String3");

        BufferedReader reader = Files.newBufferedReader(out.getFilePath());
        Vector<String> file_content = new Vector<String>();
        String line = null;
        while ((line = reader.readLine()) != null) {
            file_content.add(line);
        }

        assertEquals(file_content.size(), 3);
        assertTrue(out.clearFileContent());

        file_content.clear();
        line = null;
        while ((line = reader.readLine()) != null) {
            file_content.add(line);
        }

        assertEquals(file_content.size(), 0);
    }

    @Test
    public void testDeleteFile() throws Exception {
        if (out == null) out = new FileOutput("", "testDeleteFile.test");
        assertTrue(Files.exists(out.getFilePath()));
        out.deleteFile();
        assertFalse(Files.exists(out.getFilePath()));
    }

    @Test
    public void testGetFileName() throws Exception {
        if (out == null) out = new FileOutput("", "testGetFileName.test");
        assertEquals(out.getFileName(), "testGetFileName.test");
    }

    @Test
    public void testGetDirectory() throws Exception {
        if (out == null) out = new FileOutput("", "testGetDirectory.test");
        assertEquals(out.getDirectory(), "");
    }
}