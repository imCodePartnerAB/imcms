package imcode.util.net;

import junit.framework.TestCase;

import javax.activation.DataSource;
import java.io.*;
import java.util.Properties;

public class TestSMTP extends TestCase {

    private String fromAddress;
    private String toAddress;
    private static final String TEST_MAIL = "There should be an empty line below\n" + "\n" +
                                            "There should be a single dot below\n" + ".\n";
    private SMTP smtp;
    private String TEST_SUBJECT = "JUnit-testing SMTP.java";

    public void setUp() throws Exception {
        super.setUp();
        Properties smtpProperties = new Properties();
        smtpProperties.load( new FileInputStream( System.getProperty( "test.smtp.properties", "build.properties" ) ) );
        String smtpServer = smtpProperties.getProperty( "smtp-server" );
        fromAddress = smtpProperties.getProperty( "servermaster-email" );
        toAddress = smtpProperties.getProperty( "servermaster-email" );
        smtp = new SMTP( smtpServer, 25 );
    }

    public void testSendMailWait() throws IOException {
        smtp.sendMailWait( fromAddress, toAddress, TEST_SUBJECT, TEST_MAIL );
    }

    public void testSendMail() throws IOException {
        SMTP.Mail mail = new SMTP.Mail( fromAddress, new String[]{toAddress}, TEST_SUBJECT, TEST_MAIL );
        smtp.sendMail( mail );
    }

    public void testSendMailBcc() throws IOException {
        SMTP.Mail mail = new SMTP.Mail( fromAddress, new String[0], "BCC " + TEST_SUBJECT, TEST_MAIL );
        mail.setBccAddresses( new String[]{toAddress} );
        smtp.sendMail( mail );
    }

    public void testAttachment() throws IOException {
        SMTP.Mail mail = new SMTP.Mail( fromAddress, new String[]{toAddress}, "Attachment "+TEST_SUBJECT,TEST_MAIL);
        DataSource dataSource = new DataSource() {
                        public String getContentType() {
                            return "text/plain" ;
                        }

                        public InputStream getInputStream() throws IOException {
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            Writer writer = new OutputStreamWriter( os ) ;
                            writer.write( "Testing attachment!" );
                            writer.flush();
                            return new ByteArrayInputStream( os.toByteArray() ) ;
                        }

                        public String getName() {
                            return "Test" ;
                        }

                        public OutputStream getOutputStream() {
                            return null ;
                        }
                    };
        DataSource[] dataSources = {
                    dataSource
                };
        mail.setAttachments(dataSources);
        smtp.sendMail( mail );
    }
}
