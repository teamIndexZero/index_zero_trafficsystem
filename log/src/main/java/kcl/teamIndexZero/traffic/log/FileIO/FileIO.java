package kcl.teamIndexZero.traffic.log.fileIO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

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
     * Reads the file into a list of Strings
     * @return List of lines from file
     * @exception InvalidPathException when the path description is invalid
     * @exception IOException when the file cannot be accessed
     */
    public List<String> read() throws InvalidPathException, IOException {
        BufferedReader reader = null;
        try {
            List<String> file_content = new ArrayList<String>();
            reader = Files.newBufferedReader( this.getFilePath(), charset);
            String line = null;
            while ((line = reader.readLine()) != null) {
                file_content.add( line );
            }
            reader.close();
            return file_content;
        } catch ( InvalidPathException e ) {
            System.err.println("Caught InvalidPathException in FileIO.read(): " + e.getMessage());
            throw e;
        } catch ( IOException e ) {
            System.err.println("Caught IOException in FileIO.read(): " + e.getMessage());
            throw e;
        } finally {
            if( reader != null ) {
                try {
                    reader.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Writes a string to the end of the file
     * @param string String to append to file
     * @exception InvalidPathException when the path description is invalid
     * @exception IOException when the file cannot be accessed
     */
    public void append( String string ) throws InvalidPathException, IOException {
        BufferedWriter writer = null;
        try {
            if( !Files.exists( getFilePath() ) ) {
                Files.createFile( getFilePath() );
            }
            writer = Files.newBufferedWriter( this.getFilePath(), charset, StandardOpenOption.APPEND );
            writer.write(string, 0, string.length());
            writer.close();
        } catch ( InvalidPathException e ) {
            System.err.println("Caught InvalidPathException in FileIO.append(): " + e.getMessage());
            throw e;
        } catch ( IOException e ) {
            System.err.println("Caught IOException in FileIO.append(): " + e.getMessage());
            throw e;
        } finally {
            if( writer != null ) {
                try {
                    writer.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Writes a string to the end of the file on a new line
     * @param string String to append to file on new line
     * @exception InvalidPathException when the path description is invalid
     * @exception IOException when the file cannot be accessed
     */
    public void appendLine( String string ) throws InvalidPathException, IOException {
        BufferedWriter writer = null;
        try {
            if( !Files.exists( getFilePath() ) ) {
                Files.createFile( getFilePath() );
            }
            writer = Files.newBufferedWriter( this.getFilePath(), charset, StandardOpenOption.APPEND );
            writer.newLine();
            writer.write(string, 0, string.length());
            writer.close();
        } catch ( InvalidPathException e ) {
            System.err.println("Caught InvalidPathException in FileIO.appendLine(): " + e.getMessage());
            throw e;
        } catch ( IOException e ) {
            System.err.println("Caught IOException in FileIO.appendLine(): " + e.getMessage());
            throw e;
        } finally {
            if( writer != null ) {
                try {
                    writer.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Deletes the file if it exists
     * return Success
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
        }
    }

    /**
     * Gets the Path from the file_name and directory properties
     * @return Path Path of the file
     * @throws InvalidPathException when the path described is invalid
     */
    private Path getFilePath() throws InvalidPathException {
        try {
            return FileSystems.getDefault().getPath( this.directory, this.file_name );
        } catch ( InvalidPathException e ) {
            throw e;
        }
    }
}
