package kcl.teamIndexZero.traffic.simulator.data.mapObjects;

import kcl.teamIndexZero.traffic.simulator.data.ID;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import kcl.teamIndexZero.traffic.simulator.data.features.Feature;
import kcl.teamIndexZero.traffic.simulator.data.features.Junction;
import kcl.teamIndexZero.traffic.simulator.data.features.Lane;
import kcl.teamIndexZero.traffic.simulator.data.features.TrafficGenerator;
import kcl.teamIndexZero.traffic.simulator.data.links.JunctionLink;
import kcl.teamIndexZero.traffic.simulator.data.links.Link;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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

    public static final Color ACCELERATING_COLOR = new Color(0, 200, 0);
    public static final Color DECELERATING_COLOR = new Color(250, 0, 0);
    public static final Color CONSTANT_SPEED_COLOR = Color.BLACK;
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
            return ACCELERATING_COLOR;
        } else if (accelerationMetersPerSecondSecond < 0) {
            return DECELERATING_COLOR;
        } else {
            return CONSTANT_SPEED_COLOR;
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

    void driveIntoTrafficGenerator(SimulationTick tick, Link link) {
        Feature f = link.getNextFeature();
        if (f instanceof TrafficGenerator) {
            TrafficGenerator tg = (TrafficGenerator) f;
            tg.terminateTravel(this);
        } else {
            throw new IllegalStateException("Got a wrong type of link " + link.getClass().getName());
        }

    }

    void driveOnGenericLink(SimulationTick tick, Link link) {
        Feature f = link.getNextFeature();
        if (f instanceof TrafficGenerator)
            driveIntoTrafficGenerator(tick, link);
    }

    void driveOnJunction(SimulationTick tick, Link outLink) {
        JunctionLink junctionLink = (JunctionLink) outLink;
        Junction junction = (Junction) map.getMapFeatures().get(((JunctionLink) outLink).getJunctionID());


        //TODO maybe adapt behavior for the looking ahead distance based on projected stopping time at current speed?
        //TODO if looking ahead distance goes out of scope of the current feature then query link
        junction.incrementUsage();
        // 1. decide on feature. Remove possible features which are on too steep angle
        // 2. decide on lane of this feature. If the feature seems to be a continuation of an original road,
        //    stick to the same lane, otherwise stick to the outermost one.
        // 3. do a switch.


        List<Feature> availableFeatures = junction.getConnectedFeatures()
                .stream()
                .filter(feature -> !feature.getID().equals(lane.getRoadID())).collect(Collectors.toList());
        if (availableFeatures.isEmpty()) {
            LOG.log_Error("Got to a dead end (as I think). Probably two outgoing roads ending in same point with no further way out.");
            return;
        }
        double currentBearing = junction.getBearingForLane(lane);

        // get all possible links
        List<Link> nextLinks = junctionLink.getLinks();

        Map<Lane, Double> allOutgoingLanes = nextLinks
                .stream()
                .filter(link -> link.getNextFeature() instanceof Lane)
                .map(link -> (Lane) link.getNextFeature())
                .collect(Collectors.toMap(Function.identity(),
                        lane -> {
                            double otherLaneBearing = junction.getBearingForLane(lane);
                            return Math.abs(otherLaneBearing - currentBearing);
                        }));

        if (allOutgoingLanes.size() == 0) {
            // if nowhere to go, and also found Traffic Generator, terminate run.
            Optional<TrafficGenerator> maybeTG = nextLinks
                    .stream()
                    .map(Link::getNextFeature)
                    .filter(feature -> feature instanceof TrafficGenerator)
                    .map(feature -> (TrafficGenerator) feature)
                    .findAny();
            maybeTG.ifPresent(tg -> tg.terminateTravel(this));
            if (maybeTG.isPresent()) {
                return;
            }
        }

        List<Lane> lanesAllowedForTurn =
                allOutgoingLanes
                        .entrySet()
                        .stream()
                        .filter(laneEntry -> {
                            double bearingDifference = laneEntry.getValue();
                            return bearingDifference < Math.toRadians(110) && bearingDifference > Math.toRadians(30);
                        })
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

        List<Lane> lanesAllowedForForward =
                allOutgoingLanes
                        .entrySet()
                        .stream()
                        .filter(laneEntry -> {
                            double bearingDifference = laneEntry.getValue();
                            return bearingDifference <= Math.toRadians(30);
                        })
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
        Lane nextLane;

        boolean turning = Math.random() > 0.65;
        Collections.shuffle(lanesAllowedForTurn);
        Collections.shuffle(lanesAllowedForForward);
        if (!turning) {
            // if we want to go forward, try to find a lane in 'go forward' list
            nextLane = lanesAllowedForForward
                    .stream()
                    .findAny()
                    .orElseGet(
                            // if unable to go forward, try at least turn normally
                            () -> lanesAllowedForTurn.stream().findAny().orElseGet(
                                    // just go anywhere possible
                                    () -> allOutgoingLanes.keySet().stream().findAny().get())
                    );
            // same applies for 'want turn' branch - we first try to find one in turns zone, if impossible - go forward,
            //
        } else {
            nextLane = lanesAllowedForTurn
                    .stream()
                    .findAny()
                    .orElseGet(
                            () -> lanesAllowedForForward.stream().findAny().orElseGet(
                                    () -> allOutgoingLanes.keySet().stream().findAny().get())
                    );
        }

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
        return accelerationMetersPerSecondSecond * 1000 / 3600;
    }

    public double getSpeedKph() {
        return speedMetersPerSecond * 1000 / 3600;
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
