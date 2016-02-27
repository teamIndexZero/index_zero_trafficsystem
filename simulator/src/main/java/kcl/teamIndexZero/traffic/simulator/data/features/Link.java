package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lexaux on 17/02/2016.
 * Altered by JK on 26/02/2016.
 */
public class Link implements ISimulationAware {

    private static  List<Link> links = new ArrayList<Link>();

    public static Feature one;
    public static Feature two;

    public static void addNeighbour1(Feature feat){

        one= feat;

    }

    public static void addNeighbour2(Feature feat){

        two= feat;

    }

    public static List<Link> getLinks() { //to operate on the static list Links in different classes
        return links;
    }


    @Override
    public void tick(SimulationTick tick) {

    }
}
