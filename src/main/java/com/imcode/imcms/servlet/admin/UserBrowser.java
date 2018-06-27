package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.SaveException;
import com.imcode.imcms.api.User;
import com.imcode.imcms.api.UserAlreadyExistsException;
import com.imcode.imcms.api.UserService;
import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.servlet.superadmin.UserEditorPage;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.PhoneNumber;
import imcode.server.user.PhoneNumberType;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.HttpSessionUtils;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserBrowser extends HttpServlet {

    public static final String REQUEST_PARAMETER__USER_ID = "user_id";
    public static final String REQUEST_PARAMETER__ROLE_ID = "role_id";
    public final static String REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE = "userBrowse";
    public static final String REQUEST_PARAMETER__SHOW_USERS_BUTTON = "showUsers";
    public static final String REQUEST_PARAMETER__SEARCH_STRING = "searchstring";
    public static final String REQUEST_PARAMETER__INCLUDE_INACTIVE_USERS = "includeInactive";
    public static final String REQUEST_ATTRIBUTE__FORM_DATA = "formData";
    public static final String REQUEST_PARAMETER__SELECT_USER_BUTTON = "selectUserButton";
    public static final String REQUEST_PARAMETER__ARCHIVE_USER_BUTTON = "archiveUserButton";
    public static final String REQUEST_PARAMETER__ADD_USER = "addUser";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "cancel";
    private static final String JSP__USER_BROWSER = "/jsp/userbrowser.jsp";
    private static final LocalizedMessage ERROR__USER_ALREADY_EXISTS = new LocalizedMessage("error/servlet/AdminUserProps/username_already_exists");

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        UserFinder userFinder = (UserFinder) HttpSessionUtils.getSessionAttributeWithNameInRequest(request, REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE);
        if (null == userFinder) {
            Utility.redirectToStartDocument(request, response);
        } else if (null != request.getParameter(REQUEST_PARAMETER__SHOW_USERS_BUTTON)) {
            listUsers(request, response);
        } else if (null != request.getParameter(REQUEST_PARAMETER__SELECT_USER_BUTTON)) {
            UserDomainObject selectedUser = getSelectedUserFromRequest(request);
            if (null == selectedUser && !userFinder.isNullSelectable()) {
                listUsers(request, response);
            } else {
                userFinder.selectUser(selectedUser, request, response);
            }
        } else if (null != request.getParameter(REQUEST_PARAMETER__CANCEL_BUTTON)) {
            userFinder.cancel(request, response);

        } else if (null != request.getParameter(REQUEST_PARAMETER__ARCHIVE_USER_BUTTON)) {
            UserDomainObject selectedUser = getSelectedUserFromRequest(request);
            if (null == selectedUser && !userFinder.isNullSelectable()) {
                listUsers(request, response);
            } else {
                ContentManagementSystem contentManagementSystem = ContentManagementSystem.fromRequest(request);
                UserService userService = contentManagementSystem.getUserService();
                String userLoginName = selectedUser.getLoginName();
                User user = userService.getUser(userLoginName);
                user.setActive(false);
                try {
                    userService.saveUser(user);
                    listUsers(request, response);
                } catch (SaveException e) {
                    e.printStackTrace();
                    listUsers(request, response);
                }
            }
        } else if (null != request.getParameter(REQUEST_PARAMETER__ADD_USER) && userFinder.isUsersAddable()) {
            goToCreateUserPage(userFinder, request, response);
        }
    }

    private void goToCreateUserPage(final UserFinder userFinder, HttpServletRequest request,
                                    HttpServletResponse response) throws ServletException, IOException {

        final DispatchCommand returnCommand = userFinder::forward;
        final UserDomainObject newUser = new UserDomainObject();
        final UserEditorPage userEditorPage = new UserEditorPage(newUser, null, returnCommand);

        userEditorPage.setOkCommand((request1, response1) -> {
            try {
                String phoneNumber = request1.getParameter(UserEditorPage.REQUEST_PARAMETER__EDITED_PHONE_NUMBER);
                phoneNumber = StringUtils.trimToNull(phoneNumber);

                if (phoneNumber != null) {
                    String numberTypeIdStr = request1.getParameter(UserEditorPage.REQUEST_PARAMETER__PHONE_NUMBER_TYPE_ID);
                    int numberTypeId = NumberUtils.toInt(numberTypeIdStr, 0);
                    PhoneNumber number = new PhoneNumber(phoneNumber, PhoneNumberType.getPhoneNumberTypeById(numberTypeId));
                    newUser.addPhoneNumber(number);
                }

                Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().addUser(newUser);
                returnCommand.dispatch(request1, response1);
            } catch (UserAlreadyExistsException e) {
                userEditorPage.setErrorMessage(ERROR__USER_ALREADY_EXISTS);
                userEditorPage.forward(request1, response1);
            }
        });

        userEditorPage.forward(request, response);
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserBrowserPage userBrowserPage = createPageFromRequest(request);
        userBrowserPage.forward(request, response);
    }

    private UserBrowserPage createPageFromRequest(HttpServletRequest request) {
        UserDomainObject loggedOnUser = Utility.getLoggedOnUser(request);
        ImcmsAuthenticatorAndUserAndRoleMapper userMapperAndRole = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        boolean includeInactiveUsers = null != request.getParameter(REQUEST_PARAMETER__INCLUDE_INACTIVE_USERS);
        String searchString = request.getParameter(REQUEST_PARAMETER__SEARCH_STRING);
        String[] selectedRoleIds = request.getParameter(REQUEST_PARAMETER__ROLE_ID) != null ? request.getParameterValues(REQUEST_PARAMETER__ROLE_ID) : new String[]{};
        RoleDomainObject[] selectedRoles = selectedRoleIds.length > 0 ? new RoleDomainObject[selectedRoleIds.length] : new RoleDomainObject[0];
        UserDomainObject[] users = userMapperAndRole.findUsersByNamePrefix(searchString, includeInactiveUsers);
        for (int i = 0; i < selectedRoleIds.length; i++) {
            selectedRoles[i] = userMapperAndRole.getRoleById(Integer.parseInt(selectedRoleIds[i]));
        }
        if (selectedRoles.length > 0) {
            List<UserDomainObject> usersList = new ArrayList<>();
            for (UserDomainObject user : users) {
                boolean hasRole = false;
                for (RoleDomainObject selectedRole : selectedRoles) {
                    hasRole = user.hasRoleId(selectedRole.getId());
                    if (hasRole) {
                        break;
                    }
                }
                if (hasRole) {
                    usersList.add(user);
                }
            }

            users = usersList.toArray(new UserDomainObject[0]);
        }

        if (loggedOnUser.isUserAdminAndCanEditAtLeastOneRole()) {
            users = getUsersWithUseradminPermissibleRoles(loggedOnUser, users);
        }

        UserBrowserPage userBrowserPage = new UserBrowserPage();
        userBrowserPage.setSearchString(searchString);
        userBrowserPage.setUsers(users);
        userBrowserPage.setSelectedRoles(selectedRoles);
        userBrowserPage.setIncludeInactiveUsers(includeInactiveUsers);
        return userBrowserPage;
    }

    private UserDomainObject[] getUsersWithUseradminPermissibleRoles(UserDomainObject loggedOnUser, UserDomainObject[] users) {
        List<UserDomainObject> userList = new ArrayList<>();
        RoleId[] useradminPermissibleRoles = loggedOnUser.getUserAdminRoleIds();
        for (UserDomainObject user : users) {
            for (RoleId useradminPermissibleRole : useradminPermissibleRoles) {
                if (user.hasRoleId(useradminPermissibleRole)) {
                    userList.add(user);
                    break;
                }
            }
        }
        return userList.toArray(new UserDomainObject[0]);
    }

    private UserDomainObject getSelectedUserFromRequest(HttpServletRequest request) {
        ImcmsAuthenticatorAndUserAndRoleMapper userMapperAndRole = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        String userIdStr = request.getParameter(REQUEST_PARAMETER__USER_ID);
        if (null == userIdStr) {
            return null;
        }
        int userId = Integer.parseInt(userIdStr);
        return userMapperAndRole.getUser(userId);
    }

    public static class UserBrowserPage {

        UserDomainObject[] users = new UserDomainObject[0];
        String searchString = "";
        RoleDomainObject[] selectedRoles = new RoleDomainObject[]{};
        private boolean includeInactiveUsers;

        public String getSearchString() {
            return searchString;
        }

        public void setSearchString(String searchString) {
            this.searchString = searchString;
        }

        public UserDomainObject[] getUsers() {
            return users;
        }

        public void setUsers(UserDomainObject[] users) {
            this.users = users;
        }

        public RoleDomainObject[] getSelectedRoles() {
            return this.selectedRoles;
        }

        public void setSelectedRoles(RoleDomainObject[] selectedRoles) {
            this.selectedRoles = selectedRoles;
        }

        public boolean isIncludeInactiveUsers() {
            return includeInactiveUsers;
        }

        public void setIncludeInactiveUsers(boolean includeInactiveUsers) {
            this.includeInactiveUsers = includeInactiveUsers;
        }

        public void forward(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            request.setAttribute(REQUEST_ATTRIBUTE__FORM_DATA, this);
            UserDomainObject user = Utility.getLoggedOnUser(request);
            String userLanguage = user.getLanguage();
            request.getRequestDispatcher("/imcms/" + userLanguage + JSP__USER_BROWSER).forward(request, response);
        }
    }
}
