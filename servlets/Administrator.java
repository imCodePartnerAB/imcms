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

    boolean checkParameters(Properties aPropObj) {
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
     Returns an user object
     */

    static imcode.server.user.UserDomainObject getUserObj( HttpServletRequest req,
                                                                     HttpServletResponse res ) throws IOException {
        if ( checkSession( req, res ) == true ) {
            // Get the session
            HttpSession session = req.getSession( true );
            // Does the session indicate this user already logged in?
            Object done = session.getAttribute( "logon.isDone" );  // marker object
            imcode.server.user.UserDomainObject user = (imcode.server.user.UserDomainObject)done;
            return user;
        } else {
            return null;
        }
    }


    /**
     Verifies that the user is logged in
     */

    protected static boolean checkSession( HttpServletRequest req, HttpServletResponse res )
            throws IOException {

        // Get the session
        HttpSession session = req.getSession( true );
        // Does the session indicate this user already logged in?
        imcode.server.user.UserDomainObject user = (imcode.server.user.UserDomainObject)session.getAttribute( "logon.isDone" );  // marker object

        if ( user == null ) {
            // No logon.isDone means he hasn't logged in.

            // Lets get the login page
            IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
            // Save the request URL as the true target and redirect to the login page.
            session.setAttribute( "login.target", req.getRequestURL().toString() );
            String startUrl = imcref.getStartUrl();

            res.sendRedirect(startUrl);
            return false;
        }
        return true;
    }

    /**
     CheckAdminRights, returns true if the user is an admin.
     False if the user isn't an administrator
     */

    protected static boolean checkAdminRights( HttpServletRequest req, HttpServletResponse res )
            throws IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        imcode.server.user.UserDomainObject user = getUserObj( req, res );
        if ( user == null ) {
            return false;
        } else {
            return imcref.checkAdminRights( user );
        }
    }

    /**
     GetAdminTemplateFolder. Takes the userobject as argument to detect the language
     from the user and and returns the base path to the internal folder, hangs on the
     language prefix and an "/admin/" string afterwards...
     */
    public File getAdminTemplateFolder( IMCServiceInterface imcref, imcode.server.user.UserDomainObject user ) throws IOException {

        // Since our templates are located into the admin folder, we'll have to hang on admin
        File templateLib = imcref.getTemplateHome();

        // Lets get the users language id. Use the langid to get the lang prefix from db.
        String langPrefix = user.getLangPrefix();
        templateLib = new File( templateLib, langPrefix + "/admin" );
        return templateLib;
    }


    /**
     SendHtml. Generates the html page to the browser.
     **/
    public String createHtml( HttpServletRequest req, HttpServletResponse res,
                             VariableManager vm, String htmlFile) throws IOException {

        // Lets get the path to the admin templates folder
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        imcode.server.user.UserDomainObject user = getUserObj( req, res );
        File templateLib = this.getAdminTemplateFolder( imcref, user );

        // Lets add the server host
        vm.addProperty( "SERVLET_URL", "" );
        vm.addProperty( "SERVLET_URL2", "" );
        HtmlGenerator htmlObj = new HtmlGenerator( templateLib, htmlFile );
        String html = htmlObj.createHtmlString( vm, req );
        return html;
    }

    /**
     SendHtml. Generates the html page to the browser.
     */
    void sendHtml(HttpServletRequest req, HttpServletResponse res,
                          VariableManager vm, String htmlFile ) throws IOException {

        String str = this.createHtml( req, res, vm, htmlFile );
        HtmlGenerator htmlObj = new HtmlGenerator();
        htmlObj.sendToBrowser( req, res, str );
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
     * @param errorCode         is the code to loock upp in ErrMsg.ini file
     */
    void sendErrorMessage(IMCServiceInterface imcref, String eMailServerMaster,
                                    String languagePrefix, String errorHeader,
                                    int errorCode, HttpServletResponse response) throws IOException {

        ErrorMessageGenerator errorMessage = new ErrorMessageGenerator( imcref, eMailServerMaster,
                                                                        languagePrefix, errorHeader, TEMPLATE_ERROR, errorCode );

        errorMessage.sendHtml( response );
    }


} // End class
