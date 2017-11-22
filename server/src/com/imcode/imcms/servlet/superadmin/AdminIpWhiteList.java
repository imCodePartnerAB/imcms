package com.imcode.imcms.servlet.superadmin;

import com.imcode.db.DatabaseCommand;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.commands.SqlQueryDatabaseCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.db.handlers.CollectionHandler;
import com.imcode.db.handlers.RowTransformer;
import com.imcode.imcms.servlet.RoleIpRange;
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
import java.net.UnknownHostException;
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
    private static final String WARN_DEL_IP_TEMPLATE = "AdminIpWhiteList_Delete.jsp";

    private static final String TABLE_NAME = "imcms_ip_white_list";
    private static final String IS_ADMIN = "is_admin";
    private static final String IP_RANGE_FROM = "ip_range_from";
    private static final String IP_RANGE_TO = "ip_range_to";

    private final InetAddressValidator ipValidator = InetAddressValidator.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final UserDomainObject user = Utility.getLoggedOnUser(request);

        if (!user.isSuperAdmin()) {
            AdminIpAccess.printNonAdminError(user, request, response, getClass());
            return;
        }

        setRangesAndViewDataAndForwardTo(WHITE_LIST_TEMPLATE, request, response, user);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final UserDomainObject user = Utility.getLoggedOnUser(request);

        if (!user.isSuperAdmin()) {
            AdminIpAccess.printNonAdminError(user, request, response, getClass());

        } else if (request.getParameter("ADD_IP_RANGE") != null) {
            setViewDataAndForwardTo(WHITE_LIST_ADD_TEMPLATE, request, response, user);

        } else if (request.getParameter("ADD_NEW_IP_RANGE") != null) {
            addNewIpRange(request, response);

        } else if ((request.getParameter("CANCEL_ADD_IP") != null)
                || (request.getParameter("IP_CANCEL_DELETE") != null))
        {
            doGet(request, response);

        } else if (request.getParameter("UPDATE_IP_RANGE") != null) {
            updateIpRange(request, response);

        } else if (request.getParameter("IP_WARN_DELETE") != null) {
            showDeletionWarning(request, response, user);

        } else if (request.getParameter("DEL_IP_RANGE") != null) {
            deleteIpRange(request, response);
        }
    }

    private void showDeletionWarning(HttpServletRequest request, HttpServletResponse response, UserDomainObject user)
            throws ServletException, IOException {

        final String[] editIpRangeIds = request.getParameterValues("EDIT_IP_RANGE_ID");

        if (editIpRangeIds == null || editIpRangeIds.length == 0) {
            doGet(request, response);
            return;
        }

        request.setAttribute("DELETE_IP_RANGE_ID", editIpRangeIds);
        setViewDataAndForwardTo(WARN_DEL_IP_TEMPLATE, request, response, user);
    }

    private void deleteIpRange(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        final String[] deleteIpRangeIdsStr = request.getParameterValues("DELETE_IP_RANGE_ID");

        if (deleteIpRangeIdsStr == null || deleteIpRangeIdsStr.length == 0) {
            doGet(request, response);
            return;
        }

        final Integer[] deleteIpRangeIds = new Integer[deleteIpRangeIdsStr.length];
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE";

        for (int i = 0; i < deleteIpRangeIdsStr.length; i++) {
            if (i != 0) {
                sql = sql.concat(" OR");
            }

            sql = sql.concat(" id = ?");

            deleteIpRangeIds[i] = Integer.parseInt(deleteIpRangeIdsStr[i]);
        }

        Imcms.getServices().getDatabase().execute(new SqlUpdateCommand(sql, deleteIpRangeIds));
        doGet(request, response);
    }

    private void updateIpRange(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        final String[] editIpRangeIds = request.getParameterValues("EDIT_IP_RANGE_ID");

        if ((editIpRangeIds == null) || (editIpRangeIds.length == 0)) {
            doGet(request, response);
            return;
        }

        for (String editIpRangeId : editIpRangeIds) {
            final int rangeId = Integer.parseInt(editIpRangeId);
            final String ipFrom = request.getParameter("IP_START" + rangeId);
            final String ipTo = request.getParameter("IP_END" + rangeId);

            if (ipValidator.isValidInet4Address(ipFrom)
                    && ipValidator.isValidInet4Address(ipTo)
                    && isIpFromLessThanIpTo(ipFrom, ipTo))
            {
                final String sql = "UPDATE " + TABLE_NAME
                        + " SET " + IP_RANGE_FROM + " = ?,"
                        + " " + IP_RANGE_TO + " = ?"
                        + " WHERE id = ?";

                final Object[] params = {ipFrom, ipTo, rangeId};

                log.info("Updating IP range with id " + rangeId + " from " + ipFrom + " to " + ipTo);

                Imcms.getServices().getDatabase().execute(new SqlUpdateCommand(sql, params));
            }
        }

        doGet(request, response);
    }

    private void addNewIpRange(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        final String ipFrom = request.getParameter("IP_START");
        final String ipTo = request.getParameter("IP_END");

        if (ipValidator.isValidInet4Address(ipFrom)
                && ipValidator.isValidInet4Address(ipTo)
                && isIpFromLessThanIpTo(ipFrom, ipTo))
        {
            final String isAdmin = String.valueOf(request.getParameter("IS_ADMIN"));
            final String[][] commandParams = new String[][]{
                    {IS_ADMIN, isAdmin},
                    {IP_RANGE_FROM, ipFrom},
                    {IP_RANGE_TO, ipTo},
            };

            log.info("Adding new IP range from " + ipFrom + " to " + ipTo + " for "
                    + ("1".equals(isAdmin) ? "super" : "non") + "-admin roles.");

            Imcms.getServices().getDatabase().execute(new InsertIntoTableDatabaseCommand(TABLE_NAME, commandParams));

            doGet(request, response);

        } else {
            doError(request, response);
        }
    }

    private void setViewDataAndForwardTo(String templateName,
                                         HttpServletRequest request,
                                         HttpServletResponse response,
                                         UserDomainObject user) throws ServletException, IOException {

        final String templatePath = getAdminTemplatePath(templateName, user);
        final String language = user.getLanguageIso639_2();

        request.setAttribute("contextPath", request.getContextPath());
        request.setAttribute("language", language);
        request.getRequestDispatcher(templatePath).forward(request, response);
    }

    private void setRangesAndViewDataAndForwardTo(String templateName,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  UserDomainObject user) throws ServletException, IOException {

        final String templatePath = getAdminTemplatePath(templateName, user);
        final List<RoleIpRange> roleIpRanges = getRoleIpRanges();
        final String language = user.getLanguageIso639_2();

        request.setAttribute("contextPath", request.getContextPath());
        request.setAttribute("language", language);
        request.setAttribute("roleIpRanges", roleIpRanges);
        request.getRequestDispatcher(templatePath).forward(request, response);
    }

    private void doError(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final UserDomainObject user = Utility.getLoggedOnUser(request);
        final String header = "Error in AdminIpWhiteList";
        final String msg = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user)
                .getProperty("error/servlet/AdminIpWhiteList/validate_form_parameters") + "<br>";

        AdminRoles.printErrorMessage(request, response, header, msg);
    }

    private boolean isIpFromLessThanIpTo(String ipFrom, String ipTo) throws UnknownHostException {
        final int firstSegmentFrom = Integer.parseInt(ipFrom.split("\\.")[0]);
        final int firstSegmentTo = Integer.parseInt(ipTo.split("\\.")[0]);

        return firstSegmentFrom < firstSegmentTo;
    }

    public static List<RoleIpRange> getRoleIpRanges() {
        final RowTransformer<RoleIpRange> rowTransformer = new RowTransformer<RoleIpRange>() {
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

}
