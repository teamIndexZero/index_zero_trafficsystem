package kcl.teamIndexZero.traffic.log.fileIO;

import kcl.teamIndexZero.traffic.log.microLogger.MicroLogger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;

/**
 * Created by Es on 02/02/2016.
 */
public class FileIO {
    private String file_name;
    private String directory;
    private static final Charset charset = Charset.forName("UTF-8");

    /**
     * Constructor
     *
     * @param directory Directory relative to running instance
     * @param file_name File name
     */
    public FileIO(String directory, String file_name) {
        this.file_name = file_name;
        this.directory = directory;
    }

    /**
     * Creates file if it doesn't exist
     *
     * @return Creation success
     */
    public boolean createFile() {
        try {
            if (!Files.exists(getFilePath())) {
                Files.createDirectories(getDirectoryPath());
                Files.createFile(getFilePath());
            }
            return true;
        } catch (SecurityException e) {
            MicroLogger.INSTANCE.log_Error("SecurityException raised in [FileIO.createFile()] in regards to ", getFilePath(), ".");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        } catch (FileAlreadyExistsException e) {
            MicroLogger.INSTANCE.log("FileAlreadyExistsException raised in [FileIO.createFile()] in regards to ", getFilePath(), ".");
            return true;
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [FileIO.createFile()] in regards to ", getFilePath(), ".");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        }
    }

    /**
     * Deletes the file if it exists
     * return Success
     *
     * @throws InvalidPathException when the path description is invalid
     * @throws IOException          when the file cannot be accessed
     */
    public boolean deleteFile() throws InvalidPathException, IOException {
        try {
            if (Files.exists(this.getFilePath())) {
                Files.delete(this.getFilePath());
                return true;
            } else {
                return false;
            }
        } catch (SecurityException e) {
            MicroLogger.INSTANCE.log_Error("SecurityException raised in [FileIO.deleteFile()] for , ", this.getFilePath());
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        } catch (InvalidPathException e) {
            MicroLogger.INSTANCE.log_Error("InvalidPathException raised in [FileIO.deleteFile()] for , ", this.getFilePath());
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            throw e;
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [FileIO.deleteFile()] for , ", this.getFilePath());
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            throw e;
        }
    }

    /**
     * Gets the complete Path from the directory properties
     *
     * @return Path of the directory
     * @throws InvalidPathException when the path described is invalid
     */
    protected Path getDirectoryPath() throws InvalidPathException {
        try {
            return FileSystems.getDefault().getPath(this.directory);
        } catch (InvalidPathException e) {
            MicroLogger.INSTANCE.log_Error("InvalidPathException raised in [FileIO.getDirectoryPath()] for , ", this.directory);
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            throw e;
        }
    }

    /**
     * Gets the complete Path from the file_name and directory properties
     *
     * @return Path of the file
     * @throws InvalidPathException when the path described is invalid
     */
    protected Path getFilePath() throws InvalidPathException {
        try {
            return FileSystems.getDefault().getPath(this.directory, this.file_name);
        } catch (InvalidPathException e) {
            MicroLogger.INSTANCE.log_Error("InvalidPathException raised in [FileIO.getFilePath()] for , ", this.directory, ", and ", this.file_name, ".");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            throw e;
        }
    }

    /**
     * Gets the Character Set
     *
     * @return Charset
     */
    protected Charset getCharset() {
        return charset;
    }

    /**
     * Gets the file name
     *
     * @return File name
     */
    protected String getFileName() {
        return this.file_name;
    }

    /**
     * Gets the file directory
     *
     * @return Directory
     */
    protected String getDirectory() {
        return this.file_name;
    }

}
