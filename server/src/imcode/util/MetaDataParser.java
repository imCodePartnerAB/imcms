package imcode.util;

import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;

public class MetaDataParser {

    private static final String PERMISSIONS_TABLE_HEAD_TEMPLATE = "permissions/roles_rights_table_head.html";
    private static final String PERMISSIONS_TABLE_ROW_TEMPLATE = "permissions/roles_rights_table_row.html";
    private static final String PERMISSIONS_TABLE_TAIL_TEMPLATE = "permissions/roles_rights_table_tail.html";
    private static final String RESTRICTED_1_ADMINISTRATES_RESTRICTED_2_CHECKBOX_TEMPLATE = "permissions/sets_precedence.html";
    private static final String DEFINE_RESTRICTED_1_BUTTON_TEMPLATE = "permissions/set_1_button.html";
    private static final String DEFINE_RESTRICTED_2_BUTTON_TEMPLATE = "permissions/set_2_button.html";
    private static final String DEFINE_NEW_RESTRICTED_1_BUTTON_TEMPLATE = "permissions/new_set_1_button.html";
    private static final String DEFINE_NEW_RESTRICTED_2_BUTTON_TEMPLATE = "permissions/new_set_2_button.html";
    private static final String ALL_DEFINE_RESTRICTED_BUTTONS_TEMPLATE = "permissions/define_sets.html";
    private static final String RESTRICTED_1_DEFAULT_TEMPLATE_CHOICE_TEMPLATE = "docinfo/default_templates_1.html";
    private static final String RESTRICTED_2_AND_MAYBE_1_DEFAULT_TEMPLATE_CHOICE_TEMPLATE = "docinfo/default_templates.html";
    private static final String USER_HASH_KEY__TEMPORARY_PERMISSION_SETTINGS = "temp_perm_settings";

    private MetaDataParser() {
    }

