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
import java.util.*;
import java.util.List;
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

    public static final int SPEED_THRESHOLD_TO_START_BRAKING_BEFORE_CROSSING = 10 * 1000 / 3600;
    private static HashMap<Integer, Color> allColoursCache = new HashMap<>();

    public double minSpeedMetersPerSec = (Math.random() * 4 + 4) * 1000 / 3600;
    public double maxAccelerationMetersPerSecSec = 0.4 + Math.random() * 0.4;
    public double maxSpeedMetersPerSec = (30 + 30 * Math.random()) * 1000 / 3600;
    private double currentSpeedMetersPerSec;
    private double currentAccelerationMetersPerSecondSecond;


    /**
     * Vehicle Constructor.
     *
     * @param name used for identification in logs for example
     * @param lane Lane the vehicle is on
     */
    public Vehicle(ID id, String name,
                   Lane lane) {
        super(id, name, lane);
        setLane(lane, false);
        this.currentSpeedMetersPerSec = 0;
    }

    /**
     * Gets the vehicle's colour
     *
     * @return Colour
     */
    @Override
    public Color getColor() {
        int colourScale = (int) (currentSpeedMetersPerSec / maxSpeedMetersPerSec * 10);
        if (!allColoursCache.containsKey(colourScale)) {
            allColoursCache.put(colourScale, Color.getHSBColor((float) (0.65 + 0.35 * colourScale / 10), 1, 0.95f));
        }
        return allColoursCache.get(colourScale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(SimulationTick tick) {
        Link link = driveOnLane(tick);
        if (link == null) return;

        // if we have terminated our lane, decide what to do next.
        if (positionOnLane < 0 || positionOnLane >= lane.getLength()) {
            if (link instanceof JunctionLink && ((JunctionLink) link).isInflowLink()) {
                driveOnJunction(tick, link);
            } else if (link instanceof JunctionLink && ((JunctionLink) link).isOutflowLink()) {
                driveIntoTrafficGenerator(tick, link);
            } else {
                driveOnGenericLink(tick, link);
            }
        }
    }

    public Link driveOnLane(SimulationTick tick) {
        Link link = lane.getNextLink();
        if (link == null) {
            LOG.log_Error("Car terminating its run. Seems to be dead end (map end).");
            return null;
        }

        boolean isThisLaneTerminatingAtCrossing = false;
        if ((link.getNextFeature() instanceof Junction) && ((Junction) link.getNextFeature()).getConnectedFeatures().size() > 2) {
            isThisLaneTerminatingAtCrossing = true;
        }
        boolean shouldAccelerate = false;
        boolean shouldBrake = false;

        double distanceToKeep = getDistanceToKeepToNextObject();
        if (!lane.isClearAhead(positionOnLane, distanceToKeep)) {
            Lane outerLane = lane.tryGetLaneToOuterSide();
            Lane innerLane = lane.tryGetLaneToInnerSide();
            if (outerLane != null
                    && outerLane.isClearAhead(positionOnLane, distanceToKeep)) {
                // switch to outerLane;
                setLane(outerLane, true);
            } else if (innerLane != null
                    && innerLane.isClearAhead(positionOnLane, distanceToKeep)) {
                // switch to inner lane
                setLane(innerLane, true);
            } else {
                shouldBrake = true;
            }
        }

        double distanceToTheEndOfTheLane = lane.isBackwardLane() ? positionOnLane : getLane().getLength() - positionOnLane;
        if (!shouldBrake
                && ((isThisLaneTerminatingAtCrossing && distanceToTheEndOfTheLane > getDistanceToKeepToNextObject())
                || !isThisLaneTerminatingAtCrossing)
                && Math.abs(currentSpeedMetersPerSec) < maxSpeedMetersPerSec
                || Math.abs(currentSpeedMetersPerSec) < minSpeedMetersPerSec) {
            shouldAccelerate = true;
        } else if (isThisLaneTerminatingAtCrossing && distanceToTheEndOfTheLane < getDistanceToKeepToNextObject()
                && Math.abs(currentSpeedMetersPerSec) > SPEED_THRESHOLD_TO_START_BRAKING_BEFORE_CROSSING) {
            shouldBrake = true;
        } else {
            currentAccelerationMetersPerSecondSecond = 0;
        }

        if (shouldAccelerate && !shouldBrake) {
            currentAccelerationMetersPerSecondSecond = maxAccelerationMetersPerSecSec;
        }
        if (shouldBrake) {
            currentAccelerationMetersPerSecondSecond = -currentSpeedMetersPerSec / 2.8;
        }

        currentSpeedMetersPerSec =
                Math.max(0, currentSpeedMetersPerSec + currentAccelerationMetersPerSecondSecond * tick.getTickDurationSeconds());
        if (!lane.isClearAhead(positionOnLane, 3)) {
            currentSpeedMetersPerSec = 0;
        }

        positionOnLane += (lane.isBackwardLane() ? -1 : 1)
                * currentSpeedMetersPerSec
                * tick.getTickDurationSeconds();
        return link;
    }

    public double getDistanceToKeepToNextObject() {
        return 3 + currentSpeedMetersPerSec * 2.2;
    }

    void driveIntoTrafficGenerator(SimulationTick tick, Link link) {
        Feature f = link.getNextFeature();
        if (f instanceof TrafficGenerator) {
            TrafficGenerator tg = (TrafficGenerator) f;
            lane.removeVehicle(this);
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
                            return Math.min(2 * Math.PI - Math.abs(otherLaneBearing - currentBearing),
                                    Math.abs(otherLaneBearing - currentBearing));
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
            nextLane = tryGetSameLaneNumber(lanesAllowedForForward);
            if (nextLane == null) {
                nextLane = getAnyLane(lanesAllowedForForward);
            }
            if (nextLane == null) {
                nextLane = tryGetOuterLane(lanesAllowedForTurn);
            }
            if (nextLane == null) {
                nextLane = getAnyLane(lanesAllowedForTurn);
            }
            if (nextLane == null) {
                nextLane = getAnyLane(allOutgoingLanes.keySet());
            }
        } else {
            // same applies for 'want turn' branch - we first try to find one in turns zone, if impossible - go forward,
            nextLane = tryGetOuterLane(lanesAllowedForTurn);
            if (nextLane == null) {
                nextLane = getAnyLane(lanesAllowedForTurn);
            }
            if (nextLane == null) {
                nextLane = tryGetSameLaneNumber(lanesAllowedForForward);
            }
            if (nextLane == null) {
                nextLane = getAnyLane(lanesAllowedForForward);
            }
            if (nextLane == null) {
                nextLane = getAnyLane(allOutgoingLanes.keySet());
            }
        }

        if (lane.getRoad().getName() != null && !lane.getRoad().getName().equals(nextLane.getRoad().getName())) {
            LOG.log(String.format("%s turning from %s to %s", getName(), lane.getRoad().getName(),
                    nextLane.getRoad().getName()));
        }
        setLane(nextLane, false);
    }

    private Lane tryGetOuterLane(List<Lane> lanesAllowedForTurn) {
        return lanesAllowedForTurn
                .stream()
                .filter(possibleLane -> possibleLane.getLanes().getNumberOfLanes() - possibleLane.getIndexInDirectedLanes() == 1)
                .findAny()
                .orElse(null);
    }

    private Lane getAnyLane(Collection<Lane> lanesAllowedForForward) {
        Lane nextLane;
        nextLane = lanesAllowedForForward
                .stream()
                // if there is none with same index, pick any from the allowed for forward
                .findAny()
                .orElse(null);
        return nextLane;
    }

    private Lane tryGetSameLaneNumber(List<Lane> lanesAllowedForForward) {
        return lanesAllowedForForward
                .stream()
                // if possible, with the same lane index from the last lane (outer first)
                .filter(possibleLane ->
                        possibleLane.getLanes().getNumberOfLanes() - possibleLane.getIndexInDirectedLanes()
                                == lane.getLanes().getNumberOfLanes() - lane.getIndexInDirectedLanes())
                .findAny()
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("{Vehicle \"%s\" with speed %.1f m/s and acceleration of %.5f m/s^2}",
                name,
                currentSpeedMetersPerSec,
                currentAccelerationMetersPerSecondSecond);
    }

    public double getAccelerationKphH() {
        return currentAccelerationMetersPerSecondSecond * 1000 / 3600;
    }

    public double getSpeedKph() {
        return currentSpeedMetersPerSec * 1000 / 3600;
    }

    public void setLane(Lane lane, boolean preservePositionOnLane) {
        if (this.lane != null) {
            this.lane.removeVehicle(this);
        }
        this.lane = lane;
        lane.addVehicle(this);
        // This is a weird case for forward/backward movement. If we are moving in the forward lane, we start
        // from road length 0, and carry on with positive speed/acceleration.
        // if we start from the 'end' of the road - say, in backwards lane - we end up starting movement in
        // roadLength, and movement decreases this position.
        // TODO probably worth making lanes direction agnostic.
        if (lane.isForwardLane()) {
            this.positionOnLane = preservePositionOnLane ? this.positionOnLane : 0;
        } else {
            this.positionOnLane = preservePositionOnLane ? this.positionOnLane : lane.getLength();
        }
    }
}
