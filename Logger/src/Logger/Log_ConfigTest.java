package Logger;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by Alwlyan on 28/01/2016.
 */
public class Log_ConfigTest extends TestCase {

    @Test
    public void testGetFileName() throws Exception {
        Log_Config config = new Log_Config();
        System.out.println( config.getFileName() );
    }

    @Test
    public void testGetGlobalLogLevel() throws Exception {

    }

    @Test
    public void testGetOutputs() throws Exception {

    }
}