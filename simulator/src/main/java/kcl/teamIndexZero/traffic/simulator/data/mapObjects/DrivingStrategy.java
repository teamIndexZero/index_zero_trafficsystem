package kcl.teamIndexZero.traffic.simulator.data.mapObjects;

/**
 * Driving strategy - something encapsulating a behavior of a driver (when to accelerate, when to brake, what are the
 * limits of speed, whether be aggressive and change lanes, or stay calm and just move slowly after the other.
 */
public interface DrivingStrategy {

    /**
     * Vehicle calls this strategy every time it needs to do some action. Strategy may change vehicle's speed, acceleartion,
     * turn somewhere (i.e. put it on a different lane/road), but the vehicle itself needs to do advancement.
     *
     * @param vehicle the one which is driving.
     */
    void drive(Vehicle vehicle);

    /**
     * Called by vehicle when it is at the junction and wants to move forward.
     *
     * @param vehicle the one to maybe change.
     */
    void maybeMoveToNextRoad(Vehicle vehicle);
}
