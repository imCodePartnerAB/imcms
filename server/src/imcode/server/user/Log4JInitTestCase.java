package imcode.server.user;

import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;

import java.io.File;
import java.io.IOException;

abstract class Log4JInitTestCase extends TestCase {

   public Log4JInitTestCase() {
      super( "Log4JInitTestCase" );
      try {
         initLog4J();
      } catch( IOException e ) {
         e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }
   }

   private void initLog4J() throws IOException {
      String tmpDir = System.getProperty("java.io.tmpdir") ;
      File tmpFile = new File(tmpDir, "log4joutput.log") ;
      BasicConfigurator.configure(new FileAppender(new SimpleLayout(), tmpFile.toString()));
   }
}
