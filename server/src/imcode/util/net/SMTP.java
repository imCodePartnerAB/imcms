package imcode.util.net;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * class SMTP - Manages a connection to a SMTP-server, and provides methods for sending mail.
 *
 * @author Kreiger
 * @version $Revision$
 */
public class SMTP {

    protected PrintStream out;
    protected BufferedReader in;
    protected Socket sock;
    String host;
    int port;
    int timeout;

    /**
     * Connects to an SMTP-server
     *
     * @throws IllegalArgumentException Thrown when given a timeout of zero or less.
     * @throws UnknownHostException     Thrown when the server can't be found.
     * @throws IOException              Thrown when an I/O-error occurs, or the connection times out.
     * @param	host		The address of the server.
     * @param	port		The port of the server, usually 25.
     * @param	timeout The time in milliseconds to wait for a response from the server before the connection times out. Depends on the connection and the traffic, but should be at least a few seconds.
     */
    public SMTP( String host, int port, int timeout ) throws IOException, ProtocolException, IllegalArgumentException {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        connect();
    }

    /**
     * Shuts down the connection to the server. Should always be done when finished.
     *
     * @throws IOException Thrown when an I/O-error occurs, or the connection times out.
     */
    public void close() throws IOException {
        sendLine( "QUIT" );
        readResponse();
        sock.close();
        sock = null;
    }

    public void sendMail(Mail mail)
            throws IOException {
        if ( sock == null ) {
            connect();
        }
        try {
            resetServer();
        } catch ( IOException ex ) {
            connect();
            resetServer();
        }
        sendFromAddress( mail.getFromAddress() );
        sendRecipients( mail.getToAddresses() );
        sendRecipients( mail.getCcAddresses() );
        sendRecipients( mail.getBccAddresses() );

        ArrayList headers = new ArrayList() ;
        headers.add( "From: " + mail.getFromAddress() );
        if ( null != mail.getToAddresses() ) {
            headers.add("To: " + StringUtils.join( mail.getToAddresses(), ", " ));
        }
        if ( null != mail.getCcAddresses() )  {
            headers.add("Cc: " + StringUtils.join( mail.getCcAddresses(), ", " )) ;
        }
        headers.add( "Subject: " + ( mail.getSubject() == null ? "" : mail.getSubject().trim().replaceAll( "\\s", " " ) ) );

        String[] headersArray = (String[])headers.toArray(new String[headers.size()]);
        sendMail( headersArray, mail.getBody() );
    }

