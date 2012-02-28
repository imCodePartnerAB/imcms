package com.imcode.imcms.servlet.superadmin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.SystemData;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import com.imcode.imcms.servlet.Version;
import com.imcode.db.DatabaseCommand;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class AdminSystemInfo extends HttpServlet {

    private final static Logger log = Logger.getLogger( AdminSystemInfo.class.getName() );

    private final static String HTML_TEMPLATE = "AdminSystemMessage.htm";

    private final static String VERSION_FILE = "/WEB-INF/version.txt";

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();

        SystemData sysData = imcref.getSystemData();

        int startDoc = sysData.getStartDocument();

        String msg = sysData.getSystemMessage();

        String webMaster = sysData.getWebMaster();
        String webMasterEmail = sysData.getWebMasterAddress();
        String serverMaster = sysData.getServerMaster();
        String serverMasterEmail = sysData.getServerMasterAddress();
	    
	      
        String imcmsVersion = new Version().getImcmsVersion();
        String serverInfo = getServletContext().getServerInfo();
        String databaseProductNameAndVersion = new Version().getDatabaseProductNameAndVersion();
        String javaVersion = new Version().getJavaVersion();
        String imCmsData = "Version:  " + imcmsVersion +
                           "\nServer:   " + serverInfo +
                           "\nDatabase: " + databaseProductNameAndVersion +
                           "\nJava:     " + javaVersion +
                           "\nPath:     " + Imcms.getPath() ;


        // Lets generate the html page
        Map vm = new HashMap();
        vm.put("IMCMS_DATA", "" + imCmsData);
        vm.put("STARTDOCUMENT", "" + startDoc);
        vm.put("SYSTEM_MESSAGE", msg);
        vm.put("WEB_MASTER", webMaster);
        vm.put("WEB_MASTER_EMAIL", webMasterEmail);
        vm.put("SERVER_MASTER", serverMaster);
        vm.put("SERVER_MASTER_EMAIL", serverMasterEmail);
        vm.put("PASSWORD_RESET_EXPIRATION_INTERVAL", "" + sysData.getUserLoginPasswordResetExpirationInterval());

        AdminRoles.sendHtml(req, res, vm, HTML_TEMPLATE);
    } // End doGet

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();

        // Lets check if the user is an admin, otherwise throw him out.
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !user.isSuperAdmin() ) {
            String header = "Error in AdminSystemInfo. ";
            Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            log.debug(header + "- user is not an administrator");
            AdminRoles.printErrorMessage(req, res, header, msg);
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
            String sysMsg = req.getParameter("SYSTEM_MESSAGE") == null ? "" : req.getParameter("SYSTEM_MESSAGE");

            imcode.server.SystemData sysData = imcref.getSystemData();
            sysData.setSystemMessage(sysMsg);

            imcref.setSystemData(sysData);

            doGet(req, res);
            return;
        }

        // ******* UPDATE THE SYSTEM SetServerMasterInfo IN THE DB **********

        if (req.getParameter("SetServerMasterInfo") != null) {

            // Lets get the parameters from html page and validate them
            String serverMaster = req.getParameter("SERVER_MASTER") == null ? "" : req.getParameter("SERVER_MASTER");
            String serverMasterEmail = req.getParameter("SERVER_MASTER_EMAIL") == null ? "" : req.getParameter("SERVER_MASTER_EMAIL");

            // Lets validate the parameters
            if (serverMaster.equalsIgnoreCase("") || !Utility.isValidEmail( serverMasterEmail )) {
                String header = "Error in AdminSystemInfo, servermaster info.";
                Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                String msg = langproperties.getProperty("error/servlet/AdminSystemInfo/validate_form_parameters")
                             + "<br>";
                AdminRoles.printErrorMessage(req, res, header, msg);
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
            String webMaster = req.getParameter("WEB_MASTER") == null ? "" : req.getParameter("WEB_MASTER");
            String webMasterEmail = req.getParameter("WEB_MASTER_EMAIL") == null ? "" : req.getParameter("WEB_MASTER_EMAIL");

            // Lets validate the parameters
            if (webMaster.equalsIgnoreCase("") || !Utility.isValidEmail( webMasterEmail )) {
                String header = "Error in AdminSystemInfo, webmaster info.";
                Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                String msg = langproperties.getProperty("error/servlet/AdminSystemInfo/validate_form_parameters")
                             + "<br>";
                AdminRoles.printErrorMessage(req, res, header, msg);
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


        if (req.getParameter("SetPasswordResetExpirationInterval") != null) {
            String value = StringUtils.trimToEmpty(req.getParameter("PASSWORD_RESET_EXPIRATION_INTERVAL"));

            if (!value.isEmpty()) {
                try {
                    int interval = Integer.parseInt(value);
                    if (interval > 0) {
                        imcode.server.SystemData sysData = imcref.getSystemData();
                        sysData.setUserLoginPasswordResetExpirationInterval(interval);

                        imcref.setSystemData(sysData);
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }
            }

            doGet(req, res);
            return;
        }


        if (req.getParameter("Cancel") != null) {
            res.sendRedirect("AdminManager");
        }

    } // end HTTP POST

}
