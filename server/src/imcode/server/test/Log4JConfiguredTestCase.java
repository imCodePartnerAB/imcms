package imcode.server.test;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.ConsoleAppender;

public class Log4JConfiguredTestCase extends TestCase {

    static {
        initLog4J();
    }

    private static void initLog4J() {
//            String tmpDir = System.getProperty( "java.io.tmpdir" );
//            File tmpFile = new File( tmpDir, "log4joutput.log" );
            BasicConfigurator.configure( new ConsoleAppender( new SimpleLayout()) );
    }
}
