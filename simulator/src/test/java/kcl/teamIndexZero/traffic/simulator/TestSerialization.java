package kcl.teamIndexZero.traffic.simulator;

import kcl.teamIndexZero.traffic.simulator.data.MapPosition;
import kcl.teamIndexZero.traffic.simulator.data.Obstacle;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.Vehicle;
import org.junit.Test;

import java.io.*;

/**
 *
 */
public class TestSerialization {


    @Test
    public void shouldSerializeDeserializeCorrectly() throws IOException, ClassNotFoundException {
        File f = File.createTempFile("serialization", "");

        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f));

        SimulationMap map = new SimulationMap(4, 400);

        map.addMapObject(new Obstacle("Fallen tree 1", new MapPosition(0, 0, 2, 4)));
        map.addMapObject(new Obstacle("Stone 1", new MapPosition(230, 2, 1, 1)));
        os.writeObject(map);
        os.close();

        ObjectOutputStream os1 = new ObjectOutputStream(new FileOutputStream(f));

        map.addMapObject(new Vehicle("Ferrari ES3 4FF", new MapPosition(0, 0, 1, 2), 0.05f, 0));
        map.addMapObject(new Vehicle("Land Rover RRT 2YG", new MapPosition(0, 1, 1, 2), 0.01f, 0.00002f));

        os1.writeObject(map);
        os1.close();

        FileInputStream fis = new FileInputStream(f);
        ObjectInputStream is = new ObjectInputStream(fis);

        while (fis.available() > 0) {
            SimulationMap sm = (SimulationMap) is.readObject();
            System.out.println(sm);
        }
        is.close();

    }
}
