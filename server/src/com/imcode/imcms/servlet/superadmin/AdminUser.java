package com.imcode.imcms.servlet.superadmin;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import imcode.external.diverse.*;
import imcode.server.*;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import org.apache.log4j.*;
import com.imcode.imcms.servlet.superadmin.AdminError;
import com.imcode.imcms.servlet.superadmin.Administrator;

public class AdminUser extends Administrator {

    private final static String HTML_TEMPLATE = "AdminChangeUser.htm";
    private static Category log = Logger.getInstance( AdminUser.class.getName() );
    private String CHANGE_EXTERNAL_USER_URL = "/adminuser/changeexternaluser.jsp";

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        // check if user is a Useradmin, adminRole = 2
        UserDomainObject user = Utility.getLoggedOnUser( req );
        boolean isUseradmin = imcref.checkUserAdminrole( user.getUserId(), 2 );

        // check if user is a Superadmin, adminRole = 1
        boolean isSuperadmin = imcref.checkUserAdminrole( user.getUserId(), 1 );

        // Lets verify that the user is an admin, otherwise throw him out.
        if ( !isSuperadmin && !isUseradmin ) {
            String header = "Error in AdminCounter.";
            String msg = "The user is not an administrator." + "<BR>";
            this.log( header + msg );
            new AdminError( req, res, header, msg );
            return;
        }

        VariableManager vm = new VariableManager();

        // Lets get the category from the request Object.
        String category = req.getParameter( "category" );
        if ( category == null ) {
            category = ""; // all category
        }

        if ( ( "All_Choice" ).equals( category ) ) {
            category = "-1";
        }

        String lang_prefix = user.getLangPrefix();

        // Lets get all USERTYPES from DB
        String[] userTypes = imcref.sqlProcedure( "GetUserTypes", new String[]{lang_prefix} );
        Vector userTypesV = new Vector( java.util.Arrays.asList( userTypes ) );
        String user_type = Html.createOptionList( category, userTypesV );
        vm.addProperty( "USER_TYPES", user_type );


        // Lets get all USERS from DB with firstname or lastname or login name like the searchString

        // parameter to db
        // @showAll = 1 : all users don't care about serchstring
        // @showAll = 0 : only users like serchstring
        // @active = 1 : only active users (where active=1)
        // @active = 0 : all users  (where active= 0 or 1)

        String searchString = req.getParameter( "search" );
        int showAll = 0;
        if ( searchString == null ) {
            searchString = "";
            showAll = 1;
        }
        String active = ( req.getParameter( "active" ) == null ) ? "1" : req.getParameter( "active" );
        String activeChecked = ( "0".equals( active ) ) ? "checked" : "";
        if ( req.getParameter( "showUsers" ) != null ) {
            String[] usersArr = imcref.sqlProcedure( "GetCategoryUsers", new String[]{category, searchString, "" + user.getUserId(), "" + showAll, active} );

            Vector usersV = new Vector( java.util.Arrays.asList( usersArr ) );
            String usersOption = Html.createOptionList( "", usersV );
            vm.addProperty( "USERS_MENU", usersOption );
            vm.addProperty( "active", activeChecked );
            vm.addProperty( "searchstring", req.getParameter( "search" ) );
        } else {
            vm.addProperty( "USERS_MENU", "" );
            vm.addProperty( "active", activeChecked );
            vm.addProperty( "searchstring", "" );
        }

