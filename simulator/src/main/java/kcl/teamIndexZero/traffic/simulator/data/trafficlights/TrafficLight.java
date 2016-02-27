package kcl.teamIndexZero.traffic.simulator.data.trafficlights;

import java.util.concurrent.TimeUnit;

/**
 * Created by lexaux on 17/02/2016.
 * Altered by JK on 27/02/2016.
 */
public class TrafficLight {
    public enum State {
        RED, GREEN
    }
    public int timer;
    public int length;

    public TrafficLight(TrafficLight.State state, int timer, int length){

        TrafficLight.State State =  state;
        this.timer = timer;
        this.length = length;
    }
    public void setColor(TrafficLight.State state, int timer, int length) {


        try {

            for (int i = 0, j = 1; i < this.length; i = i + this.timer, j++) {
                if (j % 2 == 0) {

                    TrafficLight.State State = TrafficLight.State.RED;
                    TimeUnit.SECONDS.sleep(5);//automatic lights

                } else {

                    TrafficLight.State State = TrafficLight.State.GREEN;
                    TimeUnit.SECONDS.sleep(this.timer);
                }
            }

        } catch (Exception e) {
            System.out.println("Timer in Lights !" + e.getMessage());
        }

    }
}
