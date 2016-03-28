package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.log.Logger_Interface;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kumar awijeet on 2/24/2016. thanks for Working!!
 */
public class GUI_Primitives {
    protected static Logger_Interface log;
    InputStream imageStream;
    int j = 0, k = 0;
    Image image, image1;

    public void drawSmallCar(int x3, int y3, double angleToXAxis, Graphics2D g) throws IOException {
        drawCar(x3, y3, angleToXAxis, "gui/src/main/resources/sprites/bmw_z_top_view_clip_art_18132.jpg", g);
    }

    public void drawTruck(int x3, int y3, double angleToXAxis, Graphics2D g) throws IOException {
        drawCar(x3, y3, angleToXAxis, "gui/src/main/resources/sprites/8460_st0640_117.jpg", g);
    }

    private void drawCar(int x3, int y3, double angleToXAxis, String filename, Graphics2D g) throws IOException {
        /*
        Files taken from
        http://all-free-download.com/free-vector/car-vector-top-view-download.html
        http://hdimagelib.com/trailer+truck+top+view
        */
        Graphics2D graphics = g;
        AffineTransform originalTransform = graphics.getTransform();
        try {
            AffineTransform newTransformation = new AffineTransform();
            newTransformation.rotate(angleToXAxis, x3, y3);
            graphics.setTransform(newTransformation);
            //InputStream imageStream;
            if ((filename == "gui/src/main/resources/sprites/bmw_z_top_view_clip_art_18132.jpg") && (k == 0)) {
                imageStream = new BufferedInputStream(getClass().getResourceAsStream(filename));
                image = ImageIO.read(imageStream);
                graphics.drawImage(image, x3 - 18, y3 - 18, 40, 40, null);
                k++;
            } else if ((filename == "gui/src/main/resources/sprites/8460_st0640_117.jpg") && (j == 0)) {
                imageStream = new BufferedInputStream(getClass().getResourceAsStream(filename));
                image1 = ImageIO.read(imageStream);
                graphics.drawImage(image1, x3 - 18, y3 - 18, 40, 40, null);
                j++;
            } else {
                if (filename == "gui/src/main/resources/sprites/8460_st0640_117.jpg") {
                    graphics.drawImage(image1, x3 - 18, y3 - 18, 40, 40, null);
                } else if (filename == "gui/src/main/resources/sprites/bmw_z_top_view_clip_art_18132.jpg") {
                    graphics.drawImage(image, x3 - 18, y3 - 18, 40, 40, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof IOException) {
                log.log_Fatal(e.getMessage(), "GUI_Primnitives");
            }
            if (e instanceof IllegalArgumentException) {
                log.log_Error("Check function, parameter passing error", "GUI_Primitives");
            } else {
                log.log_Exception(e);
            }
        } finally {
            graphics.setTransform(originalTransform);
            if (imageStream != null) {
                imageStream.close();
            }
        }
    }
}
