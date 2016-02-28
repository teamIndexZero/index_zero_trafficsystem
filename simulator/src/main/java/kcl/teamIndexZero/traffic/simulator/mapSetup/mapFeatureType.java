package kcl.teamIndexZero.traffic.simulator.mapSetup;

/**
 * Created by Es on 27/02/2016.
 * Enum of possible basic feature types for manual construction of a simulation map
 */
public enum mapFeatureType {
    SIMPLE_ONE_WAY_ROAD,    //1 lane
    SIMPLE_TWO_WAY_ROAD,    //1 lane on either side
    DUAL_CARRIAGEWAY,       //2 lanes on either side
    T_JUNCTION,
    X_JUNCTION
}
