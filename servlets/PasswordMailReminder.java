/*
 *
 * @(#)PasswordMailReminder.java
 *
 *
 * 2000-09-27
 *
 * Copyright (c)
 *
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
import imcode.util.SMTP;

import imcode.server.* ;

/**
 * Proces if user can and has right to recive password by mail.
 * Server master recives a mail that describes what has happend.
 * Returns suitable message.
 *
 * Documents in use:
 *
 * doGet()
 * password_submit.html, response internalDocument for empty input field
 *
 * doPost():
 * password_no_user.html, response internalDocument for user doesn´t exist
 * password_no_email.html, response internalDocument for user hasen´t a valid e-mail
 * password_no_right.html, response internalDocument for user hasnt rights to recive password by mail
 * password_sent.html, response internalDocument for password sent
 * password_submit.html, response internalDocument for empty input field
 *
 * password_no_user.txt, servermaster e-mail body if user doesn´t exist
 * password_no_email.txt, servermaster e-mail body if user hasn´t a valid e-mail
 * password_no_right.txt, servermaster e-mail body if user hasn´ rights to recive password by mail
 * password_sent.txt, servermaster e-mail body if password sent
 * password_user_mail.txt, user e-mail body
 * password_error_input.txt, error string in password_submit
 *
 * Data in webserver config:
 * smtp_server=smtp.intron.net
 * smtp_port=25
 * smtp_timeout=10000
 * servermaster_email=abc@test.com
 * system_email=system@test.com


 * stored procedures in use:
 - PermissionsGetPermission
 *
 * @version 1.1 24 Oct 2000
 * @author Jerker Drottenmyr
 */

public class PasswordMailReminder extends HttpServlet {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    /* filnames for templates */
    private final static String USER_DONT_EXIST = "password_no_user.txt";
    private final static String USER_HAS_NO_EMAIL = "password_no_email.txt";
    private final static String USER_HAS_NOT_RIGHT = "password_no_right.txt";
    private final static String SENT_USER_PASSWORD = "password_sent.txt";
    private final static String USER_MAIL_BODY = "password_user_mail.txt";

    /* filnames for errors */
    private final static String ERROR_STRING = "password_error_input.txt";
    private static final String HTML_EMAIL_ERROR = "Error.html";

    /* returning internalDocument */
    private final static String RETURNING_DOCUMENT_NO_USER_NAME = "password_no_user.html";
    private final static String RETURNING_DOCUMENT_NO_EMAIL = "password_no_email.html";
    private final static String RETURNING_DOCUMENT_NO_RIGHT = "password_no_right.html";
    private final static String RETURNING_DOCUMENT_SENT = "password_sent.html";
    private final static String RETURNING_DOCUMENT_INPUT = "password_submit.html";

    /* */
    private final static int PASSWORD_PERMISSION_ID = 1;

    public void init(ServletConfig config) throws ServletException {

	super.init(config);
    }

    /**
     * showing input internalDocument whit out error
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

	/* server info */

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
	String deafultLanguagePrefix = imcref.getLanguage();

	res.setContentType("text/html");
	ServletOutputStream out = res.getOutputStream();

	Vector parsVector = new Vector();

	parsVector.add( "#errorininput#" );
	parsVector.add( "" );

