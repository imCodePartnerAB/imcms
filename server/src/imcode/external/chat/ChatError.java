package imcode.external.chat;

import imcode.external.diverse.HtmlGenerator;
import imcode.external.diverse.SettingsAccessor;
import imcode.external.diverse.VariableManager;
import imcode.external.chat.ChatBase;
import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class ChatError extends ChatBase {

    private static final String ERROR_FILE = "Chat_Error.htm";
    private String errorMessage;


    public ChatError( HttpServletRequest req, HttpServletResponse res, String header, int errorCode )
            throws IOException {

        VariableManager vm = new VariableManager();

        // Lets get the errormessage from the error file
        String myErrorMessage = this.getErrorMessage( req, errorCode );

        vm.addProperty( "ERROR_HEADER", header );
        vm.addProperty( "ERROR_MESSAGE", myErrorMessage );
        //String fileName = "Chat_Error.htm" ;

        // Lets send a html string to the browser
        //super.sendHtml(req, res, vm, fileName) ;
        sendErrorHtml( req, res, vm, ERROR_FILE );
        return;

    }


    /**
     Returns the errormessage for this object
     */

    public String getErrorMsg() {
        return errorMessage;
    }

    /**
     Retrieves the errormessage corresponding to the errorcode. Reads the
     information from a file in the template folder called errmsg.ini
     */

    private String getErrorMessage( HttpServletRequest req, int errCode ) {
        try {
            // Lets get the path to the template library
            File folder = super.getExternalTemplateRootFolder( req );

            // Lets get the error code

            SettingsAccessor setObj = new SettingsAccessor( new File( folder, "errmsg.ini" ) );
            setObj.setDelimiter( "=" );
            setObj.loadSettings();
            errorMessage = setObj.getSetting( "" + errCode );
            if ( errorMessage == null ) {
                errorMessage = "Missing Errorcode " + errCode;
            }

        } catch ( Exception e ) {
            log( "An error occured while reading the errmsg.ini file" );
        }
        return errorMessage;
    }

    public void log( String msg ) {
        super.log( "imcode.external.chat.ChatError: " + msg );
        // System.out.println("imcode.external.chat.ChatError: " + msg) ;
    }

    /*
    For special messages, if we want to pass a special htmlfile
    */
    public ChatError( HttpServletRequest req, HttpServletResponse res, String header, int errorCode, String fileName )
            throws IOException {

        VariableManager vm = new VariableManager();

        // Lets get the errormessage from the error file
        errorMessage = this.getErrorMessage( req, errorCode );
        vm.addProperty( "ERROR_CODE", "" + errorCode );
        vm.addProperty( "ERROR_HEADER", header );
        vm.addProperty( "ERROR_MESSAGE", errorMessage );

        // Lets send a html string to the browser
        //super.sendHtml(req, res, vm, fileName) ;
        sendErrorHtml( req, res, vm, fileName );
        return;

    }

    private void sendErrorHtml( HttpServletRequest req, HttpServletResponse res,
                                  VariableManager vm, String htmlFile ) throws IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        
        // Lets get the TemplateFolder  and the foldername used for this certain metaid
        String lang_prefix = imcref.getDefaultLanguageAsIso639_2();
        if (this.getUserObj(req,res) != null){
            lang_prefix = this.getUserObj(req,res).getLangPrefix();
        }
        File templateLib = this.getExternalTemplateFolder( req, lang_prefix);

        // Lets get the path to the imagefolder.
        String imagePath = this.getExternalImageFolder( req, res );

        vm.addProperty( "IMAGE_URL", imagePath );
        vm.addProperty( "SERVLET_URL", "" );

        HtmlGenerator htmlObj = new HtmlGenerator( templateLib, htmlFile );
        String html = htmlObj.createHtmlString( vm, req );

        htmlObj.sendToBrowser( req, res, html );
    }

} // End of class


