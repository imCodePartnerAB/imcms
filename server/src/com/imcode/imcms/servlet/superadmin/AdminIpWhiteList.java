package com.imcode.imcms.servlet.superadmin;

import com.imcode.db.DatabaseCommand;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.commands.SqlQueryDatabaseCommand;
import com.imcode.db.handlers.CollectionHandler;
import com.imcode.db.handlers.RowTransformer;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Feature allows to define IP white list per user role.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.11.17.
 */
public class AdminIpWhiteList extends HttpServlet {

    private static final Logger log = Logger.getLogger(AdminIpWhiteList.class);

    private static final String WHITE_LIST_TEMPLATE = "AdminIpWhiteList.jsp";
    private static final String WHITE_LIST_ADD_TEMPLATE = "AdminIpWhiteList_Add.jsp";
    private static final String TABLE_NAME = "imcms_ip_white_list";

    private final InetAddressValidator ipValidator = InetAddressValidator.getInstance();

    private final RowTransformer<RoleIpRange> rowTransformer = new RowTransformer<RoleIpRange>() {
        @Override
        public RoleIpRange createObjectFromResultSetRow(ResultSet resultSet) throws SQLException {
            final boolean isAdmin = resultSet.getBoolean(1);
            final String ipFrom = resultSet.getString(2);
            final String ipTo = resultSet.getString(3);
            return new RoleIpRange(isAdmin, ipFrom, ipTo);
        }

        @Override
        public Class<RoleIpRange> getClassOfCreatedObjects() {
            return RoleIpRange.class;
        }
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final UserDomainObject user = Utility.getLoggedOnUser(request);
        final String language = user.getLanguageIso639_2();

        if (!user.isSuperAdmin()) {
            AdminIpAccess.printNonAdminError(user, request, response, getClass());
            return;
        }

        final DatabaseCommand<List<RoleIpRange>> queryCommand = new SqlQueryDatabaseCommand<List<RoleIpRange>>(
                "SELECT is_admin, ip_range_from, ip_range_to FROM " + TABLE_NAME, new Object[]{},
                new CollectionHandler<RoleIpRange, List<RoleIpRange>>(new ArrayList<RoleIpRange>(), rowTransformer)
        );

        final List<RoleIpRange> roleIpRanges = Imcms.getServices().getDatabase().execute(queryCommand);
        final String templatePath = getAdminTemplatePath(WHITE_LIST_TEMPLATE, user);

        response.setContentType("text/html");
        request.setAttribute("contextPath", request.getContextPath());
        request.setAttribute("language", language);
        request.setAttribute("roleIpRanges", roleIpRanges);
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

            if (ipValidator.isValidInet4Address(ipFrom) && ipValidator.isValidInet4Address(ipTo)) {
                final String isAdmin = String.valueOf(request.getParameter("IS_ADMIN"));
                final String[][] commandParams = new String[][]{
                        {"is_admin", isAdmin},
                        {"ip_range_from", ipFrom},
                        {"ip_range_to", ipTo},
                };

                log.info("Adding new IP range from " + ipFrom + " to " + ipTo + " for "
                        + ("1".equals(isAdmin) ? "super" : "non") + "-admin roles.");

                Imcms.getServices().getDatabase().execute(new InsertIntoTableDatabaseCommand(TABLE_NAME, commandParams));

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

    public final class RoleIpRange {
        private final boolean isAdmin;
        private final String ipFrom;
        private final String ipTo;

        private RoleIpRange(boolean isAdmin, String ipFrom, String ipTo) {
            this.isAdmin = isAdmin;
            this.ipFrom = ipFrom;
            this.ipTo = ipTo;
        }

        public boolean isAdmin() {
            return isAdmin;
        }

        public String getIpFrom() {
            return ipFrom;
        }

        public String getIpTo() {
            return ipTo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RoleIpRange that = (RoleIpRange) o;

            if (isAdmin != that.isAdmin) return false;
            if (!ipFrom.equals(that.ipFrom)) return false;
            return ipTo.equals(that.ipTo);
        }

        @Override
        public int hashCode() {
            int result = (isAdmin ? 1 : 0);
            result = 31 * result + ipFrom.hashCode();
            result = 31 * result + ipTo.hashCode();
            return result;
        }
    }

}
