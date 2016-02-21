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
public class Log_Config {
    private String global_file_name = "log"; //Default
    private int global_log_level = Log_Levels.WARNING; //Default
    private boolean log_exception_flag = true; //Default
    private Vector<Output> outputs = new Vector<>();
    private String config_file_name = "log_config.cfg";
    private Log_TimeStamp ts = null;


    /**
     * Constructor (default)
     */
    public Log_Config() {
        this.ts = new Log_TimeStamp();
        this.global_file_name += "_" + this.ts.getCustomStamp("yyyyMMdd'-'HHmmss");
        try {
            if (!this.loadConfiguration(config_file_name)) {
                applyDefaultConfiguration();
            }
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [Log_Config.Log_Config()]");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            e.printStackTrace();
        }
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
    private boolean loadConfiguration(String file_name) {
        try {
            FileInput in = new FileInput("", this.config_file_name);
            List<String> lines = in.read();
            if (lines.size() > 0) {
                int line_counter = 0;
                int invalid_counter = 0;
                for (String line : lines) {
                    line_counter++;
                    if (!applyConfigurationLine(line)) {
                        MicroLogger.INSTANCE.log_Error("[Log_Config.loadConfiguration( ", file_name, " )] Line ", line_counter, " is invalid. Please check syntax.");
                        invalid_counter++;
                    }
                }
                if (invalid_counter < 1) return true;
            } else { //Empty file
                MicroLogger.INSTANCE.log("[Log_Config.loadConfiguration()] Couldn't find anything in the config file.");
                if (!writeDefaultsToFile()) {
                    MicroLogger.INSTANCE.log_Fatal("[Log_Config.loadConfiguration()] Failed to create/write a default config file!");
                }
            }
            return false;
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [Log_Config.loadConfiguration( ", file_name, " )] Cannot read/load the config file. Reverting to defaults.. ");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            if (!writeDefaultsToFile()) {
                MicroLogger.INSTANCE.log_Fatal("[Log_Config.loadConfiguration( ", file_name, " )] Couldn't write a new defaults file for the configuration. Using hard-coded defaults.");
            } else {
                MicroLogger.INSTANCE.log("[Log_Config.loadConfiguration( ", file_name, " )] Success in creating a new default configuration file!");
            }
            return false;
        } catch (InvalidPathException e) {
            MicroLogger.INSTANCE.log_Error("InvalidPathException raised in [Log_Config.loadConfiguration( ", file_name, " )] Cannot read/load the config file. Reverting to defaults.. ");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        } catch (RuntimeException e) {
            MicroLogger.INSTANCE.log_Error("RuntimeException raised in [Log_Config.loadConfiguration( ", file_name, " )] Couldn't create a valid file output from the configuration file.");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            return false;
        }
    }

    /**
     * Checks and applies the configuration line
     *
     * @param line Line to check
     * @return line validity
     * @throws RuntimeException when an output couldn't be created
     */
    private boolean applyConfigurationLine(String line ) throws RuntimeException {
        try {
            if (line.matches("^//(?s:.)*$")) { //Comment ('//...')
                return true;
            }
            if (line.matches("^OUTPUT=<[A-Z]+,\\w+>$")) { //Output
                final Pattern pattern = Pattern.compile(",(.+?)>");
                if (line.matches("^OUTPUT=<TXT,\\w+>$")) {
                    Matcher matcher = pattern.matcher(line);
                    matcher.find();
                    this.outputs.add(new Output_TXT(matcher.group(1) + "_" + this.ts.getCustomStamp("yyyyMMdd'-'HHmmss")));
                    return true;
                }
                if (line.matches("^OUTPUT=<CSV,\\w+>$")) {
                    Matcher matcher = pattern.matcher(line);
                    matcher.find();
                    this.outputs.add(new Output_CSV(matcher.group(1) + "_" + this.ts.getCustomStamp("yyyyMMdd'-'HHmmss")));
                    return true;
                }
                if (line.matches("^OUTPUT=<TERMINAL,\\w+>$")) {
                    Matcher matcher = pattern.matcher(line);
                    matcher.find();
                    this.outputs.add(new Output_TERM(matcher.group(1)));
                    return true;
                }
                return false;
            }
            if (line.matches("^FLAG=<[A-Z]+,[01]?>$")) { //Flag
                if (line.matches("^FLAG=<EXCEPTIONS,0>$")) {
                    this.log_exception_flag = false;
                    return true;
                }
                if (line.matches("^FLAG=<EXCEPTIONS,1>$")) {
                    this.log_exception_flag = true;
                    return true;
                }
                return false;
            }
            if (line.matches("^VARIABLE=<[A-Z]+,[A-Z]+>$")) { //Variable
                if (line.matches("^VARIABLE=<LEVEL,OFF>$")) {
                    this.global_log_level = Log_Levels.OFF;
                    return true;
                }
                if (line.matches("^VARIABLE=<LEVEL,FATAL>$")) {
                    this.global_log_level = Log_Levels.FATAL;
                    return true;
                }
                if (line.matches("^VARIABLE=<LEVEL,ERROR>$")) {
                    this.global_log_level = Log_Levels.ERROR;
                    return true;
                }
                if (line.matches("^VARIABLE=<LEVEL,WARNING>$")) {
                    this.global_log_level = Log_Levels.WARNING;
                    return true;
                }
                if (line.matches("^VARIABLE=<LEVEL,MESSAGE>$")) {
                    this.global_log_level = Log_Levels.MSG;
                    return true;
                }
                if (line.matches("^VARIABLE=<LEVEL,DEBUG>$")) {
                    this.global_log_level = Log_Levels.DEBUG;
                    return true;
                }
                if (line.matches("^VARIABLE=<LEVEL,TRACE>$")) {
                    this.global_log_level = Log_Levels.TRACE;
                    return true;
                }
                return false;
            }
            return false;
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [Log_Config.applyConfigurationLine()] Couldn't create file output.");
            throw new RuntimeException("applyConfigurationLine(..) couldn't apply an output.");
        }
    }

    /**
     * Applies hard-coded default configuration for the logger
     *
     * @throws IOException when there was a problem creating the default TXT output
     */
    private void applyDefaultConfiguration() throws IOException {
        this.global_log_level = Log_Levels.WARNING;
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
                        "//=======================================EXAMPLES=======================================" + System.lineSeparator() +
                        "// OUTPUT=<TERMINAL,my_name>" + System.lineSeparator() +
                        "// VARIABLE<LEVEL,OFF>" + System.lineSeparator() +
                        "// FLAG=<EXCEPTION,0>" + System.lineSeparator() +
                        "//======================================================================================" + System.lineSeparator() +
                        "VARIABLE=<LEVEL,WARNING>" + System.lineSeparator() +
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