    /**
     * parseMetaPermission parses the page which consists of  the information for a certain meta_id from the db and
     * parses the information into the change_meta.html (the plain admin mode file).
     */
    public static String parseMetaPermission( String metaIdStr, String parent_meta_id, UserDomainObject user ) {
        int metaId = Integer.parseInt( metaIdStr );

        ImcmsServices imcref = Imcms.getServices();

        final String NORMAL = "NORMAL";
        final String CHECKBOX = "CHECKBOX";
        final String OTHER = "OTHER";

        String[] metatable = {
            /* Nullable		        Nullvalue   Type */
            "shared", "0", CHECKBOX,
            "disable_search", "0", CHECKBOX,
            "show_meta", "0", CHECKBOX,
            "meta_headline", null, NORMAL,
            "meta_text", null, NORMAL,
            "date_created", null, OTHER,
            "date_modified", null, OTHER,
            "doc_type", null, NORMAL,
            "publication_start_datetime", null, OTHER,
            "archived_datetime", null, OTHER,
            "target", null, OTHER,
            "lang_prefix", null, OTHER,
            "publisher_id", null, OTHER,
        };

        // Lets get all info for the meta id
        Map hash = imcref.sqlProcedureHash( "GetDocumentInfo", new String[]{"" + metaId} );

        // Get the info from the user object.
        // "temp_perm_settings" is an array containing a stringified meta-id, a hashtable of meta-info (column=value),
        // and a hashtable of roles and their corresponding set_id for this page (role_id=set_id).
        // This array comes from selecting the permissionpage. People set a lot of stuff in the page,
        // and then they forget to press "Save" before pressing another button.
        // If they press another button, this array will be put in the user-object, to remember their settings.
        Object[] temp_perm_settings = (Object[])user.get( USER_HASH_KEY__TEMPORARY_PERMISSION_SETTINGS );

        List vec = new ArrayList();

        getRolesFromDb( metaId, user, vec );

        user.remove( USER_HASH_KEY__TEMPORARY_PERMISSION_SETTINGS );	// Forget about it, so it won't appear on a reload.

        if ( temp_perm_settings != null && metaId == Integer.parseInt( (String)temp_perm_settings[0] ) ) {		// Make sure this is the right document.
            // Copy everything from this temporary hashtable into the meta-hash.
            Enumeration temp_enum = ( (Hashtable)temp_perm_settings[1] ).keys();
            while ( temp_enum.hasMoreElements() ) {
                String temp_key = (String)temp_enum.nextElement();
                ( (String[])hash.get( temp_key ) )[0] =
                (String)( (Hashtable)temp_perm_settings[1] ).get( temp_key );
            }
        }

        // Lets get the template file
        String htmlFile = "docinfo/change_meta_rights.html";
        String htmlStr = imcref.getAdminTemplate( htmlFile, user, null );

        // Lets fill the info from db into the vector vec

        String checks = "";
        for ( int i = 0; i < metatable.length; i += 3 ) {
            String temp = ( (String[])hash.get( metatable[i] ) )[0];
            String[] pd = {"&", "&amp;", "<", "&lt;", ">", "&gt;", "\"", "&quot;", };
            temp = Parser.parseDoc( temp, pd );
            String tag = "#" + metatable[i] + "#";
            if ( NORMAL.equals( metatable[i + 2] ) ) {			// This is not a checkbox or an optionbox
                if ( htmlStr.indexOf( tag ) == -1 ) {
                    checks += "<input type=\"hidden\" name=\"" + metatable[i] + "\" value=\"" + temp + "\">";
                } else {
                    vec.add( tag );							// Replace its corresponding tag
                    vec.add( temp );
                }
            } else if ( CHECKBOX.equals( metatable[i + 2] ) ) {	// This is a checkbox
                if ( !temp.equals( metatable[i + 1] ) ) {	// If it is equal to the nullvalue, it must not appear (i.e. equal null)
                    if ( htmlStr.indexOf( tag ) == -1 ) {
                        checks += "<input type=\"hidden\" name=\"" + metatable[i] + "\" value=\"" + temp + "\">";
                    } else {
                        vec.add( tag );
                        vec.add( "checked" );
                    }
                }
            }

        }

        // Lets add the standard fileItemMap to the vector
        vec.add( "#meta_id#" );
        vec.add( "" + metaId );

        vec.add( "#parent_meta_id#" );
        vec.add( parent_meta_id );

        // "#checks#" contains the extra hidden fields that are put in as a substitute for
        // the missing fileItemMap.
        vec.add( "#checks#" );
        vec.add( checks );

        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( metaId );
        // Lets get the menu with the buttons
        String menuStr = imcref.getAdminButtons( user, document );
        vec.add( "#adminMode#" );
        vec.add( menuStr );

        // Lets get the owner from the db and add it to vec
        String owner = imcref.sqlQueryStr( "select rtrim(first_name)+' '+rtrim(last_name) from users join meta on users.user_id = meta.owner_id and meta.meta_id = ?",
                                           new String[]{"" + metaId} );
        vec.add( "#owner#" );
        if ( owner != null ) {
            vec.add( owner );
        } else {
            vec.add( "?" );
        }

        // Lets fix the date_today tag
        return imcref.getAdminTemplate( htmlFile, user, vec );
    }

