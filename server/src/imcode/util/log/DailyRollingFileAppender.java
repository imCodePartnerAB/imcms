package imcode.util.log ;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
    Complete ripoff of org.apache.log4j.DailyRollingFileAppender in log4j 1.2.8,
    since it has bugs and was impossible to subclass.
 */
public class DailyRollingFileAppender extends FileAppender {


    // The code assumes that the following constants are in a increasing
    // sequence.
    static final int TOP_OF_TROUBLE = -1;
    static final int TOP_OF_MINUTE = 0;
    static final int TOP_OF_HOUR = 1;
    static final int HALF_DAY = 2;
    static final int TOP_OF_DAY = 3;
    static final int TOP_OF_WEEK = 4;
    static final int TOP_OF_MONTH = 5;


    /**
     The date pattern. By default, the pattern is set to
     "'.'yyyy-MM-dd" meaning daily rollover.
     */
    private String datePattern = "'.'yyyy-MM-dd";

    /**
     The log file will be renamed to the value of the
     scheduledFilename variable when the next interval is entered. For
     example, if the rollover period is one hour, the log file will be
     renamed to the value of "scheduledFilename" at the beginning of
     the next hour.

     The precise time when a rollover occurs depends on logging
     activity.
     */
    private String scheduledFilename;

    /**
     The next time we estimate a rollover should occur. */
    private long nextCheck = System.currentTimeMillis() - 1;

    private Date now = new Date();

    private SimpleDateFormat sdf;

    private RollingCalendar rc = new RollingCalendar();

    int checkPeriod = TOP_OF_TROUBLE;

    // The gmtTimeZone is used only in computeCheckPeriod() method.
    private static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone( "GMT" );


    /**
     The default constructor does nothing. */
    public DailyRollingFileAppender() {
    }

    /**
     Instantiate a <code>DailyRollingFileAppender</code> and open the
     file designated by <code>filename</code>. The opened filename will
     become the ouput destination for this appender.

     */
    public DailyRollingFileAppender( Layout layout, String filename,
                                     String datePattern ) throws IOException {
        super( layout, filename, true );
        this.datePattern = datePattern;
        activateOptions();
    }

    /**
     The <b>DatePattern</b> takes a string in the same format as
     expected by {@link SimpleDateFormat}. This options determines the
     rollover schedule.
     */
    public void setDatePattern( String pattern ) {
        datePattern = pattern;
    }

    /** Returns the value of the <b>DatePattern</b> option. */
    private String getDatePattern() {
        return datePattern;
    }

    public void activateOptions() {
        super.activateOptions();
        if ( datePattern != null && fileName != null ) {
            now.setTime( System.currentTimeMillis() );
            sdf = new SimpleDateFormat( datePattern );
            int type = computeCheckPeriod();
            printPeriodicity( type );
            rc.setType( type );
            File file = new File( fileName );
            scheduledFilename = fileName + sdf.format( new Date( file.lastModified() ) );

        } else {
            LogLog.error( "Either File or DatePattern options are not set for appender ["
                          + name + "]." );
        }
    }

    private void printPeriodicity( int type ) {
        switch ( type ) {
            case TOP_OF_MINUTE:
                LogLog.debug( "Appender [" + name + "] to be rolled every minute." );
                break;
            case TOP_OF_HOUR:
                LogLog.debug( "Appender [" + name
                              + "] to be rolled on top of every hour." );
                break;
            case HALF_DAY:
                LogLog.debug( "Appender [" + name
                              + "] to be rolled at midday and midnight." );
                break;
            case TOP_OF_DAY:
                LogLog.debug( "Appender [" + name
                              + "] to be rolled at midnight." );
                break;
            case TOP_OF_WEEK:
                LogLog.debug( "Appender [" + name
                              + "] to be rolled at start of week." );
                break;
            case TOP_OF_MONTH:
                LogLog.debug( "Appender [" + name
                              + "] to be rolled at start of every month." );
                break;
            default:
                LogLog.warn( "Unknown periodicity for appender [" + name + "]." );
        }
    }


    // This method computes the roll over period by looping over the
    // periods, starting with the shortest, and stopping when the r0 is
    // different from from r1, where r0 is the epoch formatted according
    // the datePattern (supplied by the user) and r1 is the
    // epoch+nextMillis(i) formatted according to datePattern. All date
    // formatting is done in GMT and not local format because the test
    // logic is based on comparisons relative to 1970-01-01 00:00:00
    // GMT (the epoch).

