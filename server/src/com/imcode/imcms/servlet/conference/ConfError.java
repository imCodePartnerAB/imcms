package com.imcode.imcms.servlet.conference;

import imcode.external.diverse.HtmlGenerator;
import imcode.external.diverse.SettingsAccessor;
import imcode.external.diverse.VariableManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import com.imcode.imcms.servlet.conference.Conference;

public class ConfError extends Conference {

    private static final String ERROR_FILE = "Conf_Error.htm";
    private String myErrorHeader;
    private String myErrorMessage;

    /**
     * Constructor which is used to read the error strings in the translation file. This
     * one should not be used to generate errormessages
     */
    public ConfError() {
        myErrorHeader = "";
        myErrorMessage = "";
    }

    public ConfError( HttpServletRequest req, HttpServletResponse res, String header, int errorCode )
            throws IOException {

        myErrorHeader = header;
        VariableManager vm = new VariableManager();

        // Lets get the errormessage from the error file
        String myErrorMessage = this.getErrorMessage( req, errorCode );

        vm.addProperty( "ERROR_HEADER", header );
        vm.addProperty( "ERROR_MESSAGE", myErrorMessage );
        //String fileName = "Conf_Error.htm" ;

        // Lets send a html string to the browser
        //super.sendHtml(req, res, vm, fileName) ;
        sendErrorHtml( req, res, vm, ERROR_FILE );
        return;

    }

    public ConfError( HttpServletRequest req, HttpServletResponse res, String header, String msg, int errorCode )
            throws IOException {

        VariableManager vm = new VariableManager();

        // Lets get the errormessage from the error file
        String aMessage = this.getErrorMessage( req, errorCode );
        aMessage += " " + msg;

        vm.addProperty( "ERROR_HEADER", header );
        vm.addProperty( "ERROR_MESSAGE", aMessage );
        //String fileName = "Conf_Error.htm" ;

        // Lets send a html string to the browser
        //super.sendHtml(req, res, vm, fileName) ;
        sendErrorHtml( req, res, vm, ERROR_FILE );
        return;

    }

    /**
     * ConfError, takes a message instead of an int
     */
    public ConfError( HttpServletRequest req, HttpServletResponse res, String header, String msg )
            throws IOException {

        VariableManager vm = new VariableManager();

        vm.addProperty( "ERROR_HEADER", header );
        vm.addProperty( "ERROR_MESSAGE", msg );
        //String fileName = "Conf_Error.htm" ;

        // Lets send a html string to the browser
        //super.sendHtml(req, res, vm, fileName) ;
        sendErrorHtml( req, res, vm, ERROR_FILE );
        return;

    }

    /**
     * Returns the error header och message for this object
     */

    public String getErrorString() {
        return myErrorHeader + " " + myErrorMessage;
    }

    /**
     * Returns the errormessage for this object
     */

    public String getErrorMsg() {
        return myErrorMessage;
    }

    /**
     * Retrieves the errormessage corresponding to the errorcode. Reads the
     * information from a file in the template folder called errmsg.ini
     */

    public String getErrorMessage( HttpServletRequest req, int errCode ) {
        try {
            // Lets get the path to the template library
            File folder = super.getExternalTemplateRootFolder( req );
            log( "ExternalFolder was: " + folder );

            // Lets get the error code

            SettingsAccessor setObj = new SettingsAccessor( new File( folder, "errmsg.ini" ) );
            setObj.setDelimiter( "=" );
            setObj.loadSettings();
            myErrorMessage = setObj.getSetting( "" + errCode );
            if ( myErrorMessage == null ) {
                myErrorMessage = "Missing Errorcode " + errCode;
            }

        } catch ( Exception e ) {
            log( "An error occured while reading the errmsg.ini file" );
        }
        return myErrorMessage;
    }

    public void log( String msg ) {
        System.out.println( "ConfError: " + msg );
    }

    /*
	    For special messages, if we want to pass a special htmlfile
    */
    public ConfError( HttpServletRequest req, HttpServletResponse res, String header, int errorCode, String fileName )
            throws IOException {

        myErrorHeader = header;
        VariableManager vm = new VariableManager();

        // Lets get the errormessage from the error file
        myErrorMessage = this.getErrorMessage( req, errorCode );
        vm.addProperty( "ERROR_CODE", "" + errorCode );
        vm.addProperty( "ERROR_HEADER", header );
        vm.addProperty( "ERROR_MESSAGE", myErrorMessage );

        // Lets send a html string to the browser
        //super.sendHtml(req, res, vm, fileName) ;
        sendErrorHtml( req, res, vm, fileName );
        return;

    }

    private void sendErrorHtml( HttpServletRequest req, HttpServletResponse res,
                                VariableManager vm, String htmlFile ) throws IOException {

        // Lets get the TemplateFolder  and the foldername used for this certain metaid
        File templateLib = this.getExternalTemplateFolder( req );

        // Lets get the path to the imagefolder.
        String imagePath = this.getExternalImageFolder( req );

        vm.addProperty( "IMAGE_URL", imagePath );
        vm.addProperty( "SERVLET_URL", "" );

        HtmlGenerator htmlObj = new HtmlGenerator( templateLib, htmlFile );
        String html = htmlObj.createHtmlString( vm );
        log( html );
        log( htmlFile );

        htmlObj.sendToBrowser( res, html );
    }

} // End of class


