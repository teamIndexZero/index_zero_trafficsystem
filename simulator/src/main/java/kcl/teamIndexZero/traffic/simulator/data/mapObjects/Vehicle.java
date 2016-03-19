package kcl.teamIndexZero.traffic.simulator.data.mapObjects;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.*;
import kcl.teamIndexZero.traffic.simulator.data.links.JunctionLink;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Vehicle(ID id, String name,
                   Lane lane) {
        super(id, name, lane);
        setLane(lane);
        this.speedMetersPerSecond = 5;
    }

    /**
     * Gets the vehicle's colour
     *
     * @return Colour
     */
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
            return;
        }

        boolean isThisLaneTerminatingAtCrossing = false;
        if ((link.getNextFeature() instanceof Junction) && ((Junction) link.getNextFeature()).getConnectedFeatures().size() > 2) {
            isThisLaneTerminatingAtCrossing = true;
        }

        double distanceToTheEndOfTheLane = isOnReverseLane ? positionOnRoad : getLane().getLength() - positionOnRoad;
        if (((isThisLaneTerminatingAtCrossing && distanceToTheEndOfTheLane > 60) || !isThisLaneTerminatingAtCrossing)
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

        // if we have terminated our lane, decide what to do next.
        if (positionOnRoad < 0 || positionOnRoad >= lane.getLength()) {
            if (link instanceof JunctionLink && ((JunctionLink) link).isInflowLink()) {
                driveOnJunction(tick, link);
            } else if (link instanceof JunctionLink && ((JunctionLink) link).isOutflowLink()) {
                driveIntoTrafficGenerator(tick, link);
            } else {
                driveOnGenericLink(tick, link);
            }
        }
    }

    private void driveIntoTrafficGenerator(SimulationTick tick, Link link) {
        Feature f = link.getNextFeature();
        if (f instanceof TrafficGenerator) {
            TrafficGenerator tg = (TrafficGenerator) f;
            tg.terminateTravel(this);
        } else {
            throw new IllegalStateException("Got a wrong type of link " + link.getClass().getName());
        }

    }

    private void driveOnGenericLink(SimulationTick tick, Link link) {
        Feature f = link.getNextFeature();
        if (f instanceof TrafficGenerator)
            driveIntoTrafficGenerator(tick, link);
    }

    public void driveOnJunction(SimulationTick tick, Link link) {
        JunctionLink j = (JunctionLink) link;
        Junction junction = (Junction) map.getMapFeatures().get(((JunctionLink) link).getJunctionID());


        //TODO maybe adapt behavior for the looking ahead distance based on projected stopping time at current speed?
        //TODO if looking ahead distance goes out of scope of the current feature then query link
        junction.incrementUsage();
        // 1. decide on feature. Remove possible features which are on too steep angle
        // 2. decide on lane of this feature. If the feature seems to be a continuation of an original road,
        //    stick to the same lane, otherwise stick to the outermost one.
        // 3. do a switch.
        boolean turning = Math.random() > 0.6;

        List<Feature> availableFeatures = junction.getConnectedFeatures()
                .stream()
                .filter(feature -> !feature.getID().equals(lane.getRoadID())).collect(Collectors.toList());
        if (availableFeatures.isEmpty()) {
            LOG.log_Error("Got to a dead end (as I think). Probably two outgoing roads ending in same point with no further way out.");
            return;
        }
        double bearingNowNormalized = junction.getBearingForFeature(lane.getRoad()) % Math.toRadians(180);

        List<Link> nextLinks = j.getLinks();
        Set<Feature> possibleFeaturesByLink = nextLinks
                .stream()
                .map(aLink -> {
                    if (aLink.getNextFeature() instanceof Lane) {
                        return ((Lane) aLink.getNextFeature()).getRoad();
                    }
                    return aLink.getNextFeature();
                })
                .collect(Collectors.toSet());

        // choose a feature to go to
        Feature featureToGoTo = possibleFeaturesByLink
                .stream()
                // and depending on whether we want to turn or not, look at the angle between them
                .filter(feature -> {
                    if (!(feature instanceof Road)) {
                        // for all non-road features, allow them in result (for ex. traffic generator)
                        return true;
                    }
                    // for simplicity, we don't care about direction forward or backward, just that we have
                    double bearingAnother = junction.getBearingForFeature(feature) % Math.toRadians(180);
                    if (turning) {
                        // if we want to turn, we return all the features whose angle is rather different to incoming one
                        // but we also make sure we omit too steep back turns
                        return Math.abs(bearingAnother - bearingNowNormalized) > Math.toRadians(60);

                    } else {
                        // keeping in around the same lane. Now, we need to identify feature which has around the same angle.
                        return Math.abs(bearingAnother - bearingNowNormalized) < Math.toRadians(15);
                    }
                })
                .findAny()
                // in case none found, just default to any from the available
                .orElseGet(() -> availableFeatures.stream().filter(possibleFeaturesByLink::contains).findAny().orElse(null));
        if (featureToGoTo == null) {
            LOG.log_Error("Got to a dead end (as I think). Probably two outgoing roads ending in same point with no further way out.");
            return;
        }
        if (featureToGoTo instanceof TrafficGenerator) {
            ((TrafficGenerator) featureToGoTo).terminateTravel(this);
            return;
        }
        List<Link> linksToGivenFeature = nextLinks.stream()
                .filter(possibleLink -> {
                    if (possibleLink.getNextFeature().equals(featureToGoTo)) {
                        return true;
                    } else if (featureToGoTo instanceof Road) {
                        Road r = (Road) featureToGoTo;
                        return r.getForwardSide().getLanes().contains(possibleLink.getNextFeature()) ||
                                r.getBackwardSide().getLanes().contains(possibleLink.getNextFeature());
                    }
                    throw new IllegalStateException("Linked not to a lane and not to a non-road. Weird.");
                })
                .collect(Collectors.toList());
        Link nextLink = linksToGivenFeature
                .stream()
                // if we don't turn, try to stay in same lane.
                .filter(candidateLink -> {
                    // stick to the outer lane.
                    Lane otherLane = (Lane) candidateLink.getNextFeature();
                    return otherLane.getLanes().getNumberOfLanes() - otherLane.getIndexInDirectedLanes()
                            == lane.getLanes().getNumberOfLanes() - lane.getIndexInDirectedLanes();
                })
                .findAny()
                // if not found, select random
                .orElse(linksToGivenFeature.get(((int) (Math.random() * linksToGivenFeature.size())) % linksToGivenFeature.size()));
        Lane nextLane = ((Lane) nextLink.getNextFeature());

        if (lane.getRoad().getName() != null && !lane.getRoad().getName().equals(nextLane.getRoad().getName())) {
            LOG.log(String.format("%s turning from %s to %s", getName(), lane.getRoad().getName(),
                    nextLane.getRoad().getName()));
        }
        setLane(nextLane);

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

    public double getAccelerationKphH() {
        return accelerationMetersPerSecondSecond;
    }

    public double getSpeedKph() {
        return speedMetersPerSecond;
    }

    public void setLane(Lane lane) {
        this.lane = lane;
        // This is a weird case for forward/backward movement. If we are moving in the forward lane, we start
        // from road length 0, and carry on with positive speed/acceleration.
        // if we start from the 'end' of the road - say, in backwards lane - we end up starting movement in
        // roadLength, and movement decreases this position.
        // TODO probably worth making lanes direction agnostic.
        if (lane.getRoad().getForwardSide().getLanes().contains(lane)) {
            this.positionOnRoad = 0;
            isOnReverseLane = false;
        } else {
            this.positionOnRoad = lane.getLength();
            isOnReverseLane = true;
        }

    }
}
