package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
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
        Page page = createPageFromRequest( request );
        page.forward( request, response );
    }

    private Page createPageFromRequest( HttpServletRequest request ) {
        ImcmsAuthenticatorAndUserMapper userMapper = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        boolean includeInactiveUsers = null != request.getParameter( REQUEST_PARAMETER__INCLUDE_INACTIVE_USERS );
        String searchString = request.getParameter( REQUEST_PARAMETER__SEARCH_STRING );
        UserDomainObject[] users = userMapper.findUsersByNamePrefix( searchString, includeInactiveUsers );
        Page page = new Page();
        page.setSearchString( searchString );
        page.setUsers( users );
        page.setIncludeInactiveUsers( includeInactiveUsers );
        return page;
    }

    private UserDomainObject getSelectedUserFromRequest( HttpServletRequest request ) {
        ImcmsAuthenticatorAndUserMapper userMapper = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        String userIdStr = request.getParameter( REQUEST_PARAMETER__USER_ID );
        if ( null == userIdStr ) {
            return null;
        }
        int userId = Integer.parseInt( userIdStr );
        UserDomainObject user = userMapper.getUser( userId );
        return user;
    }

    public static class Page {

        UserFinder userFinder = new UserFinder() ;
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
