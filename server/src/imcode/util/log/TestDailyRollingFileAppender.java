package imcode.util.log;

import junit.framework.TestCase;
import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LoggerRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2003-sep-12
 * Time: 11:24:48
 * To change this template use Options | File Templates.
 */
public class TestDailyRollingFileAppender extends TestCase {

    private static final String DATE_PATTERN = ".yyyy-MM-dd HH:mm";

    public TestDailyRollingFileAppender(String name) {
        super(name) ;
    }

    public void setUp() {
        getTempFile().delete() ;
    }

    public void testLogFileCreated() throws IOException {
        createAppender( getTempFile() );
        assertTrue(getTempFile().exists()) ;
    }

    public void testLineLogged() throws IOException {
        DailyRollingFileAppender appender = createAppender( getTempFile() ) ;
        appender.doAppend(getLoggingEvent() );
        assertTrue(getTempFile().length() > 0) ;
    }

    public void testRollOver() throws IOException, InterruptedException {
        File tempFile = getTempFile();
        DailyRollingFileAppender appender = createAppender( tempFile ) ;
        appender.doAppend(getLoggingEvent()) ;
        waitForMinuteRollOver() ;
        appender.doAppend(getLoggingEvent()) ;

        File rolledOverFile = getRolledOverFile( tempFile );

        assertTrue("Checking that log file '"+tempFile.getPath()+"' exists", tempFile.exists());
        assertTrue("Checking that log file '"+tempFile.getPath()+"' is non-empty", tempFile.length() > 0) ;
        assertTrue("Checking that rolled over file '"+rolledOverFile.getPath()+"' exists",rolledOverFile.exists()) ;
        assertTrue("Checking that rolled over file '"+rolledOverFile.getPath()+"' is non-empty",rolledOverFile.length() > 0) ;
    }

    private static File getRolledOverFile( File tempFile ) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN) ;
        Calendar gregorianCalendar = new GregorianCalendar() ;
        gregorianCalendar.add(Calendar.MINUTE, -1) ;
        String rolledOverFileName = tempFile.getPath()+dateFormat.format(gregorianCalendar.getTime()) ;
        File rolledOverFile = new File(rolledOverFileName) ;
        return rolledOverFile;
    }

    private static void waitForMinuteRollOver() throws InterruptedException {
        Calendar gregorianCalendar = new GregorianCalendar() ;
        int secondsOfMinute = gregorianCalendar.get(Calendar.SECOND) ;
        Thread.sleep((60-secondsOfMinute)*1000);
    }

    private LoggingEvent getLoggingEvent() {
        return new LoggingEvent(MockLogger.class.getName(),new MockLogger("mock"),Priority.DEBUG,"Test",null);
    }

    private static DailyRollingFileAppender createAppender( File filePath ) throws IOException {
        String fileName = filePath.getPath() ;
        Layout layout = new SimpleLayout() ;
        DailyRollingFileAppender appender = new DailyRollingFileAppender(layout, fileName, DATE_PATTERN ) ;
        return appender;
    }

    private static File getTempFile() {
        String tmpDir = System.getProperty("java.io.tmpdir") ;
        File filePath = new File(tmpDir, TestDailyRollingFileAppender.class.getName()+".test") ;
        return filePath;
    }

    private class MockLogger extends Logger {
        public MockLogger(String name) {
            super(name) ;
        }
    }

}
