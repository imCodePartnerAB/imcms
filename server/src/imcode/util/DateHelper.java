package imcode.util;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateHelper {
    /** start using theses instead in all places suitable */
    public final static String DATE_FORMAT_STRING = "yyyy-MM-dd" ;
    public final static String DATETIME_FORMAT_NO_SECONDS_FORMAT_STRING = DATE_FORMAT_STRING + " HH:mm" ;
    public final static String DATETIME_SECONDS_FORMAT_STRING = DATETIME_FORMAT_NO_SECONDS_FORMAT_STRING+":ss";

    public static Date createDateObjectFromString( String dateStr ) {
        Date result = null;
        if( dateStr != null ) {
            try {
                result = new SimpleDateFormat( DATETIME_SECONDS_FORMAT_STRING ).parse( dateStr );
            } catch( ParseException ex ) {
                result = null;
            }
        }
        return result;
    }
}
