package kcl.teamIndexZero.traffic.log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Es on 27/01/2016.
 */
public class Logger implements Logger_Interface {
    private static Map<String, Logger> loggers;
    private static Log_Engine log_engine;
    private String calling_instance_name;

    static {
        Log_Engine.load(new Log_Engine(new Log_Config()));
        log_engine = Log_Engine.getInstance();
        loggers = new HashMap<>();
    }

    /**
     * Constructor
     *
     * @param instance_name Name of the class/package calling the logger (e.g.: MyClass.class.getName())
     */
    private Logger(String instance_name) {
        this.calling_instance_name = instance_name;
    }

    /**
     * Gets the Logger associated with the instance name or creates a new one if none found
     *
     * @param instance_name Name of the Logger instance
     * @return Logger
     */
    public static synchronized Logger getLoggerInstance(String instance_name) {
        synchronized (Logger.class) {
            Logger logger_instance = loggers.get(instance_name);
            if (logger_instance == null) {
                logger_instance = new Logger(instance_name);
                loggers.put(instance_name, logger_instance);
            }
            return logger_instance;
        }
    }

    /**
     * Logs a standard log message
     *
     * @param objects Message details
     */
    @Override
    public void log(Object... objects) {
        log_engine.processLogMsg(new Log_TimeStamp(), Log_Levels.MSG, calling_instance_name, objects);
    }


    /**
     * Logs a fatal log message
     *
     * @param objects Message details
     */
    @Override
    public void log_Fatal(Object... objects) {
        log_engine.processLogMsg(new Log_TimeStamp(), Log_Levels.FATAL, calling_instance_name, objects);
    }


    /**
     * Logs an error log message
     *
     * @param objects Message details
     */
    @Override
    public void log_Error(Object... objects) {
        log_engine.processLogMsg(new Log_TimeStamp(), Log_Levels.ERROR, calling_instance_name, objects);
    }

    /**
     * Logs a warning log message
     *
     * @param objects Message details
     */
    @Override
    public void log_Warning(Object... objects) {
        log_engine.processLogMsg(new Log_TimeStamp(), Log_Levels.WARNING, calling_instance_name, objects);
    }


    /**
     * Logs a debug log message
     *
     * @param objects Message details
     */
    @Override
    public void log_Debug(Object... objects) {
        log_engine.processLogMsg(new Log_TimeStamp(), Log_Levels.DEBUG, calling_instance_name, objects);
    }

    /**
     * Logs a trace log message
     *
     * @param objects Message details
     */
    @Override
    public void log_Trace(Object... objects) {
        log_engine.processLogMsg(new Log_TimeStamp(), Log_Levels.TRACE, calling_instance_name, objects);
    }

    /**
     * Logs an Exception
     *
     * @param e Exception
     */
    @Override
    public void log_Exception(Exception e) {
        log_engine.processException(new Log_TimeStamp(), calling_instance_name, e);
    }
}
