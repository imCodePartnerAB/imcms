package imcode.util.net ;

import java.io.*;
import java.net.*;
import java.util.*;

/**
   class SMTP - Manages a connection to a SMTP-server, and provides methods for sending mail.
   @author Kreiger
   @version $Revision$
*/
public class SMTP {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    protected PrintStream out;
    protected BufferedReader in;
    protected Socket sock ;
    String host ;
    int port ;
    int timeout ;

    /**
       Connects to an SMTP-server
       @param	host		The address of the server.
       @param	port		The port of the server, usually 25.
       @param	timeout The time in milliseconds to wait for a response from the server before the connection times out. Depends on the connection and the traffic, but should be at least a few seconds.

       @exception IllegalArgumentException Thrown when given a timeout of zero or less.
       @exception UnknownHostException Thrown when the server can't be found.
       @exception IOException Thrown when an I/O-error occurs, or the connection times out.
    */
    public SMTP (String host, int port, int timeout) throws IOException, ProtocolException, IllegalArgumentException {
	this.host = host ;
	this.port = port ;
	this.timeout = timeout ;
	connect() ;
    }

    public void connect () throws IOException, ProtocolException, IllegalArgumentException {
	sock = new Socket(host,port);
	if (timeout <= 0)
	    throw new IllegalArgumentException("Illegal timeout set.");
	sock.setSoTimeout(timeout);
	out = new PrintStream(sock.getOutputStream());
	in = new BufferedReader(new InputStreamReader(sock.getInputStream(),"LATIN1"));
	if (readStatus()!=220) {
	    throw new ProtocolException("No welcome from server.");
	}
	greetServer();
    }

    /**
       Introduces you to the server.

       @exception ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
       @exception IOException Thrown when an I/O-error occurs, or the connection times out.
    */
    protected void greetServer () throws UnknownHostException, ProtocolException, IOException {
	InetAddress localHost = InetAddress.getLocalHost();
	String greet = "HELO " + localHost.getHostAddress();
	sendLine(greet);
	String resp = readResponse();
	if (status(resp)!=250)
	    throw new ProtocolException("HELO response is: "+resp);
    }

    /**
       Reads the response from the server and returns the three-digit result code.

       @return The three digit result code
    */
    protected int readStatus () throws IOException {
	String temp = readResponse();
	if (temp.length()<=3)
	    return 0;
	return status(temp);
    }

    /**
       Reads the first three digits of a string and returns them in an int.

       @param str The string.
       @return The three digit result code
    */
    protected int status (String str) {
	try {
	    return Integer.parseInt(str.substring(0,3));
	} catch (NumberFormatException e) {
	    return 0;
	}
    }

    /**
       Reads the response from the server.
       @return A string containing the response from the server.

       @exception IOException Thrown when an I/O-error occurs, or if the connection times out.
    */
    protected String readResponse () throws IOException {
	String tmp;
	String temp="";
	while (true) {
	    tmp = in.readLine();
	    if (tmp == null) {
		throw new IOException ("Connection timed out.");
	    }
	    temp += tmp+"\n";
	    if (tmp.charAt(3)==' ') {
		break;
	    }
	    Thread.yield() ;
	}
	//Remove comments to print all responses to System.out.
	//System.out.println(temp);
	return temp;
    }

    /**
       Sends a line of data to the server.

       @param line The line to send.
    */
    protected void sendLine (String line) throws IOException {
	//Remove comments to print all lines sent to the server to System.out.
	//System.out.println(line);
	out.print(line+"\r\n");
	if ( out.checkError() ) {
	    throw new IOException ("Connection closed.") ;
	}
    }

    /**
       Resets the state of the server. This aborts the current mail, and prepares for a new one.
    */
    public void resetServer () throws ProtocolException, IOException {
	if (sock == null) {
	    throw new IOException("Connection closed.");
	}
	sendLine("RSET");
	String resp = readResponse();
	if (status(resp)!=250) {
	    throw new ProtocolException("RSET response is: "+resp);
	}
    }

    /**
       Give the address of the sender to the server.

       Must be done first when sending a mail.
       @param address The address of the sender.
       @exception ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
       @exception IOException Thrown when an I/O-error occurs, or if the connection times out.
    */
    public void giveSender (String address) throws ProtocolException, IOException {
	if (sock == null) {
	    throw new IOException("Connection closed.");
	}
	address.trim();
	String mail = "MAIL FROM:<"+address+">";
	sendLine(mail);
	String resp = readResponse();
	if (status(resp)!=250) {
	    throw new ProtocolException("MAIL FROM response is: "+resp);
	}
    }

