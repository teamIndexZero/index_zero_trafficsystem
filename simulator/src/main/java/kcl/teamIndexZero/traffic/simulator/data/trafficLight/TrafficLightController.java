package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import kcl.teamIndexZero.traffic.simulator.ISimulationAware;
//import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.TrafficLightRule;
import kcl.teamIndexZero.traffic.simulator.data.descriptors.TrafficLightsInSetRule;
import kcl.teamIndexZero.traffic.simulator.data.trafficLight.TrafficLight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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
        private TrafficLight model;
        private TrafficLightSet modelSet;
        SimulationTick simulationTick;


        public long formatTimeToLong(LocalDateTime date) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            this.temp = date.format(formatter);
            return Long.parseLong(temp, 10);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void tick(SimulationTick tick) {
            Temporary = simulationTick.getSimulatedTime();
            CurrentTime = formatTimeToLong(Temporary);

            if ((CurrentTime - lastChange) > timer) {
                if (modelSet != null) {
                    TrafficLightRule.changeColour(model, model.currentState);
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
