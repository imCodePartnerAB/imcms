package imcode.util.net;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.mail.MultiPartEmail;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Arrays;

/**
 * class SMTP - Manages a connection to a SMTP-server, and provides methods for sending mail.
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
        MultiPartEmail email = mail.getMail() ;

        try {
            email.setHostName( host );
            email.setSmtpPort( port );
            email.send();
        } catch ( MessagingException e ) {
            throw new UnhandledException( e );
        }
    }

    /**
     * Composes and sends a mail to the SMTP server, and returns when finished.
     *
     * @param	from		The address sent from.
     * @param	to		The comma- or space-separated string of addresses to send to.
     * @param	subject		The message subject.
     * @param	msg		String containing the message.
     * @deprecated Use {@link #sendMail(imcode.util.net.SMTP.Mail)} instead.
     */
    public void sendMailWait( String from, String to, String subject, String msg ) throws IOException {
        sendMailWait( from, to.split( "\\s," ), subject, msg );
    }

    /**
     * Composes and sends a mail to the SMTP server, and returns when finished.
     * <p/>
     * <BR>Example: <BR><CODE>sendMailWait (	"bill.gates@microsoft.com",
     * "linus.torvalds@linux.org,steve.jobs@apple.com",
     * "Microsoft sucks!",
     * "I really dig you guys! You are my idols!" );</CODE>
     *
     * @throws ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
     * @throws IOException       Thrown when an I/O-error occurs, or if the connection times out.
     * @param	from		The address sent from.
     * @param	to		The addresses to send to.
     * @param	subject		The message subject.
     * @param	body		String containing the message.
     * @deprecated Use {@link #sendMail(imcode.util.net.SMTP.Mail)} instead.
     */
    public void sendMailWait( String from, String[] to, String subject, String body ) throws IOException {
        Mail mail = new Mail( from, to, subject, body );
        sendMail( mail );
    }

    public static class Mail {

        MultiPartEmail mail = new MultiPartEmail();

        public Mail( String fromAddress ) {
            try {
                mail.setFrom( fromAddress );
            } catch ( MessagingException e ) {
                throw new UnhandledException( e );
            }
        }

        public Mail( String fromAddress, String[] toAddresses, String subject, String body ) {
            this( fromAddress );
            try {
                setToAddresses( toAddresses );
                mail.setSubject( subject );
                mail.setMsg( body );
            } catch ( MessagingException e ) {
                throw new UnhandledException( e );
            }
        }

        public void setBccAddresses( String[] bccAddresses ) {
            mail.setBcc( CollectionUtils.collect( Arrays.asList( bccAddresses ), new StringToInternetAddressTransformer() ) );
        }

        public void setBody( String body ) {
            try {
                mail.setMsg( body );
            } catch ( MessagingException e ) {
                throw new UnhandledException( e );
            }
        }

        public void setCcAddresses( String[] ccAddresses ) {
            mail.setCc( CollectionUtils.collect( Arrays.asList( ccAddresses ), new StringToInternetAddressTransformer() ) );
        }

        public void setSubject( String subject ) {
            mail.setSubject( subject );
        }

        public void setToAddresses( String[] toAddresses ) {
            mail.setTo( CollectionUtils.collect(Arrays.asList(toAddresses), new StringToInternetAddressTransformer() ) );
        }

        public void setAttachments( DataSource[] attachments ) {
            try {
                for ( int i = 0; i < attachments.length; i++ ) {
                    DataSource attachment = attachments[i];
                    mail.attach( attachment, attachment.getName(), "" );
                }
            } catch ( MessagingException e ) {
                throw new UnhandledException( e );
            }
        }

        private MultiPartEmail getMail() {
            return mail;
        }

        private static class StringToInternetAddressTransformer implements Transformer {

            public Object transform( Object input ) {
                try {
                    return new InternetAddress( (String)input );
                } catch ( AddressException e ) {
                    throw new UnhandledException( e );
                }
            }
        }
    }
}
