package com.imcode.imcms.servlet.admin;

import imcode.server.ApplicationServer;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.HttpSessionUtils;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserBrowser extends HttpServlet {

    public static final String REQUEST_PARAMETER__USER_ID = "user_id";
    static final String REQUEST_ATTRIBUTE__USER = "user";
    public final static String REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE = "userBrowse";
    public static final String REQUEST_PARAMETER__FORWARD_RETURN_URL = "forwardreturnurl";
    public static final String REQUEST_PARAMETER__SHOW_USERS_BUTTON = "showUsers";
    public static final String REQUEST_PARAMETER__SEARCH_STRING = "searchstring";
    public static final String REQUEST_PARAMETER__INCLUDE_INACTIVE_USERS = "includeInactive";
    public static final String REQUEST_ATTRIBUTE__FORM_DATA = "formData";
    private static final String JSP__USER_BROWSER = "/jsp/userbrowser.jsp";
    public static final String REQUEST_PARAMETER__SELECT_USER_BUTTON = "selectUserButton";
    public static final int SELECT_BUTTON__SELECT_USER = 0;
    public static final int SELECT_BUTTON__EDIT_USER = 1;
    public static final String REQUEST_PARAMETER__ADD_USER = "addUser";

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        UserFinder userFinder = (UserFinder)HttpSessionUtils.getObjectFromSessionWithKeyInRequest( request, REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE );
        if ( null != request.getParameter( REQUEST_PARAMETER__SHOW_USERS_BUTTON ) ) {
            listUsers( request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__SELECT_USER_BUTTON ) ) {
            UserDomainObject selectedUser = getSelectedUserFromRequest( request );
            if (null == selectedUser && !userFinder.isNullSelectable()) {
                listUsers( request, response );
            } else {
                userFinder.setSelectedUser( selectedUser );
                String forwardReturnUrl = userFinder.getForwardReturnUrl();
                request.getRequestDispatcher( forwardReturnUrl ).forward( request, response );
            }
        } else if ( null != request.getParameter( REQUEST_PARAMETER__ADD_USER ) && userFinder.isUsersAddable() ) {
            response.sendRedirect( "AdminUserProps?ADD_USER=true" );
        }
    }

    private void listUsers( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        String searchString = request.getParameter( REQUEST_PARAMETER__SEARCH_STRING );
        boolean includeInactiveUsers = null != request.getParameter( REQUEST_PARAMETER__INCLUDE_INACTIVE_USERS );
        ImcmsAuthenticatorAndUserMapper userMapper = ApplicationServer.getIMCServiceInterface().getImcmsAuthenticatorAndUserAndRoleMapper();
        UserDomainObject[] users = userMapper.findUsersByNamePrefix( searchString, includeInactiveUsers );
        FormData formData = new FormData();
        formData.setSearchString( searchString );
        formData.setUsers( users );
        formData.setIncludeInactiveUsers( includeInactiveUsers );
        forwardToJsp( request, response, formData );
    }

    static void forwardToJsp( HttpServletRequest request, HttpServletResponse response, FormData formData ) throws ServletException, IOException {
        request.setAttribute( REQUEST_ATTRIBUTE__FORM_DATA, formData );
        UserDomainObject user = Utility.getLoggedOnUser( request );
        String userLanguage = user.getLanguageIso639_2();
        request.getRequestDispatcher( "/imcms/" + userLanguage + JSP__USER_BROWSER ).forward( request, response );
    }

    private UserDomainObject getSelectedUserFromRequest( HttpServletRequest request ) {
        ImcmsAuthenticatorAndUserMapper userMapper = ApplicationServer.getIMCServiceInterface().getImcmsAuthenticatorAndUserAndRoleMapper();
        String userIdStr = request.getParameter( REQUEST_PARAMETER__USER_ID );
        if ( null == userIdStr ) {
            return null;
        }
        int userId = Integer.parseInt( userIdStr );
        UserDomainObject user = userMapper.getUser( userId );
        return user;
    }

    public static class FormData {

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
    }

}
