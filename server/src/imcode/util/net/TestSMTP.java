package imcode.util.net;

import junit.framework.TestCase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestSMTP extends TestCase {

    private String fromAddress;
    private String toAddress;
    private static final String TEST_MAIL = "There should be an empty line below\n" + "\n" +
                                            "There should be a single dot below\n" + ".\n";
    private SMTP smtp;
    private String TEST_SUBJECT = "JUnit-testing SMTP.java";

    public void setUp() throws IOException {
        Properties smtpProperties = new Properties();
        smtpProperties.load( new FileInputStream( System.getProperty( "test.smtp.properties", "build.properties" ) ) );
        String smtpServer = smtpProperties.getProperty( "smtp-server" );
        fromAddress = smtpProperties.getProperty( "servermaster-email" );
        toAddress = smtpProperties.getProperty( "servermaster-email" );
        smtp = new SMTP( smtpServer, 25, 10000 );
    }

    public void testSendMailWait() throws IOException {
        smtp.sendMailWait( fromAddress, toAddress, TEST_SUBJECT, TEST_MAIL );
    }

    public void testSendMail() throws IOException {
        SMTP.Mail mail = new SMTP.Mail( fromAddress, new String[]{toAddress}, TEST_SUBJECT, TEST_MAIL );
        smtp.sendMail( mail );
    }

    public void testSendMailBcc() throws IOException {
        SMTP.Mail mail = new SMTP.Mail( fromAddress, null, "BCC " + TEST_SUBJECT, TEST_MAIL );
        mail.setBccAddresses( new String[]{toAddress} );
        smtp.sendMail( mail );
    }
}
