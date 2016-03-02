package kcl.teamIndexZero.traffic.gui;

import java.awt.*;
import java.awt.geom.AffineTransform;
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

    public void drawSmallCar(int x3, int y3, double angleToXAxis, Graphics g)
    {
        //g.drawPolygon(new int[] {0, 50, 25}, new int[] {0, 0, 50}, 3);
        g.setColor(Color.BLACK);
        int b, x, y, x1, y1, x2, y2;
        b = 0;
        x = b;
        y= b+x3;
        x1 = x3;
        y1 = x3;
        x2 = b+x3;
        y2 = b;
        AffineTransform c;
        Graphics2D h = (Graphics2D) g;
        c = h.getTransform();
        AffineTransform at = new AffineTransform();
        at.rotate(angleToXAxis,(x+x1+x2)/3,(y+y1+y2)/3);
        //this is rotation with reference to middle of the car, the one below is rotation with reference to
        //the front of the car.
        //at.rotate(angleToXAxis,x1,y1);
        h.setTransform(at);
        //at.translate(x3,y3);
        g.drawPolygon(new int[] {x , x1, x2}, new int[] {y , y1 , y2}, 3);
        h.setTransform(c);
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