	String returnString = imcref.parseDoc( parsVector, PasswordMailReminder.RETURNING_DOCUMENT_INPUT, deafultLanguagePrefix );
	out.print( returnString );
    }

    /**
     * proces submit
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

	String emptyString = "";

	/* server info */

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
	String hostName = emptyString;
	try {
	    hostName = InetAddress.getLocalHost().getHostName();
	} catch ( SecurityException e ) {
	    log( "checkConnect doesn't allow the operation" );
	}

	/* mailserver info */
	imcode.server.SystemData sysData = imcref.getSystemData() ;
	String eMailServerMaster = sysData.getServerMasterAddress() ;
	String emailFromServer = sysData.getServerMasterAddress() ;

	String mailFrom = eMailServerMaster;
	String deafultLanguagePrefix = imcref.getLanguage();

	String mailserver = Utility.getDomainPref( "smtp_server" );
	String stringMailPort = Utility.getDomainPref( "smtp_port" );
	String stringMailtimeout = Utility.getDomainPref( "smtp_timeout" );

	// Handling of default-values is another area where java can't hold a candle to perl.
	int mailport = 25 ;
	try {
	    mailport = Integer.parseInt( stringMailPort );
	} catch (NumberFormatException ignored) {
	    // Do nothing, let mailport stay at default.
	}

	int mailtimeout = 10000 ;
	try {
	    mailtimeout = Integer.parseInt( stringMailtimeout );
	} catch (NumberFormatException ignored) {
	    // Do nothing, let mailtimeout stay at default.
	}

	/* user info */
	String postedLoginName;
	String firstName = emptyString;
	String lastName = emptyString;
	String password = emptyString;
	String userEmail = emptyString;

	String returnFileBody = emptyString;
	String serverMasterMailBody = emptyString;
	String returnString = emptyString;
	Vector errorParsVector = new Vector();

	/* condition variabels */
	boolean validLoginName = false;
	boolean sendMailToUser = false;

	postedLoginName = req.getParameter( "login_name" );
	validLoginName = ( !(postedLoginName == null || postedLoginName.length() == 0) );

	if ( validLoginName ) {

	    String sqlQ = "PermissionsGetPermission " + postedLoginName + ", " + PasswordMailReminder.PASSWORD_PERMISSION_ID;
	    String[] queryResult = imcref.sqlProcedure( sqlQ ) ;

	    if ( (queryResult != null) && (queryResult.length > 0) ) {

		firstName = queryResult[1];
		lastName = queryResult[2];
		userEmail = queryResult[3];

		boolean hasUserRight = !( "0".equals(queryResult[4]) );

		if ( hasUserRight ) {

		    if ( (userEmail != null) && (validateEmail(userEmail)) ) {
			sendMailToUser = true;
			password = queryResult[0];

			returnFileBody = PasswordMailReminder.RETURNING_DOCUMENT_SENT;
			serverMasterMailBody = PasswordMailReminder.SENT_USER_PASSWORD;

		    } else {
			returnFileBody = PasswordMailReminder.RETURNING_DOCUMENT_NO_EMAIL;
			serverMasterMailBody = PasswordMailReminder.USER_HAS_NO_EMAIL;
		    }

		} else {
		    returnFileBody = PasswordMailReminder.RETURNING_DOCUMENT_NO_RIGHT;
		    serverMasterMailBody = PasswordMailReminder.USER_HAS_NOT_RIGHT;
		}

	    } else {
		returnFileBody = PasswordMailReminder.RETURNING_DOCUMENT_NO_USER_NAME;
		serverMasterMailBody = PasswordMailReminder.USER_DONT_EXIST;
	    }

	    /* send mail */
	    //			try {
	    SMTP smtp = new SMTP( mailserver, mailport, mailtimeout );

	    if ( sendMailToUser ) {
		Vector parsVector = new Vector();

		parsVector.add( "#firstname#" );
		parsVector.add( firstName );
		parsVector.add( "#lastname#" );
		parsVector.add( lastName );
		parsVector.add( "#password#" );
		parsVector.add( password );

		String userLanguagePrefix = queryResult[5];
		String userMessage = imcref.parseDoc( parsVector,
						      PasswordMailReminder.USER_MAIL_BODY, userLanguagePrefix);

		smtp.sendMailWait( mailFrom, userEmail ,null , userMessage );

	    }

	    Vector parsVector = new Vector();
        String host = req.getServerName() ;

	    parsVector.add( "#username#" );
	    parsVector.add( postedLoginName );
	    parsVector.add( "#email#" );
	    parsVector.add( userEmail );
	    parsVector.add( "#host#" );
	    parsVector.add( host );


	    String serverMasterMessage = imcref.parseDoc( parsVector, serverMasterMailBody,
							  deafultLanguagePrefix);

	    smtp.sendMailWait( emailFromServer, eMailServerMaster, null , serverMasterMessage );

	    returnString = imcref.parseDoc( null, returnFileBody, deafultLanguagePrefix );

	} else {
	    String errorString = imcref.parseDoc( null, PasswordMailReminder.ERROR_STRING,
						  deafultLanguagePrefix );

	    errorParsVector.add( "#errorininput#" );
	    errorParsVector.add( errorString );
	    returnString = imcref.parseDoc( errorParsVector, PasswordMailReminder.RETURNING_DOCUMENT_INPUT,
					    deafultLanguagePrefix );
	}

	res.setContentType("text/html");
	ServletOutputStream out = res.getOutputStream();

	out.print( returnString );

    }


    /**
       Log function. Logs the message to the log file and console
    */
    public void log(String msg) {
	super.log(msg) ;
	System.out.println("PasswordMailReminder: " + msg) ;

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
