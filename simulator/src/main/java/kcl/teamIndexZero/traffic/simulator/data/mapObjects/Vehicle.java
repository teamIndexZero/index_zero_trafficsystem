package kcl.teamIndexZero.traffic.simulator.data.mapObjects;

import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.Junction;
import kcl.teamIndexZero.traffic.simulator.data.features.Lane;
import kcl.teamIndexZero.traffic.simulator.data.links.JunctionLink;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;

import java.awt.*;
import java.util.List;

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
    private boolean isOnReverseLane = false;


    /**
     * Vehicle Constructor.
     *
     * @param name used for identification in logs for example
     * @param lane Lane the vehicle is on
     */
    public Vehicle(String name,
                   Lane lane) {
        super(name, lane);
        this.speedMetersPerSecond = 0 * 1000 / 3600;
    }

    @Override
    public Color getColor() {
        if (accelerationMetersPerSecondSecond > 0) {
            return Color.GREEN;
        } else if (accelerationMetersPerSecondSecond < 0) {
            return Color.RED;
        } else {
            return Color.BLACK;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
        Link link = lane.getNextLink();
        if (link == null) {
            LOG.log_Error("Car terminating its run. Seems to be dead end (map end).");
            pleaseRemoveMeFromSimulation = true;
            return;
        }
        JunctionLink j = (JunctionLink) link;
        Junction junction = (Junction) map.getMapFeatures().get(((JunctionLink) link).getJunctionID());
        boolean isThisLaneTerminatingAtCrossing = false;
        if (junction.getConnectedFeatures().size() > 2) {
            isThisLaneTerminatingAtCrossing = true;
        }

        double distanceToTheEndOfTheLane = isOnReverseLane ? positionOnRoad : getLane().getLength() - positionOnRoad;
        if (
                ((isThisLaneTerminatingAtCrossing && distanceToTheEndOfTheLane > 60) || !isThisLaneTerminatingAtCrossing)
                        && Math.abs(speedMetersPerSecond) < (50 * 1000 / 3600)
                        || Math.abs(speedMetersPerSecond) < 5 * 1000 / 3600) {
            accelerationMetersPerSecondSecond = 0.7;
        } else if (isThisLaneTerminatingAtCrossing && distanceToTheEndOfTheLane < 50
                && Math.abs(speedMetersPerSecond) > 10 * 1000 / 3600) {
            accelerationMetersPerSecondSecond = (Math.abs(speedMetersPerSecond) > 40 ? -0.7 : -0.15) * Math.abs(speedMetersPerSecond);
        } else {
            accelerationMetersPerSecondSecond = 0;
        }

        speedMetersPerSecond =
                speedMetersPerSecond + accelerationMetersPerSecondSecond * tick.getTickDurationSeconds();

        positionOnRoad += (isOnReverseLane ? -1 : 1)
                * speedMetersPerSecond
                * tick.getTickDurationSeconds();


        //TODO if end of road is reached then pass to next feature with the remaining travel length??
        //TODO maybe adapt behavior for the looking ahead distance based on projected stopping time at current speed?
        //TODO if looking ahead distance goes out of scope of the current feature then query link
        if (positionOnRoad < 0 || positionOnRoad >= lane.getLength()) {
            junction.incrementUsage();

            List<Link> nextLinks = j.getLinks();
            if (nextLinks.size() > 0) {
                // get random link
                Link outLink = nextLinks.get((int) (Math.random() * nextLinks.size()));
                Lane nextLane = (Lane) outLink.getNextFeature();
                if (lane.getRoad().getName() != null && !lane.getRoad().getName().equals(nextLane.getRoad().getName())) {
                    LOG.log(String.format("%s turning from %s to %s", getName(), lane.getRoad().getName(),
                            nextLane.getRoad().getName()));
                }
                this.lane = nextLane;

                // This is a weird case for forward/backward movement. If we are moving in the forward lane, we start
                // from road length 0, and carry on with positive speed/acceleration.
                // if we start from the 'end' of the road - say, in backwards lane - we end up starting movement in
                // roadLength, and movement decreases this position.
                // TODO probably worth making lanes direction agnostic.
                if (nextLane.getRoad().getForwardSide().getLanes().contains(nextLane)) {
                    this.positionOnRoad = 0;
                    isOnReverseLane = false;
                } else {
                    this.positionOnRoad = lane.getLength();
                    isOnReverseLane = true;
                }
            } else {
                LOG.log_Debug(String.format("Got end-link, car going to exit map soon. Link: %s", link.toString()));
                pleaseRemoveMeFromSimulation = true;
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("{Vehicle \"%s\" with speed %.1f m/s and acceleration of %.5f m/s^2}",
                name,
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
