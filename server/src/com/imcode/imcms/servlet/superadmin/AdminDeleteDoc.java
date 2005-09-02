package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.mapping.DefaultDocumentMapper;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AdminDeleteDoc extends HttpServlet {

    private final static Logger log = Logger.getLogger( AdminDeleteDoc.class.getName() );

    private final static String HTML_TEMPLATE = "AdminDeleteDoc.htm";

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Lets verify that this user is an admin
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser(req);
        if ( !user.isSuperAdmin() ) {
            String header = "Error in AdminCounter.";
            Properties langproperties = imcref.getLanguageProperties(user);
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            log.debug(header + "- user is not an administrator");
            AdminRoles.printErrorMessage(req, res, header, msg);
            return;
        }

        Map vm = new HashMap();
        AdminRoles.sendHtml(req, res, vm, HTML_TEMPLATE);

    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {


        // Lets check if the user is an admin, otherwise throw him out.
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if (user.isSuperAdmin() == false) {
            String header = "Error in AdminCounter.";
            Properties langproperties = imcref.getLanguageProperties(user);
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            log.debug(header + "- user is not an administrator");
            AdminRoles.printErrorMessage(req, res, header, msg);
            return;
        }

        // ******* DELETE DOC **********

        if (req.getParameter("DELETE_DOC") != null) {

            // Lets get the parameters from html page and validate them
            Properties params = this.getParameters(req);
            if (this.validateParameters(params) == false) {
                String header = "Error in AdminDeleteDoc.";
                Properties langproperties = imcref.getLanguageProperties(user);
                String msg = langproperties.getProperty("error/servlet/AdminDeleteDoc/no_valid_metaid") + "<br>";
                log.debug(header + "- no valid metaid");
                AdminRoles.printErrorMessage(req, res, header, msg);
                return;
            }

            // OK, Lets check that the metaid were gonna delete exists in db
            int metaId = Integer.parseInt(params.getProperty("DEL_META_ID"));
            String foundMetaId = imcref.getDatabase().executeStringProcedure( "FindMetaId", new String[] {""
                                                                                                          + metaId} );
            log.debug("FoundMetaId: " + foundMetaId);

            if (foundMetaId == null) {
                String header = "Error in AdminDeleteDoc. ";
                Properties langproperties = imcref.getLanguageProperties(user);
                String msg = langproperties.getProperty("error/servlet/AdminDeleteDoc/no_metaid_in_db") + "( " + metaId
                             + " ) <br>";
                log.debug(header + "- metaid could not be found in db");
                AdminRoles.printErrorMessage(req, res, header, msg);
                return;
            }

            // Ok, Lets delete the meta id
            DefaultDocumentMapper documentMapper = imcref.getDefaultDocumentMapper();
            DocumentDomainObject document = documentMapper.getDocument( metaId ) ;
            documentMapper.deleteDocument( document, user);
            imcref.updateMainLog( "Document  " + "[" + document.getId() +
                                  "] ALL deleted by user: [" + user.getFullName() + "]" );

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
        log.debug("Unidentified argument was sent!");
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

        if ( params.values().contains("") ) return false;
        try {
            Integer.parseInt(params.getProperty("DEL_META_ID"));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


} // End of class