    /**
       Give the address of the recipient to the server.

       Must be done second when sending a mail.
       Call multiple times to specify multiple recipients.

       @param address The address of the recipient.
       @exception ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
       @exception IOException Thrown when an I/O-error occurs, or the connection times out.
    */
    public void giveRecipient (String address) throws ProtocolException, IOException {
	if (sock == null) {
	    throw new IOException("Connection closed.");
	}
	address.trim();
	String mail = "RCPT TO:<"+address+">";
	sendLine(mail);
	String resp = readResponse();
	if (status(resp)!=250) {
	    throw new ProtocolException("RCPT TO response is: "+resp);
	}
    }

    /**
       Give the rest of the mail to the server.

       Must be done last when sending a mail.

       @param msg The message to send.
       @exception ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
       @exception IOException Thrown when an I/O-error occurs, or the connection times out.
    */
    public void giveMail (String[] headers, String msg) throws ProtocolException, IOException {
	if (sock == null) {
	    throw new IOException("Connection closed.");
	}
	sendLine("DATA");
	String resp = readResponse();
	if (status(resp)!=354) {
	    throw new ProtocolException("DATA response is: "+resp);
	}
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < headers.length; ++i) {
	    String header = headers[i] ;
	    sb.append(header).append("\r\n") ;
	}
	sb.append("\r\n") ;
	StringTokenizer st = new StringTokenizer(msg,"\r\n");
	while (st.hasMoreTokens()) {
	    String temp = st.nextToken();
	    temp.trim();
	    if (temp != null && temp.length() > 0 && temp.charAt(0) == '.')
		sb.append("."+temp+"\r\n");
	    else
		sb.append(temp+"\r\n");
	}
	sendLine(sb.toString()+".\r\n");

	resp = readResponse();
	if (status(resp)!=250 && status(resp)!=251) {
	    throw new ProtocolException("DATA-body response is: "+resp);
	}
    }

    /**
       Shuts down the connection to the server. Should always be done when finished.

       @exception IOException Thrown when an I/O-error occurs, or the connection times out.
    */
    public void close () throws IOException {
	sendLine("QUIT");
	readResponse();
	sock.close();
	sock = null ;
    }

    /**
       Composes and sends a mail to the SMTP server, and returns when finished.

       @param	from		The address sent from.
       @param	to		The comma- or space-separated string of addresses to send to.
       @param	subject		The message subject.
       @param	msg		String containing the message.
    **/
    public void sendMailWait (String from, String to, String subject, String msg) throws ProtocolException, IOException {
	StringTokenizer st = new StringTokenizer(to,", ");
	String[] toAddresses = new String[st.countTokens()] ;
	for (int i=0; st.hasMoreTokens(); ++i) {
	    toAddresses[i] = st.nextToken() ;
	}
	sendMailWait(from, toAddresses, subject, msg) ;
    }


    /**
       Composes and sends a mail to the SMTP server, and returns when finished.

       <BR>Example: <BR><CODE>sendMailWait (	"bill.gates@microsoft.com",
       "linus.torvalds@linux.org,steve.jobs@apple.com",
       "Microsoft sucks!",
       "I really dig you guys! You are my idols!" );</CODE>

       @param	from		The address sent from.
       @param	to[]		The addresses to send to.
       @param	subject		The message subject.
       @param	msg		String containing the message.

       @exception ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
       @exception IOException Thrown when an I/O-error occurs, or if the connection times out.
    */
    public void sendMailWait (String from, String[] to, String subject, String msg) throws ProtocolException, IOException {
	if (sock == null) {
	    connect () ;
	}
	try {
	    resetServer() ;
	} catch ( IOException ex ) {
	    connect () ;
	    resetServer() ;
	}
	giveSender(from);
	StringBuffer toAddressesString = new StringBuffer() ;
	for (int i = 0; i < to.length; ++i) {
	    if (i != 0) {
		toAddressesString.append(", ") ;
	    }
	    toAddressesString.append(to[i]) ;
	    giveRecipient(to[i]) ;
	}
	String fromHeader = "From: "+from ;
	String toHeader = "To: "+toAddressesString.toString() ;
	String subjectHeader = "Subject: " + (subject == null ? "" : subject) ;

	String[] headers = new String[] {
	    fromHeader,
	    toHeader,
	    subjectHeader,
	} ;
	giveMail(headers,msg);
    }

}
