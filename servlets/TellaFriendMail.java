/*
 *
 * @(#)TellaFriendMail.java
 */


import java.io.IOException;
import java.net.InetAddress;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import imcode.util.IMCServiceRMI;
import imcode.util.Utility;
import java.net.* ;
/**
  * Proces if user can and has right to recive password by mail.
  * Server master recives a mail that describes what has happend.
  * Returns suitable message.
  *
  * Documents in use:
  *
  *
  * Data in webserver config:
  * smtp_server=smtp.intron.net
  * smtp_port=25
  * smtp_timeout=10000
  * servermaster_email=abc@test.com
  * system_email=system@test.com
*/

public class TellaFriendMail extends HttpServlet {

	public void init(ServletConfig config) throws ServletException {

		super.init(config);
	}

	/**
        * Showing input document whit out error
        */

	public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
          this.doPost(req, res) ;
	}

	/**
	*   process submit
	*/
	public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

	      /* server info */
		String host = req.getHeader("Host") ;
		String imcserver = Utility.getDomainPref( "adminserver", host );
                Vector vec = new Vector() ;
                String htmlStr = "" ;
                String lang_prefix = IMCServiceRMI.getLanguage(imcserver) ;

            // Lets get the parameters
      		String mailFrom = (req.getParameter("mailFrom")==null) ? "" : (req.getParameter("mailFrom")) ;
      		String userMailFrom = (req.getParameter("myMail")==null) ? "" : (req.getParameter("myMail")) ;
                String mailTo = (req.getParameter("mailTo")==null) ? "" : (req.getParameter("mailTo")) ;
      		StringBuffer comment = new StringBuffer(1024) ;
                comment.append( (req.getParameter("comment")==null) ? "" : (req.getParameter("comment"))) ;
                comment.append( '\n' + " " + '\n' + "I think this Conference may be of interest to you." + '\n' ) ;
                comment.append( "Check this site out!" + '\n' + " " + '\n' ) ;
                comment.append( "http://www.technet-baltic.net" + '\n' + " " + '\n' ) ;
                comment.append( "Best regards!" + '\n' + " " + '\n' ) ;
                comment.append( userMailFrom ) ;

                String defSubject = (req.getParameter("DefaultSubject")==null) ? "" : (req.getParameter("DefaultSubject")) ;
                String mailDone = (req.getParameter("mailDonePage")==null) ? "" : (req.getParameter("mailDonePage")) ;
                String mailError = (req.getParameter("mailErrorPage")==null) ? "" : (req.getParameter("mailErrorPage")) ;
                String mailError2 = (req.getParameter("mailErrorPage2")==null) ? "" : (req.getParameter("mailErrorPage2")) ;


                if( !this.validateEmail(mailFrom) || !this.validateEmail(mailTo) ) {
                  log("Du måste ange en korrekt mailadress") ;
                  //htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, mailError, lang_prefix) ;
                  //res.setContentType("text/html");
		  //ServletOutputStream out = res.getOutputStream();
		  //out.print( htmlStr );
                  res.sendRedirect(mailError);
                  return ;
                }

		/* mailserver info */
		String mailserver = Utility.getDomainPref( "smtp_server", host );
		String deafultLanguagePrefix = IMCServiceRMI.getLanguage( imcserver );
		String stringMailPort = Utility.getDomainPref( "smtp_port", host );
		String stringMailtimeout = Utility.getDomainPref( "smtp_timeout", host );


               // log("smtp_server: " + mailserver ) ;
               // log("smtp_port: " + mailport ) ;
               /// log("smtp_timeout: " + mailtimeout ) ;


		// send mail
		try {
                  int mailport = Integer.parseInt( stringMailPort );
		  int mailtimeout = Integer.parseInt( stringMailtimeout );

                  // FIXME Will only handle one mail at a time. If a user
                  // tries to send a mail when the smtp is busy, an error will occur.
                  SMTP smtp = new SMTP( mailserver, mailport, mailtimeout ) ;
                  smtp.sendMailWait( userMailFrom, mailTo, defSubject , comment.toString() );

		} catch (ProtocolException ex ) {
			log ("Protocol error while sending mail. " + ex.getMessage()) ;
			//htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, mailError, lang_prefix) ;
                        res.sendRedirect(mailError2);
                        return ;
                } catch (IOException ex ) {
			log ("The mailservlet probably timed out. " + ex.getMessage()) ;
                        //htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, mailError, lang_prefix) ;
                        res.sendRedirect(mailError2);
                        return ;
                } catch (NumberFormatException ex ) {
			log ("No smtp settings could be found on server. " + ex.getMessage()) ;
                        //htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, mailError, lang_prefix) ;
                        res.sendRedirect(mailError2);
                        return ;
                }

              // Lets parse the page to return
                // String mailDonePage = IMCServiceRMI.getTemplateHome(host) ;
                //htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "TellaFriendMailDone.htm", lang_prefix) ;

	//	res.setContentType("text/html");
	//	ServletOutputStream out = res.getOutputStream();
	//	out.print( htmlStr );
                res.sendRedirect(mailDone);
	}


	/**
		Log function. Logs the message to the log file and console
	*/
	public void log(String msg) {
		super.log(msg) ;
		System.out.println("TellaFriendMail: " + msg) ;

	}

	/* checks if email address is valid (abc@x)*/
	private boolean validateEmail( String eMail ) {
		int stringLength = eMail.length();
		int indexAt = eMail.indexOf( "@" );

		if ( indexAt > 0 && indexAt < (stringLength - 1) ) {
			return true;
		} else {
			return false;
		}
	}

}