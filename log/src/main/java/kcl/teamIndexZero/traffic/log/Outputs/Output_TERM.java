package kcl.teamIndexZero.traffic.log.outputs;

import kcl.teamIndexZero.traffic.log.Log_TimeStamp;

/**
 * Created by Es on 29/01/2016.
 */
public class Output_TERM extends Output {
    private Formatter_TERM formatter = new Formatter_TERM();

    /**
     * Constructor
     */
    public Output_TERM(String name) {
        super(name, GlobalOutputTypes.TERMINAL);
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
     * Gets the output's name
     *
     * @return Output name
     */
    @Override
    public String getOutputName() {
        return super.getOutputName();
    }

    /**
     * Gets the output's global type
     *
     * @return Output type
     */
    @Override
    public GlobalOutputTypes getOutputType() {
        return super.getOutputType();
    }

    /**
     * Outputs the message to the console
     *
     * @param origin_name Origin name of the call
     * @param log_level   Log level
     * @param log_number  Message number in session
     * @param time_stamp  Time stamp of the message
     * @param objects     Message description
     */
    @Override
    public void output(String origin_name, int log_level, Long log_number, Log_TimeStamp time_stamp, Object... objects) {
        System.out.println(formatter.format(origin_name, log_level, log_number, time_stamp, objects));
    }

    /**
     * Outputs Exception details to the console
     *
     * @param origin_name Origin name of the call
     * @param time_stamp  Time stamp of the message
     * @param e           Exception raised
     */
    public void output(String origin_name, Log_TimeStamp time_stamp, Exception e) {
        System.out.println(formatter.format(origin_name, time_stamp, e));
    }
}
