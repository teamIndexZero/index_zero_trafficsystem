package kcl.teamIndexZero.traffic.simulator.data.features;

/**
 * Created by JK on 26-02-16.
 */
public class Lanes {

    public int  startx;//the starting horizontal point of the line
    public int  starty;//the starting vertical point of the line
    public int length;
    public char type; //horizontal or vertical, so h or v
    public int number;


    public Lanes (Character type, int startx, int starty, int length, int number)
    {
        this.startx=startx;
        this.starty=starty;
        this.length=length;
        this.type=type;
        this.number = number; //'line's number at a particular feature

    }
}
