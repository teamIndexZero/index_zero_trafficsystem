package kcl.teamIndexZero.traffic.simulator.data.trafficlights;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

import java.util.List;

/**
 * Created by lexaux on 17/02/2016.
 */
public class TrafficLightController implements ISimulationAware {
    List<TrafficLight> lights;


    @Override
    public void tick(SimulationTick tick) {
        // alternate traffic lights if needed.
        lights.forEach(light -> light.setColor(TrafficLight.State.RED));
    }
}
