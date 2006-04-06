package imcode.util.net;

import imcode.util.Utility;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import javax.activation.DataSource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.util.Arrays;

/**
 * class SMTP - Provides methods for sending mail.
 *
 * @author Kreiger
 * @version $Revision$
 */
public class SMTP {

    private String host;
    private int port;

    /**
     * Connects to an SMTP-server
     *
     * @throws IllegalArgumentException Thrown when given a timeout of zero or less.
     * @param	host		The address of the server.
     * @param	port		The port of the server, usually 25.
     */
    public SMTP( String host, int port ) {
        this.host = host;
        this.port = port;
    }

    public void sendMail( Mail mail )
            throws IOException {
        MultiPartEmail email = mail.getMail();

        try {
            email.setHostName( host );
            email.setSmtpPort( port );
            email.send();
        } catch ( EmailException e ) {
            if (Utility.throwableContainsMessageContaining( e, "no object DCH")) {
                throw new UnhandledException( "\"no object DCH\" Likely cause: the activation jar-file cannot see the mail jar-file. Different ClassLoaders?", e ) ;
            } else {
                throw new UnhandledException( e ) ;
            }
        }
    }

    public static class Mail {

        MultiPartEmail mail = new MultiPartEmail();

        public Mail( String fromAddress ) {
            try {
                mail.setFrom( fromAddress );
            } catch ( EmailException e ) {
                throw new UnhandledException( e );
            }
        }

        public Mail( String fromAddress, String[] toAddresses, String subject, String body ) {
            this( fromAddress );
            try {
                setToAddresses( toAddresses );
                mail.setSubject( subject );
                mail.setMsg( body );
            } catch ( EmailException e ) {
                throw new UnhandledException( e );
            }
        }

        public void setBccAddresses( String[] bccAddresses ) {
            try {
                mail.setBcc( CollectionUtils.collect( Arrays.asList( bccAddresses ), new StringToInternetAddressTransformer() ) );
            } catch ( EmailException e ) {
                throw new UnhandledException( e );
            }
        }

        public void setBody( String body ) {
            try {
                mail.setMsg( body );
            } catch ( EmailException e ) {
                throw new UnhandledException( e );
            }
        }

        public void setCcAddresses( String[] ccAddresses ) {
            try {
                mail.setCc( CollectionUtils.collect( Arrays.asList( ccAddresses ), new StringToInternetAddressTransformer() ) );
            } catch ( EmailException e ) {
                throw new UnhandledException( e );
            }
        }

        public void setSubject( String subject ) {
            mail.setSubject( subject );
        }

        public void setToAddresses( String[] toAddresses ) {
            try {
                mail.setTo( CollectionUtils.collect( Arrays.asList( toAddresses ), new StringToInternetAddressTransformer() ) );
            } catch ( EmailException e ) {
                throw new UnhandledException( e );
            }
        }

        public void setAttachments( DataSource[] attachments ) {
            try {
                for ( int i = 0; i < attachments.length; i++ ) {
                    DataSource attachment = attachments[i];
                    mail.attach( attachment, attachment.getName(), "" );
                }
            } catch ( EmailException e ) {
                throw new UnhandledException( e );
            }
        }

        private MultiPartEmail getMail() {
            return mail;
        }

        private static class StringToInternetAddressTransformer implements Transformer {

            public Object transform( Object input ) {
                try {
                    return new InternetAddress( (String)input, false );
                } catch ( AddressException e ) {
                    throw new UnhandledException( e );
                }
            }
        }
    }
}
