package kcl.teamIndexZero.traffic.log.fileIO;

import org.junit.After;
import org.junit.Before;
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

    @Before
    public void setUp() throws Exception {
        System.out.println("@setUP");
        if (out == null) out = new FileOutput("", "testfile.test");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("@tearDown");
        if (out.isOpen()) out.closeWriter();
        if (out.deleteFile()) System.out.println("Successfully deleted file...");
        out = null;
    }

    @Test
    public void testReOpenWriter() throws Exception {
        System.out.println("@testReOpenWriter()");
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
        System.out.println("@testCloseWriter()");
        System.out.println("OK");
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
        System.out.println("@testAppendString()");
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
        System.out.println("@testClearFileContent()");
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
        System.out.println("testDeleteFile()");
    }

    @Test
    public void testGetFileName() throws Exception {
        System.out.println("@testGetFileName()");
        assertEquals(out.getFileName(), "testfile.test");
    }

    @Test
    public void testGetDirectory() throws Exception {
        System.out.println("@testGetDirectory()");
        assertEquals(out.getDirectory(), "");
    }
}