        //create the page
        this.sendHtml( req, res, vm, HTML_TEMPLATE );

    } // End doGet

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        HttpSession session = req.getSession( false );
        try {
            session.removeAttribute( "userToChange" );
            session.removeAttribute( "next_url" );
            session.removeAttribute( "Ok_phoneNumbers" );

        } catch ( IllegalStateException ise ) {
            log( "session has been invalidated so no need to remove parameters" );
        }

        // check if user is a Useradmin, adminRole = 2
        UserDomainObject user = Utility.getLoggedOnUser( req );
        boolean isUseradmin = imcref.checkUserAdminrole( user.getUserId(), 2 );

        // check if user is a Superadmin, adminRole = 1
        boolean isSuperadmin = imcref.checkAdminRights( user );

        // Lets check if the user is an admin, otherwise throw him out.
        if ( !isSuperadmin && !isUseradmin ) {
            String header = "Error in AdminCounter.";
            String msg = "The user is not an administrator." + "<BR>";
            this.log( header + msg );
            new AdminError( req, res, header, msg );
            return;
        }

        if ( req.getParameter( "searchstring" ) != null ) {
            String active = ( req.getParameter( "active" ) == null ) ? "1" : req.getParameter( "active" );
            res.sendRedirect( "AdminUser?search=" + req.getParameter( "searchstring" ).trim().replaceAll( "'", "''" ) + "&category=" + req.getParameter( "user_categories" ) + "&active=" + active + "&showUsers=true" );
            return;
        }

        if ( req.getParameter( "ADD_USER" ) != null ) {
            redirectAddUser( res );
        } else if ( req.getParameter( "CHANGE_USER" ) != null ) {

            String userToChangeId = getCurrentUserId( req, res );
            UserDomainObject userToChange = imcref.getImcmsAuthenticatorAndUserAndRoleMapper().getUser( Integer.parseInt( userToChangeId ) );

            if ( !userToChange.isImcmsExternal() ) {
                redirectChangeUser( req, res, imcref, user, isUseradmin, session, userToChangeId );
            } else {
                String queryString = "?" + java.net.URLEncoder.encode( WebAppGlobalConstants.USER_LOGIN_NAME_PARAMETER_NAME, "UTF-8" ) + "=" + java.net.URLEncoder.encode( userToChange.getLoginName(), "UTF-8" );
                RequestDispatcher rd = req.getRequestDispatcher( CHANGE_EXTERNAL_USER_URL + queryString );
                rd.forward( req, res );
            }
        } else if ( req.getParameter( "DELETE_USER" ) != null ) {
        } else if ( req.getParameter( "GO_BACK" ) != null ) {
            res.sendRedirect( "AdminManager" );
        } else {
            doGet( req, res );
        }
    } // end HTTP POST

    private void redirectChangeUser( HttpServletRequest req, HttpServletResponse res, IMCServiceInterface imcref, UserDomainObject user, boolean useradmin, HttpSession session, String userToChangeId ) throws IOException {
        // ******* GENERATE AN CHANGE_USER PAGE**********
        log( "Change_User" );

        // return if we don´t get a user
        if ( userToChangeId != null ) {
            // Lets check if the user has right to do changes
            // only if he is an superadmin, useradmin or if he try to change his own values
            // otherwise throw him out.
            if ( imcref.checkAdminRights( user ) == false && !useradmin && !userToChangeId.equals( "" + user.getUserId() ) ) {
                String header = "Error in AdminCounter.";
                String msg = "The user has no rights to change user values." + "<BR>";
                this.log( header + msg );
                new AdminError( req, res, header, msg );
            } else {
                // get a user object by userToChangeId
                session.setAttribute( "userToChange", userToChangeId );
                // Lets redirect to AdminUserProps and get the HTML page to change a user.
                res.sendRedirect( "AdminUserProps?CHANGE_USER=true" );
            }
        }
    }

    private void redirectAddUser( HttpServletResponse res ) throws IOException {
        log( "Add_User" );

        // Lets redirect to AdminUserProps and get the HTML page to add a new user.
        res.sendRedirect( "AdminUserProps?ADD_USER=true" );
    }

    /**
     * Returns a String, containing the userID in the request object.If something failes,
     * a error page will be generated and null will be returned.
     */

    private String getCurrentUserId( HttpServletRequest req, HttpServletResponse res ) throws IOException {

        String userId = req.getParameter( "user_Id" );

        // Get the session
        HttpSession session = req.getSession( false );

        if ( userId == null ) {
            // Lets get the userId from the Session Object.
            userId = (String)session.getAttribute( "userToChange" );

        }

        if ( userId == null ) {
            String header = "ChangeUser error. ";
            String msg = "No user_id was available." + "<BR>";
            this.log( header + msg );
            new AdminError( req, res, header, msg );
            return null;
        } else {
            this.log( "AnvändarId=" + userId );
        }

        return userId;
    } // End getCurrentUserId

    public void log( String str ) {
        log.debug( "AdminUser: " + str );
    }

}
