package kcl.teamIndexZero.traffic.gui;

import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.QuadCurve2D;
import java.io.*;

/**
 * Created by kumar awijeet on 2/24/2016. thanks for Working!!
 */
public class GUI_Primitives {
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
    public void drawCar(int x3, int y3, double angleToXAxis, int choice, Graphics g)
    {
        // file taken from http://all-free-download.com/free-vector/car-vector-top-view-download.html
        g.setColor(Color.black);
        AffineTransform c;
        Graphics2D h = (Graphics2D) g;
        c = h.getTransform();
        AffineTransform at = new AffineTransform();
        at.rotate(angleToXAxis,x3,y3);
        h.setTransform(at);
        Image img = null;
        InputStream i = null;
        switch(choice)
        {
            case 1:
                try {
                        i = new BufferedInputStream(new FileInputStream("gui/src/main/resources/sprites/bmw_z_top_view_clip_art_18132.jpg"));
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                }
                try {
                        assert i != null;
                        img = ImageIO.read(i);
                } catch (IOException e) {
                        e.printStackTrace();
                }
                    h.drawImage(img,x3-18,y3-18,40,40,null);
                    h.setTransform(c);
                break;
            case 2:
                try {
                    i = new BufferedInputStream(new FileInputStream("gui/src/main/resources/sprites/8460_st0640_117.jpg"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    assert i != null;
                    img = ImageIO.read(i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                h.drawImage(img,x3-18,y3-18,40,40,null);
                h.setTransform(c);
        }


    }
}
