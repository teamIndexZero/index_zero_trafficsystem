package kcl.teamIndexZero.traffic.simulator.data.trafficLight;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.time.LocalDateTime;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by JK on 14-03-16.
 */
public class TrafficLightControllerTest {
    private TrafficLightController controllerModel;
    private Date date;


    @Before
    public void setUp() throws Exception {
        controllerModel = new TrafficLightController();
    }

    @After
    public void tearDown() throws Exception {
        controllerModel = null;
    }

    @Test
    public void test() {


    }
}