package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.util.HttpSessionUtils;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class UserBrowser extends HttpServlet {

    public static final String REQUEST_PARAMETER__USER_ID = "user_id";
    public final static String REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE = "userBrowse";
    public static final String REQUEST_PARAMETER__SHOW_USERS_BUTTON = "showUsers";
    public static final String REQUEST_PARAMETER__SEARCH_STRING = "searchstring";
    public static final String REQUEST_PARAMETER__INCLUDE_INACTIVE_USERS = "includeInactive";
    public static final String REQUEST_ATTRIBUTE__FORM_DATA = "formData";
    private static final String JSP__USER_BROWSER = "/jsp/userbrowser.jsp";
    public static final String REQUEST_PARAMETER__SELECT_USER_BUTTON = "selectUserButton";
    public static final String REQUEST_PARAMETER__ADD_USER = "addUser";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "cancel";

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        UserFinder userFinder = (UserFinder)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE );
        if ( null != request.getParameter( REQUEST_PARAMETER__SHOW_USERS_BUTTON ) ) {
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
            ;
        } else if ( null != request.getParameter( REQUEST_PARAMETER__ADD_USER ) && userFinder.isUsersAddable() ) {
            response.sendRedirect( "AdminUserProps?ADD_USER=true" );
        }
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
        UserDomainObject[] users = userMapperAndRole.findUsersByNamePrefix( searchString, includeInactiveUsers );
        if (loggedOnUser.isUserAdmin()){
            users = getUsersWithUseradminPermissibleRoles(userMapperAndRole, loggedOnUser, users);
        }
        UserBrowserPage userBrowserPage = new UserBrowserPage();
        userBrowserPage.setSearchString( searchString );
        userBrowserPage.setUsers( users );
        userBrowserPage.setIncludeInactiveUsers( includeInactiveUsers );
        return userBrowserPage;
    }

    private UserDomainObject[] getUsersWithUseradminPermissibleRoles(ImcmsAuthenticatorAndUserAndRoleMapper userMapperAndRole, UserDomainObject loggedOnUser, UserDomainObject[] users) {
        List userList = new ArrayList();
        RoleDomainObject[] useradminPermissibleRoles = userMapperAndRole.getUseradminPermissibleRoles( loggedOnUser );
        for( int i=0; i < users.length; i++){
            for( int k=0; k < useradminPermissibleRoles.length; k++){
                if( users[i].hasRole( useradminPermissibleRoles[k] ) ){
                    userList.add(users[i]);
                }
            }
        }
        users = (UserDomainObject[])userList.toArray(new UserDomainObject[userList.size()]);
        return users;
    }

    private UserDomainObject getSelectedUserFromRequest( HttpServletRequest request ) {
        ImcmsAuthenticatorAndUserAndRoleMapper userMapperAndRole = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        String userIdStr = request.getParameter( REQUEST_PARAMETER__USER_ID );
        if ( null == userIdStr ) {
            return null;
        }
        int userId = Integer.parseInt( userIdStr );
        UserDomainObject user = userMapperAndRole.getUser( userId );
        return user;
    }

    public static class UserBrowserPage {

        UserDomainObject[] users = new UserDomainObject[0];
        String searchString = "";
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
