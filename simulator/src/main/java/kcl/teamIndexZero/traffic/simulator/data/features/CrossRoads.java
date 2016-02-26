package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle2;

import java.util.concurrent.TimeUnit;

/**
 * Created by JK 12-02-16.
 */
public class CrossRoads {
    public CrossRoads crossRoads = null;
    public int[] cellsLength = null;
    public static int[][] cells = (int[][]) null;
    public Vehicle vehicle = null;

    private boolean greenHorizontal;
    private boolean greenVertical;
    private int start;
    private int timer;
    private int end;

    public CrossRoads(CrossRoads crossRoads) {
        this.cellsLength = new int[1];
        this.cellsLength[0] = 32;
        CrossRoads.cells = new int[12][];

        this.greenHorizontal = true;
        this.greenVertical = false;
        this.start = 0;
        this.timer = 4;
        this.end = 30;

        for (int i = 0; i < 12; i++) {
            CrossRoads.cells[i] = new int[this.cellsLength[0]];
        }

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < this.cellsLength[0]; j++) {
                CrossRoads.cells[i][j] = -1;
            }
        }

        for (int i = 5; i < 7; i++) //creating the horizontal road
        {
            for (int j = 0; j < this.cellsLength[0]; j++) {
                CrossRoads.cells[i][j] = 0;
            }
        }

        for (int i = ((this.cellsLength[0] / 2) - 1); i < ((this.cellsLength[0] / 2) + 1); i++) // vertical road
        {
            for (int j = 0; j < 12; j++) {
                CrossRoads.cells[j][i] = 0;
            }
        }
        //System.out.println(Arrays.toString(cells[0]))
    }

    public int getCellsLength(int i) {
        return this.cellsLength[i];
    }

    long Random(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }

    public static void StraightUpdateCrossRoadsH(int hor, int ver, Character charx) //horizontal
    {
        switch (charx) {
            case 1:
                charx = 'R'; //R->going right

                CrossRoads.cells[hor][ver] = 0;
                CrossRoads.cells[hor][ver + 1] = 1;

                break;

            case 2:
                charx = 'L'; //L->going left

                CrossRoads.cells[hor][ver] = 0;
                CrossRoads.cells[hor][ver - 1] = 1;

                break;

        }
    }

    public static void StraightUpdateCrossRoadsV(int hor, int ver, Character charx) //vertical
    {
        switch (charx) {
            case 1:
                charx = 'U'; //U->going up

                CrossRoads.cells[hor][ver] = 0;
                CrossRoads.cells[hor + 1][ver] = 1;

                break;

            case 2:
                charx = 'D'; //D->going down

                CrossRoads.cells[hor][ver] = 0;
                CrossRoads.cells[hor - 1][ver] = 1;

                break;

        }
    }


    public static void StraightUdateCrossRoadsV(int hor, int ver) //vertical
    {
        CrossRoads.cells[hor][ver] = 0;
        CrossRoads.cells[hor + 1][ver] = -1;
    }

    public void run() {
        try {
            boolean flag = false;

            while (flag) {
                //I Horizontal cars creating
                if ((CrossRoads.cells[5][0] == 0) && (CrossRoads.cells[5][1] == 0) && (Random(0, 75) % 3 == 1)) {

                    int x = 5;
                    int y = 0;
                    Vehicle2 vehicle2 = new Vehicle2('H', 'R', x, y);
                    vehicle2.movement();
//                  this.cells[5][0] = vehicle.position;

                }
                //movement for vehicles inside the class vehicles

                if ((CrossRoads.cells[6][31] == 0) && (CrossRoads.cells[6][30] == 0) && (Random(0, 75) % 3 == 1)) {

                    int x = 6;
                    int y = 31;
                    Vehicle2 vehicle2 = new Vehicle2('H', 'L', x, y);
                    vehicle2.movement();
//                  this.cells[6][31] = vehicle.position;
                }
                //the end of h. cars creating
                /////////////////////////////
                //II Vertical cars creating
                if ((CrossRoads.cells[0][16] == 0) && (CrossRoads.cells[1][16] == 0) && (Random(0, 75) % 3 == 1)) {

                    int x = 0; //v. starting position
                    int y = 16;
                    Vehicle2 vehicle2 = new Vehicle2('V', 'D', x, y);
                    vehicle2.movement();
//                  this.cells[0][16] = vehicle.position;
                }

                if ((CrossRoads.cells[11][17] == 0) && (CrossRoads.cells[11][16] == 0) && (Random(0, 75) % 3 == 1)) {

                    int x = 11;
                    int y = 17;
                    Vehicle2 vehicle2 = new Vehicle2('V', 'U', x, y);
                    vehicle2.movement();
//                  this.cells[11][17] = vehicle.position;
                }

                //the end of v. cars creating
                /////////////////////////////
                //Lights

                for (int i = this.start, j = this.start; i < this.end; i = i + this.timer, j++) {
                    if (j % 2 == 0) {
                        this.greenHorizontal = true;
                        this.greenVertical = false;

                        CrossRoads.cells[4][14] = 1; //lights positions
                        CrossRoads.cells[7][17] = 1;
                        CrossRoads.cells[4][17] = -1;
                        CrossRoads.cells[7][14] = -1;

                        TimeUnit.SECONDS.sleep(this.timer);//automatic lights
                    } else {
                        this.greenHorizontal = false;
                        this.greenVertical = true;

                        CrossRoads.cells[4][14] = -1;
                        CrossRoads.cells[7][17] = -1;
                        CrossRoads.cells[4][17] = 1;
                        CrossRoads.cells[7][14] = 1;

                        TimeUnit.SECONDS.sleep(this.timer);
                    }
                }
                //the end of Lights
                ///////////////////

                //Thread.sleep(900); ---->      0.9 seconds

                if (!flag) break;
            }
        } catch (Exception e) {
            System.out.println("Timer in Lights !" + e.getMessage());
        }
    }
}