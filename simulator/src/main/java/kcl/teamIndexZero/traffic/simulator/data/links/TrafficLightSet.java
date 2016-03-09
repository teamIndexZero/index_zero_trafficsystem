package kcl.teamIndexZero.traffic.simulator.data.links;

import javafx.beans.binding.ListBinding;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.TrafficLightRule;
import kcl.teamIndexZero.traffic.simulator.data.links.TrafficLight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Es on 02/03/2016.
 */

public class TrafficLightSet implements ISimulationAware{

    List<TrafficLight> lights = new ArrayList<>();
    private ID id;
    private TrafficLight trafficLight;
    private Map <ID,TrafficLight>;


    public Map createLightsmap(ID id, TrafficLight trafficLight) {
     Map.put(id, trafficLight);
    }


    public TrafficLight.State changeColour(TrafficLight trafficLight, TrafficLight.State currentState){

     TrafficLight.State temp;

     if (currentState == TrafficLight.State.RED) ? temp = TrafficLight.State.GREEN : temp =  TrafficLight.State.RED ;

      return temp;
    }

    public void addLights(TrafficLight light) {
        lights.add(light);
    }


    @Override
    public void tick(SimulationTick tick) {

    }
}
