package imcode.util.log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.helpers.LogLog;

public class DailyRollingFileAppender extends org.apache.log4j.DailyRollingFileAppender {

    private String scheduledFilename;

    SimpleDateFormat sdf;

    public void activateOptions() {
        super.activateOptions();
        if ( getDatePattern() != null && fileName != null ) {
            sdf = new SimpleDateFormat( getDatePattern() );
            File file = new File( fileName );
            scheduledFilename = fileName + sdf.format( new Date( file.lastModified() ) );

        } else {
            LogLog.error( "Either File or DatePattern options are not set for appender ["
                          + name + "]." );
        }
    }

    /**
     Rollover the current file to a new file.
     */
    void rollOver() throws IOException {

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
            LogLog.debug( fileName + " -> " + scheduledFilename );
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
            InputStream in = new BufferedInputStream( new FileInputStream( from ) );
            OutputStream out = new BufferedOutputStream( new FileOutputStream( to ) );
            int b;
            while ( ( b = in.read() ) != -1 ) {
                out.write( b );
            }
            return true;
        } catch ( FileNotFoundException e ) {
            return false;
        } catch ( IOException e ) {
            return false;
        }
    }

}
