package com.imcode.imcms.servlet.superadmin;

/*
 *
 * @(#)AdminRoleBelongings.java
 *
 *
 * 2000-10-09
 *
 * Copyright (c)
 *
 */

import imcode.external.diverse.VariableManager;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

/**
 * Takes care of administration of users by roles.
 * <p/>
 * Documents in use:
 * <p/>
 * doGet()
 * AdminRoleBelongings.html, start document
 * <p/>
 * doPost():
 * AdminRoleBelongings_edit.html, page for administrate users to roles
 * AdminRoleBelongings_activate.html, page for de/activate users
 * <p/>
 * Data in webserver config:
 * servermaster_email=abc@test.com
 * <p/>
 * stored procedures in use:
 * - RemoveUserFromRole
 * - RoleAdminGetAll
 * - RoleGetAllApartFromRole
 * - GetAllUsersInList
 * - GetUsersWhoBelongsToRole
 * - AddUserRole
 * - ChangeUserActiveStatus
 * - GetLangPrefixFromId
 *
 * @author Jerker Drottenmyr
 * @version 1.3 17 Oct 2000
 */

public class AdminRoleBelongings extends Administrator {

    private final static Logger log = Logger.getLogger( AdminRoleBelongings.class.getName() );

    private static final String HTML_ADMIN_ROLE_BELONGING = "AdminRoleBelongings.html";
    private static final String HTML_ADMIN_ROLE_BELONGING_EDIT = "AdminRoleBelongings_edit.html";
    private static final String HTML_ADMIN_ROLE_BELONGING_ACTIVATE = "AdminRoleBelongings_activate.html";

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        // Lets verify that the user who tries to add a new user is an admin
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( user.isSuperAdmin() == false ) {
            return;
        }

        // Lets get all ROLES from DB
        String[][] queryResult = imcref.sqlProcedureMulti( "RoleAdminGetAll", new String[0] );

        // Lets generate the html page
        String optionList = createListOfOptions( queryResult );

        VariableManager vm = new VariableManager();
        vm.addProperty( "ROLES_MENU", optionList );

