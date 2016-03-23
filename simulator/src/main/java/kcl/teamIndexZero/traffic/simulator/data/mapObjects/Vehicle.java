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

    public final double minSpeedMetersPerSec;
    public final double maxSpeedMetersPerSec;
    public final double maxAccelerationMetersPerSecSec;
    private double currentSpeedMetersPerSec;
    private double currentAccelerationMetersPerSecondSecond;
    private double lengthMeters;
    private double widthMeters;


    /**
     * Vehicle Constructor.
     *
     * @param name used for identification in logs for example
     * @param lane Lane the vehicle is on
     */
    private Vehicle(ID id,
                    String name,
                    Lane lane,
                    double minSpeedMetersPerSec,
                    double maxSpeedMetersPerSec,
                    double maxAccelerationMetersPerSecSec) {
        super(id, name, lane);
        this.minSpeedMetersPerSec = minSpeedMetersPerSec;
        this.maxSpeedMetersPerSec = maxSpeedMetersPerSec;
        this.maxAccelerationMetersPerSecSec = maxAccelerationMetersPerSecSec;
        setLane(lane, false);
        this.currentSpeedMetersPerSec = 0;
    }

    /**
     * Create a passenger car. Physical parameters taken from BMW 5 series data sheet.
     *
     * @param id   id
     * @param name name
     * @param lane lane to start with
     * @return newly created car.
     */
    public static Vehicle createPassengerCar(ID id, String name,
                                             Lane lane) {
        Vehicle v = new Vehicle(id,
                name,
                lane,
                8 * 1000 / 3600,
                60 * 1000 / 3600,
                0.9);
        v.setLengthMeters(3.5);
        v.setWidthMeters(1.901);
        return v;
    }

    /**
     * Create a truck. Truck is much slower to accelerate, and also bigger in both width and height.
     *
     * @param id   id of tte truck
     * @param name name
     * @param lane lane to strat with
     * @return new truck
     */
    public static Vehicle createTruck(ID id, String name,
                                      Lane lane) {
        Vehicle v = new Vehicle(id,
                name,
                lane,
                5 * 1000 / 3600,
                45 * 1000 / 3600,
                0.25
        );
        v.setLengthMeters(9.2);
        v.setWidthMeters(2.490);
        return v;
    }

    public double getLengthMeters() {
        return lengthMeters;
    }

    public void setLengthMeters(double lengthMeters) {
        this.lengthMeters = lengthMeters;
    }

    public double getWidthMeters() {
        return widthMeters;
    }

    public void setWidthMeters(double widthMeters) {
        this.widthMeters = widthMeters;
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
            allColoursCache.put(colourScale, Color.getHSBColor((float) (0.65 + 0.35 * colourScale / 10), 1, 1));
        }
        return allColoursCache.get(colourScale);
    }

    @Override
    public String getNameAndRoad() {
        return super.getNameAndRoad() + String.format(" %.1f˚", Math.toDegrees(getBearing()));
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
                driveOnJunction(link);
            } else {
                driveOnGenericLink(link);
            }
        }
    }

    /**
     * Logic for the car moving on the lane (not a crossing or traffic collector or something.
     * In few words, the car would try to reach its maximum cruising speed in lane. In case there is other car
     * in front which forces this car to lower speed, it will try to change lanes - to outer lane,
     * and if not possible, to inner one.
     * <p>
     * When changing, the car will check availability of space.
     *
     * @param tick simulation moment of time. We need to know that for period of time it covers, to calculate speed and
     *             position advancement
     * @return next link
     */
    public Link driveOnLane(SimulationTick tick) {
        Link link = lane.getNextLink();
        if (link == null) {
            LOG.log_Error("Car terminating its run. Seems to be dead end (map end).");
            return null;
        }

        // Check if the next crossing is a real crossing and not a connection of two segments of road.
        boolean isThisLaneTerminatingAtCrossing = false;
        if ((link.getNextFeature() instanceof Junction) && ((Junction) link.getNextFeature()).getConnectedFeatures().size() > 2) {
            isThisLaneTerminatingAtCrossing = true;
        }
        boolean shouldAccelerate = false;
        boolean shouldBrake = false;

        // depending on current speed, determine the distance to keep. It is defined by current speed in such a way so
        // car can brake within the short given period of time.
        double distanceToKeep = getDistanceToKeepToNextObject();
        double carForwardPoint = lane.isForwardLane() ? positionOnLane + lengthMeters : positionOnLane - lengthMeters;
        // if there is something ahead which stops car from going fast,
        if (!lane.isClearAhead(this, carForwardPoint, distanceToKeep)) {
            Lane outerLane = lane.tryGetLaneToOuterSide();
            Lane innerLane = lane.tryGetLaneToInnerSide();
            // check if the outer lane has free space and switch to it
            if (outerLane != null
                    && outerLane.isClearAhead(this, carForwardPoint, distanceToKeep)
                    && outerLane.isClearBehind(this, carForwardPoint, distanceToKeep / 2)) {
                // switch to outerLane;
                setLane(outerLane, true);
            } else if (innerLane != null // if not possible do same for inner lane.
                    && innerLane.isClearAhead(this, carForwardPoint, distanceToKeep)
                    && innerLane.isClearBehind(this, carForwardPoint, distanceToKeep / 2)) {
                // switch to inner lane
                setLane(innerLane, true);
            } else {
                shouldBrake = true;
            }
        }

        // if we are not braking yet, and have some distance to the crossing (or next link is not a crossing), accelerate
        // TODO check for traffic light status should be here.
        double distanceToTheEndOfTheLane = lane.isBackwardLane() ? positionOnLane : getLane().getLength() - positionOnLane;
        if (!shouldBrake
                && ((isThisLaneTerminatingAtCrossing && distanceToTheEndOfTheLane > getDistanceToKeepToNextObject())
                || !isThisLaneTerminatingAtCrossing)
                && Math.abs(currentSpeedMetersPerSec) < maxSpeedMetersPerSec
                || Math.abs(currentSpeedMetersPerSec) < minSpeedMetersPerSec) {
            shouldAccelerate = true;
        } else if (isThisLaneTerminatingAtCrossing && distanceToTheEndOfTheLane < getDistanceToKeepToNextObject()
                // in case we are moving too fast and the crossing is near, brake
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
        if (!lane.isClearAhead(this, positionOnLane, 3)) {
            // if we are really close to some other car, force stop
            currentSpeedMetersPerSec = 0;
        }

        positionOnLane += (lane.isBackwardLane() ? -1 : 1)
                * currentSpeedMetersPerSec
                * tick.getTickDurationSeconds();
        return link;
    }

    /**
     * Calculate the normal distance in meters we want to keep to next object depending on speed. It should be
     * such that car can safely brake within the period.
     *
     * @return distance to other object in meters.
     */
    public double getDistanceToKeepToNextObject() {
        return 3 + currentSpeedMetersPerSec * 2.2;
    }

    /**
     * Drive into the {@link TrafficGenerator} which also acts as collector.
     *
     * @param tg traffic generator to drive to
     */
    void driveIntoTrafficGenerator(TrafficGenerator tg) {
        lane.removeVehicle(this);
        tg.terminateTravel(this);
    }

    /**
     * What to do on generic link (not a JunctionLink).
     *
     * @param link to drive to
     */
    void driveOnGenericLink(Link link) {
        Feature f = link.getNextFeature();
        if (f instanceof TrafficGenerator)
            driveIntoTrafficGenerator((TrafficGenerator) f);
    }

    /**
     * Decision block on what to do on Junction.
     *
     * @param outflowLink the link via which this cars enters junction.
     */
    void driveOnJunction(Link outflowLink) {
        JunctionLink junctionLink = (JunctionLink) outflowLink;
        Junction junction = (Junction) map.getMapFeatures().get((junctionLink).getJunctionID());

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

        // if nowhere to go, and also found Traffic Generator, terminate run.
        if (allOutgoingLanes.size() == 0) {
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

        // lanes allowed to turn are between 30 and 110 degrees turn. less then 30 is considered straight, more than 110
        // looks like impossible from physical standpoint.
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

        // lanes allowed for forward movement. Differnce between incoming angle and outgoing should be less than 30 degrees.
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

        if (nextLane.isClearAhead(this, nextLane.isForwardLane() ? 0 : nextLane.getWidth(), getDistanceToKeepToNextObject())) {
            if (lane.getRoad().getName() != null && !lane.getRoad().getName().equals(nextLane.getRoad().getName())) {
                LOG.log(String.format("%s turning from %s to %s", getName(), lane.getRoad().getName(),
                        nextLane.getRoad().getName()));
            }
            setLane(nextLane, false);
        } else {
            LOG.log_Warning("Can not turn to desired lane - it is busy.");
        }
    }

    /**
     * Try to get outermost lane from lane set (by index).
     *
     * @param lanesAllowed list to search in
     * @return null in case none found, or the outermost lane.
     */
    private Lane tryGetOuterLane(List<Lane> lanesAllowed) {
        return lanesAllowed
                .stream()
                .filter(possibleLane -> possibleLane.getLanes().getNumberOfLanes() - possibleLane.getIndexInDirectedLanes() == 1)
                .findAny()
                .orElse(null);
    }

    /**
     * Just get any lane from set (NOT RANDOM!)
     *
     * @param lanesAllowed list of lanes allowed
     * @return null if not found, lane otherwise
     */
    private Lane getAnyLane(Collection<Lane> lanesAllowed) {
        Lane nextLane;
        nextLane = lanesAllowed
                .stream()
                // if there is none with same index, pick any from the allowed for forward
                .findAny()
                .orElse(null);
        return nextLane;
    }

    /**
     * Try to find lane with the same index (counting from outermost) as the one we are now.
     *
     * @param lanesAllowed all lanes to search
     * @return null if not found, otherwise - lane.
     */
    private Lane tryGetSameLaneNumber(List<Lane> lanesAllowed) {
        return lanesAllowed
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
        return currentAccelerationMetersPerSecondSecond / 1000 * 3600;
    }

    public double getSpeedKph() {
        return currentSpeedMetersPerSec / 1000 * 3600;
    }

    /**
     * Sets the car into the lane. Car is removed from previous lane.
     *
     * @param lane                   lane to attach to.
     * @param preservePositionOnLane try to preserve position. If set to true, it will not update position on lane. If set
     *                               to true, it will reset it to starting position.
     */
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

    /**
     * Get a bearing for a car basing on its lane and position in lane (which section of polyline it is attached to.
     *
     * @return bearing.
     */
    public double getBearing() {
        return lane.getBearingAtDistanceFromStart(positionOnLane);
    }
}
