package kcl.teamIndexZero.traffic.gui;

import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by lexaux on 21/03/2016.
 */
public class GuiPrimitivesTest {

    @Test
    public void testDrawingPerformance() throws IOException {
        // record time
        GUI_Primitives primitives = new GUI_Primitives();

        long timestamp = System.currentTimeMillis();
        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        int count = 25 * 500;
        while (count-- > 0) {
            if (count % 2 == 1) {
                primitives.drawSmallCar(count, count, Math.random(), g);
            } else {
                primitives.drawTruck(count, count, Math.random(), g);
            }
        }

        long diff = System.currentTimeMillis() - timestamp;
        System.out.println(diff);

    }
}