    /**
     * getRolesFromDb collects the information for a certain meta_id regarding the
     * rolesrights and parses the information into the assigned htmlFile.
     */
    private static void getRolesFromDb( int meta_id, UserDomainObject user, List vec ) {

        ImcmsServices imcref = Imcms.getServices();

        // Lets get the roles_rights_table_header template file
        StringBuffer roles_rights = new StringBuffer( imcref.getAdminTemplate( PERMISSIONS_TABLE_HEAD_TEMPLATE, user, null ) );

        // Get the info from the user object.
        // "temp_perm_settings" is an array containing a stringified meta-id, a hashtable of meta-info (column=value),
        // and a hashtable of roles and their corresponding set_id for this page (role_id=set_id).
        // This array comes from selecting the permissionpage. People set a lot of stuff in the page,
        // and then they forget to press "Save" before pressing another button.
        // If they press another button, this array will be put in the user-object, to remember their settings.
        Object[] temp_perm_settings = (Object[])user.remove( USER_HASH_KEY__TEMPORARY_PERMISSION_SETTINGS );

        Map temp_perm_hash = null;
        String[] temp_default_templates = null;

        if ( temp_perm_settings != null && meta_id == Integer.parseInt( (String)temp_perm_settings[0] ) ) {		// Make sure this is the right document.
            temp_perm_hash = (Hashtable)temp_perm_settings[2];
            temp_default_templates = (String[])temp_perm_settings[3];
        }


        // Hey, hey! Watch as i fetch the permission-set set (pun intended) for each role!
        String[][] role_permissions = imcref.sqlProcedureMulti( "GetUserRolesDocPermissions",
                                                                new String[]{"" + meta_id, "" + user.getId()} );

        // Now watch as i fetch the permission_set for the user...
        String[] current_permissions = imcref.sqlProcedure( "GetUserPermissionSet",
                                                            new String[]{"" + meta_id, "" + user.getId()} );
        int user_set_id = Integer.parseInt( current_permissions[0] );
        int currentdoc_perms = Integer.parseInt( current_permissions[2] );		// A bitvector containing the permissions for this document. (For example if Set-id 1 is more privileged than Set-id 2 (bit 0))

        StringBuffer roles_no_rights = new StringBuffer();
        for ( int i = 0; i < role_permissions.length; ++i ) {
            // Get role_id and set_id for role.
            int role_set_id = Integer.parseInt( role_permissions[i][2] );
            String role_name = role_permissions[i][1];
            String role_id = role_permissions[i][0];
            // Check if we have a temporary setting saved, and then set the role_set_id to it.
            if ( temp_perm_hash != null ) {
                String temp_role_set_id = (String)temp_perm_hash.get( role_id );
                if ( temp_role_set_id != null ) {
                    role_set_id = Integer.parseInt( temp_role_set_id );
                }
            }
            // If the role has no permissions for this document, we put it away in a special html-optionlist.
            if ( role_set_id == DocumentPermissionSetDomainObject.TYPE_ID__NONE ) {
                roles_no_rights.append( "<option value=\"" + role_id + "\">" + role_name + "</option>" );
                roles_rights.append( "<input type=\"hidden\" name=\"role_" + role_id + "\" value=\"4\">" );
                // So... it's put away for later... we don't need it now.
                continue;
            }
            List vec2 = new ArrayList();
            vec2.add( "#role_name#" );
            vec2.add( role_name );
            vec2.add( "#user_role#" );
            vec2.add( String.valueOf( DocumentPermissionSetDomainObject.TYPE_ID__FULL ).equals( role_permissions[i][3] )
                      ? "" : "*" );

            for ( int j = DocumentPermissionSetDomainObject.TYPE_ID__FULL; j
                                                                           <= DocumentPermissionSetDomainObject.TYPE_ID__NONE; ++j ) { // From DOC_PERM_SET_FULL to DOC_PERM_SET_NONE (0 to 4)
                vec2.add( "#" + j + "#" );
                if ( user_set_id <= role_set_id		// User has more privileged set_id than role
                     && ( user_set_id <= j
                          && ( user_set_id != DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1
                               || j != DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2
                               || ( currentdoc_perms & ImcmsConstants.DOC_PERM_RESTRICTED_1_ADMINISTRATES_RESTRICTED_2 )
                                  != 0 ) )			// User has more privileged set_id than this set_id
                     && ( user_set_id != DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1
                          || role_set_id != DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2
                          || ( currentdoc_perms & ImcmsConstants.DOC_PERM_RESTRICTED_1_ADMINISTRATES_RESTRICTED_2 ) != 0 ) )	// User has set_id 1, and may modify set_id 2?
                {
                    vec2.add( "<input type=\"radio\" name=\"role_"
                              + role_id
                              + "\" value=\""
                              + j
                              + "\" "
                              + ( ( j == role_set_id ) ? "checked>" : ">" ) );
                } else {
                    vec2.add( ( j == role_set_id ) ? "*" : "O" );
                }
            }
            roles_rights.append( imcref.getAdminTemplate( PERMISSIONS_TABLE_ROW_TEMPLATE, user, vec2 ) );

        }
        vec.add( "#roles_no_rights#" );
        vec.add( roles_no_rights.toString() );

        roles_rights.append( imcref.getAdminTemplate( PERMISSIONS_TABLE_TAIL_TEMPLATE, user, null ) );
        vec.add( "#roles_rights#" );
        vec.add( roles_rights.toString() );

        if ( user_set_id < 2 ) {
            // If the permission_set_id of the user is 0 (full) or 1 (level 1 admin)
            // We want the buttons for defining permissionsets.

            List ftr = new ArrayList();

            int doc_type = imcref.getDocType( meta_id );
            String default_templates = "";//the string containing default-templates-option-list

            if ( user_set_id == DocumentPermissionSetDomainObject.TYPE_ID__FULL ) {
                List perm_vec = new ArrayList();
                if ( ( currentdoc_perms & ImcmsConstants.DOC_PERM_RESTRICTED_1_ADMINISTRATES_RESTRICTED_2 ) != 0 ) {
                    perm_vec.add( "#permissions#" );
                    perm_vec.add( "checked" );
                }
                String sets_precedence = imcref.getAdminTemplate( RESTRICTED_1_ADMINISTRATES_RESTRICTED_2_CHECKBOX_TEMPLATE, user, perm_vec );
                ftr.add( "#sets_precedence#" );
                ftr.add( sets_precedence );
                ftr.add( "#set_1#" );
                ftr.add( imcref.getAdminTemplate( DEFINE_RESTRICTED_1_BUTTON_TEMPLATE, user, null ) );
                ftr.add( "#set_2#" );
                ftr.add( imcref.getAdminTemplate( DEFINE_RESTRICTED_2_BUTTON_TEMPLATE, user, null ) );
                if ( doc_type == DocumentDomainObject.DOCTYPE_TEXT ) {
                    ftr.add( "#new_set_1#" );
                    ftr.add( imcref.getAdminTemplate( DEFINE_NEW_RESTRICTED_1_BUTTON_TEMPLATE, user, null ) );
                    ftr.add( "#new_set_2#" );
                    ftr.add( imcref.getAdminTemplate( DEFINE_NEW_RESTRICTED_2_BUTTON_TEMPLATE, user, null ) );
                    //ok lets setup the default_template-option-lists for restricted 1 & 2

                    default_templates =
                    getDefaultTemplateOptionList( imcref, temp_default_templates, meta_id, user, true );
                    ftr.add( "#default_templates#" );
                    ftr.add( default_templates );
                } else {
                    ftr.add( "#new_set_1#" );
                    ftr.add( "" );
                    ftr.add( "#new_set_2#" );
                    ftr.add( "" );
                    ftr.add( "#default_templates#" );
                    ftr.add( "" );
                }
                vec.add( "#define_sets#" );
                vec.add( imcref.getAdminTemplate( ALL_DEFINE_RESTRICTED_BUTTONS_TEMPLATE, user, ftr ) );

            } else if ( ( currentdoc_perms & ImcmsConstants.DOC_PERM_RESTRICTED_1_ADMINISTRATES_RESTRICTED_2 ) != 0 ) {

                ftr.add( "#sets_precedence#" );
                ftr.add( "" );
                ftr.add( "#set_1#" );
                ftr.add( "" );
                ftr.add( "#new_set_1#" );
                ftr.add( "" );
                ftr.add( "#set_2#" );
                ftr.add( imcref.getAdminTemplate( "permissions/set_2_button.html", user, null ) );
                if ( doc_type == DocumentDomainObject.DOCTYPE_TEXT ) {
                    default_templates =
                    getDefaultTemplateOptionList( imcref, temp_default_templates, meta_id, user, false );

                    ftr.add( "#new_set_2#" );
                    ftr.add( imcref.getAdminTemplate( "permissions/new_set_2_button.html", user, null ) );
                } else {
                    ftr.add( "#new_set_2#" );
                    ftr.add( "" );
                }
                ftr.add( "#default_templates#" );
                ftr.add( default_templates );
                vec.add( "#define_sets#" );
                vec.add( imcref.getAdminTemplate( "permissions/define_sets.html", user, ftr ) );
            } else {
                vec.add( "#define_sets#" );
                vec.add( "" );
            }

        } else {
            vec.add( "#define_sets#" );
            vec.add( "" );
        }

    } // End of getRolesFromDb

