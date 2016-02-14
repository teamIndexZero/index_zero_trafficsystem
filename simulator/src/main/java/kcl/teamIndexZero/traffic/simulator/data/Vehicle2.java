package kcl.teamIndexZero.traffic.simulator.data;

/**
 * Created by JK on 13-02-16.
 */

public class Vehicle2 {

    private Character positionOpt;
    // private int[][] position;
    private int positionHorizontal;
    private int positionVertical;

    public Vehicle2(Character positionOptx, int positionHorizontalx, int positionVerticalx){
        this.positionOpt = positionOptx;
        this.positionHorizontal = positionHorizontalx;
        this.positionVertical = positionVerticalx;
    }

    public void movement(){
        System.out.println("Let's move");

    }

}