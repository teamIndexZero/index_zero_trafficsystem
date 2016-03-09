package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Es on 02/03/2016.
 */

public class TrafficLightSet implements ISimulationAware {

    List<TrafficLight> lights = new ArrayList<>();
    private ID id;
    private TrafficLight trafficLight;
    private Map<ID, TrafficLight> map;


    public Map createLightsmap(ID id, TrafficLight trafficLight) {
        map.put(id, trafficLight);
        return map;
    }


    public TrafficLight.State changeColour(TrafficLight trafficLight, TrafficLight.State currentState) {
        TrafficLight.State temp;
        temp = (currentState == TrafficLight.State.RED) ? TrafficLight.State.GREEN : TrafficLight.State.RED;
        return temp;
    }

    public void addLights(TrafficLight light) {
        lights.add(light);
    }


    @Override
    public void tick(SimulationTick tick) {

    }
}
