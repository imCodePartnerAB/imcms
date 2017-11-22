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
    private static final String IS_ADMIN = "is_admin";
    private static final String IP_RANGE_FROM = "ip_range_from";
    private static final String IP_RANGE_TO = "ip_range_to";

    private final InetAddressValidator ipValidator = InetAddressValidator.getInstance();

    private final RowTransformer<RoleIpRange> rowTransformer = new RowTransformer<RoleIpRange>() {
        @Override
        public RoleIpRange createObjectFromResultSetRow(ResultSet resultSet) throws SQLException {
            final int id = resultSet.getInt("id");
            final boolean isAdmin = resultSet.getBoolean(IS_ADMIN);
            final String ipFrom = resultSet.getString(IP_RANGE_FROM);
            final String ipTo = resultSet.getString(IP_RANGE_TO);
            return new RoleIpRange(id, isAdmin, ipFrom, ipTo);
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

        final List<RoleIpRange> roleIpRanges = getRoleIpRanges();
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
                        {IS_ADMIN, isAdmin},
                        {IP_RANGE_FROM, ipFrom},
                        {IP_RANGE_TO, ipTo},
                };

                log.info("Adding new IP range from " + ipFrom + " to " + ipTo + " for "
                        + ("1".equals(isAdmin) ? "super" : "non") + "-admin roles.");

                Imcms.getServices().getDatabase().execute(new InsertIntoTableDatabaseCommand(TABLE_NAME, commandParams));

                final String templatePath = getAdminTemplatePath(WHITE_LIST_TEMPLATE, user);
                final List<RoleIpRange> roleIpRanges = getRoleIpRanges();

                request.setAttribute("contextPath", request.getContextPath());
                request.setAttribute("language", language);
                request.setAttribute("roleIpRanges", roleIpRanges);
                request.getRequestDispatcher(templatePath).forward(request, response);

            } else {
                final String header = "Error in AdminIpWhiteList";
                final String msg = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user)
                        .getProperty("error/servlet/AdminIpWhiteList/validate_form_parameters") + "<br>";
                AdminRoles.printErrorMessage(request, response, header, msg);
            }
        }
    }

    private List<RoleIpRange> getRoleIpRanges() {
        final String sqlCommand = "SELECT id, " + IS_ADMIN + ", " + IP_RANGE_FROM + ", " + IP_RANGE_TO
                + " FROM " + TABLE_NAME
                + " ORDER BY id";
        final CollectionHandler<RoleIpRange, List<RoleIpRange>> collectionHandler =
                new CollectionHandler<RoleIpRange, List<RoleIpRange>>(new ArrayList<RoleIpRange>(), rowTransformer);
        final DatabaseCommand<List<RoleIpRange>> queryCommand = new SqlQueryDatabaseCommand<List<RoleIpRange>>(
                sqlCommand, new Object[]{}, collectionHandler
        );

        return Imcms.getServices().getDatabase().execute(queryCommand);
    }

    private String getAdminTemplatePath(String templateFileName, UserDomainObject user) {
        return "/WEB-INF/templates/" + user.getLanguageIso639_2() + "/admin/" + templateFileName;
    }

    public final class RoleIpRange {
        private final int id;
        private final boolean isAdmin;
        private final String ipFrom;
        private final String ipTo;

        private RoleIpRange(int id, boolean isAdmin, String ipFrom, String ipTo) {
            this.id = id;
            this.isAdmin = isAdmin;
            this.ipFrom = ipFrom;
            this.ipTo = ipTo;
        }

        public int getId() {
            return id;
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
    }

}
