import java.io.*;
import java.net.*;
import java.util.*;

/**
		class SMTP - Manages a connection to a SMTP-server, and provides methods for sending mail.
		@author Kreiger
		@version 0.6
*/
public class SMTP implements Runnable {
	
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

		@exception IOException Thrown when an I/O-error occurs, or the connection times out.
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
		if (finished==false) {
			throw new IOException("Connection busy.");
		}
		if (sock == null) {
			throw new IOException("Connection closed.");
		}
		sendLine("RSET");
		String resp = readResponse();
		if (status(resp)!=250) {
			error = true;
			throw new ProtocolException("RSET response is: "+resp);
		}
	}		
	
	/**
		Give the address of the sender to the server.
	
		Must be done first when sending a mail.
		@param address The address of the sender.
		@exception ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
		@exception IOException Thrown when an I/O-error occurs, the connection times out, or if the server is busy with the mailsending thread.
	*/
	public void giveSender (String address) throws ProtocolException, IOException {
		if (finished==false) {
			throw new IOException("Connection busy.");
		}
		if (sock == null) {
			throw new IOException("Connection closed.");
		}
		address.trim();
		String mail = "MAIL FROM:<"+address+">";
		sendLine(mail);
		String resp = readResponse();
		if (status(resp)!=250) {
			error = true;
			throw new ProtocolException("MAIL FROM response is: "+resp);
		}
	}

	/**
		Give the address of the recipient to the server.
	
		Must be done second when sending a mail.
		Call multiple times to specify multiple recipients.
		
		@param address The address of the recipient.
		@exception ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
		@exception IOException Thrown when an I/O-error occurs, the connection times out, or if the server is busy with the mailsending thread.
	*/
	public void giveRecipient (String address) throws ProtocolException, IOException {
		if (finished==false) {
			throw new IOException("Connection busy.");
		}
		if (sock == null) {
			throw new IOException("Connection closed.");
		}
		address.trim();
		String mail = "RCPT TO:<"+address+">";
		sendLine(mail);
		String resp = readResponse();
		if (status(resp)!=250) {
			error = true;
			throw new ProtocolException("RCPT TO response is: "+resp);
		}
	}
	
	/**
		Give the rest of the mail to the server.
		
		Must be done last when sending a mail.
		
		@param msg The message to send.
		@exception ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
		@exception IOException Thrown when an I/O-error occurs, the connection times out, or if the server is busy with the mailsending thread.
	*/
	public void giveMail (String msg) throws ProtocolException, IOException {
		if (finished==false) {
			throw new IOException("Connection busy.");
		}
		if (sock == null) {
			throw new IOException("Connection closed.");
		}
		sendLine("DATA");
		String resp = readResponse();
		if (status(resp)!=354) {
			throw new ProtocolException("DATA response is: "+resp);
		}
		StringBuffer sb = new StringBuffer();
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

		@exception IOException Thrown when an I/O-error occurs, the connection times out, or if the server is busy with the mailsending thread.
	*/
	public void close () throws IOException {
		if (finished==false) {
			throw new IOException("Connection busy.");
    	}
    	sendLine("QUIT");
		readResponse();
		sock.close();
		sock = null ;
		error = false ;
	}

	/**
		Composes and sends a mail to the SMTP server, and returns when finished.

		<BR>Example: <BR><CODE>sendMailWait (	"bill.gates@microsoft.com",
															"linus.torvalds@linux.org,steve.jobs@apple.com",
															"Microsoft sucks!",
															"I really dig you guys! You are my idols!" );</CODE>
		
		@param	from		The address sent from.
		@param	to			A comma-delimited string of addresses to send to.
		@param	subject		The message subject.
		@param 	msg 		String containing the message.

		@exception ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
		@exception IOException Thrown when an I/O-error occurs, the connection times out, or if the server is busy with the mailsending thread.
	*/
	public void sendMailWait (String from, String to, String subject, String msg) throws ProtocolException, IOException {
		if (finished==false) {
			throw new IOException("Connection busy.");
		}
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
		StringTokenizer st = new StringTokenizer(to,",");
		while (st.hasMoreTokens()) {
			giveRecipient(st.nextToken());
		}
		String temp = "From: "+from+"\r\nTo: "+to+"\r\n";
		if (subject!=null) {
			temp+="Subject: "+subject+"\r\n";
		}
		temp+=msg;
		giveMail(temp);
	}
	
	/**
		Adds a mail to the list of mails to be sent. Creates a thread that composes and sends mails to the SMTP server. If an error occurs the sending is interrupted, and may be resumed again with resumeSending().

		<BR>Example: <BR><CODE>sendMail (	"bill.gates@microsoft.com",
															"linus.torvalds@linux.org,steve.jobs@apple.com",
															"Microsoft sucks!",
															"I really dig you guys! You are my idols!" );</CODE>
		
		@deprecated Do not use! Has serious bugs! (Is useless anyway...)

		@param	from		The address sent from.
		@param	to			A comma-delimited string of addresses to send to.
		@param	subject	The message subject.
		@param 	msg 		String containing the message.
	*/	
	public void sendMail (String from, String to, String subject, String msg) {
		if (mailList == null) {
			mailList = new LinkedList();
		}
		mailList.addLast(new Mail(from,to,subject,msg));
		if (mailThread == null) {
			mailThread = new Thread (this);
			mailThread.start() ;
		}
		finished = false;
	}
	
	protected class Mail {
		Mail (String from,String to,String subject,String msg) {
			_from = from;
			_to = to;
			_subject = subject;
			_msg = msg;
		}
		String _from,_to,_subject,_msg;
	}
	
	protected Thread mailThread;
	private boolean mailStop;
	private boolean mailInterrupted;
	protected LinkedList mailList;
	
	/**
		Composes and sends a mail. Used by the mailsending thread.
	*/
	synchronized public void run () {
		String address, mail, resp, temp, tmp;
		StringTokenizer st;
		StringBuffer sb;
		while (true) {
			if (mailInterrupted||mailStop) {
				if (mailStop) {
					mailList.clear();
					mailStop = false;
				}
				mailThread = null;
				finished = true;
				return ;
			}
			try {
				if (mailList.size()>0) {
					Mail m = (Mail)mailList.getFirst();
					//Reset server
					if ( sock == null ) {
						connect () ;
					}
					try {
						sendLine("RSET");
					} catch ( IOException ex ) {
						connect () ;
						sendLine("RSET");
					}
					resp = readResponse();
					if (status(resp)!=250) {
						error = true;
						throw new ProtocolException("RSET response is: "+resp);
					} else {
						error = false ;
					}
					//Give sender
					address = m._from;
					address.trim();
					mail = "MAIL FROM:<"+address+">";
					sendLine(mail);
					resp = readResponse();
					if (status(resp)!=250) {
						error = true;
						throw new ProtocolException("MAIL FROM response is: "+resp);
					}
					//Give recipients
					st = new StringTokenizer(m._to,",");
					while (st.hasMoreTokens()) {
						address = st.nextToken();
						address.trim();
						mail = "RCPT TO:<"+address+">";
						sendLine(mail);
						resp = readResponse();
						if (status(resp)!=250) {
							error = true;
							throw new ProtocolException("RCPT TO response is: "+resp);
						}
					}
					temp = "From: "+m._from+"\r\nTo: "+m._to+"\r\n";
					if (m._subject!=null) {
						temp+="Subject: "+m._subject+"\r\n";
					}
					temp+=m._msg;
					sendLine("DATA");
					resp = readResponse();
					if (status(resp)!=354) {
						throw new ProtocolException("DATA response is: "+resp);
					}
					sb = new StringBuffer();
					st = new StringTokenizer(temp,"\r\n");
					while (st.hasMoreTokens()) {
						tmp = st.nextToken();
						tmp.trim();
						if (tmp != null && tmp.length() > 0 && tmp.charAt(0) == '.')
							sb.append("."+tmp+"\r\n");
						else
							sb.append(tmp+"\r\n");
					}
					sendLine(sb.toString()+".\r\n");
					resp = readResponse();
					if (status(resp)!=250 && status(resp)!=251) {
						throw new ProtocolException("DATA-body response is: "+resp);
					}
//					System.out.println("Removing mail: "+m._subject+"\r\nResponse was: "+resp) ;
					mailList.removeFirst();
				} else {
					mailThread = null;
					finished = true ;
					return ;
				}
			}
			catch (ProtocolException e) {
				error = true;
				except = e;
			}
			catch (IOException e) {
				error = true;
				except = e;
			}
		}
	}

	private boolean finished = true;
	private boolean error;
	private Exception except;
	
	/**
		Resumes sending of mails. Has no effect if the sending isn't interrupted. Only used with sendMail().
	*/
	public void resumeSending () {
		if (mailThread == null && mailList.size()>0) {
			mailThread = new Thread (this);
			mailThread.start() ;
			finished = false ;
		}
		mailInterrupted = false;
	}
	
	/**
		Removes the next mail to be sent from the list of mails. May only be done when the sending is interrupted, otherwise it has no effect. Only used with sendMail().
	*/	
	public void removeNextMail () {
		if (mailThread!=null && mailList!=null && mailInterrupted && mailList.size()>0) {
			mailList.removeFirst();
		}
	}
	
	/**
		Get remaining number of mails to be sent, including the one currently being sent. Only used with sendMail().
	*/	
	public int getNumMails () {
		if (mailList==null) {
			return 0;
		}
		return mailList.size();
	}
	
	/**
		Interrupts the sending of mails. Has no effect if the sending is aborted or already interrupted. Only used with sendMail().
		Finishes the current mail, and then returns.
	*/
	public void interruptSending () {
		if (mailThread!=null && !finished) {
			mailInterrupted = true;
			while (!finished) {
				Thread.yield() ;
			}
		}
	}

	/**
		Aborts the sending of mails. Has no effect if the sending already is aborted. Only used with sendMail().
		Finishes the current mail, and then returns.
	*/
	public void abortSending () {
		if (mailThread!=null && !finished && getNumMails()>0) {
			mailStop = true;
			while (!finished) {
				Thread.yield() ;
			}
		}
	}
	
	/**
		Get the latest exception. Only used with sendMail().

		@exception ProtocolException Is thrown whenever an errormessage is received from the server, i.e. an SMTP-protocol error.
		@exception IOException Thrown when an I/O-error occurs, or the connection times out.
	
		@return The latest exception.
	*/	
	public Exception getException () {
		return except;
	}		
	
	/**
		Check if the communication with the server has finished. Only used with sendMail().
		@return True if communication has ended.
	*/
	public boolean hasFinished () {
		return finished;
	}

	/**
		Check if an exception occured. Clears the exception flag. Only used with sendMail().
		@return True if an exception occured.
	*/
	public boolean hasException () {
		boolean temp = error;
		error = false;		
		return temp;
	}

}