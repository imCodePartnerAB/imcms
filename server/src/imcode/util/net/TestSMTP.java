package imcode.util.net;

import junit.framework.TestCase;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Properties;

public class TestSMTP extends TestCase {

    private String smtpServer ;
    private String fromAddress ;
    private String toAddress ;

    public void setUp() throws IOException {
        Properties smtpProperties = new Properties();
        smtpProperties.load( new FileInputStream( System.getProperty( "test.smtp.properties", "build.properties" ) ) );
        smtpServer = smtpProperties.getProperty( "smtp-server" ) ;
        fromAddress = smtpProperties.getProperty( "servermaster-email") ;
        toAddress = smtpProperties.getProperty( "servermaster-email") ;
    }

    public void testSendMailWait() {
        try {
            SMTP smtp = new SMTP( smtpServer, 25, 10000 );
            smtp.sendMailWait( fromAddress, toAddress, "JUnit-testing SMTP.java", "Test" );
        } catch ( ProtocolException pe ) {
            fail();
        } catch ( IOException ioe ) {
            fail();
        }
    }
}
