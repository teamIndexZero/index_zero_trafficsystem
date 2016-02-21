package kcl.teamIndexZero.traffic.log.fileIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by Es on 03/02/2016.
 */
public class FileIOTest {
    private String test_file_name = "test_file.test";
    private Path path = null;

    @Before
    public void setUp() throws Exception {
        path = FileSystems.getDefault().getPath("", test_file_name);
    }

    @After
    public void tearDown() throws Exception {
        path = null;
    }


    @Test
    public void testCreateFile() throws Exception {
        FileIO file_io = new FileIO("", test_file_name);
        if (!Files.exists(path)) {
            Files.createFile(path);
            assertTrue(Files.exists(path));
            file_io.deleteFile();
        } else {
            assertTrue(false);
        }
    }

    @Test
    public void testDeleteFile() throws Exception {
        FileIO file_io = new FileIO("", test_file_name);
        if (!Files.exists(path)) {
            Files.createFile(path);
            assertTrue(Files.exists(path));
            file_io.deleteFile();
            assertFalse(Files.exists(path));
        } else {
            assertTrue(false);
        }
    }

    @Test
    public void testGetDirectoryPath() throws Exception {
        FileIO file_io = new FileIO("test_directory", test_file_name);
        assertEquals(file_io.getDirectoryPath(), FileSystems.getDefault().getPath("test_directory"));
    }

    @Test
    public void testGetFilePath() throws Exception {
        FileIO file_io = new FileIO("", test_file_name);
        assertEquals(path, file_io.getFilePath());
    }

    @Test
    public void testGetCharset() throws Exception {
        FileIO file_io = new FileIO("", test_file_name);
        assertEquals(file_io.getCharset(), Charset.forName("UTF-8")); //Default value
    }

    @Test
    public void testGetFileName() throws Exception {
        FileIO file_io = new FileIO("", test_file_name);
        assertEquals(file_io.getFileName(), test_file_name);
    }

    @Test
    public void testGetDirectory() throws Exception {
        FileIO file_io = new FileIO("test_directory", test_file_name);
        assertEquals(file_io.getDirectory(), "test_directory");
    }
}