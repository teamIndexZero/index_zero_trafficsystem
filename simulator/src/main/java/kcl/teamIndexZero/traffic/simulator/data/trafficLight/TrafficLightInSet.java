package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import kcl.teamIndexZero.traffic.simulator.data.ID;

import java.util.Date;

/**
 * Traffic Light part of a synchronous set
 */
public class TrafficLightInSet {
    long CurrentDate = (new Date().getTime()) / 1000;
    long lastChange = CurrentDate;
    long timer;

    private TrafficLightSet modelSet;

    public TrafficLightInSet(ID linkId) {
        //TODO Fix construction?
    }
}