    private static String getDefaultTemplateOptionList( ImcmsServices imcref,
                                                        String[] def_templates, int meta_id,
                                                        UserDomainObject user,
                                                        boolean canEditRestricted1DefaultTemplate ) {
        String returnValue = "";
        //ok lets setup the default_template-option-lists for restricted 1 & 2
        String[][] templates = imcref.sqlProcedureMulti( "GetTemplates", new String[0] );
        if ( def_templates == null ) { //if we dont already have the ones to mark as selected
            def_templates =
            imcref.sqlQuery( "SELECT default_template_1,default_template_2 FROM text_docs WHERE meta_id=?",
                             new String[]{"" + meta_id} );
        }

        // We allocate a string to contain the default-template-option-list
        String options_templates_1 = "";
        if ( canEditRestricted1DefaultTemplate ) {
            String tempStr = "";
            for ( int i = 0; i < templates.length; i++ ) {
                String selected = "";
                if ( def_templates[0].equals( templates[i][0] ) ) {
                    selected = "selected";
                }
                tempStr += "<option value=\""
                           + templates[i][0]
                           + "\""
                           + selected
                           + ">"
                           + templates[i][1]
                           + "</option>\n\t";
            }
            List tempV = new ArrayList();
            tempV.add( "#templ_option_list#" );
            tempV.add( tempStr );
            options_templates_1 = imcref.getAdminTemplate( RESTRICTED_1_DEFAULT_TEMPLATE_CHOICE_TEMPLATE, user, tempV );
        }
        String options_templates_2 = "";
        for ( int i = 0; i < templates.length; i++ ) {
            String selected = "";
            if ( def_templates[1].equals( templates[i][0] ) ) {
                selected = "selected";
            }
            options_templates_2 +=
            "<option value=\"" + templates[i][0] + "\"" + selected + ">" + templates[i][1] + "</option>\n\t";
        }

        if ( !options_templates_1.equals( "" ) || !options_templates_2.equals( "" ) ) {
            List vect = new ArrayList();
            vect.add( "#def_templ_1#" );
            vect.add( options_templates_1 );
            vect.add( "#def_templ_2#" );
            vect.add( options_templates_2 );
            returnValue = imcref.getAdminTemplate( RESTRICTED_2_AND_MAYBE_1_DEFAULT_TEMPLATE_CHOICE_TEMPLATE, user, vect );
        }
        return returnValue;
    }// end getDefaultTemplateOptionList(...)

