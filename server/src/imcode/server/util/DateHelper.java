package imcode.server.util;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateHelper {
    /** start using theses instead in all places suitable */
    private static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
    private static final String TIME_FORMAT_STRING = "HH:mm";
    public final static String DATETIME_FORMAT_STRING = DATE_FORMAT_STRING + " " + TIME_FORMAT_STRING ;
    public static final SimpleDateFormat DATE_TIME_FORMAT_IN_DATABASE = new SimpleDateFormat( DATETIME_FORMAT_STRING );
    public static final SimpleDateFormat LOG_DATE_TIME_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS " );

    public static Date createDateObjectFromString( String dateStr ) {
        Date result = null;
        if( dateStr != null ) {
            try {
                result = DATE_TIME_FORMAT_IN_DATABASE.parse( dateStr );
            } catch( ParseException ex ) {
                result = null;
            }
        }
        return result;
    }
}
