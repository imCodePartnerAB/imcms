package com.imcode.imcms.servlet.billboard;

import imcode.external.diverse.SettingsAccessor;
import imcode.external.diverse.VariableManager;
import imcode.server.user.UserDomainObject;
import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;
import imcode.util.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class BillBoardError extends BillBoard {

    private static final String ERROR_FILE_2 = "billboard_user_error.htm";

    private String myErrorMessage;

    /**
     * Constructor which is used to read the error strings in the translation file. This
     * one should not be used to generate errormessages
     */
    public BillBoardError() {
        myErrorMessage = "";
    }

    public BillBoardError( HttpServletRequest req, HttpServletResponse res, int errorCode, UserDomainObject user ) throws IOException {

        VariableManager vm = new VariableManager();

        // Lets get the errormessage from the error file
        String myErrorMessage = this.getErrorMessage( req, errorCode );

        vm.addProperty( "ERROR_MESSAGE", myErrorMessage );
        //String fileName = "Conf_Error.htm" ;

        // Lets send a html string to the browser
        //super.sendHtml(req, res, vm, fileName) ;
        sendErrorHtml( req, res, vm, ERROR_FILE_2, user );
        return;

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
            SettingsAccessor setObj = new SettingsAccessor( "errmsg.ini", Utility.getLoggedOnUser( req ), "104" );
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
    public BillBoardError( HttpServletRequest req, HttpServletResponse res, String header, int errorCode,
                           String fileName, UserDomainObject user ) throws IOException {

        VariableManager vm = new VariableManager();

        // Lets get the errormessage from the error file
        myErrorMessage = this.getErrorMessage( req, errorCode );
        vm.addProperty( "ERROR_CODE", "" + errorCode );
        vm.addProperty( "ERROR_HEADER", header );
        vm.addProperty( "ERROR_MESSAGE", myErrorMessage );

        // Lets send a html string to the browser
        //super.sendHtml(req, res, vm, fileName) ;
        sendErrorHtml( req, res, vm, fileName, user );
        return;

    }

    private void sendErrorHtml( HttpServletRequest req, HttpServletResponse res,
                                VariableManager vm, String htmlFile, UserDomainObject user ) throws IOException {

        // Lets get the TemplateFolder  and the foldername used for this certain metaid
        File templateLib = this.getExternalTemplateFolder( req );

        // Lets get the path to the imagefolder.
        String imagePath = this.getExternalImageFolder( req );

        vm.addProperty( "IMAGE_URL", imagePath );
        vm.addProperty( "SERVLET_URL", "" );

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
        String html = imcref.getTemplateFromSubDirectoryOfDirectory( htmlFile, user, vm.getTagsAndData(), "104", "original"  );

        // Lets send settings to a browser
        PrintWriter out = res.getWriter();
        Utility.setDefaultHtmlContentType( res );
        out.println( html );
    }

} // End of class


