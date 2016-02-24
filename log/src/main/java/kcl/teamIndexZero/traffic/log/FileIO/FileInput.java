package kcl.teamIndexZero.traffic.log.fileIO;

import kcl.teamIndexZero.traffic.log.microLogger.MicroLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Es on 04/02/2016.
 */
public class FileInput extends FileIO {
    private BufferedReader reader = null;

    /**
     * Constructor
     *
     * @param folder_path Directory path
     * @param file_name   File name
     * @throws InvalidPathException
     * @throws IOException
     */
    public FileInput(String folder_path, String file_name) throws InvalidPathException, IOException {
        super(folder_path, file_name);
        try {
            this.reader = Files.newBufferedReader(super.getFilePath());
        } catch (InvalidPathException e) {
            MicroLogger.INSTANCE.log_Error("InvalidPathException raised in [FileInput.FileInput( ", folder_path, ", ", file_name, " )]");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            throw e;
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [FileInput.FileInput( ", folder_path, ", ", file_name, " )]");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            throw e;
        }
    }

    /**
     * Reads the file into a list of Strings
     *
     * @return List of lines from file
     * @throws InvalidPathException when the path description is invalid
     * @throws IOException          when the file cannot be accessed
     */
    public synchronized List<String> read() throws InvalidPathException, IOException {
        try {
            List<String> file_content = new ArrayList<>();
            String line = null;
            while ((line = reader.readLine()) != null) {
                file_content.add(line);
            }
            return file_content;
        } catch (InvalidPathException e) {
            MicroLogger.INSTANCE.log_Error("InvalidPathException raised in [FileInput.read()] for , ", super.getFilePath());
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            throw e;
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [FileInput.read()] for , ", super.getFilePath());
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            throw e;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Closes the reader
     *
     * @return Success
     * @throw IOException when trying to close the opened file and fails
     */
    public synchronized void closeReader() throws IOException {
        if (this.reader != null) {
            try {
                reader.close();
                this.reader = null;
            } catch (IOException e) {
                MicroLogger.INSTANCE.log_Error("IOException raised in [FileInput.closeReader()] for , ", super.getFilePath());
                MicroLogger.INSTANCE.log_ExceptionMsg(e);
                throw e;
            }
        }
    }

    /**
     * Deletes the file
     *
     * @return Success
     */
    public synchronized boolean deleteFile() {
        try {
            return super.deleteFile();
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("InvalidPathException raised in [FileInput.deleteFile()] for , ", super.getFilePath());
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        } catch (InvalidPathException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [FileInput.deleteFile()] for , ", super.getFilePath());
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        }
    }

    /**
     * Checks if the FileInput file is open
     *
     * @return Opened state
     */
    public synchronized boolean isOpen() {
        return this.reader != null;
    }

}
