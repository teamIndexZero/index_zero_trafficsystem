package kcl.teamIndexZero.traffic.log.microLogger;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Es on 06/02/2016.
 */
public enum MicroLogger {
    INSTANCE;
    private String file_name = "micrologger.log";
    MicroLogger() {
        writeToFile( "==================================[ NEW SESSION ]==================================" + System.lineSeparator());
    };
    MicroLogger( String name ) {
        file_name = name;
        writeToFile( "==================================[ NEW SESSION ]==================================" + System.lineSeparator());
    }

    public void log_Fatal( Object... objects ) {
        String s = "|--FATAL--| ";
        for( Object o : objects ) {
            s += o;
        }
        writeToFile( s + System.lineSeparator() );
    }
    public void log_Error( Object... objects ) {
        String s = "|--ERROR--| ";
        for( Object o : objects ) {
            s += o;
        }
        writeToFile( s + System.lineSeparator() );
    }
    public void log_Warning( Object... objects ) {
        String s = "|-WARNING-| ";
        for( Object o : objects ) {
            s += o;
        }
        writeToFile( s + System.lineSeparator() );
    }
    public void log( Object... objects ) {
        String s = "|-MESSAGE-| ";
        for( Object o : objects ) {
            s += o;
        }
        writeToFile( s + System.lineSeparator() );
    }
    public void log_Debug( Object... objects ) {
        String s = "|--DEBUG--| ";
        for( Object o : objects ) {
            s += o;
        }
        writeToFile( s + System.lineSeparator() );
    }

    private void writeToFile( String out ) {
        try {
            FileWriter writer = new FileWriter( file_name, true );
            writer.append( out );
            writer.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
