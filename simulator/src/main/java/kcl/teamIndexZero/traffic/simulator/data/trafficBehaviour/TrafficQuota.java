package kcl.teamIndexZero.traffic.simulator.data.trafficBehaviour;


/**
 * TrafficQuota class to keep a tally of the traffic flow on a link in a round
 */
class TrafficQuota {
    private final int weight;
    private Integer count;

    /**
     * Constructor
     *
     * @param weight Weight of the quota
     */
    public TrafficQuota(int weight) {
        this.weight = weight;
        this.count = 0;
    }

    /**
     * Resets the quota counter to 0
     */
    public void reset() {
        this.count = 0;
    }

    /**
     * Increments the quota counter by 1
     *
     * @return Fulfilment of quota
     */
    public boolean incrementCounter() {
        if (this.count >= this.weight) return false;
        else this.count++;
        return true;
    }
}