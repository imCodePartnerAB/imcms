package imcode.util.log;

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

	public TestDailyRollingFileAppender(String name) {
		super(name);
	}

	private static DailyRollingFileAppender createAppender(File filePath) throws IOException {
		String fileName = filePath.getPath();
		Layout layout = new SimpleLayout();
		return new DailyRollingFileAppender(layout, fileName, DATE_PATTERN);
	}

	private static File getTempFile() {
		String tmpDir = "tmp";
		return new File(tmpDir, TestDailyRollingFileAppender.class.getName() + ".test");
	}

	public void setUp() {
		getTempFile().delete();
	}

	public void testLogFileCreated() throws IOException {
		createAppender(getTempFile());
		assertTrue(getTempFile().exists());
	}

	public void testLineLogged() throws IOException {
		DailyRollingFileAppender appender = createAppender(getTempFile());
		appender.doAppend(getLoggingEvent());
		appender.close();
		assertTrue(getTempFile().length() > 0);
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
