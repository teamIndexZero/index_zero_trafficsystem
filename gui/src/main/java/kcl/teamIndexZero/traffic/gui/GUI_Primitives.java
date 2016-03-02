package kcl.teamIndexZero.traffic.gui;

import java.awt.*;
import java.awt.geom.QuadCurve2D;

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

    public void drawSmallCar(int x3, int y3, double angleToXAxis, Graphics g) {
        /*
        Assuming that b is the size of the car and this is splitting median of the design of the car in the ratio 2:1
        The shape of the car is a triangle.
        */
        g.setColor(Color.BLACK);
        int b;
        b = 10;
        int c, d;
        c = (int) (Math.sin(angleToXAxis) * b);
        d = (int) (Math.cos(angleToXAxis) * b);
        int x, y, x1, y1, x2, y2, e;
        e = b + (b / 2);
        x = x3 + c;
        y = y3 + d;
        c = (int) (Math.sin(40) * e);
        d = (int) (Math.cos(40) * e);
        x1 = x + c;
        y1 = y + d;
        c = (int) (Math.sin(40) * e);
        d = (int) (Math.cos(40) * e);
        x2 = x + c;
        y2 = x + d;
        drawLine(x, y, x1, y1, g);
        drawLine(x1, y1, x2, y2, g);
        drawLine(x2, y2, x, y, g);
    }

    public void drawDoubleCrossRoad(int length, int width, int x, int y, int length2, int width2, int x1, int y1, Graphics g) {
        /*
        The x and y give the coordinates of the start point of the first road, x1 y1 give the start point of the second road.
        The start and end points are of the bottom line in case of first road and left line in case of second road.
        The length and width are respective of the road they represent.
        */
        int x2, x3, y2, y3;
        x2 = x + length;
        y2 = y + width;
        /*
        The first road coordinates are (x,y) (x2,y) (x,y2) (x2,y2)
        Now we need to find the same for the second line.
        */
        x3 = x1 + width2;
        y3 = y1 + length2;
        /*
        The second road coordinates are (x1,y1) (x1,y3) (x3,y1) (x3,y3)
        The next step is to find the coordinates of the intersection, the four center points.
        */
        int d, xi, yi;
        d = (x - x2) * (y1 - y3) - (y - y) * (x1 - x1);
        xi = ((x1 - x1) * (x * y - y * x2) - (x - x2) * (x1 * y3 - y1 * x1)) / d;
        yi = ((y1 - y3) * (x * y - y * x2) - (y - y) * (x1 * y3 - y1 * x1)) / d;
        /*
        Now we have got one intersection point so we can go ahead and draw the complete road.
        */
        drawLine(xi, yi, x, y, g);
        drawLine(xi, yi, x1, y3, g);
        d = (x - x2) * (y1 - y3) - (y - y) * (x3 - x3);
        xi = ((x3 - x3) * (x * y - y * x2) - (x - x2) * (x3 * y3 - y1 * x3)) / d;
        yi = ((y1 - y3) * (x * y - y * x2) - (y - y) * (x3 * y3 - y1 * x3)) / d;
        //xi = xi+width2;
        drawLine(xi, yi, x2, y, g);
        drawLine(xi, yi, x3, y3, g);
        d = (x - x2) * (y1 - y3) - (y2 - y2) * (x3 - x3);
        xi = ((x3 - x3) * (x * y2 - y2 * x2) - (x - x2) * (x3 * y3 - y1 * x3)) / d;
        yi = ((y1 - y3) * (x * y2 - y2 * x2) - (y2 - y2) * (x3 * y3 - y1 * x3)) / d;
        //yi= yi+width;
        drawLine(xi, yi, x2, y2, g);
        drawLine(xi, yi, x3, y1, g);
        d = (x - x2) * (y1 - y3) - (y2 - y2) * (x1 - x1);
        xi = ((x1 - x1) * (x * y2 - y2 * x2) - (x - x2) * (x1 * y3 - y1 * x1)) / d;
        yi = ((y1 - y3) * (x * y2 - y2 * x2) - (y2 - y2) * (x1 * y3 - y1 * x1)) / d;
        //xi = xi-width2;
        drawLine(xi, yi, x, y2, g);
        drawLine(xi, yi, x1, y1, g);
    }
}
