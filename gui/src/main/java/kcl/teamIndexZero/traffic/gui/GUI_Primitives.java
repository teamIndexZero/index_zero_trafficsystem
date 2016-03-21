package kcl.teamIndexZero.traffic.gui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.QuadCurve2D;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kumar awijeet on 2/24/2016. thanks for Working!!
 */
public class GUI_Primitives {
    // TODO add log instance here.
    public void drawLine(int x1, int y1, int x2, int y2, Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g.drawLine(x1, y1, x2, y2);
    }

    public void drawCurve(int x1, int y1, int x2, int y2, int ctrlx, int ctrly, Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        QuadCurve2D q = new QuadCurve2D.Float();
        q.setCurve(x1, y1, ctrlx, ctrly, x2, y2);
        g2.draw(q);
    }

    public void drawSingleRoad(int x, int y, int x1, int y1, int x2, int y2, int x3, int y3, Graphics g) {
        drawLine(x, y, x1, y1, g);
        drawLine(x2, y2, x3, y3, g);
    }

    public void drawSmallCar(int x3, int y3, double angleToXAxis, Graphics g) {
        drawCar(x3, y3, angleToXAxis, "gui/src/main/resources/sprites/bmw_z_top_view_clip_art_18132.jpg", g);
    }

    public void drawTruck(int x3, int y3, double angleToXAxis, Graphics g) {
        drawCar(x3, y3, angleToXAxis, "gui/src/main/resources/sprites/8460_st0640_117.jpg", g);
    }

    private void drawCar(int x3, int y3, double angleToXAxis, String filename, Graphics g) {
        // file taken from http://all-free-download.com/free-vector/car-vector-top-view-download.html
        Graphics2D graphics = (Graphics2D) g;
        AffineTransform originalTransform = graphics.getTransform();

        try {
            AffineTransform newTransformation = new AffineTransform();
            newTransformation.rotate(angleToXAxis, x3, y3);
            graphics.setTransform(newTransformation);

            InputStream imageStream = new BufferedInputStream(new FileInputStream(filename));
            Image image = ImageIO.read(imageStream);
            graphics.drawImage(image, x3 - 18, y3 - 18, 40, 40, null);
        } catch (IOException e) {
            // TODO add logger.
            e.printStackTrace();
        } finally {
            graphics.setTransform(originalTransform);
        }
    }
}