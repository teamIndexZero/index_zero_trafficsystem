package kcl.teamIndexZero.traffic.log;

import kcl.teamIndexZero.traffic.log.fileIO.FileInput;
import kcl.teamIndexZero.traffic.log.fileIO.FileOutput;
import kcl.teamIndexZero.traffic.log.microLogger.MicroLogger;
import kcl.teamIndexZero.traffic.log.outputs.Output;
import kcl.teamIndexZero.traffic.log.outputs.Output_TERM;
import kcl.teamIndexZero.traffic.log.outputs.Output_TXT;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by Es on 27/01/2016.
 */
//TODO make diagram for the logic steps in there
public class Log_Config {
    private String global_file_name = "log"; //Default
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
            global_file_name += "_" + time_stamp.getCustomStamp("yyyyMMdd'-'HHmmss");
            applyDefaultConfiguration(); //TODO uncomment below and remove this line once all the other TODOs are implemented
            /*
            if (!this.configurationLoader(config_file_name)) {
                applyDefaultConfiguration();
            }
            */
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
            if (lines.size() < 1) { //Empty file
                MicroLogger.INSTANCE.log("[Log_Config.configurationLoader()] Couldn't find anything in the config file.");
                if (!writeDefaultsToFile()) {
                    MicroLogger.INSTANCE.log_Fatal("[Log_Config.configurationLoader()] Failed to create/write a default config file!");
                }
                return false;
            } else {
                int line_counter = 1;
                int invalid_counter = 0;
                Iterator<String> it = lines.iterator();
                while (it.hasNext()) {
                    int check_value = configurationChecker(it.next());
                    if (check_value < 0) { //Invalid
                        MicroLogger.INSTANCE.log_Error("[Log_Config.configurationLoader( ", file_name, " )] Line ", line_counter, " is invalid. Please check syntax.");
                        invalid_counter++;
                    } else if (check_value == 0) { //Ignored (comment)
                        it.remove();
                    }
                    line_counter++;
                }
                if (invalid_counter > 0) {
                    MicroLogger.INSTANCE.log_Error("[Log_Config.configurationLoader( ", file_name, " )] Configuration file has ", invalid_counter, " invalid lines.");
                    return false;
                }
                return applyConfiguration(lines);
            }
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [Log_Config.configurationLoader( ", file_name, " )] Cannot read/load the config file. Reverting to defaults.. ");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            if (!writeDefaultsToFile()) {
                MicroLogger.INSTANCE.log_Fatal("[Log_Config.configurationLoader( ", file_name, " )] Couldn't write a new defaults file for the configuration. Using hard-coded defaults.");
            } else {
                MicroLogger.INSTANCE.log("[Log_Config.configurationLoader( ", file_name, " )] Success in creating a new default configuration file!");
            }
            return false;
        } catch (InvalidPathException e) {
            MicroLogger.INSTANCE.log_Error("InvalidPathException raised in [Log_Config.configurationLoader( ", file_name, " )] Cannot read/load the config file. Reverting to defaults.. ");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        }
    }

    /**
     * Configuration line checker
     *
     * @param line Line to check
     * @return Validity (-1:Invalid, 0:Ignore, 1:Valid)
     */
    private int configurationChecker(String line) {
        if (line.matches("^OUTPUT=<[A-Z]+,\\w+>$")) { //Output
            //TODO check specifics


            return 1;
        }
        if (line.matches("^FLAG=<[A-Z]+,[01]?")) { //Flag
            //TODO check specifics

            return 1;
        }
        if (line.matches("^//(?s:.)*$")) return 0; //Comment ('//...')
        return -1;
    }

    /**
     * Checks & applies configuration lines to the log system
     *
     * @param config_lines Configuration lines from file
     * @return Success
     */
    private boolean applyConfiguration(List<String> config_lines) {
        //TODO apply lines of configuration
        //if flag


        //if OUTPUT, extract output type and name
        //switch/case for output types
        //case default: unknown
        return true; //if all is good
    }

    /**
     * Applies hard-coded default configuration for the logger
     *
     * @throws IOException when there was a problem creating the default TXT output
     */
    private void applyDefaultConfiguration() throws IOException {
        this.log_exception_flag = true;
        this.outputs.clear();
        this.outputs.add(new Output_TERM("Console"));
        try {
            this.outputs.add(new Output_TXT(this.global_file_name));
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Fatal("IOException raised in [Log_Config.applyDefaultConfiguration()] on creation of TXT output. Log messages will only show in the console.");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            throw e;
        }
    }

    /**
     * Writes default values and example to config file
     * Note: will overwrites previous file
     *
     * @return Success
     */
    private boolean writeDefaultsToFile() {
        try {
            FileOutput out = new FileOutput("", this.config_file_name);
            if (out.clearFileContent()) {
                String s = "//================================================================" + System.lineSeparator() +
                        "// - Log configuration file - " + System.lineSeparator() +
                        "//===============================================================" + System.lineSeparator() +
                        "// Output types available: TERMINAL, TXT, CSV" + System.lineSeparator() +
                        "// Flag types available: EXCEPTIONS" + System.lineSeparator() +
                        "// Flag syntax: NAME_OF_FLAG=1 for On or NAME_OF_FLAG=0 for Off" + System.lineSeparator() +
                        "// Syntax example: OUTPUT=<TERMINAL,my_name>" + System.lineSeparator() +
                        "//===============================================================" + System.lineSeparator() +
                        "//--------------------------Global Flags-------------------------" + System.lineSeparator() +
                        "FLAG=<EXCEPTIONS,1>;" + System.lineSeparator() +
                        "//----------------------------Outputs----------------------------" + System.lineSeparator() +
                        "//" + System.lineSeparator() +
                        "OUTPUT=<TERMINAL,Console>" + System.lineSeparator() +
                        "OUTPUT=<TXT,log>";
                out.appendString(s);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [Log_Config.writeDefaultsToFile()] Failed to create a clean default config file.");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        }
    }
}