    /**
     * Composes and sends a mail to the SMTP server, and returns when finished.
     *
     * @param	from		The address sent from.
     * @param	to		The comma- or space-separated string of addresses to send to.
     * @param	subject		The message subject.
     * @param	msg		String containing the message.
     *
     * @deprecated Use {@link #sendMail(imcode.util.net.SMTP.Mail)} instead.
     */
    public void sendMailWait( String from, String to, String subject, String msg ) throws ProtocolException, IOException {
        sendMailWait( from, to.split( "\\s,"), subject, msg );
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
     *
     * @deprecated Use {@link #sendMail(imcode.util.net.SMTP.Mail)} instead.
     */
    public void sendMailWait( String from, String[] to, String subject, String body ) throws ProtocolException, IOException {
        Mail mail = new Mail(from, to, subject, body) ;
        sendMail( mail );
    }

    private void connect() throws IOException, ProtocolException, IllegalArgumentException {
        sock = new Socket( host, port );
        if ( timeout <= 0 ) {
            throw new IllegalArgumentException( "Illegal timeout set." );
        }
        sock.setSoTimeout( timeout );
        out = new PrintStream( sock.getOutputStream() );
        in = new BufferedReader( new InputStreamReader( sock.getInputStream(), "LATIN1" ) );
        if ( readStatus() != 220 ) {
            throw new ProtocolException( "No welcome from server." );
        }
        greetServer();
    }

    protected void finalize() throws IOException {
        close();
    }

    /**
     * Introduces you to the server.
     *
     * @throws ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
     * @throws IOException       Thrown when an I/O-error occurs, or the connection times out.
     */
    private void greetServer() throws UnknownHostException, ProtocolException, IOException {
        InetAddress localHost = InetAddress.getLocalHost();
        String greet = "HELO " + localHost.getHostAddress();
        sendLine( greet );
        String resp = readResponse();
        if ( status( resp ) != 250 ) {
            throw new ProtocolException( "HELO response is: " + resp );
        }
    }

    /**
     * Reads the response from the server.
     *
     * @return A string containing the response from the server.
     * @throws IOException Thrown when an I/O-error occurs, or if the connection times out.
     */
    private String readResponse() throws IOException {
        String tmp;
        String temp = "";
        while ( true ) {
            tmp = in.readLine();
            if ( tmp == null ) {
                throw new IOException( "Connection timed out." );
            }
            temp += tmp + "\n";
            if ( tmp.charAt( 3 ) == ' ' ) {
                break;
            }
            Thread.yield();
        }
        //Remove comments to print all responses to System.out.
        //System.out.println(temp);
        return temp;
    }

    /**
     * Reads the response from the server and returns the three-digit result code.
     *
     * @return The three digit result code
     */
    private int readStatus() throws IOException {
        String temp = readResponse();
        if ( temp.length() <= 3 ) {
            return 0;
        }
        return status( temp );
    }

    /**
     * Resets the state of the server. This aborts the current mail, and prepares for a new one.
     */
    private void resetServer() throws ProtocolException, IOException {
        if ( sock == null ) {
            throw new IOException( "Connection closed." );
        }
        sendLine( "RSET" );
        String resp = readResponse();
        if ( status( resp ) != 250 ) {
            throw new ProtocolException( "RSET response is: " + resp );
        }
    }

    private void sendBody( String body ) throws IOException {
        BufferedReader reader = new BufferedReader( new StringReader( body ) );
        for ( String line; null != ( line = reader.readLine() ); ) {
            if ( line.length() > 0 && line.charAt( 0 ) == '.' ) {
                line = '.' + line;
            }
            sendLine( line );
        }
    }

    /**
     * Give the address of the sender to the server.
     * <p/>
     * Must be done first when sending a mail.
     *
     * @param address The address of the sender.
     * @throws ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
     * @throws IOException       Thrown when an I/O-error occurs, or if the connection times out.
     */
    private void sendFromAddress( String address ) throws ProtocolException, IOException {
        if ( sock == null ) {
            throw new IOException( "Connection closed." );
        }
        address.trim();
        String mail = "MAIL FROM:<" + address + ">";
        sendLine( mail );
        String resp = readResponse();
        if ( status( resp ) != 250 ) {
            throw new ProtocolException( "MAIL FROM response is: " + resp );
        }
    }

    private void sendHeaders( String[] headers ) throws IOException {
        for ( int i = 0; i < headers.length; ++i ) {
            String header = headers[i];
            sendLine( header );
        }
    }

    /**
     * Sends a line of data to the server.
     *
     * @param line The line to send.
     */
    private void sendLine( String line ) throws IOException {
        //Remove comments to print all lines sent to the server to System.out.
        //System.out.println(line);
        out.print( line + "\r\n" );
        if ( out.checkError() ) {
            throw new IOException( "Connection closed." );
        }
    }

    /**
     * Give the rest of the mail to the server.
     * <p/>
     * Must be done last when sending a mail.
     *
     * @param msg The message to send.
     * @throws ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
     * @throws IOException       Thrown when an I/O-error occurs, or the connection times out.
     */
    private void sendMail( String[] headers, String msg ) throws ProtocolException, IOException {
        if ( sock == null ) {
            throw new IOException( "Connection closed." );
        }
        sendLine( "DATA" );
        String resp = readResponse();
        if ( status( resp ) != 354 ) {
            throw new ProtocolException( "DATA response is: " + resp );
        }
        sendHeaders( headers );
        sendLine( "" );
        sendBody( msg );
        sendLine( "." );
        resp = readResponse();
        if ( status( resp ) != 250 && status( resp ) != 251 ) {
            throw new ProtocolException( "DATA-body response is: " + resp );
        }
    }

    /**
     * Give the address of the recipient to the server.
     * <p/>
     * Must be done second when sending a mail.
     * Call multiple times to specify multiple recipients.
     *
     * @param address The address of the recipient.
     * @throws ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
     * @throws IOException       Thrown when an I/O-error occurs, or the connection times out.
     */
    private void sendRecipient( String address ) throws ProtocolException, IOException {
        if ( sock == null ) {
            throw new IOException( "Connection closed." );
        }
        address.trim();
        String mail = "RCPT TO:<" + address + ">";
        sendLine( mail );
        String resp = readResponse();
        if ( status( resp ) != 250 ) {
            throw new ProtocolException( "RCPT TO response is: " + resp );
        }
    }

    private void sendRecipients( String[] recipients ) throws IOException {
        for ( int i = 0; null != recipients && i < recipients.length; ++i ) {
            sendRecipient( recipients[i] );
        }
    }

    /**
     * Reads the first three digits of a string and returns them in an int.
     *
     * @param str The string.
     * @return The three digit result code
     */
    private int status( String str ) {
        try {
            return Integer.parseInt( str.substring( 0, 3 ) );
        } catch ( NumberFormatException e ) {
            return 0;
        }
    }

    public static class Mail {

        private String fromAddress ;
        private String[] toAddresses ;
        private String[] ccAddresses ;
        private String[] bccAddresses ;
        private String subject ;
        private String body ;

        public Mail(String fromAddress, String[] toAddresses, String subject, String body) {
            this.fromAddress = fromAddress;
            this.toAddresses = toAddresses;
            this.subject = subject;
            this.body = body;
        }

        public String[] getBccAddresses() {
            return bccAddresses;
        }

        public void setBccAddresses( String[] bccAddresses ) {
            this.bccAddresses = bccAddresses;
        }

        public String getBody() {
            return body;
        }

        public void setBody( String body ) {
            this.body = body;
        }

        public String[] getCcAddresses() {
            return ccAddresses;
        }

        public void setCcAddresses( String[] ccAddresses ) {
            this.ccAddresses = ccAddresses;
        }

        public String getFromAddress() {
            return fromAddress;
        }

        public void setFromAddress( String fromAddress ) {
            this.fromAddress = fromAddress;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject( String subject ) {
            this.subject = subject;
        }

        public String[] getToAddresses() {
            return toAddresses;
        }

        public void setToAddresses( String[] toAddresses ) {
            this.toAddresses = toAddresses;
        }
    }
}
