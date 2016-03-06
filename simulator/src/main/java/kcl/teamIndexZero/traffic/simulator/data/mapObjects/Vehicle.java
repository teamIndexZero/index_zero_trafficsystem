package kcl.teamIndexZero.traffic.simulator.data.mapObjects;

import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.Lane;
import kcl.teamIndexZero.traffic.simulator.data.features.Road;

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

    private double speedMetersPerSecond;
    private double accelerationMetersPerSecondSecond;

    /**
     * Vehicle Constructor. //FIXME Depreciated! Delete that once we don't need it!!
     *
     * @param name                              used for identification in logs for example
     * @param position                          initial {@link MapPosition} of the vehicle
     * @param road                              Lane the vehicle is on
     * @param speedMetersPerSecond              initial speed m/s
     * @param accelerationMetersPerSecondSecond initial acceleration m/s/s
     */
    public Vehicle(String name,
                   MapPosition position,
                   Road road,
                   double speedMetersPerSecond,
                   double accelerationMetersPerSecondSecond) {
        super(name, position, road);
        this.speedMetersPerSecond = speedMetersPerSecond;
        this.accelerationMetersPerSecondSecond = accelerationMetersPerSecondSecond;
    }


    /**
     * Vehicle Constructor.
     *
     * @param name                              used for identification in logs for example
     * @param position                          initial {@link MapPosition} of the vehicle
     * @param lane                              Lane the vehicle is on
     * @param speedMetersPerSecond              initial speed m/s
     * @param accelerationMetersPerSecondSecond initial acceleration m/s/s
     */
    public Vehicle(String name,
                   MapPosition position,
                   Lane lane,
                   double speedMetersPerSecond,
                   double accelerationMetersPerSecondSecond) {
        super(name, position, lane.getRoad());
        this.speedMetersPerSecond = speedMetersPerSecond;
        this.accelerationMetersPerSecondSecond = accelerationMetersPerSecondSecond;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
        speedMetersPerSecond = speedMetersPerSecond + accelerationMetersPerSecondSecond * tick.getTickDurationSeconds();
        positionOnRoad += speedMetersPerSecond + accelerationMetersPerSecondSecond * tick.getTickDurationSeconds();
        //TODO if end of road is reached then pass to next feature with the remaining travel length??
        //TODO maybe addapt behavior for the looking ahead distance based on projected stopping time at current speed?
        //TODO if looking ahead distance goes out of scope of the current feature then query link
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

    public double getAccelerationMetersPerSecondSecond() {
        return accelerationMetersPerSecondSecond;
    }

    public double getSpeedMetersPerSecond() {
        return speedMetersPerSecond;
    }
}
