package kcl.teamIndexZero.traffic.log.fileIO;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.StandardOpenOption;

/**
 * Created by Es on 04/02/2016.
 */
public class FileOutput extends FileIO {
    BufferedWriter writer = null;

    /**
     * Constructor
     * @param directory Directory path
     * @param file_name File name
     * @exception IOException when file cannot be accessed
     * @exception InvalidPathException when the path description is invalid
     */
    public FileOutput( String directory, String file_name ) throws IOException {
        super( directory, file_name );
        try {
            this.writer = Files.newBufferedWriter(super.getFilePath(), super.getCharset(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Caught IOException in FileOutput(): " + e.getMessage());
            throw e;
        } catch (InvalidPathException e ) {
            System.err.println("Caught InvalidPathException in FileOutput(): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Re-Opens the BufferedWriter to the same path/file
     * @return Success
     */
    public boolean reOpenBufferedWriter() {
        try {
            this.writer = Files.newBufferedWriter(super.getFilePath(), super.getCharset(), StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.err.println("Caught IOException in FileOutput.reOpenBufferedWriter(): " + e.getMessage());
            return false;
        } catch (InvalidPathException e ) {
            System.err.println("Caught InvalidPathException in FileOutput.reOpenBufferedWriter(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Closes the BufferedWriter
     * @return Success (
     */
    public boolean closeBufferedWriter() {
        if( this.writer != null ) {
            try {
                writer.close();
                return true;
            } catch ( IOException e ) {
                System.err.println( "Caught IOException in FileOutput.closeBufferedWriter(): "+ e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Writes a string to the end of the file
     * @param string String to append to file
     * @exception IOException when the file cannot be accessed
     */
    public void appendString( String string ) throws InvalidPathException, IOException {
        try {
            if( !Files.exists( getFilePath() ) ) {
                Files.createFile( getFilePath() );
            }
            this.writer.write(string, 0, string.length());
        } catch ( IOException e ) {
            System.err.println("Caught IOException in FileOutput.appendString(): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Writes a string to the end of the file on a new line
     * @param string String to append to file on new line
     * @exception IOException when the file cannot be accessed
     */
    public void appendLine( String string ) throws InvalidPathException, IOException {
        try {
            if( !Files.exists( getFilePath() ) ) {
                Files.createFile( getFilePath() );
            }
            writer.newLine();
            writer.write(string, 0, string.length());
        } catch ( IOException e ) {
            System.err.println("Caught IOException in FileOutput.appendLine(): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Deletes file
     * @return Success
     */
    public boolean deleteFile() throws InvalidPathException, IOException {
        try {
            return super.deleteFile();
        } catch ( InvalidPathException e ) {
            System.err.println("Caught InvalidPathException in FileOutput.deleteFile(): " + e.getMessage());
            return false;
        } catch ( IOException e ) {
            System.err.println("Caught IOException in FileOutput.deleteFile(): " + e.getMessage());
            return false;
        }
    }
}
