package kcl.teamIndexZero.traffic.simulator.data.mapObjects;

import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;

/**
 * An active object on a map - a vehicle. Can be a car, or truck, or a bike (should check these subtypes later - if
 * we need them as separate subclasses.
 * <p>
 * It extends {@link MapObject}, which means it also belongs to map. However, in comparison to other objects it can move
 * it has a speed, probably acceleration and/or other properties.
 * <p>
 * Vehicle actively works with the map it belongs to by 'seeing' into the distance and makign assumptions/decision
 * basing on perception.
 * <p>
 * TODO: probably get to the Belief-Desire-Intention reasoning agents model for the vehicle behavior as we go on
 */
public class Vehicle extends MapObject {

    private float speedMetersPerSecond;
    private float accelerationMetersPerSecondSecond;


    /**
     * Vehicle Constructor.
     *
     * @param name                              used for identification in logs for example
     * @param position                          initial {@link MapPosition} of the vehicle
     * @param speedMetersPerSecond              initial speed m/s
     * @param accelerationMetersPerSecondSecond initial acceleration m/s/s
     */
    public Vehicle(String name,
                   MapPosition position,
                   float speedMetersPerSecond,
                   float accelerationMetersPerSecondSecond) {
        super(name, position);
        this.speedMetersPerSecond = speedMetersPerSecond;
        this.accelerationMetersPerSecondSecond = accelerationMetersPerSecondSecond;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
        speedMetersPerSecond = speedMetersPerSecond + accelerationMetersPerSecondSecond * tick.getTickDurationSeconds();
        map.moveObject(
                this,
                new MapPosition(
                        Math.round(position.x + speedMetersPerSecond * tick.getTickDurationSeconds()),
                        position.y,
                        position.width,
                        position.height));
        LOG.log(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("{Vehicle \"%s\" is now at position %s with speed %.1f m/s and acceleration of %.5f m/s^2}",
                name,
                position.toString(),
                speedMetersPerSecond,
                accelerationMetersPerSecondSecond);
    }
}