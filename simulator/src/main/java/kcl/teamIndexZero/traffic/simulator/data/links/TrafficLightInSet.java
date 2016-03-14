package kcl.teamIndexZero.traffic.simulator.data.links;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightSet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



/**
 * Created by Es on 02/03/2016.
 */
/*public  class TrafficLightInSet extends TrafficLight implements ISimulationAware {
    public ID id;
    public String temp;
    public long CurrentTime;
    public LocalDateTime Temporary;
    public long lastChange = 0;
    public long timer;
    private TrafficLightmodel;
    private TrafficLightSet modelSet;
    SimulationTick simulationTick;


    public TrafficLightInSet(ID id) {
        this.id = id;
    }


    public long formatTimeToLong(LocalDateTime date ){
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
       this. temp = date.format(formatter);
       return Long.parseLong(temp, 10);
    }


    @Override
    public void tick(SimulationTick tick) {
        Temporary  =simulationTick.getSimulatedTime();
        CurrentTime = formatTimeToLong(Temporary);

        if ((CurrentTime - lastChange) > timer) {
            if (modelSet != null) {
              //  modelSet.changeColour(model, model.currentState);
                lastChange = CurrentTime;
            }
        }
    }
}
*/