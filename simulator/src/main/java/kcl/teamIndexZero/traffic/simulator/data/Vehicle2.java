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
        switch (this.positionOpt) {

            case 1: this.positionOpt = 'H';
                /*switch (this.positionOpt) {
                    case 1:

                        break;
                    case 2:

                        break;
                 }
                */
                break;


            case 2: this.positionOpt = 'V';
                /*switch (this.positionOpt) {
                    case 1:

                        break;
                    case 2:

                        break;
                }
                */
                break;
        }

        System.out.println("Let's move");

    }
}