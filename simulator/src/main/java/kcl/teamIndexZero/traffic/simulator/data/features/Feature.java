package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.features.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lexaux on 17/02/2016.
 * Altered by JK on 24/02/2016.
 */
public class Feature implements ISimulationAware{

    public Feature feature = null;
    public Character type;
    public int horizontal_size;
    public int vertical_size;
    public int[][] cells = (int[][])null;
    public int neighbours;
    public int numberOfLines;
    public Character typeOfRoad;

    private static List<Feature> features = new ArrayList<Feature>();
    public Link newlink = null;


    /**
     * Constructor.
     *
     * @param type feature type
     * @param horizontal feature hor. size
     * @param vertical feature vert. size
     * @param neighbours number of neighbours
     *
     */
        public Feature(Character type, int horizontal, int vertical, int neighbours, int numberOfLines, char typeOfRoad) {

            this.type = type;
            this.horizontal_size= horizontal;
            this.vertical_size = vertical;
            this.cells = new int[horizontal][vertical];
            this.neighbours = neighbours;
            this.numberOfLines = numberOfLines;
            this.typeOfRoad=typeOfRoad; //only for roads if they are vertical or horizontal for all others not important!
                                        // typeOfRoad in CAPITAL LETTERS please!

            features.add(feature); //adding new feature to the list

            int i = neighbours;

            while (i>0 && i<5){  //there will not be more than 4 neighbours!

                if(Link.one == null){
                    Link.getLinks().add(newlink);
                    Link.addNeighbour1(feature);
                }
                else
                    Link.addNeighbour2(feature);
            }

        }

    public static List<Feature> getFeature() { //to use static list in different classes
        return features;
    }


    @Override
    public void tick(SimulationTick tick) {

    }
}
