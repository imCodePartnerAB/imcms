package com.imcode.imcms.servlet.superadmin;

import com.imcode.db.handlers.SingleObjectHandler;
import com.imcode.imcms.db.StringFromRowFactory;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AdminDeleteDoc extends HttpServlet {

    private final static Logger log = LogManager.getLogger(AdminDeleteDoc.class.getName());

    private final static String HTML_TEMPLATE = "AdminDeleteDoc.htm";

    private final static String ERROR_MESSAGE = "ERROR_MESSAGE";

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Lets verify that this user is an admin
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (!user.isSuperAdmin()) {
            AdminIpAccess.printNonAdminError(user, req, res, getClass());
        } else {
            sendHtmlWithErrorMessage(req, res, "");
        }

    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();

        // Lets check if the user is an admin, otherwise throw him out.
        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (!user.isSuperAdmin()) {
            AdminIpAccess.printNonAdminError(user, req, res, getClass());
            return;
        }

        if (req.getParameter("DELETE_DOC") != null) {
            // Lets get the parameters from html page and validate them
            Properties params = getParameters(req);

            if (!validateParameters(params)) {
                String header = "Error in AdminDeleteDoc.";
                Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                String msg = langproperties.getProperty("error/servlet/AdminDeleteDoc/no_valid_metaid");
                log.debug(header + "- no valid metaid");

                sendHtmlWithErrorMessage(req, res, msg);
                return;
            }

            final int metaId = Integer.parseInt(params.getProperty("DEL_META_ID"));

            //Let's check if the document is protected from deletion
            if (isDeletionProtectedDocument(metaId)) {
                String header = "Error in AdminDeleteDoc.";
                Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                String msg = langproperties.getProperty("error/servlet/AdminDeleteDoc/protected_document");
                log.debug(header + "- metaid could not be found in db");

                sendHtmlWithErrorMessage(req, res, msg);
                return;
            }

            // OK, Lets check that the metaid were gonna delete exists in db
            final Object[] parameters = new String[]{""
                    + metaId};
            String foundMetaId = (String) imcref.getProcedureExecutor().executeProcedure("FindMetaId", parameters, new SingleObjectHandler<>(new StringFromRowFactory()));
            log.debug("FoundMetaId: " + foundMetaId);

            if (foundMetaId == null) {
                String header = "Error in AdminDeleteDoc. ";
                Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                String msg = langproperties.getProperty("error/servlet/AdminDeleteDoc/no_metaid_in_db") + " ( " + metaId + " )";
                log.debug(header + "- metaid could not be found in db");

                sendHtmlWithErrorMessage(req, res, msg);
                return;
            }
            // Ok, Lets delete the meta id
            DocumentMapper documentMapper = imcref.getDocumentMapper();
            DocumentDomainObject document = documentMapper.getDocument(metaId);
            documentMapper.deleteDocument(document, user);
            imcref.updateMainLog("Document  " + "[" + document.getId() +
                    "] ALL deleted by user: [" + user.getFullName() + "]");

            doGet(req, res);

        } else if (req.getParameter("GO_BACK") != null) {
            String url = "AdminManager";
            res.sendRedirect(url);
        } else {
            log.debug("Unidentified argument was sent!");
            doGet(req, res);
        }

    } // end HTTP POST

    /**
     * Collects the parameters from the request object
     */

    private Properties getParameters(HttpServletRequest req) {

        Properties params = new Properties();
        // Lets get the parameters we know we are supposed to get from the request object
        String del_meta_id = req.getParameter("delete_meta_id") == null ? "" : req.getParameter("delete_meta_id");

        params.setProperty("DEL_META_ID", del_meta_id);

        return params;
    }

    /**
     * Collects the parameters from the request object
     */

    private boolean validateParameters(Properties params) {

        if (params.values().contains("")) {
            return false;
        }
        try {
            Integer.parseInt(params.getProperty("DEL_META_ID"));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean isDeletionProtectedDocument(int docId) {
        String deleteProtectedMetaIds = Imcms.getServerProperties().getProperty("DeleteProtectedMetaIds");

        return !deleteProtectedMetaIds.isEmpty() && Arrays.stream(deleteProtectedMetaIds.split(","))
                .anyMatch(protectedId -> docId == Integer.parseInt(protectedId.trim()));
    }

    private void sendHtmlWithErrorMessage(HttpServletRequest req, HttpServletResponse res, String errorMsg) throws IOException {
        Map vm = new HashMap();
        vm.put(ERROR_MESSAGE, errorMsg);
        AdminRoles.sendHtml(req, res, vm, HTML_TEMPLATE);
    }

} // End of class