        this.sendHtml( req, res, vm, AdminRoleBelongings.HTML_ADMIN_ROLE_BELONGING );
        return;
    }

    /**
     * POST
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        String eMailServerMaster = imcref.getSystemData().getServerMasterAddress();

        // lets get ready for errors
        String errorHeader = "AdminRoleBelongings";

        // Lets check if the user is an admin, otherwise throw him out.
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( user.isSuperAdmin() == false ) {
            String header = "Error in AdminRoleBelongings.";
            Properties langproperties = imcref.getLanguageProperties( user );
            String msg = langproperties.getProperty( "error/servlet/global/no_administrator" ) + "<br>";
            log.debug( header + "- user is not an administrator" );
            new AdminError( req, res, header, msg );
            return;
        }

        // *************** RETURN TO ADMIN ROLES *****************
        if ( req.getParameter( "CANCEL" ) != null ) {
            res.sendRedirect( "AdminRoles" );
            return;
        }

        // *************** RETURN TO ADMINMANAGER *****************
        if ( req.getParameter( "BELONGING_CANCEL" ) != null ) {
            doGet( req, res );
        }

        // *************** GENERATE THE USER BELONGING TO ROLE PAGE *****************
        if ( req.getParameter( "VIEW_USER_BELONGING_ROLE" ) != null ) {

            String roleId = req.getParameter( "ROLE_ID" );

            if ( roleId == null ) {

                // no role choisen
                sendErrorMessage( imcref, eMailServerMaster, user, errorHeader, 100, res );

                return;
            }

            String userOptionListTag = getUserOptionListTag( roleId, imcref );

            // Lets get all ROLES from DB

            String curentRoleId = roleId;
            try {
                Integer.parseInt( curentRoleId );
            } catch ( NumberFormatException e ) {
                curentRoleId = "0";
            }

            String[][] roleQueryResult = imcref.sqlProcedureMulti( "RoleGetAllApartFromRole", new String[]{
                curentRoleId
            } );

            String roleOptionList = createListOfOptions( roleQueryResult );
            String curentRoleName = getRoleName( roleId, imcref );

            // Lets generate the html page
            VariableManager vm = new VariableManager();
            vm.addProperty( "CURENT_ROLE_ID", roleId );
            vm.addProperty( "CURENT_ROLE_NAME", curentRoleName );
            vm.addProperty( "USER_MENU", userOptionListTag );
            vm.addProperty( "ROLES_MENU", roleOptionList );

            this.sendHtml( req, res, vm, AdminRoleBelongings.HTML_ADMIN_ROLE_BELONGING_EDIT );

            return;
        }

        // *************** GENERATE THE USER DE/ACTIVATE TO ROLE PAGE *****************
        if ( req.getParameter( "VIEW_USER_ACTIVATE" ) != null ) {

            String roleId = req.getParameter( "ROLE_ID" );

            if ( roleId == null ) {

                // no role choisen
                sendErrorMessage( imcref, eMailServerMaster, user, errorHeader, 100, res );

                return;
            }

            String userOptionListTag = getUserOptionListTag( roleId, imcref );
            String curentRoleName = getRoleName( roleId, imcref );

            //Lets generate the html page
            VariableManager vm = new VariableManager();
            vm.addProperty( "CURENT_ROLE_ID", roleId );
            vm.addProperty( "CURENT_ROLE_NAME", curentRoleName );
            vm.addProperty( "USER_MENU", userOptionListTag );

            this.sendHtml( req, res, vm, AdminRoleBelongings.HTML_ADMIN_ROLE_BELONGING_ACTIVATE );

            return;
        }

        // *************** REMOVE ROLE FROM USERS  **********
        if ( req.getParameter( "BELONGING_REMOVE_ROLE" ) != null ) {
            String curentRoleId = req.getParameter( "CURENT_ROLE_ID" );
            String[] userIds = req.getParameterValues( "USER_ID" );

            if ( curentRoleId == null || userIds == null ) {

                // no role choisen or/and no users
                sendErrorMessage( imcref, eMailServerMaster, user, errorHeader, 101, res );
                return;
            }

            for ( int i = 0; i < userIds.length; i++ ) {
                removeUserFromRole( userIds[i], curentRoleId, imcref );
            }

            doGet( req, res );
        }

        // *************** ADD ROLE TO USERS  **********
        if ( req.getParameter( "BELONGING_ADD_ROLE" ) != null ) {
            String roleId = req.getParameter( "ROLE_ID" );
            String[] userIds = req.getParameterValues( "USER_ID" );

            if ( roleId == null || userIds == null ) {

                // no role choisen or/and no users
                sendErrorMessage( imcref, eMailServerMaster, user, errorHeader, 102, res );
                return;
            }

            for ( int i = 0; i < userIds.length; i++ ) {

                addUserToRole( userIds[i], roleId, imcref );
            }

            doGet( req, res );
        }

        // *************** CHANGE ROLE x TO y FOR USERS  **********
        if ( req.getParameter( "BELONGING_MOVE_ROLE" ) != null ) {
            String roleId = req.getParameter( "ROLE_ID" );
            String curentRoleId = req.getParameter( "CURENT_ROLE_ID" );
            String[] userIds = req.getParameterValues( "USER_ID" );

            if ( roleId == null || userIds == null || curentRoleId == null ) {
                // no role choisen or/and no users
                sendErrorMessage( imcref, eMailServerMaster, user, errorHeader, 102, res );

                return;
            }

            for ( int i = 0; i < userIds.length; i++ ) {

                // remove old role
                removeUserFromRole( userIds[i], curentRoleId, imcref );
                addUserToRole( userIds[i], roleId, imcref );
            }

            doGet( req, res );
        }

        // *************** DEACTIVATE USERS  **********
        if ( req.getParameter( "BELONGING_DEACTIVATE" ) != null ) {
            String[] userIds = req.getParameterValues( "USER_ID" );

            if ( userIds == null ) {
                // no user choisen
                sendErrorMessage( imcref, eMailServerMaster, user, errorHeader, 101, res );
                return;
            }

            for ( int i = 0; i < userIds.length; i++ ) {
                setUsersActive( userIds[i], "0", imcref );
            }

            doGet( req, res );
        }

        // *************** ACTIVATE USERS  **********
        if ( req.getParameter( "BELONGING_ACTIVATE" ) != null ) {
            String[] userIds = req.getParameterValues( "USER_ID" );

            if ( userIds == null ) {
                // no user choisen
                sendErrorMessage( imcref, eMailServerMaster, user, errorHeader, 101, res );
                return;
            }

            for ( int i = 0; i < userIds.length; i++ ) {
                setUsersActive( userIds[i], "1", imcref );
            }

            doGet( req, res );
        }
    }

    /**
     * creats list of options.
     * param options must be in order name, value, (selected)
     */
    private String createListOfOptions( String[][] options ) {

        StringBuffer optionList = new StringBuffer();

        for ( int i = 0; i < options.length; i++ ) {
            boolean selected = options[i].length == 3;

            optionList.append( createOption( options[i][0], options[i][1], selected ) );
        }

        return optionList.toString();
    }

    /**
     * creats list of options.
     * param options must be in order value, name
     */
    private String createListOfOptions( String[][] options, boolean selected ) {

        StringBuffer optionList = new StringBuffer();

        for ( int i = 0; i < options.length; i++ ) {

            optionList.append( createOption( options[i][0], options[i][1], selected ) );
        }

        return optionList.toString();
    }

    /**
     * creats option.
     */
    private String createOption( String elementValue, String elementName, boolean selected ) {
        StringBuffer option = new StringBuffer();

        option.append( "<option value=\"" + elementValue + "\"" );
        if ( selected ) {
            option.append( " selected" );
        }
        option.append( ">" + elementName + "</option>" );

        return option.toString();
    }

    private void addUserToRole( String userId, String roleId, IMCServiceInterface imcref ) {
        // lets be certain that the update process works ( avoid error then row alredy exist )
        removeUserFromRole( userId, roleId, imcref );
        imcref.sqlUpdateProcedure( "AddUserRole", new String[]{userId, roleId} );
    }

    private void removeUserFromRole( String userId, String roleId, IMCServiceInterface imcref ) {
        imcref.sqlUpdateProcedure( "RemoveUserFromRole", new String[]{userId, roleId} );
    }

    private String getUserOptionListTag( String roleId, IMCServiceInterface imcref ) {
        String[][] userQueryResult;
        String userOptionList;

        // lets get all user or users who has role role_id
        if ( roleId.equals( "ALL_USERS" ) ) {
            userQueryResult = imcref.sqlProcedureMulti( "GetAllUsersInList", new String[0] );
            userOptionList = createListOfOptions( userQueryResult );
        } else {
            userQueryResult = imcref.sqlProcedureMulti( "GetUsersWhoBelongsToRole", new String[]{roleId} );
            userOptionList = createListOfOptions( userQueryResult, true );
        }

        return userOptionList;
    }

    /**
     * returns name for roll or empty if all
     */
    private String getRoleName( String roleId, IMCServiceInterface imcref ) {

        String roleName = "";

        if ( !( roleId.equalsIgnoreCase( "ALL_USERS" ) ) ) {
            roleName = imcref.sqlProcedureStr( "RoleGetName", new String[]{roleId} );
        }

        return roleName;
    }

    private void setUsersActive( String userId, String state, IMCServiceInterface imcref ) {

        String sqlD = "ChangeUserActiveStatus";
        imcref.sqlUpdateQuery( sqlD, new String[]{userId, state} );
    }
}
