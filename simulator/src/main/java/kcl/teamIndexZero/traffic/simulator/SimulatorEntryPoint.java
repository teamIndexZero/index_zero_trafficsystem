package kcl.teamIndexZero.traffic.simulator;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.*;

import javax.swing.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

/**
 * Main class (entry point) for the Simulator command line interface.
 * <p>
 * TODO: add the CLI parameters parsing with commons-cli and set the params as such from CLI.
 */
public class SimulatorEntryPoint {

    protected static Logger_Interface LOG = Logger.getLoggerInstance(MapObject.class.getSimpleName());
    /**
     * Main method.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {



        SimulationMap map = new SimulationMap(4, 400);

        map.addMapObject(new Obstacle("Fallen tree 1", new MapPosition(0, 0, 2, 4)));
        map.addMapObject(new Obstacle("Stone 1", new MapPosition(230, 2, 1, 1)));
        map.addMapObject(new Vehicle("Ferrari ES3 4FF", new MapPosition(0, 0, 1, 2), 0.05f, 0));
        map.addMapObject(new Vehicle("Land Rover RRT 2YG", new MapPosition(0, 1, 1, 2), 0.01f, 0.00002f));

        Simulator simulator = new Simulator(
                new SimulationParams(LocalDateTime.now(), 20, 100),
                Collections.singletonList(map)
        );

        //serialization

        try (FileOutputStream fs = new FileOutputStream("map_objects.bin")){

            ObjectOutputStream os = new ObjectOutputStream(fs);

            os.writeObject(map);
            //os.writeObject(Vehicle);

            os.close();
/**
 *
 *`Logger log = Logger.getLoggerInstance( Myclass.class.getName() )
 * try { /** some stuff that raises an exception * }
 *
 * catch ( Exception e )
 * { log.log_error( "An exception was raise because..." ),
 * log.log_Exception( e ) }

 */

        } catch (FileNotFoundException e) {
         LOG.log_Error("File not found");
            LOG.log_Exception(e);

        } catch (IOException e) {
            LOG.log_Error("IO Error");
            LOG.log_Exception(e);
        }
        //deserialization
        try (FileInputStream fi  = new FileInputStream("map_objects.bin")) {

            ObjectInputStream os = new ObjectInputStream(fi);

            SimulationMap sm1 = (SimulationMap)os.readObject();

            os.close();


        } catch (FileNotFoundException e) {
           LOG.log_Error("FileNotFound");
            LOG.log_Exception(e);

        } catch (ClassNotFoundException e) {
            LOG.log_Error("Class not found");
            LOG.log_Exception(e);

        } catch (IOException e) {
            LOG.log_Error("IO error");
            LOG.log_Exception(e);
        }

        simulator.start();
        simulator.stop();
    }
}
