package com.imcode.imcms.servlet.conference;

import imcode.external.diverse.SettingsAccessor;
import imcode.external.diverse.VariableManager;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ConfError extends Conference {

    private static final String ERROR_FILE = "conf_error.htm";
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

    public ConfError( HttpServletRequest req, HttpServletResponse res, String header, int errorCode,
                      UserDomainObject user )
            throws IOException {

        myErrorHeader = header;
        VariableManager vm = new VariableManager();

        // Lets get the errormessage from the error file
        String myErrorMessage = this.getErrorMessage( req, errorCode );

        vm.addProperty( "ERROR_HEADER", header );
        vm.addProperty( "ERROR_MESSAGE", myErrorMessage );

        sendErrorHtml( req, res, vm, ERROR_FILE, user );
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
            SettingsAccessor setObj = new SettingsAccessor( "errmsg.ini", Utility.getLoggedOnUser( req ), "102" );
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

    /*
	    For special messages, if we want to pass a special htmlfile
    */
    public ConfError( HttpServletRequest req, HttpServletResponse res, String header, int errorCode, String fileName,
                      UserDomainObject user )
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
        sendErrorHtml( req, res, vm, fileName, user );
    }

    private void sendErrorHtml( HttpServletRequest req, HttpServletResponse res,
                                VariableManager vm, String htmlFile, UserDomainObject user ) throws IOException {

        // Lets get the path to the imagefolder.
        String imagePath = this.getExternalImageFolder( req );

        vm.addProperty( "IMAGE_URL", imagePath );
        vm.addProperty( "SERVLET_URL", "" );

        String html = getTemplate( htmlFile, user, vm.getTagsAndData() );

        // Lets send settings to a browser
        PrintWriter out = res.getWriter();
        Utility.setDefaultHtmlContentType( res );
        out.print( html );
    }

} // End of class


