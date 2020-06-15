package imcode.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateConstants {
    public final static String DATE_FORMAT_STRING = "yyyy-MM-dd";
    public final static String TIME_NO_SECONDS_FORMAT_STRING = "HH:mm";
    public final static String TIME_FORMAT_STRING = "HH:mm:ss";
    public final static String DATETIME_NO_SECONDS_FORMAT_STRING = DATE_FORMAT_STRING + " " + TIME_NO_SECONDS_FORMAT_STRING;
    public final static String DATETIME_FORMAT_STRING = DATETIME_NO_SECONDS_FORMAT_STRING + ":ss";

    public final static String TIME_NO_SECONDS_REGEX = "^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$";

    public final static DateFormat DATETIME_DOC_FORMAT = new SimpleDateFormat(DATETIME_FORMAT_STRING);
    public final static DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);
    public final static DateFormat TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT_STRING);
}
