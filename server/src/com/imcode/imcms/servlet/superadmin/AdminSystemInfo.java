package com.imcode.imcms.servlet.superadmin;

import imcode.util.VariableManager;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

public class AdminSystemInfo extends Administrator {

    private final static Logger log = Logger.getLogger( AdminSystemInfo.class.getName() );

    private final static String HTML_TEMPLATE = "AdminSystemMessage.htm";

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();

        imcode.server.SystemData sysData = imcref.getSystemData();

        int startDoc = sysData.getStartDocument();

        String msg = sysData.getSystemMessage();

        String webMaster = sysData.getWebMaster();
        String webMasterEmail = sysData.getWebMasterAddress();
        String serverMaster = sysData.getServerMaster();
        String serverMasterEmail = sysData.getServerMasterAddress();


        // Lets generate the html page
        VariableManager vm = new VariableManager();
        vm.addProperty("STARTDOCUMENT", "" + startDoc);
        vm.addProperty("SYSTEM_MESSAGE", msg);
        vm.addProperty("WEB_MASTER", webMaster);
        vm.addProperty("WEB_MASTER_EMAIL", webMasterEmail);
        vm.addProperty("SERVER_MASTER", serverMaster);
        vm.addProperty("SERVER_MASTER_EMAIL", serverMasterEmail);

        this.sendHtml(req, res, vm, HTML_TEMPLATE);
    } // End doGet

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();

        // Lets check if the user is an admin, otherwise throw him out.
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if (user.isSuperAdmin() == false) {
            String header = "Error in AdminSystemInfo. ";
            Properties langproperties = imcref.getLanguageProperties( user );
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            log.debug( header + "- user is not an administrator" );
            new AdminError(req, res, header, msg);
            return;
        }

        if (req.getParameter("SetStartDoc") != null) {
            String metaIdString = req.getParameter("STARTDOCUMENT");
            try {
                imcode.server.SystemData sysData = imcref.getSystemData();
                sysData.setStartDocument(Integer.parseInt(metaIdString));
                imcref.setSystemData(sysData);
            } catch (NumberFormatException ignored) {
                // Illegal meta-id, ignored.
            }

            doGet(req, res);
            return;
        }

        // ******* UPDATE THE SYSTEM MESSAGE IN THE DB **********

        if (req.getParameter("SetSystemMsg") != null) {

            // Lets get the parameters from html page and validate them
            String sysMsg = (req.getParameter("SYSTEM_MESSAGE") == null) ? "" : (req.getParameter("SYSTEM_MESSAGE"));

            imcode.server.SystemData sysData = imcref.getSystemData();
            sysData.setSystemMessage(sysMsg);

            imcref.setSystemData(sysData);

            doGet(req, res);
            return;
        }

        // ******* UPDATE THE SYSTEM SetServerMasterInfo IN THE DB **********

        if (req.getParameter("SetServerMasterInfo") != null) {

            // Lets get the parameters from html page and validate them
            String serverMaster = (req.getParameter("SERVER_MASTER") == null) ? "" : (req.getParameter("SERVER_MASTER"));
            String serverMasterEmail = (req.getParameter("SERVER_MASTER_EMAIL") == null) ? "" : (req.getParameter("SERVER_MASTER_EMAIL"));

            // Lets validate the parameters
            if (serverMaster.equalsIgnoreCase("") || !Utility.isValidEmail( serverMasterEmail )) {
                String header = "Error in AdminSystemInfo, servermaster info.";
                Properties langproperties = imcref.getLanguageProperties( user );
                String msg = langproperties.getProperty("error/servlet/AdminSystemInfo/validate_form_parameters") + "<br>";
                new AdminError(req, res, header, msg);
                return;
            }

            imcode.server.SystemData sysData = imcref.getSystemData();
            sysData.setServerMaster(serverMaster);
            sysData.setServerMasterAddress(serverMasterEmail);

            imcref.setSystemData(sysData);

            doGet(req, res);
            return;
        }

        // ******* UPDATE THE SYSTEM WEBMASTER IN THE FILE **********

        if (req.getParameter("SetWebMasterInfo") != null) {

            // Lets get the parameters from html page and validate them
            String webMaster = (req.getParameter("WEB_MASTER") == null) ? "" : (req.getParameter("WEB_MASTER"));
            String webMasterEmail = (req.getParameter("WEB_MASTER_EMAIL") == null) ? "" : (req.getParameter("WEB_MASTER_EMAIL"));

            // Lets validate the parameters
            if (webMaster.equalsIgnoreCase("") || !Utility.isValidEmail( webMasterEmail )) {
                String header = "Error in AdminSystemInfo, webmaster info.";
                Properties langproperties = imcref.getLanguageProperties( user );
                String msg = langproperties.getProperty("error/servlet/AdminSystemInfo/validate_form_parameters") + "<br>";
                new AdminError(req, res, header, msg);
                return;
            }

            // Lets build the users information into a string and add it to db
            imcode.server.SystemData sysData = imcref.getSystemData();
            sysData.setWebMaster(webMaster);
            sysData.setWebMasterAddress(webMasterEmail);

            imcref.setSystemData(sysData);

            doGet(req, res);
            return;
        }
        if (req.getParameter("Cancel") != null) {
            res.sendRedirect("AdminManager");
            return;
        }

    } // end HTTP POST

}
