package kcl.teamIndexZero.traffic.simulator;

import org.junit.Test;

import java.io.*;

/**
 *
 */
public class TestSerialization {


    public static class TwoField implements Serializable {

        public TwoField(int someInt, String someString) {
            this.x = someInt;
            this.some = someString;
        }

        public int x;
        public String some;

        @Override
        public String toString() {
            return String.format("{%d, %s}", x, some);
        }
    }

    @Test
    public void shouldSerializeDeserializeCorrectly() throws IOException, ClassNotFoundException {
        File f = File.createTempFile("serialization", "");

        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f));
        os.writeObject(new TwoField(4, "four"));
        os.writeObject(new TwoField(5, "five"));
        os.close();

        FileInputStream fis = new FileInputStream(f);
        ObjectInputStream is = new ObjectInputStream(fis);
        while(fis.available() > 0) {
            TwoField tf = (TwoField) is.readObject();
            System.out.println(tf);
        }
        is.close();
    }
}
