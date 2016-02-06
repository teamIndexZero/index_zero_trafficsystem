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
    private BufferedWriter writer = null;

    /**
     * Constructor
     * @param folder_path Directory path
     * @param file_name File name
     * @exception IOException when file cannot be accessed
     * @exception InvalidPathException when the path description is invalid
     */
    public FileOutput( String folder_path, String file_name ) throws IOException {
        super( folder_path, file_name );
        try {
            if( super.createFile() ) {
                this.writer = Files.newBufferedWriter(super.getFilePath(), super.getCharset(), StandardOpenOption.APPEND);
            } else {
                //TODO log error
                throw new IOException( "Failed to create/access the file " + getFilePath().toString() );
            }
        } catch (IOException e) {
            //TODO log error
            System.err.println("Caught IOException in FileOutput(): " + e.getMessage());
            throw e;
        } catch (InvalidPathException e ) {
            //TODO log error
            System.err.println("Caught InvalidPathException in FileOutput(): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Re-Opens the Writer to the same path/file
     * @return Success
     */
    public boolean reOpenWriter() {
        try {
            if( super.createFile() ) {
                this.writer = Files.newBufferedWriter(super.getFilePath(), super.getCharset(), StandardOpenOption.APPEND);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            System.err.println("Caught IOException in FileOutput.reOpenWriter(): " + e.getMessage());
            return false;
        } catch (InvalidPathException e ) {
            System.err.println("Caught InvalidPathException in FileOutput.reOpenWriter(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Closes the Writer
     * @return Success (
     */
    public boolean closeWriter() {
        if( this.writer != null ) {
            try {
                writer.close();
                return true;
            } catch ( IOException e ) {
                //TODO log error
                System.err.println( "Caught IOException in FileOutput.closeWriter(): " + e.getMessage());
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
            this.writer.write(string, 0, string.length());
            this.writer.flush();
        } catch ( IOException e ) {
            //TODO log error
            System.err.println("Caught IOException in FileOutput.appendString(): " + e.getMessage());
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
            //TODO log error
            System.err.println("Caught InvalidPathException in FileOutput.deleteFile(): " + e.getMessage());
            return false;
        } catch ( IOException e ) {
            //TODO log error
            System.err.println("Caught IOException in FileOutput.deleteFile(): " + e.getMessage());
            return false;
        }
    }
}
