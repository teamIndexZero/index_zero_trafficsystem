package kcl.teamIndexZero.traffic.log.outputs;

import kcl.teamIndexZero.traffic.log.Log_TimeStamp;
import kcl.teamIndexZero.traffic.log.fileIO.FileOutput;
import kcl.teamIndexZero.traffic.log.microLogger.MicroLogger;

import java.io.IOException;

/**
 * Created by Es on 06/02/2016.
 */
public class Output_TXT extends Output {
    private Formatter_TXT formatter = new Formatter_TXT();
    private FileOutput out = null;

    /**
     * Constructor
     * @param output_name
     * @exception IOException
     */
    public Output_TXT(String output_name) throws IOException {
        super(output_name, GlobalOutputTypes.TXT);
        try {
            MicroLogger.INSTANCE.log( "[Output_TXT.Output_TXT( ", output_name, " )] New Text output created.");
            out = new FileOutput("logs", output_name + ".txt");
            MicroLogger.INSTANCE.log_Debug( "[Output_TXT.Output_TXT( ", output_name, " )] All done." );
        } catch ( IOException e ) {
            throw e;
        }

    }

    @Override
    public void setName(String output_name) {
        super.setName(output_name);
    }

    @Override
    public String getOutputName() {
        return super.getOutputName();
    }

    @Override
    public GlobalOutputTypes getOutputType() {
        return super.getOutputType();
    }

    @Override
    public void output(String origin_name, int log_level, Long log_number, Log_TimeStamp time_stamp, Object... objects) {
        try {
            out.appendString(formatter.format(origin_name, log_level, log_number, time_stamp, objects));
        } catch ( IOException e ) {
            System.err.println("Caught IOException in Output_TXT.output(): " + e.getMessage());
        }
    }
}
