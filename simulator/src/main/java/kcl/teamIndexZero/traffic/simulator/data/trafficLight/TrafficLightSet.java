package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLight;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightInSet;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightState;

import java.util.List;
import java.util.*;

/**
 * Traffic Light in a synchronous set
 */

public class TrafficLightSet implements ISimulationAware {
    private ID id;
    public List<TrafficLightInSet> TrafficLightSetList;
    private static Logger_Interface LOG = Logger.getLoggerInstance(TrafficLightSet.class.getSimpleName());

    /**
     * Constructor
     *
     * @param id TrafficLightSet ID
     */
    public TrafficLightSet(ID id) {
        this.id=id;
    }

    /**
     * Adds traffic lights to the List of traffic lights within one junction
     *
     * @param trafficLightLnSet object to be added to the list
     */
    public void addTrafficlight(TrafficLightInSet trafficLightLnSet){

       if (trafficLightLnSet != null) {
           TrafficLightSetList.add(trafficLightLnSet);
           LOG.log("Added the following traffic lights: ", trafficLightLnSet.getID()," to the set: ", this.id);
       }

       else {
           LOG.log_Error("Error while adding to TrafficLightLnSet to the set");
       }
     }

    /**
     * Returns a list of traffic lights within one junction
     *
     * @param id of the junction, to which list is assigned
     */
    public List<TrafficLightInSet> getSet(ID id) {return this.TrafficLightSetList;}

    /**
     * Returns the ID of the TrafficLightSet
     */
    public ID getID() { return this.id;}

    @Override
    public void tick(SimulationTick tick) {

    }
}
