package kcl.teamIndexZero.traffic.log;

import org.junit.Test;

/**
 * Created by Es on 06/02/2016.
 */
public class Log_EngineTest {

    @Test
    public void testGetInstance() throws Exception {
        Log_Engine log_engine = Log_Engine.getInstance();
        org.junit.Assert.assertFalse( log_engine != null );
        org.junit.Assert.assertEquals( log_engine.getClass(), Log_Engine.class );


    }

    @Test
    public void testProcessLogMsg() throws Exception {

    }
}