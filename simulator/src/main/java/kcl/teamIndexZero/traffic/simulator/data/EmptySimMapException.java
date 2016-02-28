package kcl.teamIndexZero.traffic.simulator.data;

/**
 * Created by Es on 28/02/2016.
 * Exception for when a simulation map is empty of features
 */
public class EmptySimMapException extends Exception {
    public EmptySimMapException() {
    }

    public EmptySimMapException(String message) {
        super(message);
    }
}
