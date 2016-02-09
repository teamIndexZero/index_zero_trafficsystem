package kcl.teamIndexZero.traffic.log;

import kcl.teamIndexZero.traffic.log.fileIO.FileInput;
import kcl.teamIndexZero.traffic.log.fileIO.FileOutput;
import kcl.teamIndexZero.traffic.log.microLogger.MicroLogger;
import kcl.teamIndexZero.traffic.log.outputs.Output;
import kcl.teamIndexZero.traffic.log.outputs.Output_CSV;
import kcl.teamIndexZero.traffic.log.outputs.Output_TERM;
import kcl.teamIndexZero.traffic.log.outputs.Output_TXT;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            if (!this.configurationLoader(config_file_name)) {
                applyDefaultConfiguration();
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
            if (lines.size() < 1) { //Empty file
                MicroLogger.INSTANCE.log("[Log_Config.configurationLoader()] Couldn't find anything in the config file.");
                if (!writeDefaultsToFile()) {
                    MicroLogger.INSTANCE.log_Fatal("[Log_Config.configurationLoader()] Failed to create/write a default config file!");
                }
                return false;
            } else {
                int line_counter = 0;
                int invalid_counter = 0;
                for (String line : lines) {
                    line_counter++;
                    if (configurationCheckerApplier(line) < 0) { //Invalid
                        MicroLogger.INSTANCE.log_Error("[Log_Config.configurationLoader( ", file_name, " )] Line ", line_counter, " is invalid. Please check syntax.");
                        invalid_counter++;
                    }
                }
                if (invalid_counter > 0) {
                    MicroLogger.INSTANCE.log_Error("[Log_Config.configurationLoader( ", file_name, " )] Configuration file has ", invalid_counter, " invalid lines.");
                    return false;
                }
            }
            return false;
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
        } catch (RuntimeException e) {
            MicroLogger.INSTANCE.log_Error("RuntimeException raised in [Log_Config.configurationLoader( ", file_name, " )] Couldn't create a valid file output from the configuration file.");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        }
    }

    /**
     * Configuration line checker
     *
     * @param line Line to check
     * @return Validity (-1:Invalid, 0:Ignore, 1:Valid)
     * @throws RuntimeException when an output couldn't be created
     */
    private int configurationCheckerApplier(String line) throws RuntimeException {
        try {
            if (line.matches("^//(?s:.)*$")) { //Comment ('//...')
                return 0;
            }
            if (line.matches("^OUTPUT=<[A-Z]+,\\w+>$")) { //Output
                final Pattern pattern = Pattern.compile(",(.+?)>");
                if (line.matches("^OUTPUT=<TXT,\\w+>$")) {
                    Matcher matcher = pattern.matcher(line);
                    matcher.find();
                    this.outputs.add(new Output_TXT(matcher.group(1)));
                    return 1;
                }
                if (line.matches("^OUTPUT=<CSV,\\w+>$")) {
                    Matcher matcher = pattern.matcher(line);
                    matcher.find();
                    this.outputs.add(new Output_CSV(matcher.group(1)));
                    return 1;
                }
                if (line.matches("^OUTPUT=<TERMINAL,\\w+>$")) {
                    Matcher matcher = pattern.matcher(line);
                    matcher.find();
                    this.outputs.add(new Output_TERM(matcher.group(1)));
                    return 1;
                }
                return -1;
            }
            if (line.matches("^FLAG=<[A-Z]+,[01]?>$")) { //Flag
                if (line.matches("^FLAG=<EXCEPTIONS,0>$")) {
                    this.log_exception_flag = false;
                    return 1;
                }
                if (line.matches("^FLAG=<EXCEPTIONS,1>$")) {
                    this.log_exception_flag = true;
                    return 1;
                }
                return -1;
            }
            if (line.matches("^VARIABLE=<[A-Z]+,[A-Z]+>$")) { //Variable
                if (line.matches("^VARIABLE=<LEVEL,OFF>$")) {
                    this.global_log_level = Log_Levels.OFF;
                    return 1;
                }
                if (line.matches("^VARIABLE=<LEVEL,FATAL>$")) {
                    this.global_log_level = Log_Levels.FATAL;
                    return 1;
                }
                if (line.matches("^VARIABLE=<LEVEL,ERROR>$")) {
                    this.global_log_level = Log_Levels.ERROR;
                    return 1;
                }
                if (line.matches("^VARIABLE=<LEVEL,WARNING>$")) {
                    this.global_log_level = Log_Levels.WARNING;
                    return 1;
                }
                if (line.matches("^VARIABLE=<LEVEL,MESSAGE>$")) {
                    this.global_log_level = Log_Levels.MSG;
                    return 1;
                }
                if (line.matches("^VARIABLE=<LEVEL,DEBUG>$")) {
                    this.global_log_level = Log_Levels.DEBUG;
                    return 1;
                }
                if (line.matches("^VARIABLE=<LEVEL,TRACE>$")) {
                    this.global_log_level = Log_Levels.TRACE;
                    return 1;
                }
                return -1;
            }
            return -1;
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [Log_Config.configurationCheckerApplier()] Couldn't create file output.");
            throw new RuntimeException("configurationCheckerApplier(..) couldn't apply an output.");
        }
    }

    /**
     * Applies hard-coded default configuration for the logger
     *
     * @throws IOException when there was a problem creating the default TXT output
     */
    private void applyDefaultConfiguration() throws IOException {
        this.global_log_level = Log_Levels.DEBUG;
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
                String s = "//======================================================================================" + System.lineSeparator() +
                        "// - Log configuration file - " + System.lineSeparator() +
                        "//======================================================================================" + System.lineSeparator() +
                        "// Output types available: TERMINAL, TXT, CSV" + System.lineSeparator() +
                        "// Flag types available: EXCEPTIONS," + System.lineSeparator() +
                        "// \t> syntax: NAME_OF_FLAG=1 for On or NAME_OF_FLAG=0 for Off" + System.lineSeparator() +
                        "// Variable types available: LEVEL" + System.lineSeparator() +
                        "// \t> level types: OFF, FATAL, ERROR, WARNING, MESSAGE, DEBUG, TRACE" + System.lineSeparator() +
                        "//======================================================================================" + System.lineSeparator() +
                        "// Syntax example: OUTPUT=<TERMINAL,my_name>" + System.lineSeparator() +
                        "//======================================================================================" + System.lineSeparator() +
                        "VARIABLE=<LEVEL,DEBUG>" + System.lineSeparator() +
                        "FLAG=<EXCEPTIONS,1>" + System.lineSeparator() +
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
