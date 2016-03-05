package kcl.teamIndexZero.traffic.simulator.data.features;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.links.RoadLink;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.MapObject;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.MapPosition;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle;

import java.awt.*;

/**
 * Traffic Generator Feature that can be linked to dead-ends on the map to make traffic
 */
public class TrafficGenerator extends Feature {
    private RoadLink roadLink;
    private int counter = 1;
    private static SimulationMap map = null;
    //TODO make some sort of traffic receptor to draw up statistic??

    /**
     * Constructor
     *
     * @param id  Feature ID tag
     * @param map SimulationMap
     */
    public TrafficGenerator(ID id, SimulationMap map) {
        super(id);
        if (map == null) TrafficGenerator.map = map;
    }

    /**
     * Gets a random lane to place a vehicle onto
     *
     * @return Random Lane
     */
    private Lane getRandomLane() {
        if (roadLink.a.equals(this)) {
            return roadLink.a_inflow.get((int) (Math.random() * roadLink.a_inflow.size() - 1)).out;
        } else {
            return roadLink.b_inflow.get((int) (Math.random() * roadLink.b_inflow.size() - 1)).out;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
        if (Math.random() > 0.7) {
            // speed somehow varies between 0.5 and 1 m/s
            double speed = Math.random() * 0.5 + 0.5;

            // some cars should accelerate
            double acceleration = Math.random() > 0.6 ? 0.04 : 0;
            Color carColor = null;
            String name = null;
            if (acceleration == 0) {
                carColor = MapObject.COLORS[0];
                name = "SLOW";
            } else {
                carColor = MapObject.COLORS[1];
                name = "FAST";
            }

            Vehicle v = new Vehicle(name + counter++, new MapPosition(0, 1, 2, 1), getRandomLane(), speed, acceleration);
            v.setColor(carColor);
            map.addMapObject(v);
        }
    }
}
