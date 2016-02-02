package kcl.teamIndexZero.traffic.log;

/**
 * Created by Es on 27/01/2016.
 */
public final class Log_Levels {
    private Log_Levels() {
    }

    ;
    public static final int OFF = 0;
    public static final int FATAL = 1;
    public static final int ERROR = 2;
    public static final int WARNING = 3;
    public static final int MSG = 4;
    public static final int DEBUG = 5;
    public static final int TRACE = 6;
    public static final String[] txtLevels;

    static {
        txtLevels = new String[]{"---OFF---", "|--FATAL--|", "|--ERROR--|", "|-WARNING-|", "|-MESSAGE-|", "|--DEBUG--|", "|--TRACE--|"};
    }

    public static final String[] csvLevels;

    static {
        csvLevels = new String[]{"OFF", "FATAL", "ERROR", "WARNING", "MESSAGE", "DEBUG", "TRACE"};
    }
}
