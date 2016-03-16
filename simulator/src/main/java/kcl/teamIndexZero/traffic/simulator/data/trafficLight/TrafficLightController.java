package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
//import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.TrafficLightRule;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.TrafficLightsInSetRule;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * Traffic Light controller
 * Controls the traffic lights in the map based on their rules
 */
public class TrafficLightController implements ISimulationAware {
        public String temp;
        public long CurrentTime;
        public LocalDateTime Temporary;
        public long lastChange = 0;
        public long timer;
        private TrafficLight model; //list this
        private TrafficLightSet modelSet; //list this
        public List<TrafficLight> TrafficLightSinglesList;
        public List<TrafficLightInSet> TrafficLightSetList;
        SimulationTick simulationTick;
        private static Logger_Interface LOG = Logger.getLoggerInstance(TrafficLightController.class.getSimpleName());


        public long formatTimeToLong(LocalDateTime date) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            this.temp = date.format(formatter);
            return Long.parseLong(temp, 10);
        }

        /**
         * Adds single traffic light to the List of all single traffic lights
         *
         * @param trafficLight object to be added to the list
         */
        public void addTrafficlight(TrafficLight trafficLight){

            if (trafficLight != null) {
                TrafficLightSinglesList.add(trafficLight);
                LOG.log("Added the following traffic lights to the set: ", trafficLight.getTrafficLightID());
            }

            else {
                LOG.log_Error("Error while adding to TrafficLightLnSet to the set");
            }
        }

        /**
         * Returns a list List of all single traffic lights
         *
         */
         public List<TrafficLight> getSingleSet() {return TrafficLightSinglesList;}

        /**
         * {@inheritDoc}
         */
        @Override
        public void tick(SimulationTick tick) {
            CurrentTime = formatTimeToLong(simulationTick.getSimulatedTime());
            if ((CurrentTime - lastChange) > timer) {
                if (modelSet != null) {
                    TrafficLightsInSetRule.changeStateofSet(TrafficLightSetList);
                    TrafficLightRule.changeStateofSingleTrafficLights(TrafficLightSinglesList);
                    lastChange = CurrentTime;
                }
            }
        }

        /**
         * Adding rule to the one traffic light
         *
         */
        public void addRule(TrafficLightRule rule){

        }
        /**
         * Adding rule to the one traffic light set
         *
         */
        public void addRule(TrafficLightsInSetRule rule){

        }

}
