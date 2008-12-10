package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.servlet.superadmin.UserEditorPage;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.user.*;
import imcode.util.HttpSessionUtils;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class UserBrowser extends HttpServlet {

    public static final String REQUEST_PARAMETER__USER_ID = "user_id";
    public static final String REQUEST_PARAMETER__ROLE_ID = "role_id";
    public final static String REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE = "userBrowse";
    public static final String REQUEST_PARAMETER__SHOW_USERS_BUTTON = "showUsers";
    public static final String REQUEST_PARAMETER__SEARCH_STRING = "searchstring";
    public static final String REQUEST_PARAMETER__INCLUDE_INACTIVE_USERS = "includeInactive";
    public static final String REQUEST_ATTRIBUTE__FORM_DATA = "formData";
    private static final String JSP__USER_BROWSER = "/jsp/userbrowser.jsp";
    public static final String REQUEST_PARAMETER__SELECT_USER_BUTTON = "selectUserButton";
    public static final String REQUEST_PARAMETER__ADD_USER = "addUser";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "cancel";
    private static final LocalizedMessage ERROR__USER_ALREADY_EXISTS = new LocalizedMessage("error/servlet/AdminUserProps/username_already_exists");

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        UserFinder userFinder = (UserFinder)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE );
        if ( null == userFinder ) {
            Utility.redirectToStartDocument(request, response);
        } else if ( null != request.getParameter( REQUEST_PARAMETER__SHOW_USERS_BUTTON ) ) {
            listUsers( request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__SELECT_USER_BUTTON ) ) {
            UserDomainObject selectedUser = getSelectedUserFromRequest( request );
            if ( null == selectedUser && !userFinder.isNullSelectable() ) {
                listUsers( request, response );
            } else {
                userFinder.selectUser( selectedUser, request, response );
            }
        } else if ( null != request.getParameter( REQUEST_PARAMETER__CANCEL_BUTTON ) ) {
            userFinder.cancel( request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__ADD_USER ) && userFinder.isUsersAddable() ) {
            goToCreateUserPage(userFinder, request, response);
        }
    }

    private void goToCreateUserPage(final UserFinder userFinder, HttpServletRequest request,
                                    HttpServletResponse response) throws ServletException, IOException {
        final DispatchCommand returnCommand = new DispatchCommand() {
            public void dispatch(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
                userFinder.forward(request, response);
            }
        };
        final UserDomainObject newUser = new UserDomainObject();
        final UserEditorPage userEditorPage = new UserEditorPage(newUser, null, returnCommand);
        DispatchCommand saveUserAndReturnCommand = new DispatchCommand() {
            public void dispatch(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
                try {
                    Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().addUser(newUser);
                    returnCommand.dispatch(request, response);
                } catch ( UserAlreadyExistsException e ) {
                    userEditorPage.setErrorMessage(ERROR__USER_ALREADY_EXISTS) ;
                    userEditorPage.forward(request, response);
                }
            }
        };
        userEditorPage.setOkCommand(saveUserAndReturnCommand) ;
        userEditorPage.forward(request, response);
    }

    private void listUsers( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        UserBrowserPage userBrowserPage = createPageFromRequest( request );
        userBrowserPage.forward( request, response );
    }

    private UserBrowserPage createPageFromRequest( HttpServletRequest request ) {
        UserDomainObject loggedOnUser = Utility.getLoggedOnUser( request );
        ImcmsAuthenticatorAndUserAndRoleMapper userMapperAndRole = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        boolean includeInactiveUsers = null != request.getParameter( REQUEST_PARAMETER__INCLUDE_INACTIVE_USERS );
        String searchString = request.getParameter( REQUEST_PARAMETER__SEARCH_STRING );
        String[] selectedRoleIds = request.getParameter(REQUEST_PARAMETER__ROLE_ID) != null ? request.getParameterValues(REQUEST_PARAMETER__ROLE_ID) : new String[] {};
        RoleDomainObject[] selectedRoles = selectedRoleIds.length > 0 ? new RoleDomainObject[selectedRoleIds.length] : new RoleDomainObject[0] ;
        UserDomainObject[] users = userMapperAndRole.findUsersByNamePrefix( searchString, includeInactiveUsers );
        for (int i=0; i < selectedRoleIds.length; i++ ){
            selectedRoles[i] = userMapperAndRole.getRoleById(Integer.parseInt(selectedRoleIds[i]));
        }
        if ( selectedRoles.length > 0 ) {
            List usersList = new ArrayList();
            for (int i=0; i < users.length; i++ ) {
                boolean hasRole = false;
                for (int k = 0; k < selectedRoles.length; k++) {
                    hasRole = users[i].hasRoleId(selectedRoles[k].getId());
                    if(hasRole){break;}
                }
                if(hasRole) { usersList.add(users[i]); }
            }

            users = (UserDomainObject[])usersList.toArray(new UserDomainObject[usersList.size()] );
        }

        if (loggedOnUser.isUserAdminAndCanEditAtLeastOneRole()){
            users = getUsersWithUseradminPermissibleRoles(loggedOnUser, users);
        }

        UserBrowserPage userBrowserPage = new UserBrowserPage();
        userBrowserPage.setSearchString( searchString );
        userBrowserPage.setUsers( users );
        userBrowserPage.setSelectedRoles(selectedRoles);
        userBrowserPage.setIncludeInactiveUsers( includeInactiveUsers );
        return userBrowserPage;
    }

    private UserDomainObject[] getUsersWithUseradminPermissibleRoles(UserDomainObject loggedOnUser, UserDomainObject[] users) {
        List userList = new ArrayList();
        RoleId[] useradminPermissibleRoles = loggedOnUser.getUserAdminRoleIds();
        for( int i=0; i < users.length; i++){
            for( int k=0; k < useradminPermissibleRoles.length; k++){
                if( users[i].hasRoleId( useradminPermissibleRoles[k] ) ){
                    userList.add(users[i]);
                    break ;
                }
            }
        }
        return (UserDomainObject[])userList.toArray(new UserDomainObject[userList.size()]);
    }

    private UserDomainObject getSelectedUserFromRequest( HttpServletRequest request ) {
        ImcmsAuthenticatorAndUserAndRoleMapper userMapperAndRole = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        String userIdStr = request.getParameter( REQUEST_PARAMETER__USER_ID );
        if ( null == userIdStr ) {
            return null;
        }
        int userId = Integer.parseInt( userIdStr );
        return userMapperAndRole.getUser( userId );
    }

    public static class UserBrowserPage {

        UserDomainObject[] users = new UserDomainObject[0];
        String searchString = "";
        RoleDomainObject[] selectedRoles = new RoleDomainObject[] {};
        private boolean includeInactiveUsers;

        public String getSearchString() {
            return searchString;
        }

        public UserDomainObject[] getUsers() {
            return users;
        }

        public void setSearchString( String searchString ) {
            this.searchString = searchString;
        }

        public void setSelectedRoles(RoleDomainObject[] selectedRoles) {
            this.selectedRoles = selectedRoles;
        }

         public RoleDomainObject[] getSelectedRoles() {
             return this.selectedRoles;
         }

        public void setUsers( UserDomainObject[] users ) {
            this.users = users;
        }

        public void setIncludeInactiveUsers( boolean includeInactiveUsers ) {
            this.includeInactiveUsers = includeInactiveUsers;
        }

        public boolean isIncludeInactiveUsers() {
            return includeInactiveUsers;
        }

        public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
            request.setAttribute( REQUEST_ATTRIBUTE__FORM_DATA, this );
            UserDomainObject user = Utility.getLoggedOnUser( request );
            String userLanguage = user.getLanguageIso639_2();
            request.getRequestDispatcher( "/imcms/" + userLanguage + JSP__USER_BROWSER ).forward( request, response );
        }
    }
}
