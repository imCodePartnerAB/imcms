package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.db.StringArrayResultSetHandler;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.*;
import imcode.util.Html;
import imcode.util.Utility;
import org.apache.commons.lang.UnhandledException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class AdminRoles extends HttpServlet {

    private final static Logger LOG = LogManager.getLogger(AdminRoles.class.getName());

    private final static String HTML_ADMIN_ROLES = "AdminRoles_roles.jsp";
    private final static String HTML_ADD_ROLE = "AdminRoles_Add.jsp";
    private final static String HTML_RENAME_ROLE = "AdminRoles_Rename.jsp";
    private final static String HTML_DELETE_ROLE_1 = "AdminRoles_Delete1.jsp";
    private final static String HTML_DELETE_ROLE_2 = "AdminRoles_Delete2.jsp";
    private final static String HTML_EDIT_ROLE = "AdminRoles_Edit.jsp";
    private final static String HTML_EDIT_ROLE_TABLE = "AdminRoles_Edit_Permissions_List.jsp";
    private final static String HTML_EDIT_ROLE_TABLE_ROW = "AdminRoles_Edit_Permission.jsp";

    static void printErrorMessage(HttpServletRequest req, HttpServletResponse res, String header, String msg)
            throws IOException {
        List<String> tagsAndData = new ArrayList<>();
        tagsAndData.add("#ERROR_HEADER#");
        tagsAndData.add(header);
        tagsAndData.add("#ERROR_MESSAGE#");
        tagsAndData.add(msg);

        String fileName = "AdminError.htm";

        // Lets get the path to the admin templates folder
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser(req);

        String html = imcref.getAdminTemplate(fileName, user, tagsAndData);
        Utility.setDefaultHtmlContentType(res);
        res.getWriter().println(html);
    }

    static String createHtml(String htmlFile, HttpServletRequest request, Map<String, String> attributeToValue,
                             HttpServletResponse response) throws ServletException, IOException {

        for (Map.Entry<String, String> attributeToValueSet : attributeToValue.entrySet()) {
            request.setAttribute(attributeToValueSet.getKey(), attributeToValueSet.getValue());
        }

        return Utility.getAdminContents(htmlFile, request, response);
    }

    static void sendHtml(HttpServletRequest req, HttpServletResponse res,
                         Map<String, String> vm, String htmlFile) throws IOException, ServletException {

        String str = createHtml(htmlFile, req, vm, res);
        Utility.setDefaultHtmlContentType(res);
        PrintWriter out = res.getWriter();

        out.println(str);
    }

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Lets verify that the user who tries to add a new user is an admin
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser(req);
        Utility.setDefaultHtmlContentType(res);
        if (!user.isSuperAdmin()) {
            String header = "Error in AdminRoles.";
            Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            LOG.debug(header + "- user is not an administrator");
            printErrorMessage(req, res, header, msg);
            return;
        }

        final Object[] parameters = new String[0];
        String[] rolesArr = imcref.getProcedureExecutor().executeProcedure("RoleAdminGetAll", parameters, new StringArrayResultSetHandler());
        List<String> rolesV = Arrays.asList(rolesArr);

        String opt = Html.createOptionList(rolesV, "");
        req.setAttribute("ROLES_MENU", opt);

        final String adminTemplatePath = imcref.getAdminTemplatePath(HTML_ADMIN_ROLES);
        req.getRequestDispatcher(adminTemplatePath).forward(req, res);

    } // End doGet

    /**
     * POST
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Lets check if the user is an admin, otherwise throw him out.
        ImcmsServices imcref = Imcms.getServices();
        ImcmsAuthenticatorAndUserAndRoleMapper userAndRoleMapper = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        UserDomainObject user = Utility.getLoggedOnUser(req);
        Utility.setDefaultHtmlContentType(res);

        if (!user.isSuperAdmin()) {
            AdminIpAccess.printNonAdminError(user, req, res, getClass());
        } else {// *************** GENERATE THE ADMINISTRATE ROLES PAGE *****************
            if (req.getParameter("VIEW_ADMIN_ROLES") != null) {
                // Lets get all ROLES from DB
                final Object[] parameters = new String[0];
                String[] rolesArr = imcref.getProcedureExecutor().executeProcedure("RoleAdminGetAll", parameters, new StringArrayResultSetHandler());
                List<String> rolesV = Arrays.asList(rolesArr);


                // Lets generate the html page
                Map<String, String> vm = new HashMap<>();
                String opt = Html.createOptionList(rolesV, "");
                vm.put("ROLES_MENU", opt);

                sendHtml(req, res, vm, HTML_ADMIN_ROLES);

                return;
            } else if (req.getParameter("CANCEL") != null || req.getParameter("CANCEL_ROLE_ADMIN") != null) {
                res.sendRedirect("AdminManager");
                return;
            } else if (req.getParameter("CANCEL_ROLE") != null) {
                doGet(req, res);
                return;
            } else if (req.getParameter("VIEW_ADD_NEW_ROLE") != null) {

                // lets adjust the list to fit method cal
                RolePermissionDomainObject[] allRolePermissions = RoleDomainObject.getAllRolePermissions();
                String[][] permissionList = new String[allRolePermissions.length][];

                for (int i = 0; i < permissionList.length; i++) {
                    RolePermissionDomainObject rolePermission = allRolePermissions[i];
                    permissionList[i] = new String[]{"0", "" + rolePermission.getId(),
                            rolePermission.getDescription().toLocalizedString(req)};
                }

                // lets get data on permissions and values
                String permissionComponent = createPermissionComponent(req, res, permissionList);

                Map<String, String> vm = new HashMap<>();
                vm.put("ROLE_PERMISSIONS", permissionComponent);

                sendHtml(req, res, vm, HTML_ADD_ROLE);

                return;
            } else if (req.getParameter("VIEW_RENAME_ROLE") != null) {
                String roleIdStr = req.getParameter("ROLE_ID");
                if (roleIdStr == null) {
                    Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                    String msg = langproperties.getProperty("error/servlet/AdminRoles/rolename_missing") + "<BR>";
                    LOG.debug("Error in rename roles, no role selected for rename");
                    printErrorMessage(req, res, "Roles error", msg);
                    return;
                }
                int roleId = Integer.parseInt(roleIdStr);
                RoleDomainObject role = imcref.getImcmsAuthenticatorAndUserAndRoleMapper().getRoleById(roleId);

                Map<String, String> vm = new HashMap<>();
                vm.put("CURRENT_ROLE_ID", roleIdStr);
                vm.put("CURRENT_ROLE_NAME", "" + role.getName());
                sendHtml(req, res, vm, HTML_RENAME_ROLE);
                return;
            } else if (req.getParameter("VIEW_EDIT_ROLE") != null) {

                String roleIdStr = req.getParameter("ROLE_ID");
                if (roleIdStr == null) {
                    String header = "Error in AdminRoles, edit role";
                    Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                    String msg = langproperties.getProperty("error/servlet/AdminRoles/role_missing") + "<br>";
                    LOG.debug(header + "- select the role to be changed");
                    printErrorMessage(req, res, header, msg);
                    return;
                }

                // dont list superadmin permissions
                if (roleIdStr.equals("0")) {
                    LOG.debug("Error in checking roles: Trying to look att superadmin permissions");
                    printErrorMessage(req, res, "Error in AdminRoles, edit role", "<BR>");
                    return;
                }

                int roleId = Integer.parseInt(roleIdStr);
                RoleDomainObject role = userAndRoleMapper.getRoleById(roleId);
                RolePermissionDomainObject[] allRolePermissions = RoleDomainObject.getAllRolePermissions();
                String[][] permissionList = new String[allRolePermissions.length][];
                for (int i = 0; i < permissionList.length; i++) {
                    RolePermissionDomainObject rolePermission = allRolePermissions[i];
                    int rolePermissionId = rolePermission.getId();
                    permissionList[i] = new String[3];
                    permissionList[i][0] = role.hasPermission(rolePermission) ? "" + rolePermissionId : "0";
                    permissionList[i][1] = "" + rolePermissionId;
                    permissionList[i][2] = "" + rolePermission.getDescription().toLocalizedString(req);
                }

                // lets get data on permissions and values
                String permissionComponent = createPermissionComponent(req, res, permissionList);

                /* create output page */
                Map<String, String> vm = new HashMap<>();
                vm.put("CURRENT_ROLE_NAME", role.getName());
                vm.put("CURRENT_ROLE_ID", roleIdStr);
                vm.put("ROLE_PERMISSIONS", permissionComponent);
                sendHtml(req, res, vm, HTML_EDIT_ROLE);

                return;
            } else if (req.getParameter("ADD_NEW_ROLE") != null) {

                // Lets get the parameters from html page and validate them
                Properties params = getAddRoleParameters(req);
                if (params.values().contains("")) {
                    String header = "Error in AdminRoles ";
                    Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                    String msg = langproperties.getProperty("error/servlet/AdminRoles/new_rolename_missing") + "<br>";
                    LOG.debug(header + "- new rolename missing");
                    printErrorMessage(req, res, header, msg);
                    return;
                }

                // Lets check that the new rolename doesnt exists already in db
                String roleName = params.getProperty("ROLE_NAME");
                if (roleExists(userAndRoleMapper, roleName)) {
                    String header = "Error in AdminRoles.";
                    Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                    String msg = langproperties.getProperty("error/servlet/AdminRoles/rolename_already_exists")
                            + "<br>";
                    LOG.debug(header + "- role name already exists");
                    printErrorMessage(req, res, header, msg);
                    return;
                }

                // lets colect permissions state
                String[] checkedPermissions = req.getParameterValues("PERMISSION_CHECKBOX");
                int permissionValue = collectPermissionsState(checkedPermissions);

                // Lets add the new role into db
                RoleDomainObject role = new RoleDomainObject(roleName);
                role.addUnionOfPermissionIdsToRole(permissionValue);
                try {
                    userAndRoleMapper.saveRole(role);
                } catch (UserAndRoleRegistryException e) {
                    throw new UnhandledException(e);
                }
                doGet(req, res);

                return;
            } else if (req.getParameter("RENAME_ROLE") != null) {

                // Lets get the parameters from html page and validate them
                Properties params = getRenameRoleParameters(req);
                if (params.values().contains("")) {
                    String header = "Error in AdminRoles, rename role ";
                    Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                    String msg = langproperties.getProperty("error/servlet/AdminRoles/new_rolename_missing") + "<br>";
                    LOG.debug(header + "- new role name is missing");
                    printErrorMessage(req, res, header, msg);
                    return;
                }

                String roleName = params.getProperty("ROLE_NAME");
                String roleIdStr = params.getProperty("ROLE_ID");
                int roleId = Integer.parseInt(roleIdStr);
                RoleDomainObject role = userAndRoleMapper.getRoleById(roleId);
                role.setName(roleName);
                try {
                    userAndRoleMapper.saveRole(role);
                } catch (NameTooLongException e) {
                    throw new UnhandledException(e);
                } catch (RoleAlreadyExistsException e) {
                    String header = "Error in AdminRoles.";
                    Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                    String msg = langproperties.getProperty("error/servlet/AdminRoles/rolename_already_exists")
                            + "<br>";
                    LOG.debug(header + "- rolename already exists");
                    printErrorMessage(req, res, header, msg);
                    return;
                }
                doGet(req, res);

                return;
            }


            // ****** VIEW AFFECTED META ID:S WHICH WILL BE AFFECTED OF A DELETE ********

            boolean warnDelRole = false;
            if (req.getParameter("VIEW_DELETE_ROLE") != null) {

                // Lets get the parameters from html page and validate them
                Properties params = getDeleteRoleParameters(req);
                if (params.values().contains("")) {
                    String header = "Error in AdminRoles ";
                    Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                    String msg = langproperties.getProperty("error/servlet/AdminRoles/role_to_delete_missing") + "<br>";
                    LOG.debug(header + "- no role was selected for delete");
                    printErrorMessage(req, res, header, msg);
                    return;
                }

                String roleIdStr = params.getProperty("ROLE_ID");
                int roleId = Integer.parseInt(roleIdStr);
                RoleDomainObject role = userAndRoleMapper.getRoleById(roleId);
                List<DocumentDomainObject> affectedDocuments = imcref.getDocumentMapper().getDocumentsWithPermissionsForRole(role);
                int affectedDocumentsCount = affectedDocuments.size();
                if (affectedDocuments.size() > 50) {
                    affectedDocuments = affectedDocuments.subList(0, 50);
                }

                List<UserDomainObject> affectedUsers = Arrays.asList(userAndRoleMapper.getAllUsersWithRole(role));
                if (affectedUsers.size() > 50) {
                    affectedUsers = affectedUsers.subList(0, 50);
                }

                if (!affectedUsers.isEmpty() || !affectedDocuments.isEmpty()) {

                    // Lets generate the affected users & metaid warning html page
                    String opt = Html.createOptionList(affectedDocuments, doc -> new String[]{"" + doc.getId(), "" + doc.getId()});
                    String users = Html.createOptionList(affectedUsers, user1 -> new String[]{"" + user1.getId(),
                            user1.getLastName() + ", " + user1.getFirstName() + " (" + user1.getLoginName()
                                    + ")"});
                    Map<String, String> vm = new HashMap<>();
                    vm.put("META_ID_LIST", opt);
                    vm.put("USER_ID_LIST", users);
                    vm.put("USER_COUNT", "" + affectedUsers.size());
                    vm.put("ROLE_COUNT", "" + affectedDocumentsCount);
                    vm.put("CURRENT_ROLE_ID", params.getProperty("ROLE_ID"));
                    sendHtml(req, res, vm, HTML_DELETE_ROLE_1);
                    return;
                } else {

                    // Lets generate the last warning html page
                    warnDelRole = true;
                }
            }

            // *************** GENERATE THE LAST DELETE ROLE WARNING PAGE  **********
            if (req.getParameter("WARN_DELETE_ROLE") != null || warnDelRole) {
                // Lets get the parameters from html page and validate them
                Properties params = getDeleteRoleParameters(req);
                if (params.values().contains("")) {
                    String header = "Error in AdminRoles, delete ";
                    Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                    String msg = langproperties.getProperty("error/servlet/AdminRoles/role_to_delete_missing") + "<br>";
                    LOG.debug(header + "- no role was selected for delete");
                    printErrorMessage(req, res, header, msg);
                    return;
                } else {
                    // Lets generate the last warning html page
                    Map<String, String> vm = new HashMap<>();
                    vm.put("CURRENT_ROLE_ID", params.getProperty("ROLE_ID"));
                    sendHtml(req, res, vm, HTML_DELETE_ROLE_2);
                    return;
                }
            }

            // ****** DELETE A ROLE ********
            if (req.getParameter("DELETE_ROLE") != null) {
                // Lets get the parameters from html page and validate them
                Properties params = getDeleteRoleParameters(req);
                if (params.values().contains("")) {
                    String header = "Error in AdminRoles, delete";
                    Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                    String msg = langproperties.getProperty("error/servlet/AdminRoles/role_to_delete_missing") + "<br>";
                    LOG.debug(header + "- no role was selected for delete");
                    printErrorMessage(req, res, header, msg);
                    return;
                } else {
                    String roleIdStr = params.getProperty("ROLE_ID");
                    int roleId = Integer.parseInt(roleIdStr);
                    RoleDomainObject role = userAndRoleMapper.getRoleById(roleId);
                    userAndRoleMapper.deleteRole(role);
                    doGet(req, res);
                    return;
                }
            }

            // ****** UPDATE ROLE PERMISSIONS ********
            if (req.getParameter("UPDATE_ROLE_PERMISSIONS") != null) {

                // Lets check that role_id is corect, not lost or manipulated
                Properties params = getEditRoleParameters(req);
                String[] checkedPermissions = req.getParameterValues("PERMISSION_CHECKBOX");

                if (params.values().contains("")) {
                    String header = "Error in AdminRoles ";
                    Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
                    String msg = langproperties.getProperty("error/servlet/AdminRoles/role_to_delete_missing") + "<br>";
                    LOG.debug(header + "- no role was selected for delete");
                    printErrorMessage(req, res, header, msg);
                } else {
                    int permissionValue = collectPermissionsState(checkedPermissions);
                    // lets update
                    final Object[] parameters = new String[]
                            {params.getProperty("ROLE_ID"),
                                    String.valueOf(permissionValue)};
                    imcref.getProcedureExecutor().executeUpdateProcedure("RoleUpdatePermissions", parameters);
                    doGet(req, res);
                }
            }
        }
    } // end HTTP POST

    private boolean roleExists(ImcmsAuthenticatorAndUserAndRoleMapper userAndRoleMapper, String roleName) {
        return null != userAndRoleMapper.getRoleByName(roleName);
    }

    /**
     * Collects the parameters from the request object
     */

    private Properties getAddRoleParameters(HttpServletRequest req) {
        Properties roleInfoP = new Properties();
        String roleInfo = req.getParameter("ROLE_NAME") != null ? (req.getParameter("ROLE_NAME")) : "";
        roleInfoP.setProperty("ROLE_NAME", roleInfo);
        return roleInfoP;
    }

    /**
     * Collects the parameters from the request object at RENAME process
     */

    private Properties getRenameRoleParameters(HttpServletRequest req) {
        Properties roleInfoP = new Properties();
        String roleId = req.getParameter("ROLE_ID") != null ? req.getParameter("ROLE_ID") : "";
        String roleInfo = !(req.getParameter("ROLE_NAME") == null) ? req.getParameter("ROLE_NAME") : "";
        roleInfoP.setProperty("ROLE_ID", roleId);
        roleInfoP.setProperty("ROLE_NAME", roleInfo);
        return roleInfoP;
    }

    /**
     * Collects the parameters from the request object
     */

    private Properties getDeleteRoleParameters(HttpServletRequest req) {
        Properties roleInfoP = new Properties();
        String roleInfo = req.getParameter("ROLE_ID") != null ? req.getParameter("ROLE_ID") : "";
        roleInfoP.setProperty("ROLE_ID", roleInfo);
        return roleInfoP;
    }

    /**
     * Collects the parameters from the request object at UPDATE process
     */
    private Properties getEditRoleParameters(HttpServletRequest req) {
        Properties roleInfoP = new Properties();

        String roleInfo = req.getParameter("ROLE_ID") == null ? "" : req.getParameter("ROLE_ID");

        roleInfoP.setProperty("ROLE_ID", roleInfo);

        return roleInfoP;
    }

    /* create permissions tag */
    private String createPermissionComponent(HttpServletRequest req, HttpServletResponse response,
                                             String[][] permissionList) throws ServletException, IOException {

        /* create rows of permission */
        StringBuffer permissionTableRows = new StringBuffer();

        /*
         * lets create permission as a component
         * element: 0 = value, 1 = permission_id, 2 = description
         */
        for (String[] aPermissionList : permissionList) {

            String permissionId = aPermissionList[1];
            String description = aPermissionList[2];
            boolean isChecked = !aPermissionList[0].equals("0");

            Map<String, String> vm = new HashMap<>();
            vm.put("PERMISSION_DESCRIPTION", description);
            vm.put("PERMISSON_ID", permissionId);

            if (isChecked) {
                vm.put("PERMISSION_CHECKED", "checked");
            } else {
                vm.put("PERMISSION_CHECKED", "");
            }

            String rowString = createHtml(HTML_EDIT_ROLE_TABLE_ROW, req, vm, response);

            permissionTableRows.append(rowString);

        }

        //create component
        Map<String, String> vmTable = new HashMap<>();
        vmTable.put("PERMISSION_ROWS", permissionTableRows.toString());

        return createHtml(HTML_EDIT_ROLE_TABLE, req, vmTable, response);
    }

    private int collectPermissionsState(String[] checkedPermissions) {
        int permissionValue = 0;

        if (checkedPermissions != null) {

            for (String checkedPermission : checkedPermissions) {
                int permissionId = 0;

                try {
                    permissionId = Integer.parseInt(checkedPermission);
                } catch (NumberFormatException e) {
                    LOG.debug("Error in checking roles: NumberFormatException");
                }

                permissionValue |= permissionId;
            }
        }
        return permissionValue;
    }
}
