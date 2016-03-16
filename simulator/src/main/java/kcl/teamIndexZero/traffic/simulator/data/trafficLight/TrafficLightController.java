package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
//import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.TrafficLightRule;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.TrafficLightsInSetRule;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLightInSet;
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
        public long delay;
        private TrafficLight model; //list this
        private TrafficLightInSet modelSet; //list this
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
                LOG.log("Added the following traffic lights to the set of all single Traffic Lights: ", trafficLight.getTrafficLightID() );
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

            /*when single traffic lights do not work in a synchronous way with the ticks*/
            model.TrafficLightDelay = CurrentTime - lastChange;
            if ((model.TrafficLightDelay >= timer) && (model.currentState == TrafficLightState.GREEN)) {
                model.currentState = TrafficLightState.RED;
            }

            /*when set's traffic lights do not work in a synchronous way with the ticks*/
            modelSet.TrafficLightInSetDelay = CurrentTime - lastChange;
            if ((modelSet.TrafficLightInSetDelay >= timer) && (modelSet.currentState == TrafficLightState.GREEN)){
                modelSet.currentState = TrafficLightState.RED;
            }

            if ((CurrentTime - lastChange) > timer) {
                if (modelSet != null) {
                    TrafficLightsInSetRule.changeStateofSet(TrafficLightSetList);
                    TrafficLightRule.changeStateofSingleTrafficLights(TrafficLightSinglesList);
                    lastChange = CurrentTime;
                }
            }
        }

        /**
         * Adds rule to the one traffic light
         */
        public void addRule(TrafficLightRule rule){
            rule.changeStateofSingleTrafficLights(TrafficLightSinglesList);
        }
        /**
         * Adds rule to the one traffic light set
         */
        public void addRule(TrafficLightsInSetRule rule){
             rule.changeStateofSet(TrafficLightSetList);
        }

}
