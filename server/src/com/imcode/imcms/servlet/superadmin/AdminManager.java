package com.imcode.imcms.servlet.superadmin;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.server.*;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import com.imcode.imcms.servlet.superadmin.AdminError;
import com.imcode.imcms.servlet.superadmin.Administrator;
import com.imcode.imcms.servlet.superadmin.Administrator;
import com.imcode.imcms.servlet.superadmin.AdminError;

public class AdminManager extends Administrator {

    private String HTML_TEMPLATE;
    private String HTML_ADMINTASK;
    private String HTML_USERADMINTASK;

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
            Properties langproperties = imcref.getLangProperties( user );
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            this.log(header + "- user is not an administrator");

            new AdminError(req, res, header, msg);
            return;
        }

        // Lets generate the html page
        VariableManager vm = new VariableManager();
        vm.addProperty("STATUS", "...");

        // lets parse and return the html_admin_part
        Vector vec = new Vector();
        String html_admin_part;

        if (imcref.checkAdminRights(user)) {
            html_admin_part = imcref.getAdminTemplate( HTML_ADMINTASK, user, vec ); // if superadmin
        } else { //if user is useradmin
            html_admin_part = imcref.getAdminTemplate( HTML_USERADMINTASK, user, vec ); //if useradmin
        }

        vm.addProperty("ADMIN_TASK", html_admin_part);

        super.sendHtml(req, res, vm, HTML_TEMPLATE);

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
        } else if (whichButton.equalsIgnoreCase("UrlDocTest")) {
            url += "UrlDocTest";
        } else if (whichButton.equalsIgnoreCase("MetaAdmin")) {
            url += "MetaAdmin";
        } else if (whichButton.equalsIgnoreCase("FileAdmin")) {
            url += "FileAdmin";
        } else if (whichButton.equalsIgnoreCase("ListDocs")) {
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

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.println("url" + url);

        // Ok, Lets redirect the user to the right adminservlet
        this.log("redirects + to:" + url);
        res.sendRedirect(url);
    }

    /**
     * Init: Detects paths and filenames.
     */

    public void init(ServletConfig config) throws ServletException {

        super.init(config);
        HTML_TEMPLATE = "AdminManager.htm";
        HTML_ADMINTASK = "AdminManager_adminTask_element.htm";
        HTML_USERADMINTASK = "AdminManager_useradminTask_element.htm";
        this.log("Initializing AdminManager");
    }


    /**
     * Log function, will work for both servletexec and Apache
     */

    public void log(String str) {
        super.log(str);
        System.out.println("AdminManager: " + str);
    }

} // End of class
