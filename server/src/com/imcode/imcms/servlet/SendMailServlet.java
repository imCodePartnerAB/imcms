package com.imcode.imcms.servlet;

/*
 *
 * @(#)SendMailServlet.java
 */

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.SystemData;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.net.SMTP;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * PARAMS in use, in doPost()
 * mailTo (the email adress were to send this.
 * mailFrom (the senders email adress, the replyTo adress)
 * mailSubject (the subject line) it also decides if its a from or at to mail
 * -if there is a subject the mail sends to webbmaster atherwise it sends from the webbmaster
 * mailTemplate (the template used to parse the mailbody)
 * mailText0 (textField that parses in the mailbody template)
 * mailText1,mailText2,mailText3,mailText4,mailText5,mailText6,mailText7,mailText8,mailText9
 * mailDone (the place to sendRedirect when done if null we return)
 * mailError (the page to redirect to when an error occures)
 * <p/>
 * Data in webserver config:
 * smtp_server=smtp.intron.net
 * smtp_port=25
 * smtp_timeout=10000
 */

public class SendMailServlet extends HttpServlet {

    private final static String SUBJECT_TEMPLATE = "mail_subject.html";
    private final static String BODY_TEMPLATE = "mail_body.html";
    private final static String BODY_TEMPLATE_TO_ADMIN = "mail_body_to_admin.html";
    private final static String BODY_TEMPLATE_SHOP = "mail_body_shop.html";

    private final static int MAIL_TEXT_COUNT = 100;

    /**
     * process submit
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        //params to use to check if its a shop-mail or not
        String metaId = req.getParameter( "metaid" );
        String param = req.getParameter( "param" );

        /* server info */

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();


        SystemData sysData = imcref.getSystemData();

        // Lets get the parameters we need
        String webmaster = sysData.getWebMasterAddress();
        ;
        String mailTo = req.getParameter( "mailTo" );
        String mailFrom = req.getParameter( "mailFrom" );
        if ( mailFrom == null ) {
            mailFrom = webmaster;
        } else {
            if ( mailFrom.trim().equals( "" ) ) {
                mailFrom = webmaster;
            }
        }
        String mailSubject = req.getParameter( "mailSubject" );

        //lets get the meta_id where to send when were done
        String mailDone = req.getParameter( "mailDone" );
        //lets get the meta_id where to send if some thing goes wrong
        String mailError = req.getParameter( "mailError" );

        //ok lets get all mailText stuff
        String[] mailTextArr = new String[MAIL_TEXT_COUNT];
        for ( int i = 0; i < MAIL_TEXT_COUNT; i++ ) {
            mailTextArr[i] = req.getParameter( "mailText" + i ) == null ? "" : req.getParameter( "mailText" + i );
        }

        //lets fix the complete URL
        StringBuffer url = req.getRequestURL();
        StringTokenizer token = new StringTokenizer( url.toString(), "/", true );
        StringBuffer mailUrl = new StringBuffer();
        while ( token.hasMoreElements() ) {
            String temp = token.nextToken();
            mailUrl.append( temp );
            if ( temp.equals( "servlet" ) )
                break;
        }
        mailUrl.append( "/GetDoc?meta_id=" + mailTextArr[0] );

        Vector mailTextV = new Vector();

        mailTextV.add( "#mailSubject#" );
        mailTextV.add( mailSubject );
        mailTextV.add( "#mailLink#" );
        mailTextV.add( mailUrl.toString() );
        for ( int i = 0; i < MAIL_TEXT_COUNT; i++ ) {
            //lets setUp the tags and text to parse in the mail body
            mailTextV.add( "#mailText" + i + "#" );
            mailTextV.add( mailTextArr[i] );
        }

        UserDomainObject user = Utility.getLoggedOnUser( req );
        String mailBody;
        //ok lets see if its a shop-mail or not
        if ( param != null ) {//its a shop-mail so lets get the mailTo adress from texts100
            String sql = "Select text from texts where name = 100 and meta_id = ?";
            mailTo = imcref.sqlQueryStr( sql, new String[]{metaId} );
            mailTextV.add( "#mailTo#" );
            mailTextV.add( mailTo );
            mailTextV.add( "#mailFrom#" );
            mailTextV.add( mailFrom );
            mailBody = imcref.getAdminTemplate( BODY_TEMPLATE_SHOP, user, mailTextV );
        } else {
            //ok lets see if we got a subject or not
            if ( mailSubject != null ) {
                //its a mail to the system
                String sql = "Select text from texts \n where name = 100 \n and meta_id = ?";
                mailTo = imcref.sqlQueryStr( sql, new String[]{metaId} );
                mailTextV.add( "#mailTo#" );
                mailTextV.add( mailTo );
                mailTextV.add( "#mailFrom#" );
                mailTextV.add( mailFrom );
                mailBody = imcref.getAdminTemplate( BODY_TEMPLATE_TO_ADMIN, user, mailTextV );
            } else {
                //its a mail from the system
                mailFrom = webmaster;
                mailTextV.add( "#mailTo#" );
                mailTextV.add( mailTo );
                mailTextV.add( "#mailFrom#" );
                mailTextV.add( mailFrom );
                //it also means that we must parse the subject line thrue SUBJECT_TEMPLATE so lets rock
                mailSubject = imcref.getAdminTemplate( SUBJECT_TEMPLATE, user, mailTextV );
                //ok lets parse the mailbody into a string
                mailBody = imcref.getAdminTemplate( BODY_TEMPLATE, user, mailTextV );
            }
        }
        //a simple check that @ symbol exists
        if ( !this.validateEmail( mailFrom ) || !this.validateEmail( mailTo ) ) {
            if ( mailError != null ) {
                res.sendRedirect( "GetDoc?meta_id=" + mailError );
            } else {
                res.sendRedirect( req.getContextPath() + "/servlet/StartDoc" );
            }
            return;
        }


        /* mailserver info */
        String mailserver = Utility.getDomainPref( "smtp_server" );
        String stringMailPort = Utility.getDomainPref( "smtp_port" );
        String stringMailtimeout = Utility.getDomainPref( "smtp_timeout" );

        // Handling of default-values is another area where java can't hold a candle to perl.
        int mailport = 25;
        try {
            mailport = Integer.parseInt( stringMailPort );
        } catch ( NumberFormatException ignored ) {
            // Do nothing, let mailport stay at default.
        }

        int mailtimeout = 10000;
        try {
            mailtimeout = Integer.parseInt( stringMailtimeout );
        } catch ( NumberFormatException ignored ) {
            // Do nothing, let mailtimeout stay at default.
        }
        // send mail
        try {
            SMTP smtp = new SMTP( mailserver, mailport, mailtimeout );
            smtp.sendMailWait( mailFrom, mailTo, mailSubject, mailBody );

        } catch ( ProtocolException ex ) {
            log( "Protocol error while sending mail. " + ex.getMessage() );
            //FIX load some error page
            if ( mailError != null ) {
                res.sendRedirect( "GetDoc?meta_id=" + mailError );
            } else {
                res.sendRedirect( req.getContextPath() + "/servlet/StartDoc" );
            }
            return;
        } catch ( IOException ex ) {
            log( "The mailservlet probably timed out. " + ex.getMessage() );
            //FIX load some error page
            if ( mailError != null ) {
                res.sendRedirect( "GetDoc?meta_id=" + mailError );
            } else {
                res.sendRedirect( req.getContextPath() + "/servlet/StartDoc" );
            }
            return;
        }
        //här sk vi nog ha en koll om vi ska redirecta eller bara returnera
        //men det får vi kolla senare
        if ( mailDone != null )
            res.sendRedirect( "GetDoc?meta_id=" + mailDone );
        return;
    }

    /**
     * Log function. Logs the message to the log file and console
     */
    public void log( String msg ) {
        if ( msg == null ) msg = "";
        super.log( msg );
        //System.out.println("SendMailServlet: " + msg) ;
    }

    /* checks if email address is valid (abc@x)*/
    private boolean validateEmail( String eMail ) {
        int stringLength = eMail.length();
        int indexAt = eMail.indexOf( "@" );

        if ( indexAt > 0 && indexAt < ( stringLength - 1 ) ) {
            return true;
        } else {
            return false;
        }
    }
}
