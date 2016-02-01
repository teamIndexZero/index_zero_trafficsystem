package Logger;

import Logger.Outputs.Output;
import Logger.Outputs.Output_TERM;

import java.util.Vector;

/**
 * Created by Es on 27/01/2016.
 */
public class Log_Config {
    private String global_file_name = "log_"; //Default
    private int global_log_level = Log_Levels.DEBUG; //Default
    private Vector<Output> outputs = new Vector<>();

    //TODO Create file loader class for getting options from outside
    //TODO if file not found or empty then load defaults (txt)

    /**
     * Constructor (default)
     */
    protected Log_Config() {
        //TODO load from file
        //TODO if invalid/empty load defaults

        //DEFAULTS
        Log_TimeStamp time_stamp = new Log_TimeStamp(); //TODO catch exception
        global_file_name += time_stamp.getCustomStamp( "yyyyMMdd'-'HHmmss" );
        outputs.add( new Output_TERM( "Console" ) ); //Change to Output_TXT when implemented
    }

    /**
     * Gets the current global file output name
     * @return Global file output name
     */
    public String getFileName() {
        return this.global_file_name;
    }

    /**
     * Gets the log level
     * @return Log level
     */
    public int getGlobalLogLevel() {
        return this.global_log_level;
    }

    /**
     * Gets the output pipes of the log
     * @return Outputs
     */
    protected Vector<Output> getOutputs() {
        return this.outputs;
    }

    /**
     * Sets the global log level
     * @param level Minimum level
     */
    private void setGlobalLogLevel( int level ) {
        if( level < Log_Levels.OFF ) level = Log_Levels.OFF;
        this.global_log_level = level;
    }
}
