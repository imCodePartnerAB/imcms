package com.imcode.imcms.servlet.superadmin;

import imcode.util.Html;
import imcode.external.diverse.VariableManager;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.Html;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

/**
 * Comments. This servlet will need the following stored procedures in the db
 * - RoleFindName
 * - RoleAddNew
 * - RoleDelete
 * - RoleGetPermissionsFromRole
 * - RoleGetPermissionsByLanguage
 * - RoleUpdatePermissions
 * - RolePermissionsAddNew
 * - GetLangPrefixFromId
 */
public class AdminRoles extends Administrator {

    private final static Logger log = Logger.getLogger( AdminRoles.class.getName() );

    private String HTML_TEMPLATE;
    private String HTML_ADMIN_ROLES;
    private String HTML_ADD_ROLE;
    private String HTML_RENAME_ROLE;
    private String HTML_DELETE_ROLE_1;
    private String HTML_DELETE_ROLE_2;
    private String HTML_EDIT_ROLE;
    private String HTML_EDIT_ROLE_TABLE;
    private String HTML_EDIT_ROLE_TABLE_ROW;

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        // Lets verify that the user who tries to add a new user is an admin
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( user.isSuperAdmin() == false ) {
            String header = "Error in AdminRoles.";
            Properties langproperties = imcref.getLanguageProperties( user );
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            log.debug( header + "- user is not an administrator" );
            new AdminError( req, res, header, msg );
            return;
        }

