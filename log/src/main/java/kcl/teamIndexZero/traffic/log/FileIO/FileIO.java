package kcl.teamIndexZero.traffic.log.fileIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Es on 02/02/2016.
 */
public class FileIO { //TODO
    /**
     * Constructor
     *
     * @param file_name File name
     * @throws IOException when File cannot be opened
     */
    public FileIO(String file_name) throws IOException {
        try {
            List<String> lines = Files.readAllLines((Paths.get(file_name)));
        } catch (IOException e) {
            //TODO LOg that crap
            //System.out.println(e.getMessage()); e.printStackTrace(); throw e;
            //TODO throw back
        }

    }
}
