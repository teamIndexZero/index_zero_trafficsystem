package kcl.teamIndexZero.traffic.log.fileIO;

import kcl.teamIndexZero.traffic.log.microLogger.MicroLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.StandardOpenOption;

/**
 * Created by Es on 04/02/2016.
 */
public class FileOutput extends FileIO {
    private BufferedWriter writer = null;

    /**
     * Constructor
     *
     * @param folder_path Directory path
     * @param file_name   File name
     * @throws IOException          when file cannot be accessed
     * @throws InvalidPathException when the path description is invalid
     */
    public FileOutput(String folder_path, String file_name) throws IOException {
        super(folder_path, file_name);
        try {
            if (super.createFile()) {
                this.writer = Files.newBufferedWriter(super.getFilePath(), super.getCharset(), StandardOpenOption.APPEND);
            } else {
                MicroLogger.INSTANCE.log_Error("[FileOutput.FileOutput( ", folder_path, ", ", file_name, " )] Couldn't create/access the file.");
                throw new IOException("Failed to create/access the file " + getFilePath().toString());
            }
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [FileOutput.FileOutput( ", folder_path, ", ", file_name, " )]");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            throw e;
        } catch (InvalidPathException e) {
            MicroLogger.INSTANCE.log_Error("InvalidPathException raised in [FileOutput.FileOutput( ", folder_path, ", ", file_name, " )]");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            throw e;
        }
    }

    /**
     * Re-Opens the Writer to the same path/file
     *
     * @return Success
     */
    public boolean reOpenWriter() {
        try {
            if (super.createFile()) {
                this.writer = Files.newBufferedWriter(super.getFilePath(), super.getCharset(), StandardOpenOption.APPEND);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [FileOutput.reOpenWriter()] for , ", super.getFilePath());
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        } catch (InvalidPathException e) {
            MicroLogger.INSTANCE.log_Error("InvalidPathException raised in [FileOutput.reOpenWriter()] for , ", super.getFilePath());
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        }
    }

    /**
     * Closes the Writer
     *
     * @return Success (
     */
    public boolean closeWriter() {
        if (this.writer != null) {
            try {
                writer.close();
                return true;
            } catch (IOException e) {
                MicroLogger.INSTANCE.log_Error("InvalidPathException raised in [FileOutput.closeWriter()] for , ", super.getFilePath());
                MicroLogger.INSTANCE.log_ExceptionMsg(e);
                return false;
            }
        }
        return true;
    }

    /**
     * Writes a string to the end of the file
     *
     * @param string String to append to file
     * @throws IOException when the file cannot be accessed
     */
    public void appendString(String string) throws InvalidPathException, IOException {
        try {
            this.writer.write(string, 0, string.length());
            this.writer.flush();
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("InvalidPathException raised in [FileOutput.appendString()] for ", super.getFilePath(), " with <", string, ">");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            throw e;
        }
    }

    /**
     * Clears the content of the file
     *
     * @return Success
     */
    public boolean clearFileContent() {
        try {
            PrintWriter pw = new PrintWriter(writer);
            pw.close();
            reOpenWriter();
            return true;
        } catch (SecurityException e) {
            MicroLogger.INSTANCE.log_Error("SecurityException raised in [FileOutput.clearFileContent()] for ", super.getFilePath());
            return false;
        }

    }

    /**
     * Deletes file
     *
     * @return Success
     */
    public boolean deleteFile() throws InvalidPathException, IOException {
        try {
            return super.deleteFile();
        } catch (InvalidPathException e) {
            MicroLogger.INSTANCE.log_Error("InvalidPathException raised in [FileOutput.deleteFile()] for , ", super.getFilePath());
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [FileOutput.deleteFile()] for , ", super.getFilePath());
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        }
    }

    /**
     * Gets the file name
     *
     * @return File name
     */
    public String getFileName() {
        return super.getFileName();
    }

    /**
     * Gets the file directory
     *
     * @return Directory
     */
    public String getDirectory() {
        return super.getDirectory();
    }
}
