package kcl.teamIndexZero.traffic.log.microLogger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * MicroLogger
 * A 'lite' file logger
 */
public enum MicroLogger {
    INSTANCE;
    private String file_name = "micrologger.log";

    MicroLogger() {
        writeToFile("==================================[ NEW SESSION ]==================================" + System.lineSeparator());
    }

    /**
     * Sets a new output file name for the rest of the session
     *
     * @param name File name
     */
    public void setFileName(String name) {
        file_name = name;
        writeToFile("==================================[ NEW SESSION ]==================================" + System.lineSeparator());
    }

    /**
     * Logs a Fatal message
     *
     * @param objects Message description
     */
    public void log_Fatal(Object... objects) {
        String s = "|--FATAL--| ";
        for (Object o : objects) {
            s += o;
        }
        writeToFile(s + System.lineSeparator());
    }

    /**
     * Logs an Error message
     *
     * @param objects Message description
     */
    public void log_Error(Object... objects) {
        String s = "|--ERROR--| ";
        for (Object o : objects) {
            s += o;
        }
        writeToFile(s + System.lineSeparator());
    }

    /**
     * Logs a warning message
     *
     * @param objects Message description
     */
    public void log_Warning(Object... objects) {
        String s = "|-WARNING-| ";
        for (Object o : objects) {
            s += o;
        }
        writeToFile(s + System.lineSeparator());
    }

    /**
     * Logs a standard message
     *
     * @param objects Message description
     */
    public void log(Object... objects) {
        String s = "|-MESSAGE-| ";
        for (Object o : objects) {
            s += o;
        }
        writeToFile(s + System.lineSeparator());
    }

    /**
     * Logs a debug message
     *
     * @param objects Message description
     */
    public void log_Debug(Object... objects) {
        String s = "|--DEBUG--| ";
        for (Object o : objects) {
            s += o;
        }
        writeToFile(s + System.lineSeparator());
    }

    /**
     * Logs an Exception's stacktrace
     *
     * @param e Exception
     */
    public void log_ExceptionMsg(Exception e) {
        String s = "\t==Exception trace==" + System.lineSeparator() + "\t";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        writeToFile(s + sw.toString() + System.lineSeparator());
    }

    /**
     * Writes string to file
     *
     * @param out String
     */
    private void writeToFile(String out) {
        try {
            FileWriter writer = new FileWriter(file_name, true);
            writer.append(out);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
