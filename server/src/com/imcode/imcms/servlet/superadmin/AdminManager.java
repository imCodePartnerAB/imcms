package com.imcode.imcms.servlet.superadmin;

import imcode.external.diverse.VariableManager;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Vector;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class AdminManager extends Administrator {

    private final static Logger log = Logger.getLogger( AdminManager.class.getName() );

    private final static String HTML_TEMPLATE = "AdminManager.htm"; ;
    private final static String HTML_ADMINTASK = "AdminManager_adminTask_element.htm" ;
    private final static String HTML_USERADMINTASK = "AdminManager_useradminTask_element.htm";

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        // Lets verify that the user who tries to add a new user is an admin
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if (!user.isSuperAdmin() && !user.isUserAdmin()) {
            String header = "Error in AdminManager.";
            Properties langproperties = imcref.getLanguageProperties( user );
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            log.debug(header + "- user is not an administrator");

            new AdminError(req, res, header, msg);
            return;
        }

        // lets parse and return the html_admin_part
        Vector vec = new Vector();
        String html_admin_part;

        if (user.isSuperAdmin()) {
            html_admin_part = imcref.getAdminTemplate( HTML_ADMINTASK, user, vec ); // if superadmin
        } else { //if user is useradmin
            html_admin_part = imcref.getAdminTemplate( HTML_USERADMINTASK, user, vec ); //if useradmin
        }

        Utility.setDefaultHtmlContentType( res );
        String page = imcref.getAdminTemplate( HTML_TEMPLATE, user, Arrays.asList(new String[]{ "#ADMIN_TASK#", html_admin_part }));
        res.getWriter().print( page ) ;

    } // End doGet

    /**
     * doPost
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Lets get the parameters and validate them, we dont have any own
        // parameters so were just validate the metadata


        String whichButton = req.getParameter("AdminTask");
        if (whichButton == null) whichButton = "";

        String url = "";
        if (whichButton.equalsIgnoreCase("UserStart")) {
            url += "AdminUser";
        } else if (whichButton.equalsIgnoreCase("CounterStart")) {
            url += "AdminCounter";
        } else if (whichButton.equalsIgnoreCase("AddTemplates")) {
            url += "TemplateAdmin";
        } else if (whichButton.equalsIgnoreCase("DeleteDocs")) {
            url += "AdminDeleteDoc";
        } else if (whichButton.equalsIgnoreCase("IP-access")) {
            url += "AdminIpAccess";
        } else if (whichButton.equalsIgnoreCase("SystemMessage")) {
            url += "AdminSystemInfo";
        } else if (whichButton.equalsIgnoreCase("AdminRoles")) {
            url += "AdminRoles";
        } else if (whichButton.equalsIgnoreCase("LinkCheck")) {
            url += "LinkCheck";
        } else if (whichButton.equalsIgnoreCase("ListDocuments")) {
            url += "ListDocuments";
        } else if (whichButton.equalsIgnoreCase("FileAdmin")) {
            url += "FileAdmin";
        } else if (whichButton.equalsIgnoreCase("AdminListDocs")) {
            url += "AdminListDocs";
        } else if (whichButton.equalsIgnoreCase("AdminConference")) {
            url += "AdminConference";
        } else if (whichButton.equalsIgnoreCase("AdminRandomTexts")) {
            url += "AdminRandomTexts";
        } else if (whichButton.equalsIgnoreCase("AdminQuestions")) {
            url += "AdminQuestions";
        } else if (whichButton.equalsIgnoreCase("AdminSection")) {
            url += "AdminSection";
        } else if (whichButton.equalsIgnoreCase("AdminCategories")) {
            url += "AdminCategories";
        } else {
            // Ok, were came here cause no valid argument was sent to us
            // Lets send the user back to the Get function.
            this.doGet(req, res);
            return;
        }

        Utility.setDefaultHtmlContentType( res );
        PrintWriter out = res.getWriter();
        out.println("url" + url);

        // Ok, Lets redirect the user to the right adminservlet
        log.debug("redirects + to:" + url);
        res.sendRedirect(url);
    }

} // End of class
