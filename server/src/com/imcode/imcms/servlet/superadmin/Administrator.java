package com.imcode.imcms.servlet.superadmin;

/*
 *
 * @(#)Administrator.java
 *
 *
 * Copyright (c)
 *
 */

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.server.*;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import org.apache.log4j.*;

/**
 * Parent servlet for administration.
 * <p/>
 * Html template in use:
 * AdminListDocs.html
 * Error.html
 * <p/>
 * <p/>
 * stored procedures in use:
 * - GetLangPrefixFromId
 *
 * @version 1.1 27 Oct 2000
 */
public class Administrator extends HttpServlet {

    private static final String TEMPLATE_ERROR = "Error.html";

    private static Category log = Logger.getInstance( Administrator.class.getName() );

    protected boolean checkParameters(Properties aPropObj) {
        // Ok, lets check that the user has typed anything in all the fields
        Enumeration enumValues = aPropObj.elements();
        Enumeration enumKeys = aPropObj.keys();
        while ( ( enumValues.hasMoreElements() && enumKeys.hasMoreElements() ) ) {
            enumKeys.nextElement();
            Object oValue = ( enumValues.nextElement() );
            String theVal = oValue.toString();
            if ( theVal.equals( "" ) )
                return false;
        }
        return true;
    } // checkparameters

    /**
     CheckAdminRights, returns true if the user is an admin.
     False if the user isn't an administrator
     */
    public static boolean checkAdminRights(HttpServletRequest req) {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        return imcref.checkAdminRights( user );
    }

    /**
     GetAdminTemplateFolder. Takes the userobject as argument to detect the language
     from the user and and returns the base path to the internal folder, hangs on the
     language prefix and an "/admin/" string afterwards...
     */
    File getAdminTemplateFolder( IMCServiceInterface imcref, imcode.server.user.UserDomainObject user ) {

        // Since our templates are located into the admin folder, we'll have to hang on admin
        File templateLib = imcref.getTemplatePath();

        // Lets get the users language id. Use the langid to get the lang prefix from db.
        String langPrefix = user.getLanguageIso639_2();
        templateLib = new File( templateLib, langPrefix + "/admin" );
        return templateLib;
    }


    /**
     SendHtml. Generates the html page to the browser.
     **/
    String createHtml( HttpServletRequest req,
                       VariableManager vm, String htmlFile ) throws IOException {

        // Lets get the path to the admin templates folder
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        File templateLib = this.getAdminTemplateFolder( imcref, user );

        // Lets add the server host
        vm.addProperty( "SERVLET_URL", "" );
        vm.addProperty( "SERVLET_URL2", "" );
        HtmlGenerator htmlObj = new HtmlGenerator( templateLib, htmlFile );
        String html = htmlObj.createHtmlString( vm );
        return html;
    }

    /**
     SendHtml. Generates the html page to the browser.
     */
    protected void sendHtml(HttpServletRequest req, HttpServletResponse res,
                          VariableManager vm, String htmlFile ) throws IOException {

        String str = this.createHtml( req, vm, htmlFile );

        // Lets send settings to a browser
        PrintWriter out = res.getWriter();
        res.setContentType("Text/html");
        out.println(str);
    }

    /**
     Log function. Logs the message to the log file and console
     */

    public void log( String msg ) {
        log.debug( "Administrator: " + msg );
    }

    /**
     * send error message
     *
     * @param user
     * @param errorCode         is the code to loock upp in ErrMsg.ini file
     */
    protected void sendErrorMessage(IMCServiceInterface imcref, String eMailServerMaster,
                          UserDomainObject user, String errorHeader,
                          int errorCode, HttpServletResponse response) throws IOException {

        ErrorMessageGenerator errorMessage = new ErrorMessageGenerator( imcref, eMailServerMaster,
                user, errorHeader, TEMPLATE_ERROR, errorCode );

        errorMessage.sendHtml( response );
    }


}
