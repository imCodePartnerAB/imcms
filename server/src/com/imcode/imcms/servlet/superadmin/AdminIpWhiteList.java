package com.imcode.imcms.servlet.superadmin;

import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Feature allows to define IP white list per user role.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.11.17.
 */
public class AdminIpWhiteList extends HttpServlet {

    private final static Logger log = Logger.getLogger(AdminIpWhiteList.class);

    private static final String WHITE_LIST_TEMPLATE = "AdminIpWhiteList.jsp";
    private static final String WHITE_LIST_ADD_TEMPLATE = "AdminIpWhiteList_Add.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final UserDomainObject user = Utility.getLoggedOnUser(request);
        final String language = user.getLanguageIso639_2();
        final String templatePath = getAdminTemplatePath(WHITE_LIST_TEMPLATE, user);

        response.setContentType("text/html");

        request.setAttribute("contextPath", request.getContextPath());
        request.setAttribute("language", language);
        request.getRequestDispatcher(templatePath).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final UserDomainObject user = Utility.getLoggedOnUser(request);
        final String language = user.getLanguageIso639_2();

        if (!user.isSuperAdmin()) {
            AdminIpAccess.printNonAdminError(user, request, response, getClass());

        } else if (request.getParameter("ADD_IP_RANGE") != null) {
            final String templatePath = getAdminTemplatePath(WHITE_LIST_ADD_TEMPLATE, user);

            request.setAttribute("contextPath", request.getContextPath());
            request.setAttribute("language", language);
            request.getRequestDispatcher(templatePath).forward(request, response);

        } else if (request.getParameter("ADD_NEW_IP_RANGE") != null) {
            final String ipFrom = request.getParameter("IP_START");
            final String ipTo = request.getParameter("IP_END");
            final InetAddressValidator ipValidator = InetAddressValidator.getInstance();

            if (ipValidator.isValidInet4Address(ipFrom) && ipValidator.isValidInet4Address(ipTo)) {
                final String isAdmin = String.valueOf(request.getParameter("IS_ADMIN"));
                final String tableName = "imcms_ip_white_list";
                final String[][] commandParams = new String[][]{
                        {"is_admin", isAdmin},
                        {"ip_range_from", ipFrom},
                        {"ip_range_to", ipTo},
                };

                log.info("Adding new IP range from " + ipFrom + " to " + ipTo + " for "
                        + ("1".equals(isAdmin) ? "super" : "non") + "-admin roles.");

                Imcms.getServices().getDatabase().execute(new InsertIntoTableDatabaseCommand(tableName, commandParams));

                final String templatePath = getAdminTemplatePath(WHITE_LIST_TEMPLATE, user);

                request.setAttribute("contextPath", request.getContextPath());
                request.setAttribute("language", language);
                request.getRequestDispatcher(templatePath).forward(request, response);

            } else {
                final String header = "Error in AdminIpWhiteList.";
                final String msg = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user)
                        .getProperty("error/servlet/AdminIpWhiteList/validate_form_parameters") + "<br>";
                AdminRoles.printErrorMessage(request, response, header, msg);
            }
        }
    }

    private String getAdminTemplatePath(String templateFileName, UserDomainObject user) {
        return "/WEB-INF/templates/" + user.getLanguageIso639_2() + "/admin/" + templateFileName;
    }

}
