package kcl.teamIndexZero.traffic.log.fileIO;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/**
 * Created by Es on 02/02/2016.
 */
public class FileIO {
    private String file_name;
    private String directory;
    private static final Charset charset = Charset.forName( "UTF-8" );
    //TODO inject buffered reader from Output so that it can remain opened during the life of the output whilst it is used then closed at the end
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
     * Gets the Path from the file_name and directory properties
     * @return Path Path of the file
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
