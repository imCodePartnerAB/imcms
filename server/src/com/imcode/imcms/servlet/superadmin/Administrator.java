package com.imcode.imcms.servlet.superadmin;

/*
 *
 * @(#)Administrator.java
 *
 *
 * Copyright (c)
 *
 */

import imcode.util.SettingsAccessor;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.UnhandledException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

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

    protected boolean assertNoEmptyStringsInPropertyValues(Properties aPropObj) {
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
     SendHtml. Generates the html page to the browser.
     **/
    String createHtml( HttpServletRequest req,
                       Map vm, String htmlFile ) throws IOException {

        // Lets get the path to the admin templates folder
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( req );

        // Lets add the server host
        vm.put("SERVLET_URL", "") ;
        vm.put("SERVLET_URL2", "") ;

        List tagsAndData1 = new ArrayList(vm.size()*2) ;
        for ( Iterator tagsIterator = vm.entrySet().iterator(); tagsIterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)tagsIterator.next();
            tagsAndData1.add("#"+entry.getKey() +"#") ;
            tagsAndData1.add(entry.getValue()) ;
        }
        List tagsAndData = tagsAndData1 ;

        String html = imcref.getAdminTemplate( htmlFile, user, tagsAndData );
        return html;
    }

    /**
     SendHtml. Generates the html page to the browser.
     */
    protected void sendHtml(HttpServletRequest req, HttpServletResponse res,
                          Map vm, String htmlFile ) throws IOException {

        String str = this.createHtml( req, vm, htmlFile );

        // Lets send settings to a browser
        PrintWriter out = res.getWriter();
        Utility.setDefaultHtmlContentType( res );
        out.println(str);
    }

    /**
     * send error message
     *
     * @param user
     * @param errorCode         is the code to loock upp in ErrMsg.ini file
     */
    protected void sendErrorMessage(ImcmsServices imcref, String eMailServerMaster,
                          UserDomainObject user, String errorHeader,
                          int errorCode, HttpServletResponse response) throws IOException {

        String errorMessage = "" ;
        try {
            // Lets get the error code
            SettingsAccessor setObj = new SettingsAccessor("errmsg.ini", user, "admin");
            setObj.setDelimiter("=");
            setObj.loadSettings();
            errorMessage = setObj.getSetting("" + errorCode);
            if (errorMessage == null) {
                errorMessage = "Missing Errorcode";
            }

        } catch (Exception e) {
            throw new UnhandledException( e ) ;
        }

        Utility.setDefaultHtmlContentType( response );
        ServletOutputStream out = response.getOutputStream();

        Vector tagParsList = new Vector();

        tagParsList.add("#ERROR_HEADER#");
        tagParsList.add(errorHeader);
        tagParsList.add("#ERROR_MESSAGE#");
        tagParsList.add(errorMessage);
        tagParsList.add("#EMAIL_SERVER_MASTER#");
        tagParsList.add(eMailServerMaster);

        out.print(imcref.getAdminTemplate( TEMPLATE_ERROR, user, tagParsList ));
    }


}