    /**
     * OK. Now to explain this to myself, the next time i read this crap.
     * This works like this: This parses one set of permissions for a document into a page of checkboxes and stuff.
     * This page is built of several templates found in the "admin/permissions" subdirectory.
     * The main template is "define_permissions.html" for the current document,
     * and "define_new_permissions.html" for new documents.
     * <p/>
     * This template contains the following tags:
     * <p/>
     * #meta_id#,    If you don't know what this is, then go away,
     * #set_id#,     The permission-set-id.
     * #1#,          Template for permission to change the headline
     * #2#,          Template for permission to change the docinfo
     * #4#,          Template for permission to change permissions
     * #doc_rights#  DOCUMENT-TYPE-SPECIFIC-RIGHTS-TEMPLATE HERE!
     * <p/>
     * The document-type-specific-rights-templates are the following ones:
     * <p/>
     * define_permissions_2.html
     * define_permissions_5.html
     * define_permissions_6.html
     * define_permissions_7.html
     * define_permissions_8.html
     * define_permissions_101.html
     * define_permissions_102.html
     * <p/>
     * The document-type-specific-rights-template contains additional tags in turn.
     * <p/>
     * For doctype 2 (define_permissions_2.html), these tags are the following:
     * <p/>
     * #65536#,   Template for permission to change texts
     * #131072#,  Template for permission to change images
     * #262144#,  Template for permission to change menus
     * #524288#,  Template for permission to change template
     * #1048576#  Template for permission to change includes
     * <p/>
     * Of these permissiontemplates (#1# to #1048576#) each contains
     * a tag like #check_2# (define_permission_2.html) or #check_65536# (define_permission_2_65536.html (editpermission for doc_type 2))
     * <p/>
     * So, what happens in this template is that the templates are read in the reverse order,
     */