        // fast fix, then role page was moved down one page
        if ( req.getParameter( "ADD_NEW_ROLE" ) != null || req.getParameter( "RENAME_ROLE" ) != null
                || req.getParameter( "DELETE_ROLE" ) != null || req.getParameter( "UPDATE_ROLE_PERMISSIONS" ) != null
                || req.getParameter( "CANCEL_ROLE" ) != null ) {

            // Lets get all ROLES from DB
            String[] rolesArr = imcref.sqlProcedure( "RoleAdminGetAll", new String[0] );
            Vector rolesV = new Vector( java.util.Arrays.asList( rolesArr ) );


            // Lets generate the html page
            VariableManager vm = new VariableManager();
            String opt = Html.createOptionList( rolesV, Arrays.asList(new String[] { "" }) );
            vm.addProperty( "ROLES_MENU", opt );

            this.sendHtml( req, res, vm, HTML_ADMIN_ROLES );

            return;

        }
        // *************** GENERATE THE ADMIN ROLE PAGE *****************
        VariableManager vm = new VariableManager();
        this.sendHtml( req, res, vm, HTML_TEMPLATE );

    } // End doGet

    /**
     * POST
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        // Lets check if the user is an admin, otherwise throw him out.
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( user.isSuperAdmin() == false ) {
            String header = "Error in AdminRoles.";
            Properties langproperties = imcref.getLanguageProperties( user );
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            log.debug( header + "- user is not an administrator" );
            new AdminError( req, res, header, msg );
            return;
        }

        // *************** GENERATE THE ADMINISTRATE ROLES PAGE *****************
        if ( req.getParameter( "VIEW_ADMIN_ROLES" ) != null ) {
            // Lets get all ROLES from DB
            String[] rolesArr = imcref.sqlProcedure( "RoleAdminGetAll", new String[0] );
            Vector rolesV = new Vector( java.util.Arrays.asList( rolesArr ) );


            // Lets generate the html page
            VariableManager vm = new VariableManager();
            String opt = Html.createOptionList( "", rolesV );
            vm.addProperty( "ROLES_MENU", opt );

            this.sendHtml( req, res, vm, HTML_ADMIN_ROLES );

            return;
        }

        // *************** GENERATE THE ADMIN ROLEBELONGIN PAGE *****************
        if ( req.getParameter( "VIEW_ADMIN_ROLE_BELONGINGS" ) != null ) {
            res.sendRedirect( "AdminRoleBelongings" );
            return;
        }

        // *************** RETURN TO ADMINMANAGER *****************
        if ( req.getParameter( "CANCEL" ) != null ) {
            res.sendRedirect( "AdminManager" );
            return;
        }

        // *************** RETURN TO ADMINROLE *****************
        if ( req.getParameter( "CANCEL_ROLE" ) != null ) {
            this.doGet( req, res );
            return;
        }

        // *************** RETURN TO ADMINROLE *****************
        if ( req.getParameter( "CANCEL_ROLE_ADMIN" ) != null ) {
            this.doGet( req, res );
            return;
        }

        // *************** GENERATE THE ADD NEW ROLE PAGE  **********
        if ( req.getParameter( "VIEW_ADD_NEW_ROLE" ) != null ) {

            String languagePrefix = user.getLanguageIso639_2();

            String[][] rolePermissions = imcref.sqlProcedureMulti( "RoleGetPermissionsByLanguage", new String[]{languagePrefix} );

            // lets adjust the list to fit method cal
            String[][] permissionList = new String[rolePermissions.length][];

            for ( int i = 0; i < permissionList.length; i++ ) {

                permissionList[i] = new String[]{"0", rolePermissions[i][0], rolePermissions[i][1]};
                log.debug( permissionList[i][1] );
            }

            // lets get data on permissions and values
            String permissionComponent = createPermissionComponent( req, permissionList );

            VariableManager vm = new VariableManager();
            vm.addProperty( "ROLE_PERMISSIONS", permissionComponent );

            this.sendHtml( req, res, vm, HTML_ADD_ROLE );

            return;
        }

        // *************** GENERATE THE RENAME ROLE PAGE  **********
        if ( req.getParameter( "VIEW_RENAME_ROLE" ) != null ) {
            String roleId = req.getParameter( "ROLE_ID" );
            if ( roleId == null ) {
                String header = "Roles error";
                Properties langproperties = imcref.getLanguageProperties( user );
                String msg = langproperties.getProperty("error/servlet/AdminRoles/rolename_missing") + "<BR>";
                log.debug( "Error in rename roles, no role selected for rename" );
                new AdminError( req, res, header, msg );
                return;
            }

            String currRoleName = imcref.sqlProcedureStr( "RoleGetName", new String[]{roleId} );

            VariableManager vm = new VariableManager();
            vm.addProperty( "CURRENT_ROLE_ID", roleId );
            vm.addProperty( "CURRENT_ROLE_NAME", "" + currRoleName );
            this.sendHtml( req, res, vm, HTML_RENAME_ROLE );
            return;
        }

        // *************** GENERATE THE EDIT ROLE PAGE  **********
        if ( req.getParameter( "VIEW_EDIT_ROLE" ) != null ) {

            String roleId = req.getParameter( "ROLE_ID" );
            if ( roleId == null ) {
                String header = "Error in AdminRoles, edit role";
                Properties langproperties = imcref.getLanguageProperties( user );
                String msg = langproperties.getProperty("error/servlet/AdminRoles/role_missing") + "<br>";
                log.debug( header + "- select the role to be changed" );
                new AdminError( req, res, header, msg );
                return;
            }

            // dont list superadmin permissions
            if ( roleId.equals( "0" ) ) {
                String header = "Error in AdminRoles, edit role";
                String msg = "" + "<BR>";
                log.debug( "Error in checking roles: Trying to look att superadmin permissions" );
                new AdminError( req, res, header, msg );
                return;
            }

            String languagePrefix = user.getLanguageIso639_2();

            String sqlQ = "RoleGetPermissionsFromRole";
            String[][] permissionList = imcref.sqlProcedureMulti( sqlQ, new String[]{roleId, languagePrefix} );

            // lets get data on permissions and values
            String permissionComponent = createPermissionComponent( req, permissionList );

            /* create output page */
            VariableManager vm = new VariableManager();
            vm.addProperty( "CURRENT_ROLE_NAME", imcref.sqlProcedureStr( "RoleGetName", new String[]{roleId} ) );
            vm.addProperty( "CURRENT_ROLE_ID", roleId );
            vm.addProperty( "ROLE_PERMISSIONS", permissionComponent );
            this.sendHtml( req, res, vm, HTML_EDIT_ROLE );

            return;
        }

        // *************** ADD NEW ROLE TO DB  **********
        if ( req.getParameter( "ADD_NEW_ROLE" ) != null ) {

            // Lets get the parameters from html page and validate them
            Properties params = this.getAddRoleParameters( req );
            if ( super.assertNoEmptyStringsInPropertyValues( params ) == false ) {
                String header = "Error in AdminRoles ";
                Properties langproperties = imcref.getLanguageProperties( user );
                String msg = langproperties.getProperty("error/servlet/AdminRoles/new_rolename_missing") + "<br>";
                log.debug( header + "- new rolename missing" );
                new AdminError( req, res, header, msg );
                return;
            }

            // Lets check that the new rolename doesnt exists already in db
            String foundRoleName = imcref.sqlProcedureStr( "RoleFindName", new String[]{(String)params.get( "ROLE_NAME" )} );
            if ( !foundRoleName.equalsIgnoreCase( "-1" ) ) {
                String header = "Error in AdminRoles.";
                Properties langproperties = imcref.getLanguageProperties( user );
                String msg = langproperties.getProperty("error/servlet/AdminRoles/rolename_already_exists") + "<br>";
                log.debug( header + "- role name already exists" );
                new AdminError( req, res, header, msg );
                return;
            }

            // lets colect permissions state
            String[] checkedPermissions = req.getParameterValues( "PERMISSION_CHECKBOX" );
            int permissionValue = colectPermissionsState( checkedPermissions );

            // Lets add the new role into db
            imcref.sqlUpdateProcedure( "RolePermissionsAddNew", new String[]{params.getProperty( "ROLE_NAME" ), "" + permissionValue} );
            this.doGet( req, res );

            return;
        }

        // *************** RENAME A ROLE IN THE DB  **********
        if ( req.getParameter( "RENAME_ROLE" ) != null ) {

            // Lets get the parameters from html page and validate them
            Properties params = this.getRenameRoleParameters( req );
            if ( super.assertNoEmptyStringsInPropertyValues( params ) == false ) {
                String header = "Error in AdminRoles, rename role ";
                Properties langproperties = imcref.getLanguageProperties( user );
                String msg = langproperties.getProperty("error/servlet/AdminRoles/new_rolename_missing") + "<br>";
                log.debug( header + "- new role name is missing" );
                new AdminError( req, res, header, msg );
                return;
            }

            // Lets check that the new rolename doesnt exists already in db
            String foundRoleName = imcref.sqlProcedureStr( "RoleFindName", new String[]{params.getProperty( "ROLE_NAME" )} );
            if ( !foundRoleName.equalsIgnoreCase( "-1" ) ) {
                String header = "Error in AdminRoles.";
                Properties langproperties = imcref.getLanguageProperties( user );
                String msg = langproperties.getProperty("error/servlet/AdminRoles/rolename_already_exists") + "<br>";
                log.debug( header + "- rolename already exists" );
                new AdminError( req, res, header, msg );

                return;
            }

            // Lets add the new role into db
            String sqlQ = "RoleUpdateName";
            log.debug( "Sql: " + sqlQ );
            imcref.sqlUpdateProcedure( sqlQ, new String[]{params.getProperty( "ROLE_ID" ), params.getProperty( "ROLE_NAME" )} );
            this.doGet( req, res );

            return;
        }


        // ****** VIEW AFFECTED META ID:S WHICH WILL BE AFFECTED OF A DELETE ********

        boolean warnDelRole = false;
        if ( req.getParameter( "VIEW_DELETE_ROLE" ) != null ) {

            // Lets get the parameters from html page and validate them
            Properties params = this.getDeleteRoleParameters( req );
            if ( super.assertNoEmptyStringsInPropertyValues( params ) == false ) {
                String header = "Error in AdminRoles ";
                Properties langproperties = imcref.getLanguageProperties( user );
                String msg = langproperties.getProperty("error/servlet/AdminRoles/role_to_delete_missing") + "<br>";
                log.debug( header + "- no role was selected for delete" );
                new AdminError( req, res, header, msg );
                return;
            }

            // Lets get the top 50 metaid:s which will be affected if we delete the role
            String[] affectedMetaIds = imcref.sqlProcedure( "RoleDeleteViewAffectedMetaIds", new String[]{params.getProperty( "ROLE_ID" )} );

            // Lets get nbr of affected  metaid:s
            String roleCount = imcref.sqlProcedureStr( "RoleCount", new String[]{params.getProperty( "ROLE_ID" )} );

            // Lets get the top 50 users:s which will be affected if we delete the role
            String[] affectedUsers = imcref.sqlProcedure( "RoleDeleteViewAffectedUsers", new String[]{params.getProperty( "ROLE_ID" )} );

            // Lets get nbr of affected users
            int userCount = affectedUsers.length / 2;

            if ( affectedUsers.length != 0 || affectedMetaIds.length != 0 ) {

                // Lets generate the affected users & metaid warning html page
                String opt = Html.createOptionList( "", new Vector( java.util.Arrays.asList( affectedMetaIds ) ) );
                String users = Html.createOptionList( "", new Vector( java.util.Arrays.asList( affectedUsers ) ) );
                VariableManager vm = new VariableManager();
                vm.addProperty( "META_ID_LIST", opt );
                vm.addProperty( "USER_ID_LIST", users );
                vm.addProperty( "USER_COUNT", "" + userCount );
                vm.addProperty( "ROLE_COUNT", roleCount );
                vm.addProperty( "CURRENT_ROLE_ID", params.get( "ROLE_ID" ) );
                this.sendHtml( req, res, vm, HTML_DELETE_ROLE_1 );
                return;
            } else {

                // Lets generate the last warning html page
                warnDelRole = true;
            }
        }

        // *************** GENERATE THE LAST DELETE ROLE WARNING PAGE  **********
        if ( req.getParameter( "WARN_DELETE_ROLE" ) != null || warnDelRole == true ) {
            // Lets get the parameters from html page and validate them
            Properties params = this.getDeleteRoleParameters( req );
            if ( super.assertNoEmptyStringsInPropertyValues( params ) == false ) {
                String header = "Error in AdminRoles, delete ";
                Properties langproperties = imcref.getLanguageProperties( user );
                String msg = langproperties.getProperty("error/servlet/AdminRoles/role_to_delete_missing") + "<br>";
                log.debug( header + "- no role was selected for delete" );
                new AdminError( req, res, header, msg );

                return;
            }

            // Lets generate the last warning html page
            VariableManager vm = new VariableManager();
            vm.addProperty( "CURRENT_ROLE_ID", params.get( "ROLE_ID" ) );
            this.sendHtml( req, res, vm, HTML_DELETE_ROLE_2 );
            return;
        }

        // ****** DELETE A ROLE ********
        if ( req.getParameter( "DELETE_ROLE" ) != null ) {

            // Lets get the parameters from html page and validate them
            Properties params = this.getDeleteRoleParameters( req );
            if ( super.assertNoEmptyStringsInPropertyValues( params ) == false ) {
                String header = "Error in AdminRoles, delete";
                Properties langproperties = imcref.getLanguageProperties( user );
                String msg = langproperties.getProperty("error/servlet/AdminRoles/role_to_delete_missing") + "<br>";
                log.debug( header +  "- no role was selected for delete" );
                new AdminError( req, res, header, msg );

                return;
            }

            imcref.sqlUpdateProcedure( "RoleDelete", new String[]{params.getProperty( "ROLE_ID" )} );

            this.doGet( req, res );

            return;
        }

        // ****** UPDATE ROLE PERMISSIONS ********
        if ( req.getParameter( "UPDATE_ROLE_PERMISSIONS" ) != null ) {

            // Lets check that role_id is corect, not lost or manipulated
            Properties params = getEditRoleParameters( req );
            String[] checkedPermissions = req.getParameterValues( "PERMISSION_CHECKBOX" );

            if ( super.assertNoEmptyStringsInPropertyValues( params ) == false ) {
                String header = "Error in AdminRoles ";
                Properties langproperties = imcref.getLanguageProperties( user );
                String msg = langproperties.getProperty("error/servlet/AdminRoles/role_to_delete_missing") + "<br>";
                log.debug( header + "- no role was selected for delete" );
                new AdminError( req, res, header, msg );
                return;
            }

            int permissionValue = colectPermissionsState( checkedPermissions );

            // lets update
            imcref.sqlUpdateProcedure( "RoleUpdatePermissions", new String[]{params.getProperty( "ROLE_ID" ), "" + permissionValue} );

            this.doGet( req, res );
        }

    } // end HTTP POST

    /**
     * Collects the parameters from the request object
     */

    private Properties getAddRoleParameters( HttpServletRequest req ) {
        Properties roleInfoP = new Properties();
        String roleInfo = ( req.getParameter( "ROLE_NAME" ) == null ) ? "" : ( req.getParameter( "ROLE_NAME" ) );
        roleInfoP.setProperty( "ROLE_NAME", roleInfo );
        return roleInfoP;
    }

    /**
     * Collects the parameters from the request object at RENAME process
     */

    private Properties getRenameRoleParameters( HttpServletRequest req ) {
        Properties roleInfoP = new Properties();
        String roleId = ( req.getParameter( "ROLE_ID" ) == null ) ? "" : ( req.getParameter( "ROLE_ID" ) );
        String roleInfo = ( req.getParameter( "ROLE_NAME" ) == null ) ? "" : ( req.getParameter( "ROLE_NAME" ) );
        roleInfoP.setProperty( "ROLE_ID", roleId );
        roleInfoP.setProperty( "ROLE_NAME", roleInfo );
        return roleInfoP;
    }

    /**
     * Collects the parameters from the request object
     */

    private Properties getDeleteRoleParameters( HttpServletRequest req ) {
        Properties roleInfoP = new Properties();
        String roleInfo = ( req.getParameter( "ROLE_ID" ) == null ) ? "" : ( req.getParameter( "ROLE_ID" ) );
        roleInfoP.setProperty( "ROLE_ID", roleInfo );
        return roleInfoP;
    }

    /**
     * Collects the parameters from the request object at UPDATE process
     */
    private Properties getEditRoleParameters( HttpServletRequest req ) {
        Properties roleInfoP = new Properties();

        String roleInfo = ( req.getParameter( "ROLE_ID" ) == null ) ? "" : ( req.getParameter( "ROLE_ID" ) );

        roleInfoP.put( "ROLE_ID", roleInfo );

        return roleInfoP;
    }

    /**
     * Init: Detects paths and filenames.
     */
    public void init( ServletConfig config ) throws ServletException {
        super.init( config );
        HTML_TEMPLATE = "AdminRoles.htm";
        HTML_ADMIN_ROLES = "AdminRoles_roles.htm";
        HTML_ADD_ROLE = "AdminRoles_Add.htm";
        HTML_RENAME_ROLE = "AdminRoles_Rename.htm";
        HTML_DELETE_ROLE_1 = "AdminRoles_Delete1.htm";
        HTML_DELETE_ROLE_2 = "AdminRoles_Delete2.htm";
        HTML_EDIT_ROLE = "AdminRoles_Edit.html";
        HTML_EDIT_ROLE_TABLE = "AdminRoles_Edit_Permissions_List.html";
        HTML_EDIT_ROLE_TABLE_ROW = "AdminRoles_Edit_Permission.html";
    }

    /* create permissions tag */
    private String createPermissionComponent( HttpServletRequest req,
                                              String[][] permissionList )
            throws IOException {

        /* create rows of permission */
        StringBuffer permissionTableRows = new StringBuffer();

        /*
         * lets create permission as a component
         * element: 0 = value, 1 = permission_id, 2 = description
         */
        for ( int i = 0; i < permissionList.length; i++ ) {

            String permissionId = permissionList[i][1];
            String description = permissionList[i][2];
            boolean isChecked = !( permissionList[i][0].equals( "0" ) );

            VariableManager vm = new VariableManager();
            vm.addProperty( "PERMISSION_DESCRIPTION", description );
            vm.addProperty( "PERMISSON_ID", permissionId );

            if ( isChecked ) {
                vm.addProperty( "PERMISSION_CHECKED", "checked" );
            } else {
                vm.addProperty( "PERMISSION_CHECKED", "" );
            }

            String rowString = createHtml( req, vm, HTML_EDIT_ROLE_TABLE_ROW );

            permissionTableRows.append( rowString );

        }

        //create component
        VariableManager vmTable = new VariableManager();
        vmTable.addProperty( "PERMISSION_ROWS", permissionTableRows.toString() );

        return createHtml( req, vmTable, HTML_EDIT_ROLE_TABLE );
    }

    /* colects permmissions state*/
    private int colectPermissionsState( String[] checkedPermissions ) {
        int permissionValue = 0;

        if ( checkedPermissions != null ) {

            for ( int i = 0; i < checkedPermissions.length; i++ ) {
                int permissionId = 0;

                try {
                    permissionId = Integer.parseInt( checkedPermissions[i] );
                } catch ( NumberFormatException e ) {
                    log.debug( "Error in checking roles: NumberFormatException" );
                }

                permissionValue |= permissionId;
            }
        }
        return permissionValue;
    }
}
