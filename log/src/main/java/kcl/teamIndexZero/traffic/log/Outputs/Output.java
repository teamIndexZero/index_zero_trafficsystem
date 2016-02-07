package kcl.teamIndexZero.traffic.log.outputs;

import kcl.teamIndexZero.traffic.log.Log_Levels;
import kcl.teamIndexZero.traffic.log.Log_TimeStamp;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Es on 29/01/2016.
 */
public class Output {
    private String output_name;
    private GlobalOutputTypes output_type;

    /**
     * Constructor
     *
     * @param output_name
     * @param output_type
     */
    protected Output(String output_name, GlobalOutputTypes output_type) {
        this.output_name = output_name;
        this.output_type = output_type;
    }

    /**
     * Sets the output name
     *
     * @param output_name Output name
     */
    public void setName(String output_name) {
        this.output_name = output_name;
    }

    /**
     * Gets output name
     *
     * @return Output name
     */
    public String getOutputName() {
        return this.output_name;
    }

    /**
     * Gets output type
     *
     * @return Output type
     */
    public GlobalOutputTypes getOutputType() {
        return this.output_type;
    }

    /**
     * Outputs the message
     *
     * @param origin_name Origin name of the call
     * @param log_level   Log level
     * @param log_number  Message number in session
     * @param time_stamp  Time stamp of the message
     * @param objects     Message description (not used in "Output" parent class)
     */
    public void output(String origin_name, int log_level, Long log_number, Log_TimeStamp time_stamp, Object... objects) {
        System.out.println("[" + log_number.toString() + "] " + time_stamp.getDate() + " - " + time_stamp.getTime() + " " + Log_Levels.txtLevels[log_level] + " [" + origin_name + "] Please use specific output child class to see the message...");
    }

    /**
     * Outputs an exception's details
     *
     * @param origin_name Origin name of the call
     * @param time_stamp  Time stamp of the message
     * @param e           Exception raised
     */
    public void output(String origin_name, Log_TimeStamp time_stamp, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        System.out.println("\t===Exception raised in [" + origin_name + "] at " + time_stamp.getDate() + " - " + time_stamp.getTime() + "===" + System.lineSeparator() + sw.toString() + System.lineSeparator());
    }
}