package kcl.teamIndexZero.traffic.simulator.data.features;

/**
 * Created by lexaux on 17/02/2016.
 * Altered by JK on 27/02/2016.
 */
public class Road extends Feature {

    public Road(Character type, int horizontal, int vertical, int neighbours, int numberOfLines, char typeOfRoad) {

        super(type, horizontal, vertical, neighbours, numberOfLines, typeOfRoad);

        /***************
         *creating lines
         ***************/
        int j = 0;

        switch ( typeOfRoad) {
            case 1:
                typeOfRoad = 'H'; //horizontal Road

                    for (int hor = horizontal, vert; hor > 0; hor--, j++) {// j = 0,1 ... horizontal-1
                        Lines line = new Lines('h', 0, j, horizontal, j);
                    }

                break;

            case 2:
                typeOfRoad = 'V'; //Vertical Road

                    for (int vert = vertical; vert > 0; vert--, j++ ) { //j = 0,1 ...vertical-1
                        Lines line = new Lines('h', 0, j, vertical, j);
                     }

                break;

            default: //say the default is horizontal road, why not?!

                    for (int hor = horizontal, vert; hor > 0; hor--, j++) {
                       Lines line = new Lines('h', 0, j, horizontal, j);
                    }
                break;

        }
    }
}

