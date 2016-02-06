package kcl.teamIndexZero.traffic.log.fileIO;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;

/**
 * Created by Es on 02/02/2016.
 */
public class FileIO {
    private String file_name;
    private String directory;
    private static final Charset charset = Charset.forName( "UTF-8" );

    /**
     * Constructor
     * @param directory Directory relative to running instance
     * @param file_name File name
     */
    public FileIO( String directory, String file_name ) {
        this.file_name = file_name;
        this.directory = directory;
    }

    /**
     * Creates file if it doesn't exist
     * @return Creation success
     */
    public boolean createFile() {
        try {
            if (!Files.exists(getFilePath())) {
                Files.createDirectories( getDirectoryPath() );
                Files.createFile(getFilePath());
            }
            return true;
        } catch ( SecurityException e ) {
            System.err.println("Caught SecurityException in FileIO.createFile(): " + e.getMessage());
            return false;
        } catch ( FileAlreadyExistsException e ) {
            System.err.println("Caught FileAlreadyExistsException in FileIO.createFile(): " + e.getMessage());
            return true;
        } catch ( IOException e ) {
            System.err.println("Caught IOException in FileIO.createFile(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes the file if it exists
     * return Success
     * @exception InvalidPathException when the path description is invalid
     * @exception IOException when the file cannot be accessed
     */
    public boolean deleteFile() throws InvalidPathException, IOException {
        try {
            if( Files.exists( this.getFilePath() )) {
                Files.delete( this.getFilePath() );
                return true;
            } else {
                return false;
            }
        } catch ( SecurityException e ) {
            System.err.println("Caught SecurityException in FileIO.deleteFile(): " + e.getMessage());
            return false;
        } catch ( InvalidPathException e) {
            throw e;
        } catch ( IOException e ) {
            throw e;
        }
    }

    /**
     * Gets the complete Path from the directory properties
     * @return Path of the directory
     * @throws InvalidPathException when the path described is invalid
     */
    protected Path getDirectoryPath() throws InvalidPathException {
        try {
            return FileSystems.getDefault().getPath( this.directory );
        } catch (InvalidPathException e ) {
            throw e;
        }
    }

    /**
     * Gets the complete Path from the file_name and directory properties
     * @return Path of the file
     * @throws InvalidPathException when the path described is invalid
     */
    protected Path getFilePath() throws InvalidPathException {
        try {
            return FileSystems.getDefault().getPath( this.directory, this.file_name );
        } catch ( InvalidPathException e ) {
            throw e;
        }
    }

    /**
     * Gets the Character Set
     * @return Charset
     */
    protected Charset getCharset() {
        return charset;
    }


}