    private int computeCheckPeriod() {
        RollingCalendar rollingCalendar = new RollingCalendar( GMT_TIME_ZONE, Locale.ENGLISH );
        // set sate to 1970-01-01 00:00:00 GMT
        Date epoch = new Date( 0 );
        if ( datePattern != null ) {
            for ( int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++ ) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( datePattern );
                simpleDateFormat.setTimeZone( GMT_TIME_ZONE ); // do all date formatting in GMT
                String r0 = simpleDateFormat.format( epoch );
                rollingCalendar.setType( i );
                Date next = new Date( rollingCalendar.getNextCheckMillis( epoch ) );
                String r1 = simpleDateFormat.format( next );
                //System.out.println("Type = "+i+", r0 = "+r0+", r1 = "+r1);
                if ( r0 != null && r1 != null && !r0.equals( r1 ) ) {
                    return i;
                }
            }
        }
        return TOP_OF_TROUBLE; // Deliberately head for trouble...
    }

    /**
     Rollover the current file to a new file.
     */
    private void rollOver() {

        /* Compute filename, but only if datePattern is specified */
        if ( getDatePattern() == null ) {
            errorHandler.error( "Missing DatePattern option in rollOver()." );
            return;
        }

        Date now = new Date();

        String datedFilename = fileName + sdf.format( now );
        // It is too early to roll over because we are still within the
        // bounds of the current interval. Rollover will occur once the
        // next interval is reached.
        if ( scheduledFilename.equals( datedFilename ) ) {
            return;
        }

        // close current file, and rename it to datedFilename
        this.closeFile();

        File scheduledFile = new File( scheduledFilename );
        if ( scheduledFile.exists() ) {
            scheduledFile.delete();
        }

        File file = new File( fileName );
        boolean copyWasSuccessful = copyFile( file, scheduledFile );
        if ( copyWasSuccessful ) {
            LogLog.debug( "Copied " + fileName + " -> " + scheduledFilename );
        } else {
            LogLog.error( "Failed to copy [" + fileName + "] to [" + scheduledFilename + "]." );
        }

        try {
            boolean appendToFile = !copyWasSuccessful;
            // This will also close the file. This is OK since multiple
            // close operations are safe.
            this.setFile( fileName, appendToFile, this.bufferedIO, this.bufferSize );
        } catch ( IOException e ) {
            errorHandler.error( "setFile(" + fileName + ", false) call failed." );
        }
        scheduledFilename = datedFilename;
    }

    private static boolean copyFile( File from, File to ) {
        try {
            InputStream in = new BufferedInputStream(new FileInputStream( from )) ;
            OutputStream out = new BufferedOutputStream(new FileOutputStream( to )) ;
            int b;
            while ( ( b = in.read() ) != -1 ) {
                out.write( b );
            }
            out.flush();
            out.close();
            return true;
        } catch ( FileNotFoundException e ) {
            return false;
        } catch ( IOException e ) {
            return false;
        }
    }

    /**
     * This method differentiates DailyRollingFileAppender from its
     * super class.
     *
     * <p>Before actually logging, this method will check whether it is
     * time to do a rollover. If it is, it will schedule the next
     * rollover time and then rollover.
     * */
    protected void subAppend( LoggingEvent event ) {
        long n = System.currentTimeMillis();
        if ( n >= nextCheck ) {
            now.setTime( n );
            nextCheck = rc.getNextCheckMillis( now );
            rollOver();
        }
        super.subAppend( event );
    }
}

/**
 *  RollingCalendar is a helper class to DailyRollingFileAppender.
 *  Given a periodicity type and the current time, it computes the
 *  start of the next interval.
 * */
class RollingCalendar extends GregorianCalendar {

    private int type = DailyRollingFileAppender.TOP_OF_TROUBLE;

    RollingCalendar() {
        super();
    }

    RollingCalendar( TimeZone tz, Locale locale ) {
        super( tz, locale );
    }

    void setType( int type ) {
        this.type = type;
    }

    public long getNextCheckMillis( Date now ) {
        return getNextCheckDate( now ).getTime();
    }

    private Date getNextCheckDate( Date now ) {
        this.setTime( now );

        switch ( type ) {
            case DailyRollingFileAppender.TOP_OF_MINUTE:
                this.set( Calendar.SECOND, 0 );
                this.set( Calendar.MILLISECOND, 0 );
                this.add( Calendar.MINUTE, 1 );
                break;
            case DailyRollingFileAppender.TOP_OF_HOUR:
                this.set( Calendar.MINUTE, 0 );
                this.set( Calendar.SECOND, 0 );
                this.set( Calendar.MILLISECOND, 0 );
                this.add( Calendar.HOUR_OF_DAY, 1 );
                break;
            case DailyRollingFileAppender.HALF_DAY:
                this.set( Calendar.MINUTE, 0 );
                this.set( Calendar.SECOND, 0 );
                this.set( Calendar.MILLISECOND, 0 );
                int hour = get( Calendar.HOUR_OF_DAY );
                if ( hour < 12 ) {
                    this.set( Calendar.HOUR_OF_DAY, 12 );
                } else {
                    this.set( Calendar.HOUR_OF_DAY, 0 );
                    this.add( Calendar.DAY_OF_MONTH, 1 );
                }
                break;
            case DailyRollingFileAppender.TOP_OF_DAY:
                this.set( Calendar.HOUR_OF_DAY, 0 );
                this.set( Calendar.MINUTE, 0 );
                this.set( Calendar.SECOND, 0 );
                this.set( Calendar.MILLISECOND, 0 );
                this.add( Calendar.DATE, 1 );
                break;
            case DailyRollingFileAppender.TOP_OF_WEEK:
                this.set( Calendar.DAY_OF_WEEK, getFirstDayOfWeek() );
                this.set( Calendar.HOUR_OF_DAY, 0 );
                this.set( Calendar.SECOND, 0 );
                this.set( Calendar.MILLISECOND, 0 );
                this.add( Calendar.WEEK_OF_YEAR, 1 );
                break;
            case DailyRollingFileAppender.TOP_OF_MONTH:
                this.set( Calendar.DATE, 1 );
                this.set( Calendar.HOUR_OF_DAY, 0 );
                this.set( Calendar.SECOND, 0 );
                this.set( Calendar.MILLISECOND, 0 );
                this.add( Calendar.MONTH, 1 );
                break;
            default:
                throw new IllegalStateException( "Unknown periodicity type." );
        }
        return getTime();
    }
}
