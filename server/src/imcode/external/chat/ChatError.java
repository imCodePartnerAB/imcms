package imcode.external.chat;

import imcode.external.diverse.SettingsAccessor;
import imcode.external.diverse.VariableManager;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ChatError extends ChatBase {

    private static final String ERROR_FILE = "chat_error.htm";
    private String errorMessage;

    public ChatError( HttpServletRequest req, HttpServletResponse res, String header, int errorCode )
            throws IOException {

        VariableManager vm = new VariableManager();

        String myErrorMessage = this.getErrorMessage( req, errorCode );

        vm.addProperty( "ERROR_HEADER", header );
        vm.addProperty( "ERROR_MESSAGE", myErrorMessage );

        sendErrorHtml( req, res, vm, ERROR_FILE );
    }

    /**
     * Returns the errormessage for this object
     */

    public String getErrorMsg() {
        return errorMessage;
    }

    /**
     * Retrieves the errormessage corresponding to the errorcode. Reads the
     * information from a file in the template folder called errmsg.ini
     */

    private String getErrorMessage( HttpServletRequest req, int errCode ) {
        try {
            SettingsAccessor setObj = new SettingsAccessor( "errmsg.ini", Utility.getLoggedOnUser( req ), "103" );
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
        sendErrorHtml( req, res, vm, fileName );
    }

    private void sendErrorHtml( HttpServletRequest req, HttpServletResponse res,
                                VariableManager vm, String htmlFile ) throws IOException {

        // Lets get the TemplateFolder  and the foldername used for this certain metaid
        UserDomainObject user = this.getUserObj( req );

        // Lets get the path to the imagefolder.
        String imagePath = this.getExternalImageFolder( req );

        vm.addProperty( "IMAGE_URL", imagePath );
        vm.addProperty( "SERVLET_URL", "" );

        List tagsAndData = vm.getTagsAndData();

        String html = getTemplate( htmlFile, user, tagsAndData );

        // Lets send settings to a browser
        Utility.setDefaultHtmlContentType( res );
        res.getWriter().println( html );
    }

} // End of class


