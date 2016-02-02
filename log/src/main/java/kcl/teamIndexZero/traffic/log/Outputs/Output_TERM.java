package kcl.teamIndexZero.traffic.log.Outputs;

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

    @Override
    public void output(String origin_name, int log_level, Long log_number, Log_TimeStamp time_stamp, Object... objects) {
        System.out.print(formatter.format(origin_name, log_level, log_number, time_stamp, objects));
    }
}
