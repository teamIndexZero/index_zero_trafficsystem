package kcl.teamIndexZero.traffic.log.fileIO;

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
     * @param directory Directory path
     * @param file_name File name
     * @throws InvalidPathException
     * @throws IOException
     */
    public FileInput(  String directory, String file_name ) throws InvalidPathException, IOException {
        super( directory, file_name );
        try {
            this.reader = Files.newBufferedReader( super.getFilePath() );
        } catch ( InvalidPathException e ) {
            System.err.println("Caught IOException in FileInput(): " + e.getMessage());
            throw e;
        } catch ( IOException e ) {
            System.err.println("Caught IOException in FileInput(): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Reads the file into a list of Strings
     * @return List of lines from file
     * @exception InvalidPathException when the path description is invalid
     * @exception IOException when the file cannot be accessed
     */
    public List<String> read() throws InvalidPathException, IOException {
        try {
            List<String> file_content = new ArrayList<String>();
            String line = null;
            while ((line = reader.readLine()) != null) {
                file_content.add( line );
            }
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
     * Deletes the file
     * @return Success
     */
    public boolean deleteFile() {
        try {
            return super.deleteFile();
        } catch ( IOException e ) {
            System.err.println("Caught IOException in FileInput.deleteFile(): " + e.getMessage());
            return false;
        } catch ( InvalidPathException e ) {
            System.err.println("Caught IOException in FileInput.deleteFile(): " + e.getMessage());
            return false;
        }
    }

}
