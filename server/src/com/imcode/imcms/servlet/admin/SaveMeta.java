package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.WebAppGlobalConstants;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.MetaDataParser;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Save meta from metaform.
 */
public class SaveMeta extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        req.setCharacterEncoding( WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252 );

        if ( null != req.getParameter( "ImageBrowse" ) ) {
            RequestDispatcher rd = req.getRequestDispatcher( "ImageBrowse" );
            rd.forward( req, res );
            return;
        }

        ImcmsServices imcref = Imcms.getServices();

        UserDomainObject user = Utility.getLoggedOnUser( req );

        Utility.setDefaultHtmlContentType( res );
        Writer out = res.getWriter();

        String metaIdStr = req.getParameter( "meta_id" );
        int metaId = Integer.parseInt( metaIdStr );

        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( metaId );

        if ( !imcref.checkDocAdminRightsAny( metaId, user, ImcmsConstants.PERM_EDIT_HEADLINE
                                                           | ImcmsConstants.PERM_EDIT_DOCINFO
                                                           | ImcmsConstants.PERM_EDIT_PERMISSIONS ) ) {	// Checking to see if user may edit this
            String output = AdminDoc.adminDoc( metaId, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;
        }

        Properties metaprops = new Properties();

        // Hey, hey! Watch as i fetch the permission-set set (pun intended) for each role!
        // Now watch as i fetch the permission_set for the user...
        String[] current_permissions = imcref.sqlProcedure( "GetUserPermissionSet", new String[]{
            metaIdStr, "" + user.getId()
        } );
        int userSetId = Integer.parseInt( current_permissions[0] );	// The users set_id
        int userPermSet = Integer.parseInt( current_permissions[1] );
        int currentDocPerms = Integer.parseInt( current_permissions[2] );

        // Check if the user has any business in here whatsoever.

        boolean hasLessThanChangeAndReadPermission = userSetId > 2; // 3 = read, 4= none
        if ( hasLessThanChangeAndReadPermission ) {
            String output = AdminDoc.adminDoc( metaId, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;
        }

        // Now i'll loop through the db-results, and read the values
        // for each roles set_id this user may change from the form.
        // Then set the new value for each.

        Properties temp_permission_settings = new Properties();

        String[][] sqlResultWithColumnsRoleIdRoleNameAndPermissionId = sprocGetRolesDocPermissions( imcref, metaIdStr );
        for ( int i = 0; i < sqlResultWithColumnsRoleIdRoleNameAndPermissionId.length; ++i ) {
            String role_set_id_str = sqlResultWithColumnsRoleIdRoleNameAndPermissionId[i][2]; // Get the old set_id for this role from the db
            int currentSetIdForRole = Integer.parseInt( role_set_id_str );
            String role_id = sqlResultWithColumnsRoleIdRoleNameAndPermissionId[i][0];                    // Get the role_id from the db
            String new_set_id_str = req.getParameter( "role_" + role_id );  // Check the value from the form
            if ( new_set_id_str == null ) {  // If a new set_id for this role didn't come from the form
                continue;							     // skip to the next role.
            }
            int newSetIdForRole = Integer.parseInt( new_set_id_str );

            boolean permissionSettingSucceded = false;

            final boolean userHasFullPermissionsForThisDocument = DocumentPermissionSetDomainObject.TYPE_ID__FULL == userSetId;
            final boolean userHasEditPermissionsBitSetForThisDocument = 0
                                                                        != ( userPermSet
                                                                             & ImcmsConstants.PERM_EDIT_PERMISSIONS );

            final boolean userMayEditPermissionsForThisDocument = userHasFullPermissionsForThisDocument
                                                                  || userHasEditPermissionsBitSetForThisDocument;

            if ( userMayEditPermissionsForThisDocument ) {
                final boolean userMaySetThisParticularPermissionSet = userSetId <= newSetIdForRole;

                if ( userMaySetThisParticularPermissionSet ) {
                    // May the user edit the permissions for this particular role?
                    final boolean restrictedOneMaySetPermissionsForRestrictedTwo = 0
                                                                                   != ( currentDocPerms
                                                                                        & ImcmsConstants.DOC_PERM_RESTRICTED_1_ADMINISTRATES_RESTRICTED_2 );
                    final boolean userHasRestrictedOne = DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1 == userSetId;
                    final boolean currentSetIdForRoleIsRestrictedTwo = DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2
                                                                       == currentSetIdForRole;
                    final boolean newSetIdForRoleIsRestrictedTwo = DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2
                                                                   == newSetIdForRole;
                    final boolean userHasAtLeastAsPrivilegedCurrentSetIdAsRole = userSetId <= currentSetIdForRole;
                    if ( userHasAtLeastAsPrivilegedCurrentSetIdAsRole ) {
                        final boolean currentOrNewSetIdForRoleIsRestrictedTwo = currentSetIdForRoleIsRestrictedTwo
                                                                                || newSetIdForRoleIsRestrictedTwo;
                        final boolean restrictedOneIsTryingToSetPermissionsForRestrictedTwo = userHasRestrictedOne
                                                                                              && currentOrNewSetIdForRoleIsRestrictedTwo;
                        if ( restrictedOneMaySetPermissionsForRestrictedTwo
                             || !restrictedOneIsTryingToSetPermissionsForRestrictedTwo ) {

                            // We used to save to the db immediately. Now we do it a little bit differently to make it possible to store stuff in the session instead of the db.
                            temp_permission_settings.setProperty( String.valueOf( role_id ), String.valueOf( newSetIdForRole ) );
                            permissionSettingSucceded = true;
                        }
                    }
                }
            }

            if ( !permissionSettingSucceded ) {
                imcref.updateMainLog( "User " + user.getId() + " with set_id " + userSetId + " and permission_set "
                              + userPermSet
                              + " was denied permission to change set_id for role "
                              + role_id
                              + " from "
                              + currentSetIdForRole
                              + " to "
                              + newSetIdForRole
                              + " on meta_id "
                              + metaIdStr );
            }
        }

        /*
          Now we're going to start accepting the input form fields.
          This table keeps track of all the fields we may encounter.
          The "nullvalue" is there to support checkboxes, which,
          if not checked, report null.
          So, if the checkboxes do not appear, we know that we should enter
          the "nullvalue" found here, into the db.
        */
        // NOTE! This table matches the one below. Don't go changing one without changing the other.
        // FIXME: They should be merged into one table.
        String[] metatable = {
            /*  Nullable			Nullvalue */
            "shared", "0",
            "disable_search", "0",
            "show_meta", "0",
            "permissions", "0",
            "meta_headline", null,
            "meta_text", null,
            "publication_start_datetime", null,
            "archived_datetime", null,
            "target", null,
            "lang_prefix", null,
            "publisher_id", null,
        };

        final int metatable_cols = 2;

        // I'll make a table to keep track of
        // what is the least privileged (highest)
        // set_id you may have, to be able to change
        // each property. Roles with set_ids 1 and 2
        // still need explicit permissions, so this is
        // mainly for fleshing out what only a user with
        // "full" (0) may do. (Change whether set-id 1 is
        // more privileged. "permissions")
        // I use a bitmask here to specify what permissions
        // are required for each.
        // 0 == Unreachable
        // 1 == Something on the "simple docinfo"-page
        // 2 == Something on the "advanced docinfo"-page
        // 3 == 1|2
        // 4 == Something on the "rights/permissions"-page
        // 5 == 1|4
        // 6 == 2|4
        // 7 == 1|2|4

        // NOTE! This table matches the one above. Don't go changing one without changing the other.
        // FIXME: They should be merged into one table.
        int[] metatable_restrictions = {
            //	set_id,	permission_bitmask
            DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, ImcmsConstants.PERM_EDIT_DOCINFO
                                                    | ImcmsConstants.PERM_EDIT_PERMISSIONS, //"shared",
            DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, ImcmsConstants.PERM_EDIT_DOCINFO, //"disable_search",
            DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, ImcmsConstants.PERM_EDIT_DOCINFO, //"archive",
            DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, ImcmsConstants.PERM_EDIT_DOCINFO
                                                    | ImcmsConstants.PERM_EDIT_PERMISSIONS, //"show_meta",
            DocumentPermissionSetDomainObject.TYPE_ID__FULL, ImcmsConstants.PERM_EDIT_PERMISSIONS, //"permissions",
            DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, ImcmsConstants.PERM_EDIT_HEADLINE | ImcmsConstants.PERM_EDIT_DOCINFO
                                                    | ImcmsConstants.PERM_EDIT_PERMISSIONS, //"meta_headline",
            DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, ImcmsConstants.PERM_EDIT_HEADLINE | ImcmsConstants.PERM_EDIT_DOCINFO, //"meta_text",
            DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, ImcmsConstants.PERM_EDIT_DOCINFO, //"activated_datetime",
            DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, ImcmsConstants.PERM_EDIT_DOCINFO, //"archived_datetime",
            DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, ImcmsConstants.PERM_EDIT_DOCINFO, //"frame_name",
            DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, ImcmsConstants.PERM_EDIT_DOCINFO, //"target"
            DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, ImcmsConstants.PERM_EDIT_DOCINFO, //"lang_prefix",
            DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, ImcmsConstants.PERM_EDIT_DOCINFO, //"publisher_id",
        };

        Map inputMap = new HashMap();
        // Loop through all meta-table-properties
        // Adding them to a HashMap to be used as input
        // That way i can mutilate the values before all the
        // permissions are checked.
        for ( int i = 0; i < metatable.length; i += metatable_cols ) {
            final String parameter = req.getParameter( metatable[i] );
            inputMap.put( metatable[i], parameter );
        }

        // If target is set to '_other', it means the real target is in 'frame_name'.
        // In this case, set target to the value of frame_name.
        String target = (String)inputMap.get( "target" );
        String frame_name = (String)inputMap.get( "frame_name" );
        if ( "_other".equals( target ) && frame_name != null && !"".equals( frame_name ) ) {
            inputMap.put( "target", frame_name );
        }
        inputMap.remove( "frame_name" );  // we only need to store frame_name in db column "target"

        // Loop through all meta-table-properties
        // Checking permissions as we go.
        // All alterations of the inputdata must happen before this
        for ( int i = 0; i < metatable.length; i += metatable_cols ) {
            String tmp = (String)inputMap.get( metatable[i] );
            if ( userSetId > metatable_restrictions[i]						// Check on set_id if user is allowed to set this particular property.
                 || userSetId > 0 && ( userPermSet & metatable_restrictions[i + 1] ) == 0 ) {
                continue;
            }
            if ( tmp != null ) {
                metaprops.setProperty( metatable[i], tmp );	// If it is found, set it.
            } else {
                tmp = metatable[i + 1];
                // FIXME: If it is null, that could mean the user
                // emptied the field. This leads to the property
                // not being updated, and left unchanged!
                // _Should_ be ok, since for checkboxes null is valid. (Means false)
                // For fields other than checkboxes and radiobuttons null would be bad.
                if ( tmp != null ) {
                    metaprops.setProperty( metatable[i], tmp );	// If it is not found, set it to the nullvalue. (For checkboxes, which do not appear if they are not checked.)
                }
            }
        }
        //ok here we fetch the settings fore the default_template 1 & 2
        String temp_default_template_1 = req.getParameter( "default_template_set_1" ) == null
                                         ? "-1" : req.getParameter( "default_template_set_1" );
        String temp_default_template_2 = req.getParameter( "default_template_set_2" ) == null
                                         ? "-1" : req.getParameter( "default_template_set_2" );
        String[] temp_default_templates = {temp_default_template_1, temp_default_template_2};

        // It's like this... people make changes on the page, and then they forget to press "save"
        // before they press one of the "define-permission" buttons, and then their settings are lost.
        // I will fix this by storing the settings in a temporary variable in the user object.
        // This variable will be an array of four objects. In order:
        // A String, containing the meta-id of the page.
        // A Properties, containing the docinfo for the page. (db-column, value)
        // A Properties, containing the permission_sets for the roles. (role_id, set_id)
        // A String[], containing default_template 1 and 2
        // We also need a name for this temporary variable... i think i shall call it... (Drumroll, please...) "temp_perm_settings" !
        //

        putTemporaryPermissionSettingsInUser( user, metaIdStr, metaprops, temp_permission_settings, temp_default_templates );
        if ( req.getParameter( "define_set_1" ) != null ) {	// If user want's to edit permission-set 1
            out.write( MetaDataParser.parsePermissionSet( metaId, user, 1, false ) );
            return;
        } else if ( req.getParameter( "define_set_2" ) != null ) {	// If user want's to edit permission-set 2
            putTemporaryPermissionSettingsInUser( user, metaIdStr, metaprops, temp_permission_settings, temp_default_templates );
            out.write( MetaDataParser.parsePermissionSet( metaId, user, 2, false ) );
            return;
        } else if ( req.getParameter( "define_new_set_1" ) != null ) {
            putTemporaryPermissionSettingsInUser( user, metaIdStr, metaprops, temp_permission_settings, temp_default_templates );
            out.write( MetaDataParser.parsePermissionSet( metaId, user, 1, true ) );
            return;
        } else if ( req.getParameter( "define_new_set_2" ) != null ) {
            putTemporaryPermissionSettingsInUser( user, metaIdStr, metaprops, temp_permission_settings, temp_default_templates );
            out.write( MetaDataParser.parsePermissionSet( metaId, user, 2, true ) );
            return;
        } else if ( req.getParameter( "add_roles" ) != null ) {		// The user wants to give permissions to roles that have none.
            String[] roles_no_rights = req.getParameterValues( "roles_no_rights" );
            for ( int i = 0; roles_no_rights != null && i < roles_no_rights.length; ++i ) {
                temp_permission_settings.setProperty( roles_no_rights[i], "3" );
            }
            putTemporaryPermissionSettingsInUser( user, metaIdStr, metaprops, temp_permission_settings, temp_default_templates );

            out.write( MetaDataParser.parseMetaPermission( metaIdStr, metaIdStr, user ) );
            return;
        }


        // From now on we enter stuff into the db.

        // Here i'll construct an sql-query that will update all docinfo
        // the user is allowed to change.
        //ok lets start and get the default templates
        String tempStr = req.getParameter( "default_template_set_1" );
        String template1 = "-1";
        String template2 = "-1";
        boolean saveDefaultTemplateToDb = false;
        if ( tempStr != null ) {
            saveDefaultTemplateToDb = true;
            template1 = req.getParameter( "default_template_set_1" ).equals( "" )
                        ? "-1" : req.getParameter( "default_template_set_1" );
        }
        tempStr = req.getParameter( "default_template_set_2" );
        if ( tempStr != null ) {
            saveDefaultTemplateToDb = true;
            template2 = req.getParameter( "default_template_set_2" ).equals( "" )
                        ? "-1" : req.getParameter( "default_template_set_2" );
        }

        List sqlUpdateColumns = new ArrayList();
        List sqlUpdateValues = new ArrayList();

        Enumeration propkeys = metaprops.propertyNames();
        while ( propkeys.hasMoreElements() ) {
            String columnName = (String)propkeys.nextElement();
            String columnValue = metaprops.getProperty( columnName );
            sqlUpdateColumns.add( columnName + " = ?" );
            sqlUpdateValues.add( columnValue );
        }

        // todo: Move this to DocumentMapper
        if ( sqlUpdateColumns.size() > 0 ) {
            String[] updateParameters = new String[sqlUpdateValues.size() + 1];
            sqlUpdateValues.toArray( updateParameters );
            updateParameters[updateParameters.length - 1] = metaIdStr;
            imcref.sqlUpdateQuery( "update meta set " + StringUtils.join( sqlUpdateColumns.iterator(), ',' )
                                   + " where meta_id = ?", updateParameters );
        }

        for ( int i = 0; i < sqlResultWithColumnsRoleIdRoleNameAndPermissionId.length; ++i ) {
            int role_id = Integer.parseInt( sqlResultWithColumnsRoleIdRoleNameAndPermissionId[i][0] );
            String new_set_id = temp_permission_settings.getProperty( "" + role_id );
            if ( new_set_id != null ) {
                DocumentMapper.sprocSetRoleDocPermissionSetId( imcref, metaId, role_id, Integer.parseInt( new_set_id ) );
            }
        }

        //ok lets save the default templates
        if ( saveDefaultTemplateToDb ) {
            sprocUpdateDefaultTemplates( imcref, metaIdStr, template1, template2 );
        }

        // Update the date_modified for all parents.
        DocumentMapper.sprocUpdateParentsDateModified( imcref, metaId );

        documentMapper.invalidateDocument(document) ;

        // Let's split this joint!
        res.sendRedirect( "AdminDoc?meta_id="+metaId);

        imcref.updateMainLog( "Document [" + metaIdStr + "] updated by user: [" + user.getFullName() + "]" );
    }

    private static void putTemporaryPermissionSettingsInUser( UserDomainObject user, String meta_id,
                                                                Properties metaprops,
                                                                Properties temp_permission_settings,
                                                                String[] temp_default_templates ) {
        user.put( "temp_perm_settings", new Object[]{
            String.valueOf( meta_id ), metaprops, temp_permission_settings, temp_default_templates
        } );
    }

    private static void sprocUpdateDefaultTemplates( ImcmsServices imcref, String meta_id, String template1,
                                                    String template2 ) {
        imcref.sqlUpdateProcedure( "UpdateDefaultTemplates", new String[]{meta_id, template1, template2} );
    }

    private static String[][] sprocGetRolesDocPermissions( ImcmsServices imcref, String meta_id ) {
        String[][] role_permissions = imcref.sqlProcedureMulti( "GetRolesDocPermissions", new String[]{meta_id} );
        return role_permissions;
    }

}
