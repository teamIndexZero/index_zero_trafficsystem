package kcl.teamIndexZero.traffic.simulator.data.mapObjects;

import kcl.teamIndexZero.traffic.simulator.data.features.CrossRoads;

/**
 * Created by JK on 13-02-16.
 */

public class Vehicle2 {

    private Character positionOpt;
    private Character direction;
    // private int[][] position;
    private int positionHorizontal;
    private int positionVertical;

    public Vehicle2(Character positionOptx, Character directionx, int positionHorizontalx, int positionVerticalx) {
        this.positionOpt = positionOptx;
        this.positionHorizontal = positionHorizontalx;
        this.positionVertical = positionVerticalx;
        this.direction = directionx;
    }

    public void movement() {
        switch (this.positionOpt) {

            case 1:
                this.positionOpt = 'H';
                switch (this.direction) {

                    case 1:
                        this.direction = 'R'; //R->going right
                        if ((CrossRoads.cells[positionHorizontal][positionVertical + 1] == 0) && (CrossRoads.cells[positionHorizontal][positionVertical + 2] == 0)) {

                            CrossRoads.StraightUpdateCrossRoadsH(this.positionHorizontal, this.positionVertical, 'R');
                            this.positionVertical++;

                        }
                        break;

                    case 2:
                        this.direction = 'L'; //L->going Left
                        if ((CrossRoads.cells[positionHorizontal][positionVertical - 1] == 0) && (CrossRoads.cells[positionHorizontal][positionVertical - 2] == 0)) {

                            CrossRoads.StraightUpdateCrossRoadsH(this.positionHorizontal, this.positionVertical, 'L');
                            this.positionVertical--;

                        }
                        break;
                }

                break;


            case 2:
                this.positionOpt = 'V';
                switch (this.direction) {
                    case 1:
                        this.direction = 'U'; //U->going up
                        if ((CrossRoads.cells[positionHorizontal + 1][positionVertical] == 0) && (CrossRoads.cells[positionHorizontal + 2][positionVertical] == 0)) {

                            CrossRoads.StraightUpdateCrossRoadsV(this.positionHorizontal, this.positionVertical, 'U');
                            this.positionHorizontal++;

                        }
                        break;

                    case 2:
                        this.direction = 'D'; //D->going down
                        if ((CrossRoads.cells[positionHorizontal - 1][positionVertical] == 0) && (CrossRoads.cells[positionHorizontal - 2][positionVertical] == 0)) {
                            CrossRoads.StraightUpdateCrossRoadsV(this.positionHorizontal, this.positionVertical, 'D');
                            this.positionHorizontal--;
                        }
                        break;
                }

                break;
        }

        System.out.println("Let's move");

    }
}