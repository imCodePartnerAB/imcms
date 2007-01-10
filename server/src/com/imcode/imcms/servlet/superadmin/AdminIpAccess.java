package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.db.StringArrayArrayResultSetHandler;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Html;
import imcode.util.Utility;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

public class AdminIpAccess extends HttpServlet {

    private final static Logger log = Logger.getLogger( AdminIpAccess.class );

    private static final String HTML_TEMPLATE = "AdminIpAccess.htm";
    private static final String HTML_IP_SNIPPET = "AdminIpAccessList.htm";
    private static final String ADD_IP_TEMPLATE = "AdminIpAccess_Add.htm";
    private static final String WARN_DEL_IP_TEMPLATE = "AdminIpAccess_Delete2.htm";

    /**
     * The GET method creates the html page when this page has been
     * redirected from somewhere else.
     */

    public void doGet( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();

        // ********** GENERATE THE IP-ACCESS PAGE *********
        // Lets get all IP-accesses from DB
        final Object[] parameters = new String[0];
        String[][] multi = (String[][]) imcref.getProcedureExecutor().executeProcedure("IPAccessesGetAll", parameters, new StringArrayArrayResultSetHandler());

        // Lets build the variables for each record
        List tags = new ArrayList();
        tags.add("IP_ACCESS_ID");
        tags.add("USER_ID");
        tags.add("LOGIN_NAME");
        tags.add("IP_START");
        tags.add("IP_END");

        // Lets parse each record and put it in a string
        String recs = "";
        int nbrOfRows = multi.length;
        for ( int counter = 0; counter < nbrOfRows; counter++ ) {
            List aRecV = new ArrayList(Arrays.asList(multi[counter]));
            Map vmRec = new HashMap();
            aRecV.set(3, Utility.ipLongToString(Long.parseLong((String) aRecV.get(3))));
            aRecV.set(4, Utility.ipLongToString(Long.parseLong((String) aRecV.get(4))));
            for ( int i = 0; i < tags.size(); i++ ) {
                vmRec.put(tags.get(i), aRecV.get(i));
            }
            vmRec.put("RECORD_COUNTER", "" + counter);
            recs += AdminRoles.createHtml(req, vmRec, HTML_IP_SNIPPET);
        }

        // Lets generate the html page
        Map vm = new HashMap();
        vm.put("ALL_IP_ACCESSES", recs);
        AdminRoles.sendHtml(req, res, vm, HTML_TEMPLATE);
    }

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        // Lets check if the user is an admin, otherwise throw him out.
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !user.isSuperAdmin() ) {
            printNonAdminError(imcref, user, req, res, getClass());
        } else {
            if ( req.getParameter("ADD_IP_ACCESS") != null ) {

                // Lets get all USERS from DB
                String usersOption = Html.createUsersOptionList(imcref);

                // Lets generate the html page
                Map vm = new HashMap();
                vm.put("USERS_LIST", usersOption);
                AdminRoles.sendHtml(req, res, vm, ADD_IP_TEMPLATE);
            } else if ( req.getParameter("CANCEL_ADD_IP") != null || req.getParameter("IP_CANCEL_DELETE") != null ) {
                res.sendRedirect("AdminIpAccess?action=start");
            } else if ( req.getParameter("ADD_NEW_IP_ACCESS") != null ) {
                log.debug("Now's ADD_IP_ACCESS running");

                // Lets get the parameters from html page and validate them
                Properties params = getAddParameters(req);
                params = validateParameters(params, req, res, imcref, user);
                if ( params == null ) {
                    return;
                }

                final Object[] parameters = new String[] { params.getProperty("USER_ID"),
                        params.getProperty("IP_START"),
                        params.getProperty("IP_END") };
                imcref.getProcedureExecutor().executeUpdateProcedure("IPAccessAdd", parameters);
                res.sendRedirect("AdminIpAccess?action=start");
            } else if ( req.getParameter("RESAVE_IP_ACCESS") != null ) {

                // Lets get all the ip_access id:s
                String[] reSavesIds = getEditedIpAccesses(req);

                // Lets resave all marked ip-accesses.
                if ( reSavesIds != null ) {
                    for ( int i = 0; i < reSavesIds.length; i++ ) {
                        log.debug("ResaveId: " + reSavesIds[i]);
                        String tmpId = reSavesIds[i];
                        // Lets get all edited fields for that ip-access
                        String ipAccessId = req.getParameter("IP_ACCESS_ID_" + tmpId);
                        String ipUserId = req.getParameter("IP_USER_ID_" + tmpId);
                        String ipStart = req.getParameter("IP_START_" + tmpId);
                        String ipEnd = req.getParameter("IP_END_" + tmpId);

                        long ipStartInt = Utility.ipStringToLong(ipStart);

                        long ipEndInt = Utility.ipStringToLong(ipEnd);

                        final Object[] parameters = new String[] { ipAccessId,
                                ipUserId,
                                "" + ipStartInt,
                                "" + ipEndInt };
                        imcref.getProcedureExecutor().executeUpdateProcedure("IPAccessUpdate", parameters);
                    }
                }

                doGet(req, res);
            } else if ( req.getParameter("IP_WARN_DELETE") != null ) {

                // Lets get the parameters from html page and validate them
                HttpSession session = req.getSession(false);
                if ( session != null ) {
                    Enumeration enumNames = req.getParameterNames();
                    while ( enumNames.hasMoreElements() ) {
                        String paramName = (String) enumNames.nextElement();
                        String[] arr = req.getParameterValues(paramName);
                        session.setAttribute("IP." + paramName, arr);
                    }
                } else {
                    String header = "Error in AdminIpAccess, delete. ";
                    Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                    String msg = langproperties.getProperty("error/servlet/AdminIpAccess/no_session") + "<br>";
                    log.debug(header + "- session could not be created");
                    AdminRoles.printErrorMessage(req, res, header, msg);
                    return;
                }

                // Lets generate the last warning html page
                Map vm = new HashMap();
                AdminRoles.sendHtml(req, res, vm, WARN_DEL_IP_TEMPLATE);
            } else if ( req.getParameter("DEL_IP_ACCESS") != null ) {
                HttpSession session = req.getSession(false);
                if ( session != null ) {
                    log.debug("Ok, ta bort en Ip-access: " + session.toString());

                    String[] deleteIds = (String[]) session.getAttribute("IP.EDIT_IP_ACCESS");

                    // Lets resave all marked ip-accesses.
                    if ( deleteIds != null ) {
                        for ( int i = 0; i < deleteIds.length; i++ ) {
                            String tmpId = "IP.IP_ACCESS_ID_" + deleteIds[i];
                            String[] tmpArr = (String[]) session.getAttribute(tmpId);
                            String ipAccessId = tmpArr[0];
                            final Object[] parameters = new String[] { ipAccessId };
                            imcref.getProcedureExecutor().executeUpdateProcedure("IPAccessDelete", parameters);
                        }
                    }
                } else {
                    String header = "Error in AdminIpAccess, delete.";
                    Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                    String msg = langproperties.getProperty("error/servlet/AdminIpAccess/no_session") + "<br>";
                    log.debug(header + "- session could not be created");
                    AdminRoles.printErrorMessage(req, res, header, msg);
                    return;
                }
                doGet(req, res);
            }
        }

    } // end HTTP POST

    static void printNonAdminError(ImcmsServices imcref, UserDomainObject user, HttpServletRequest req,
                                   HttpServletResponse res, Class clazz) throws IOException {
        Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
        String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
        String header = "Error in "+ ClassUtils.getShortClassName(clazz) +".";
        log.debug(header + "- user is not an administrator");
        AdminRoles.printErrorMessage(req, res, header, msg);
    }

    /**
     * Collects the parameters from the request object for the add function
     */
    private Properties getAddParameters( HttpServletRequest req ) {

        Properties ipInfo = new Properties();
        // Lets get the parameters we know we are supposed to get from the request object
        String user_id = req.getParameter("USER_ID") == null ? "" : req.getParameter("USER_ID").trim();
        String ipStart = req.getParameter("IP_START") == null ? "" : req.getParameter("IP_START").trim();
        String ipEnd = req.getParameter("IP_END") == null ? "" : req.getParameter("IP_END").trim();

        long ipStartInt = Utility.ipStringToLong( ipStart );

        long ipEndInt = Utility.ipStringToLong( ipEnd );

        ipInfo.setProperty( "USER_ID", user_id );
        ipInfo.setProperty( "IP_START", String.valueOf( ipStartInt ) );
        ipInfo.setProperty( "IP_END", String.valueOf( ipEndInt ) );
        return ipInfo;
    }

    /**
     * Collects the parameters used to delete a reply
     */

    private String[] getEditedIpAccesses( HttpServletRequest req ) {

        // Lets get the standard discussion_id to delete
        return req.getParameterValues("EDIT_IP_ACCESS");
    }

    /**
     * Returns a Properties, containing the user information from the html page. if Something
     * failes, a error page will be generated and null will be returned.
     */

    private Properties validateParameters( Properties aPropObj, HttpServletRequest req, HttpServletResponse res, ImcmsServices imcref, UserDomainObject user ) throws IOException {

        if ( aPropObj.values().contains("") ) {
            String header = "Error in AdminIpAccess, assertNoEmptyStringsInPropertyValues.";
            Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
            String msg = langproperties.getProperty("error/servlet/AdminIpAccess/vaidate_form_parameters") + "<br>";
            log.debug(header + "- values is missing for some parameters");
            AdminRoles.printErrorMessage(req, res, header, msg);
            return null;
        }
        return aPropObj;
    }
}
