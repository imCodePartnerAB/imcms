package imcode.util;

public class DateConstants {
    public final static String DATE_FORMAT_STRING = "yyyy-MM-dd" ;
    public final static String TIME_NO_SECONDS_FORMAT_STRING = "HH:mm";
    public final static String DATETIME_NO_SECONDS_FORMAT_STRING = DATE_FORMAT_STRING + " "+ TIME_NO_SECONDS_FORMAT_STRING ;
    public final static String DATETIME_FORMAT_STRING = DATETIME_NO_SECONDS_FORMAT_STRING+":ss";
}
