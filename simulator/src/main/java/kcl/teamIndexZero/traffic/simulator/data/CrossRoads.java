package kcl.teamIndexZero.traffic.simulator.data;

import java.util.Arrays;
/**
 * Created by JK 12-02-16.
 */
public class CrossRoads
{
    public CrossRoads crossRoads = null;
    public int[] cellsLength = null;
    public int[][] cells = int[];
    public Vehicle vehicle = null;

    cells = null;

    public CrossRoads(CrossRoads crossRoads)
    {
        this.cellsLength = new int[1];
        this.cellsLength[0] = 32;

        this.cells = new int[12][];

        for (int i = 0; i < 12; i++) {
            this.cells[i] = new int[this.cellsLength[0]];
        }

        for (int i = 0; i < 12; i++)
        {
            for (int j = 0; j < this.cellsLength[0]; j++)
            {
                this.cells[i][j]= -1;
            }
        }

        for (int i = 5; i < 7; i++)
        {
            for (int j = 0; j < this.cellsLength[0]; j++)
            {
                this.cells[i][j]= 0;
            }
        }

        for (int i = ((this.cellsLength[0]/2)-1); i < ((this.cellsLength[0]/2) +1); i++)
        {
            for (int j = 0; j < 12; j++)
            {
                this.cells[j][i]= 0;
            }
        }
        //System.out.println(Arrays.toString(cells[0]))
    }

    public int getCellsLength(int i)
    {
        return this.cellsLength[i];
    }

    long Random(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    public void run()
    {
        try
        {
            boolean flag = false;

            while (flag) {
                //I Horizontal cars creating
                if ((this.cells[5][0] == 0) && (this.cells[5][1] == 0) && (Random(0, 75) % 3 == 1)) {

                    Vehicle vehicle = new Vehicle(...); //change constructor
                    this.cells[5][0] = vehicle.position;
                    vehicle.positionOpt = "H";
                }
                //movement for vehicles inside the class vehicles

                if ((this.cells[6][31] == 0) && (this.cells[6][30] == 0) && (Random(0, 75) % 3 == 1)) {

                    Vehicle vehicle = new Vehicle(...);
                    this.cells[6][31] = vehicle.position;
                    vehicle.positionOpt = "H";
                }

                //II Vertical cars creating
                if ((this.cells[0][16] == 0) && (this.cells[1][16] == 0) && (Random(0, 75) % 3 == 1)) {

                    Vehicle vehicle = new Vehicle(...);
                    this.cells[0][16] = vehicle.position;
                    vehicle.positionOpt = "V";
                }

                if ((this.cells[11][17] == 0) && (this.cells[11][16] == 0) && (Random(0, 75) % 3 == 1)) {

                    Vehicle vehicle = new Vehicle(...);
                    this.cells[11][17] = vehicle.position;
                    vehicle.positionOpt = "V";
            }


                //lights!
                //Thread.sleep(0900); ---->      0.9 seconds

                if (!flag) break;
            }
        }
        catch(InterruptedException e)
        {
            Thread.currentThread().interrupt();
            JOptionPane.showMessageDialog(frame,"CrossRoads TIME exception -> run method");
        }
    }
}