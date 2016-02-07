package kcl.teamIndexZero.traffic.log;

import kcl.teamIndexZero.traffic.log.microLogger.MicroLogger;
import kcl.teamIndexZero.traffic.log.outputs.Output;
import kcl.teamIndexZero.traffic.log.outputs.Output_TERM;
import kcl.teamIndexZero.traffic.log.outputs.Output_TXT;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by Es on 27/01/2016.
 */
public class Log_Config {
    private String global_file_name = "log_"; //Default
    private int global_log_level = Log_Levels.DEBUG; //Default
    private boolean log_exception_flag = true; //Default
    private Vector<Output> outputs = new Vector<Output>();

    //TODO Create file loader class for getting options from outside
    //TODO if file not found or empty then load defaults (txt)

    /**
     * Constructor (default)
     */
    public Log_Config() {
            try {
                //FileInput config_file = new FileInput("", "log_config.txt");

                Log_TimeStamp time_stamp = new Log_TimeStamp();
                global_file_name += time_stamp.getCustomStamp("yyyyMMdd'-'HHmmss");
                //TODO load from file
                //TODO if invalid/empty load defaults
                //DEFAULTS
                outputs.add(new Output_TERM("Console")); //Change to Output_TXT when implemented
                outputs.add(new Output_TXT( global_file_name ) );
            } catch ( IOException e ) {
                //TODO config file cannot be accessed to default to terminal and txt(if that's doable)
                MicroLogger.INSTANCE.log_Error( "IOException raised in [Log_Config.Log_Config()]" );
                MicroLogger.INSTANCE.log_ExceptionMsg( e );
                e.printStackTrace();
            }




    }

    /**
     * Gets the current global file output name
     *
     * @return Global file output name
     */
    public String getFileName() {
        return this.global_file_name;
    }

    /**
     * Gets the log level
     *
     * @return Log level
     */
    public int getGlobalLogLevel() {
        return this.global_log_level;
    }


    /**
     * Gets the "log exceptions" flag
     * @return Flag state
     */
    public boolean getLogExceptionFlag() { return this.log_exception_flag; }

    /**
     * Gets the output pipes of the log
     *
     * @return Outputs
     */
    protected Vector<Output> getOutputs() {
        return this.outputs;
    }

    /**
     * Sets the global log level
     *
     * @param level Minimum level
     */
    private void setGlobalLogLevel(int level) {
        if (level < Log_Levels.OFF) level = Log_Levels.OFF;
        this.global_log_level = level;
    }
}
