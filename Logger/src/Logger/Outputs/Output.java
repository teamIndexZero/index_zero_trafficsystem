package Logger.Outputs;

import Logger.Log_Levels;
import Logger.Log_TimeStamp;

/**
 * Created by Es on 29/01/2016.
 */
public class Output {
    private String output_name;
    private GlobalOutputTypes output_type;

    /**
     * Constructor
     * @param output_name
     * @param output_type
     */
    protected Output( String output_name, GlobalOutputTypes output_type ) {
        this.output_name = output_name;
        this.output_type = output_type;
    }

    /**
     * Sets the output name
     * @param output_name Output name
     */
    public void setName( String output_name ) {
        this.output_name = output_name;
    }

    /**
     * Gets output name
     * @return Output name
     */
    public String getOutputName() {
        return this.output_name;
    }

    /**
     * Gets output type
     * @return Output type
     */
    public GlobalOutputTypes getOutputType() {
        return this.output_type;
    }

    /**
     * Outputs the message
     * @param origin_name Origin name of the call
     * @param log_level Log level
     * @param log_number Message number in session
     * @param time_stamp Time stamp of the message
     * @param objects Message description (not used in "Output" parent class)
     */
    public void output( String origin_name, int log_level, Long log_number, Log_TimeStamp time_stamp, Object... objects ) {
        System.out.print( "[" + log_number.toString() + "] " + time_stamp.getDate() + " - " + time_stamp.getTime() + " " + Log_Levels.txtLevels[log_level] + " [" + origin_name + "] Please use specific output child class to see the message..." );
    }
}