    public static String parsePermissionSet( int meta_id, final UserDomainObject user, int set_id,
                                             boolean forNew ) {
        final ImcmsServices imcref = Imcms.getServices();
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( meta_id );

        List tags = new ArrayList();
        tags.add( "document" );
        tags.add( document );

        DocumentPermissionSetDomainObject documentPermissionSet = null;
        if ( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1 == set_id && !forNew ) {
            documentPermissionSet = document.getPermissionSetForRestrictedOne();
        } else if ( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2 == set_id && !forNew ) {
            documentPermissionSet = document.getPermissionSetForRestrictedTwo();
        } else if ( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1 == set_id && forNew ) {
            documentPermissionSet = document.getPermissionSetForRestrictedOneForNewDocuments();
        } else if ( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2 == set_id && forNew ) {
            documentPermissionSet = document.getPermissionSetForRestrictedTwoForNewDocuments();
        }

        if ( document instanceof TextDocumentDomainObject ) {
            TextDocumentPermissionSetDomainObject currentUsersDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)documentMapper.getDocumentPermissionSetForUser( document, user );
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)documentPermissionSet;
            SortedMap templateGroups = getAllowedTemplateGroupsMap( currentUsersDocumentPermissionSet, textDocumentPermissionSet );
            tags.add( "templateGroupsMap" );
            tags.add( templateGroups );

            SortedMap documentTypesMap = getAllowedDocumentTypesMap( currentUsersDocumentPermissionSet, textDocumentPermissionSet, documentMapper, user );
            tags.add( "documentTypesMap" );
            tags.add( documentTypesMap );
        }

        tags.add( "documentPermissionSet" );
        tags.add( documentPermissionSet );
        tags.add( "forNew" );
        tags.add( new Boolean( forNew ) );

        return imcref.getAdminTemplate( "permissions/define_permissions.html", user, tags );
    }

    private static SortedMap getAllowedDocumentTypesMap(
            TextDocumentPermissionSetDomainObject currentUsersDocumentPermissionSet,
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet, DocumentMapper documentMapper,
            final UserDomainObject user ) {
        int[] selectableDocumentTypeIds = currentUsersDocumentPermissionSet.getAllowedDocumentTypeIds();
        int[] selectedDocumentTypeIds = textDocumentPermissionSet.getAllowedDocumentTypeIds();
        Map documentTypes = documentMapper.getAllDocumentTypeIdsAndNamesInUsersLanguage( user );
        SortedMap documentTypesMap = new TreeMap();
        for ( Iterator iterator = documentTypes.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Integer documentTypeId = (Integer)entry.getKey();
            String documentTypeNameInUsersLanguage = (String)documentTypes.get( documentTypeId );
            IdNamePair documentType = new IdNamePair( documentTypeId.intValue(), documentTypeNameInUsersLanguage );
            if ( ArrayUtils.contains( selectableDocumentTypeIds, documentTypeId.intValue() ) ) {
                Boolean selected = new Boolean( ArrayUtils.contains( selectedDocumentTypeIds, documentTypeId.intValue() ) );
                documentTypesMap.put( documentType, selected );
            }
        }
        return documentTypesMap;
    }

    private static SortedMap getAllowedTemplateGroupsMap(
            TextDocumentPermissionSetDomainObject currentUsersDocumentPermissionSet,
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet ) {
        TemplateGroupDomainObject[] selectableTemplateGroups = currentUsersDocumentPermissionSet.getAllowedTemplateGroups();
        List selectedTemplateGroups = Arrays.asList( textDocumentPermissionSet.getAllowedTemplateGroups() );
        SortedMap templateGroups = new TreeMap();
        for ( int i = 0; i < selectableTemplateGroups.length; i++ ) {
            TemplateGroupDomainObject selectableTemplateGroup = selectableTemplateGroups[i];
            Boolean selected = new Boolean( selectedTemplateGroups.contains( selectableTemplateGroup ) );
            templateGroups.put( selectableTemplateGroup, selected );
        }
        return templateGroups;
    }

} // End of class
