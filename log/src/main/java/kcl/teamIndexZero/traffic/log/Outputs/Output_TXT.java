package kcl.teamIndexZero.traffic.log.outputs;

import kcl.teamIndexZero.traffic.log.Log_TimeStamp;
import kcl.teamIndexZero.traffic.log.fileIO.FileOutput;
import kcl.teamIndexZero.traffic.log.microLogger.MicroLogger;

import java.io.IOException;

/**
 * Created by Es on 06/02/2016.
 * Output to '.txt' file
 */
public class Output_TXT extends Output {
    private Formatter_TXT formatter = new Formatter_TXT();
    private FileOutput out = null;

    /**
     * Constructor
     *
     * @param output_name Name of the file
     * @throws IOException when creating a FileOutput fails
     */
    public Output_TXT(String output_name) throws IOException {
        super(output_name, GlobalOutputTypes.TXT);
        try {
            out = new FileOutput("logs", output_name + ".txt");
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [Output_TXT.Output_TXT( ", output_name, " )]");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
            throw e;
        }
    }

    /**
     * FileOutput instance
     *
     * @param output Instance of the FileOutput
     * @throws IOException creating a FileOutput fails
     */
    public Output_TXT(FileOutput output) throws IOException {
        super(output.getFileName(), GlobalOutputTypes.TXT);
        out = output;
    }

    /**
     * Sets the name of the output
     *
     * @param output_name Output name
     */
    @Override
    public void setName(String output_name) {
        super.setName(output_name);
    }

    /**
     * Gets the name of the output
     *
     * @return Output name
     */
    @Override
    public String getOutputName() {
        return super.getOutputName();
    }

    /**
     * Get the type of the output
     *
     * @return Output type
     */
    @Override
    public GlobalOutputTypes getOutputType() {
        return super.getOutputType();
    }

    /**
     * Outputs message to a text file
     *
     * @param origin_name Origin name of the call
     * @param log_level   Log level
     * @param log_number  Message number in session
     * @param time_stamp  Time stamp of the message
     * @param objects     Message description (not used in "Output" parent class)
     */
    @Override
    public void output(String origin_name, int log_level, Long log_number, Log_TimeStamp time_stamp, Object... objects) {
        try {
            out.appendString(formatter.format(origin_name, log_level, log_number, time_stamp, objects));
        } catch (IOException e) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [Output_TXT.output( ", origin_name, ", ", log_level, ", ", log_number, ", ", time_stamp, ", ", objects, " )]");
            MicroLogger.INSTANCE.log_ExceptionMsg(e);
        }
    }

    /**
     * Outputs exception information to a text file
     *
     * @param origin_name Name of the message's origin
     * @param time_stamp  Time stamp of the message
     * @param log_number  Message number in session
     * @param e           Exception raised
     */
    public void output(String origin_name, Log_TimeStamp time_stamp, Long log_number, Exception e) {
        try {
            out.appendString(formatter.format(origin_name, time_stamp, log_number, e));
        } catch (IOException exception) {
            MicroLogger.INSTANCE.log_Error("IOException raised in [Output_TXT.output( ", origin_name, ", ", time_stamp, " )] <-Output Exception version.");
            MicroLogger.INSTANCE.log_ExceptionMsg(exception);
        }
    }
}
