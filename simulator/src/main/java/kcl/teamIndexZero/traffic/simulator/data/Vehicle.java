package kcl.teamIndexZero.traffic.simulator.data;

/**
 * Created by lexaux on 07/02/2016.
 */
public class Vehicle extends MapObject {

    private float speedMetersPerSecond;
    private float accelerationMetersPerSecondSecond;

    public Vehicle(String name, MapPosition position, float speedMetersPerSecond, float accelerationMetersPerSecondSecond) {
        super(name, position);
        this.speedMetersPerSecond = speedMetersPerSecond;
        this.accelerationMetersPerSecondSecond = accelerationMetersPerSecondSecond;
    }

    @Override
    public void tick(SimulationTick tick) {
        speedMetersPerSecond = speedMetersPerSecond + accelerationMetersPerSecondSecond * tick.tickDurationSeconds;
        map.moveObject(this, new MapPosition(
                Math.round(position.x + speedMetersPerSecond * tick.tickDurationSeconds),
                position.y,
                position.width,
                position.height));
        LOG.log(String.format("{Vehicle \"%s\" at %s}", toString(), position.toString()));
    }

    @Override
    public String toString() {
        return String.format("Vehicle \"%s\" is now at position %s with speed %.1f m/s and acceleration of %.5f m/s^2 ",
                name,
                position.toString(),
                speedMetersPerSecond,
                accelerationMetersPerSecondSecond);
    }
}
