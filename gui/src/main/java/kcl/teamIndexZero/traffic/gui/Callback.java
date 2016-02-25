package kcl.teamIndexZero.traffic.gui;

/**
 * An interface simple as hell: something to be called.
 */
@FunctionalInterface
public interface Callback {

    /**
     * Call an implemented method. With no parameters, nor waiting any result from it.
     */
    void call();
}
