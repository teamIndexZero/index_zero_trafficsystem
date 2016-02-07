package kcl.teamIndexZero.traffic.log;

import kcl.teamIndexZero.traffic.log.fileIO.FileInput;
import kcl.teamIndexZero.traffic.log.microLogger.MicroLogger;
import kcl.teamIndexZero.traffic.log.outputs.Output;
import kcl.teamIndexZero.traffic.log.outputs.Output_TERM;
import kcl.teamIndexZero.traffic.log.outputs.Output_TXT;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.List;
import java.util.Vector;

/**
 * Created by Es on 27/01/2016.
 */
public class Log_Config {
    private String global_file_name = "log_"; //Default
    private int global_log_level = Log_Levels.DEBUG; //Default
    private boolean log_exception_flag = true; //Default
    private Vector<Output> outputs = new Vector<Output>();
    private String config_file_name = "log_config.cfg";

    /**
     * Constructor (default)
     */
    public Log_Config() {
        try {
            Log_TimeStamp time_stamp = new Log_TimeStamp();
            global_file_name += time_stamp.getCustomStamp("yyyyMMdd'-'HHmmss");
            if (!this.configurationLoader(config_file_name)) {
                //DEFAULT Configuration
                outputs.add(new Output_TERM("Console"));
                outputs.add(new Output_TXT(global_file_name));
            }
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [Log_Config.Log_Config()]");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
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
     *
     * @return Flag state
     */
    public boolean getLogExceptionFlag() {
        return this.log_exception_flag;
    }

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

    /**
     * Config file loader
     *
     * @param file_name Name of the configuration file for the logger
     * @return Success
     */
    private boolean configurationLoader(String file_name) {
        try {
            FileInput in = new FileInput("", this.config_file_name);
            List<String> lines = in.read();
            //TODO Implement check for lines and extraction/assignments of options
            //TODO If it fails then microlog the failures and return false
            return false; //TODO change that to 'true' once everything is implemented here
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [Log_Config.configurationLoader( ", file_name, " )] Cannot read/load the config file. Reverting to defaults.. ");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        } catch (InvalidPathException e) {
            MicroLogger.INSTANCE.log_Error("InvalidPathException raised in [Log_Config.configurationLoader( ", file_name, " )] Cannot read/load the config file. Reverting to defaults.. ");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        }
    }
}
