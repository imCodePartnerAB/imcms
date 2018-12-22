package imcode.util.log;

import imcode.util.io.FileUtility;
import junit.framework.TestCase;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.LoggingEvent;

import java.io.File;
import java.io.IOException;

public class TestDailyRollingFileAppender extends TestCase {

    private static final String DATE_PATTERN = ".yyyy-MM-dd-HH-mm";

    private final File tmpDir = new File("tmp");
    private final File tempFile = new File(tmpDir, TestDailyRollingFileAppender.class.getName() + ".test");

    public TestDailyRollingFileAppender(String name) {
        super(name);
    }

    private static DailyRollingFileAppender createAppender(File filePath) throws IOException {
        String fileName = filePath.getPath();
        Layout layout = new SimpleLayout();
        return new DailyRollingFileAppender(layout, fileName, DATE_PATTERN);
    }

    public void setUp() throws Exception {
        super.setUp();

        if (tempFile.exists()) {
            FileUtility.forceDelete(tempFile);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        FileUtility.forceDelete(tmpDir);
    }

    public void testLogFileCreated() throws IOException {
        final DailyRollingFileAppender appender = createAppender(tempFile);

        try {
            assertTrue(tempFile.exists());
        } finally {
            appender.close();
        }
    }

    public void testLineLogged() throws IOException {
        final DailyRollingFileAppender appender = createAppender(tempFile);
        appender.doAppend(getLoggingEvent());
        appender.close();
        assertTrue(tempFile.length() > 0);
    }

    private LoggingEvent getLoggingEvent() {
        return new LoggingEvent(MockLogger.class.getName(), new MockLogger("mock"), Level.DEBUG, "Test", null);
    }

    private class MockLogger extends Logger {
        private MockLogger(String name) {
            super(name);
        }
    }

}
