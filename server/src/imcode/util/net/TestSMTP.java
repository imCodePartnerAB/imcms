package imcode.util.net ;

import java.net.ProtocolException ;
import java.io.IOException ;

import junit.framework.TestCase ;
import imcode.util.net.SMTP;

public class TestSMTP extends TestCase {

    private final static String SMTP_SERVER = null ;
    private final static String FROM_ADDRESS = null ;
    private final static String TO_ADDRESS = null ;

    public TestSMTP(String name) {
	super(name) ;
    }

    public void testSendMailWait() {
	try {
	    SMTP smtp = new SMTP(SMTP_SERVER,25,10000) ;
	    smtp.sendMailWait(FROM_ADDRESS, TO_ADDRESS, "JUnit-testing SMTP.java", "Test") ;
	} catch (ProtocolException pe) {
	    fail() ;
	} catch (IOException ioe) {
	    fail() ;
	}
    }
}
