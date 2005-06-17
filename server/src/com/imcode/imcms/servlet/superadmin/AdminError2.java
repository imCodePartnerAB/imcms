package com.imcode.imcms.servlet.superadmin;

import imcode.util.SettingsAccessor;
import imcode.util.VariableManager;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminError2 extends Administrator {

    private String myErrorMessage;

    public AdminError2( HttpServletRequest req, HttpServletResponse res, String header, int errorCode )
            throws IOException {

        VariableManager vm = new VariableManager();

        // Lets get the errormessage from the error file
        UserDomainObject user = Utility.getLoggedOnUser( req );

        // Lets get the error code
        SettingsAccessor setObj = new SettingsAccessor( "adminerrmsg.ini", user, "admin" );
        setObj.setDelimiter( "=" );
        setObj.loadSettings();
        myErrorMessage = setObj.getSetting( "" + errorCode );
        if ( myErrorMessage == null ) {
            myErrorMessage = "Missing Errorcode";
        }

        vm.addProperty( "ERROR_HEADER", header );
        vm.addProperty( "ERROR_MESSAGE", myErrorMessage );
        vm.addProperty( "ERROR_CODE", "" + errorCode );

        // Lets send a html string to the browser
        super.sendHtml( req, res, vm, "Admin_Error2.htm" );
    }

    /**
     * Returns the errormessage for this object
     */

    public String getErrorMsg() {
        return myErrorMessage;
    }

} // End of class
