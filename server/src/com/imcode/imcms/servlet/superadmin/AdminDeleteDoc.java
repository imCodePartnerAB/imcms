package com.imcode.imcms.servlet.superadmin;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import imcode.external.diverse.*;
import imcode.server.*;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import com.imcode.imcms.servlet.superadmin.Administrator;

public class AdminDeleteDoc extends Administrator {

    private final static String HTML_TEMPLATE = "AdminDeleteDoc.htm";

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Lets verify that this user is an admin
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (imcref.checkAdminRights(user) == false) {
            String header = "Error in AdminCounter.";
            String msg = "The user is not an administrator." + "<BR>";
            this.log(header + msg);
            new AdminError(req, res, header, msg);
            return;
        }

        VariableManager vm = new VariableManager();
        super.sendHtml(req, res, vm, HTML_TEMPLATE);

    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {


        // Lets check if the user is an admin, otherwise throw him out.
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if (imcref.checkAdminRights(user) == false) {
            String header = "Error in AdminCounter.";
            String msg = "The user is not an administrator." + "<BR>";
            this.log(header + msg);
            new AdminError(req, res, header, msg);
            return;
        }

        // ******* DELETE DOC **********

        if (req.getParameter("DELETE_DOC") != null) {

            // Lets get the parameters from html page and validate them
            Properties params = this.getParameters(req);
            if (this.validateParameters(params) == false) {
                String header = "Error in AdminDeleteDoc.";
                String msg = "The metaid was not correct. Please add a valid metaid." + "<BR>";
                this.log(header + msg);
                new AdminError(req, res, header, msg);
                return;
            }

            // OK, Lets check that the metaid were gonna delete exists in db
            int metaId = Integer.parseInt(params.getProperty("DEL_META_ID"));
            String foundMetaId = imcref.sqlProcedureStr("FindMetaId", new String[]{"" + metaId});
            log("FoundMetaId: " + foundMetaId);

            if (foundMetaId == null) {
                String header = "Error in AdminUserProps.";
                String msg = "The metaid " + metaId + " could not be found in db. <BR>";
                this.log(header + msg);
                new AdminError(req, res, header, msg);
                return;
            }

            // Ok, Lets delete the meta id
            log("Nu försöker vi ta bort ett meta id");
            imcref.deleteDocAll(metaId, user);
            this.doGet(req, res);
            //this.goAdminUsers(req, res) ;
            return;
        }

        // ******** GO_BACK TO THE MENY ***************
        if (req.getParameter("GO_BACK") != null) {
            String url = "AdminManager";
            res.sendRedirect(url);
            return;
        }

        // ******** UNIDENTIFIED ARGUMENT TO SERVER ********
        this.log("Unidentified argument was sent!");
        doGet(req, res);
        return;
    } // end HTTP POST

    /**
     * Collects the parameters from the request object
     */

    private Properties getParameters(HttpServletRequest req) {

        Properties params = new Properties();
        // Lets get the parameters we know we are supposed to get from the request object
        String del_meta_id = (req.getParameter("delete_meta_id") == null) ? "" : (req.getParameter("delete_meta_id"));

        params.setProperty("DEL_META_ID", del_meta_id);

        return params;
    }

    /**
     * Collects the parameters from the request object
     */

    private boolean validateParameters(Properties params) {

        if (super.checkParameters(params) == false) return false;
        try {
            Integer.parseInt(params.getProperty("DEL_META_ID"));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


} // End of class
