/*
 *
 * @(#)ErrorMessageGenerator.java
 *
 *
 * 2000-10-12
 *
 * Copyright (c)
 *
 */

import java.util.Vector;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import imcode.external.diverse.SettingsAccessor;
import imcode.util.IMCServiceRMI;
import imcode.util.Utility;
import imcode.server.IMCServiceInterface ;

/**
 * Generats language errormessage from a templatefile.
 * The message can be hard coded, but should be fetched by code from file ErrMsg.ini.
 *
 * Error file name ErrMsg.ini
 *
 * Tags used in html template:
 * #ERROR_HEADER#
 * #ERROR_MESSAGE#
 * #EMAIL_SERVER_MASTER#
 *
 * @version 1.2 19 Oct 2000
 * @author Jerker Drottenmyr
 */

public class ErrorMessageGenerator {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    /** File name for errorCodes */
    private static final String ERROR_CODE_FILE = "ErrMsg.ini";

    private IMCServiceInterface imcref ;
    private String emailServerMaster;
    private String languagePrefix;
    private String errorHeader;
    private String errorMessage;
    private String htmlErrorTemplate;

    /**
     * Creats an error message with pased heade and message.
     *
     * @param imcref
     * @param emailServerMaster
     * @param languagePrefix
     * @param server - rmi server name
     * @param errorHeader
     * @param errorMessage
     * @param htmlErrorTemplate documnet to pars
     */
    public ErrorMessageGenerator( IMCServiceInterface imcref, String emailServerMaster,
				  String languagePrefix, String errorHeader,
				  String errorMessage, String htmlErrorTemplate ) {

	this.imcref = imcref;
	this.emailServerMaster = emailServerMaster;
	this.languagePrefix = languagePrefix;
	this.errorHeader = errorHeader;
	this.errorMessage = errorMessage;
	this.htmlErrorTemplate = htmlErrorTemplate;
    }

    /**
     * Creats an error message with pased heade. The message is fetched from ErrMsg.ini.
     *
     * @param imcref
     * @param emailServerMaster
     * @param languagePrefix
     * @param server - rmi server name
     * @param errorHeader
     * @param errorMessage
     * @param errorCode - errorCode to look upp in ErrMsg.ini
     * @param htmlErrorTemplate documnet to pars
     */
    public ErrorMessageGenerator( IMCServiceInterface imcref, String emailServerMaster,
				  String languagePrefix, String errorHeader,
				  String htmlErrorTemplate, int errorCode) {

	this( imcref, emailServerMaster, languagePrefix, errorHeader, "", htmlErrorTemplate );
	this.errorMessage = getErrorMessage( errorCode );
    }

    /**
     * Creats an error message with pased heade and message contented with fetched errormessage from ErrMsg.ini.
     *
     * @param imcref
     * @param emailServerMaster
     * @param languagePrefix
     * @param server - rmi server name
     * @param errorHeader
     * @param errorMessage
     * @param errorCode - errorCode to look upp in ErrMsg.ini
     * @param htmlErrorTemplate documnet to pars
     */
    public ErrorMessageGenerator( IMCServiceInterface imcref, String emailServerMaster,
				  String languagePrefix, String errorHeader,
				  String errorMessage, String htmlErrorTemplate,
				  int errorCode) {

	this( imcref, emailServerMaster, languagePrefix, errorHeader, errorMessage, htmlErrorTemplate );
	this.errorMessage = errorMessage + getErrorMessage( errorCode );
    }

    /**
     * sends back html error page to client
     */
    public void sendHtml( HttpServletResponse response ) throws IOException {

	response.setContentType("text/html");
	ServletOutputStream out = response.getOutputStream();

	out.print( createErrorString() );
    }

    /**
     * creats error page as string object
     */
    public String createErrorString() throws IOException {

	Vector tagParsList = new Vector() ;

	tagParsList.add( "#ERROR_HEADER#" );
	tagParsList.add( errorHeader );
	tagParsList.add( "#ERROR_MESSAGE#" );
	tagParsList.add( errorMessage );
	tagParsList.add( "#EMAIL_SERVER_MASTER#" );
	tagParsList.add( emailServerMaster );

	return imcref.parseDoc( tagParsList, htmlErrorTemplate, languagePrefix );
    }

    /**
     * Extracts error from error file ErrMsg.ini
     */
    private String getErrorMessage( int errCode ) {
	String errorMessage = "";
	try {
	    // Lets get the path to the template library
	    File errorTemplatePath = imcref.getTemplateHome();

	    // Lets get the error code
	    SettingsAccessor setObj = new SettingsAccessor( new java.io.File(errorTemplatePath, languagePrefix + "/admin/" + ErrorMessageGenerator.ERROR_CODE_FILE) ) ;
	    setObj.setDelimiter("=") ;
	    setObj.loadSettings() ;
	    errorMessage = setObj.getSetting("" + errCode) ;
	    if ( errorMessage == null )  {
		errorMessage = "Missing Errorcode";
	    }

	} catch(Exception e) {
	    errorMessage = "An error occured while reading the errorCode file";
	}
	return errorMessage ;
    }
}
