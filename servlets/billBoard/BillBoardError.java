
import java.io.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;

public class BillBoardError extends BillBoard {

    private static final String ERROR_FILE = "BillBoard_Error.htm";
    private static final String ERROR_FILE_2 = "BillBoard_User_Error.htm";

    private String myErrorMessage;

    /**
     * Constructor which is used to read the error strings in the translation file. This
     * one should not be used to generate errormessages
     */
    public BillBoardError() {
        myErrorMessage = "";
    }

    public BillBoardError( HttpServletRequest req, HttpServletResponse res, String header, int errorCode ) throws IOException {

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

    public BillBoardError( HttpServletRequest req, HttpServletResponse res, int errorCode ) throws IOException {

        VariableManager vm = new VariableManager();

        // Lets get the errormessage from the error file
        String myErrorMessage = this.getErrorMessage( req, errorCode );

        vm.addProperty( "ERROR_MESSAGE", myErrorMessage );
        //String fileName = "Conf_Error.htm" ;

        // Lets send a html string to the browser
        //super.sendHtml(req, res, vm, fileName) ;
        sendErrorHtml( req, res, vm, ERROR_FILE_2 );
        return;

    }

    public BillBoardError( HttpServletRequest req, HttpServletResponse res, String header, String msg, int errorCode ) throws IOException {

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
    public BillBoardError( HttpServletRequest req, HttpServletResponse res, String header, String msg ) throws IOException {

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
        super.log( "BillBoardError: " + msg );

    }

    /*
    For special messages, if we want to pass a special htmlfile
    */
    public BillBoardError( HttpServletRequest req, HttpServletResponse res, String header, int errorCode, String fileName ) throws IOException {

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
        String html = htmlObj.createHtmlString( vm, req );

        htmlObj.sendToBrowser( req, res, html );
    }

} // End of class


