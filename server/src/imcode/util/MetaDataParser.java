package imcode.util;

import imcode.external.diverse.Html;
import imcode.server.*;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.parser.AdminButtonParser;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;
import org.apache.commons.collections.Transformer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MetaDataParser {

    private final static String SECTION_MSG_TEMPLATE = "sections/admin_section_no_one_msg.html";

    private static final String ADVANCED_CHANGE_DOCINFO_TEMPLATE = "docinfo/adv_change_meta.html";
    private static final String SIMPLE_CHANGE_DOCINFO_TEMPLATE = "docinfo/change_meta.html";
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
    private static final String CATEGORIES_MULTICHOICE_LIST_TEMPLATE = "docinfo/category_list.html";
    private static final String CATEGORIES_SINGLECHOICE_DROPDOWN_TEMPLATE = "docinfo/category_dropdown.html";
    private static final String PUBLISHER_NONE_MSG_TEMPLATE = "docinfo/publisher_no_one_msg.html";
    public static final String USER_HASH_KEY__TEMPORARY_PERMISSION_SETTINGS = "temp_perm_settings";

    /**
     * parseMetaData collects the information for a certain meta_id from the db and
     * parses the information into the change_meta.html (the plain admin mode file).
     */
    static public String parseMetaData( String meta_id, String parent_meta_id, UserDomainObject user,
                                        String meta_image )
            throws IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        // Now watch as i fetch the permission_set for the user...
        String[] current_permissions = imcref.sqlProcedure( "GetUserPermissionSet",
                                                            new String[]{meta_id, "" + user.getUserId()} );
        int currentuser_set_id = Integer.parseInt( current_permissions[0] );
        int currentuser_perms = Integer.parseInt( current_permissions[1] );

        if ( currentuser_set_id == IMCConstants.DOC_PERM_SET_FULL || ( currentuser_perms & IMCConstants.PERM_EDIT_DOCINFO ) != 0 ) {
            return getMetaDataFromDb( meta_id, parent_meta_id, user, ADVANCED_CHANGE_DOCINFO_TEMPLATE, false,
                                      meta_image );
        } else {
            return getMetaDataFromDb( meta_id, parent_meta_id, user, SIMPLE_CHANGE_DOCINFO_TEMPLATE, false, meta_image );
        }
    } // end of parseMetaData

    /**
     * parseMetaPermission parses the page which consists of  the information for a certain meta_id from the db and
     * parses the information into the change_meta.html (the plain admin mode file).
     */
    public static String parseMetaPermission( String meta_id, String parent_meta_id, UserDomainObject user,
                                              String htmlFile, String meta_image ) throws IOException {
        return getMetaDataFromDb( meta_id, parent_meta_id, user, htmlFile, true, meta_image );
    }

    /**
     * getMetaDataFromDb collects the information for a certain meta_id from the db and
     * parses the information into the assigned htmlFile. If the htmlfile doesnt has
     * all the properties, hidden fields will be created by default into the htmlfile
     */
    static private String getMetaDataFromDb( String meta_id, String parent_meta_id, UserDomainObject user,
                                             String htmlFile, boolean showRoles, String meta_image )
            throws IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

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
            "meta_image", null, NORMAL,
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
        Hashtable hash = imcref.sqlProcedureHash( "GetDocumentInfo", new String[]{"" + meta_id} );

        if ( null != meta_image ) {
            hash.put( "meta_image", new String[]{meta_image} );
        }

        // Get the info from the user object.
        // "temp_perm_settings" is an array containing a stringified meta-id, a hashtable of meta-info (column=value),
        // and a hashtable of roles and their corresponding set_id for this page (role_id=set_id).
        // This array comes from selecting the permissionpage. People set a lot of stuff in the page,
        // and then they forget to press "Save" before pressing another button.
        // If they press another button, this array will be put in the user-object, to remember their settings.
        Object[] temp_perm_settings = (Object[])user.get( USER_HASH_KEY__TEMPORARY_PERMISSION_SETTINGS );

        Vector vec = new Vector();

        if ( showRoles == true ) {
            getRolesFromDb( meta_id, user, vec );
        }

        user.remove( USER_HASH_KEY__TEMPORARY_PERMISSION_SETTINGS );	// Forget about it, so it won't appear on a reload.

        if ( temp_perm_settings != null && meta_id.equals( temp_perm_settings[0] ) ) {		// Make sure this is the right document.
            // Copy everything from this temporary hashtable into the meta-hash.
            Enumeration temp_enum = ( (Hashtable)temp_perm_settings[1] ).keys();
            while ( temp_enum.hasMoreElements() ) {
                String temp_key = (String)temp_enum.nextElement();
                ( (String[])hash.get( temp_key ) )[0] =
                (String)( (Hashtable)temp_perm_settings[1] ).get( temp_key );
            }
        }

        // Lets get the template file
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

        String target = HTMLConv.toHTMLSpecial( ( (String[])hash.get( "target" ) )[0] );

        if ( "_self".equals( target ) || "_top".equals( target ) || "_blank".equals( target ) ) {
            vec.add( "#" + target + "#" );
            vec.add( "checked" );
            vec.add( "#frame_name#" );
            vec.add( "" );
        } else if ( target.length() == 0 ) {
            vec.add( "#_self#" );
            vec.add( "checked" );
            vec.add( "#frame_name#" );
            vec.add( "" );
        } else {
            vec.add( "#_other#" );
            vec.add( "checked" );
            vec.add( "#frame_name#" );
            vec.add( target );
        }

        // Here i'll select all classification-strings and
        // concatenate them into one semicolon-separated string.
        String[] classifications = imcref.sqlQuery(
                "select code from classification c join meta_classification mc on mc.class_id = c.class_id where mc.meta_id = ?",
                new String[]{meta_id} );
        String classification = "";
        if ( classifications.length > 0 ) {
            classification += HTMLConv.toHTMLSpecial( classifications[0] );
            for ( int i = 1; i < classifications.length; ++i ) {
                classification += ", " + HTMLConv.toHTMLSpecial( classifications[i] );
            }
        }

        SimpleDateFormat dateFormat_date = new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat dateFormat_time = new SimpleDateFormat( "HH:mm" );
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
        Date theDate;
        try {
            theDate = dateFormat.parse( ( (String[])hash.get( "activated_datetime" ) )[0] );
            vec.add( "#activated_date#" );
            vec.add( dateFormat_date.format( theDate ) );
            vec.add( "#activated_time#" );
            vec.add( dateFormat_time.format( theDate ) );
        } catch ( NullPointerException pe ) {
            vec.add( "#activated_date#" );
            vec.add( "" );
            vec.add( "#activated_time#" );
            vec.add( "" );
        } catch ( ParseException pe ) {
            vec.add( "#activated_date#" );
            vec.add( "" );
            vec.add( "#activated_time#" );
            vec.add( "" );
        }

        try {
            theDate = dateFormat.parse( ( (String[])hash.get( "archived_datetime" ) )[0] );
            vec.add( "#archived_date#" );
            vec.add( dateFormat_date.format( theDate ) );
            vec.add( "#archived_time#" );
            vec.add( dateFormat_time.format( theDate ) );
        } catch ( NullPointerException pe ) {
            vec.add( "#archived_date#" );
            vec.add( "" );
            vec.add( "#archived_time#" );
            vec.add( "" );
        } catch ( ParseException pe ) {
            vec.add( "#archived_date#" );
            vec.add( "" );
            vec.add( "#archived_time#" );
            vec.add( "" );
        }

        try {
            theDate = dateFormat.parse( ( (String[])hash.get( "date_created" ) )[0] );
            vec.add( "#date_created#" );
            vec.add( dateFormat_date.format( theDate ) );
            vec.add( "#created_time#" );
            vec.add( dateFormat_time.format( theDate ) );
        } catch ( NullPointerException pe ) {
            vec.add( "#date_created#" );
            vec.add( "" );
            vec.add( "#created_time#" );
            vec.add( "" );
        } catch ( ParseException pe ) {
            vec.add( "#date_created#" );
            vec.add( "" );
            vec.add( "#created_time#" );
            vec.add( "" );
        }

        try {
            theDate = dateFormat.parse( ( (String[])hash.get( "date_modified" ) )[0] );
            Date now = new Date();
            vec.add( "#new_date_modified#" );
            vec.add( dateFormat_date.format( now ) );
            vec.add( "#new_modified_time#" );
            vec.add( dateFormat_time.format( now ) );
            vec.add( "#date_modified#" );
            vec.add( dateFormat_date.format( theDate ) );
            vec.add( "#modified_time#" );
            vec.add( dateFormat_time.format( theDate ) );
        } catch ( NullPointerException pe ) {
            vec.add( "#date_modified#" );
            vec.add( "" );
            vec.add( "#modified_time#" );
            vec.add( "" );
        } catch ( ParseException pe ) {
            vec.add( "#date_modified#" );
            vec.add( "" );
            vec.add( "#modified_time#" );
            vec.add( "" );
        }

        vec.add( "#classification#" );
        vec.add( classification );

        // Lets add the standard fileItemMap to the vector
        vec.add( "#meta_id#" );
        vec.add( meta_id );

        vec.add( "#parent_meta_id#" );
        vec.add( parent_meta_id );

        // "#checks#" contains the extra hidden fields that are put in as a substitute for
        // the missing fileItemMap.
        vec.add( "#checks#" );
        vec.add( checks );

        // Lets get the menu with the buttons
        String menuStr = imcref.getMenuButtons( meta_id, user );
        vec.add( "#adminMode#" );
        vec.add( menuStr );

        // Lets get the owner from the db and add it to vec
        String owner = imcref.sqlQueryStr(
                "select rtrim(first_name)+' '+rtrim(last_name) from users join meta on users.user_id = meta.owner_id and meta.meta_id = ?",
                new String[]{meta_id} );
        vec.add( "#owner#" );
        if ( owner != null ) {
            vec.add( owner );
        } else {
            vec.add( "?" );
        }

        getSectionDataFromDbAndAddSectionRelatedTagsToParseList( imcref, meta_id, vec, user );

        addLanguageRelatedTagsForDocInfoPageToParseList( vec, hash, imcref, user );

        addPublisherRelatedTagsForDocInfoPageToParseList( vec, hash, imcref, user );

        vec.add( "#categories#" );
        vec.add( createHtmlListBoxesOfCategoriesForEachCategoryType( imcref.getDocumentMapper(),
                                                                     Integer.parseInt( meta_id ), imcref, user ) );

        // Lets fix the date_today tag
        vec.add( "#date_today#" );
        SimpleDateFormat dateformat = new SimpleDateFormat( "yyyy-MM-dd" );
        vec.add( dateformat.format( new Date() ) );

        return imcref.getAdminTemplate( htmlFile, user, vec );

    } // end of parseMetaData

    public static String createHtmlListBoxesOfCategoriesForEachCategoryType( DocumentMapper documentMapper,
                                                                             int documentId,
                                                                             IMCServiceInterface imcref,
                                                                             UserDomainObject user ) {
        StringBuffer result = new StringBuffer();
        DocumentDomainObject document = documentMapper.getDocument( documentId );
        CategoryTypeDomainObject[] categoryTypes = documentMapper.getAllCategoryTypes();
        for ( int i = 0; i < categoryTypes.length; i++ ) {
            CategoryTypeDomainObject categoryType = categoryTypes[i];

            List tags = new ArrayList();
            tags.add( "#category_type_name#" );
            tags.add( categoryType.getName() );
            tags.add( "#max_choices#" );
            tags.add( "" + categoryType.getMaxChoices() );

            tags.add( "#multiple#" );
            if ( 1 != categoryType.getMaxChoices() ) {
                tags.add( "multiple" );
            } else {
                tags.add( "" );
            }

            String categoryOptionList = Html.createOptionListOfCategoriesOfTypeForDocument( documentMapper, categoryType,
                                                                                         document );

            tags.add( "#category_options#" );
            tags.add( categoryOptionList.toString() );

            final String template = 1 == categoryType.getMaxChoices()
                                    ? CATEGORIES_SINGLECHOICE_DROPDOWN_TEMPLATE : CATEGORIES_MULTICHOICE_LIST_TEMPLATE;
            result.append( imcref.getAdminTemplate( template, user, tags ) );

        }
        return result.toString();
    }

    public static void addPublisherRelatedTagsForDocInfoPageToParseList( Vector vec, Hashtable hash,
                                                                         IMCServiceInterface imcref,
                                                                         UserDomainObject user ) {
        ImcmsAuthenticatorAndUserMapper userAndRoleMapper = imcref.getImcmsAuthenticatorAndUserAndRoleMapper();

        String publisherIdStr = ( (String[])hash.get( "publisher_id" ) )[0];
        UserDomainObject publisher = null;
        if ( null != publisherIdStr ) {
            publisher = userAndRoleMapper.getUser( Integer.parseInt( publisherIdStr ) );
        }

        vec.add( "#current_publisher_name#" );
        if ( null != publisher ) {
            vec.add( publisher.getLastName() + ", " + publisher.getFirstName() );
        } else {
            vec.add( imcref.getAdminTemplate( PUBLISHER_NONE_MSG_TEMPLATE, user, null ) );
        }

        String optionList = Html.createPublisherOptionList( userAndRoleMapper, publisher );

        vec.add( "#publisher_id#" );
        vec.add( optionList );
    }

    public static void addLanguageRelatedTagsForDocInfoPageToParseList( Vector vec, Hashtable hash,
                                                                        IMCServiceInterface imcref,
                                                                        UserDomainObject user ) {
        String documentLanguage = ( (String[])hash.get( "lang_prefix" ) )[0];
        documentLanguage = LanguageMapper.getAsIso639_2OrDefaultLanguage( documentLanguage, imcref );
        vec.add( "#lang_prefix#" );
        vec.add( LanguageMapper.getLanguageOptionList( imcref, user, documentLanguage ) );
        vec.add( "#current_language#" );
        vec.add( LanguageMapper.getCurrentLanguageNameInUsersLanguage( imcref, user, documentLanguage ) );
    }

    public static void getSectionDataFromDbAndAddSectionRelatedTagsToParseList( IMCServiceInterface imcref,
                                                                                String meta_id, Vector vec,
                                                                                UserDomainObject user ) {
        //**************** section index word stuff *****************
        //lets get the section stuff from db
        String[][] documentSections = imcref.sqlProcedureMulti( "SectionGetInheritId", new String[]{meta_id} );

        //lets add the stuff that ceep track of the inherit section id and name
        List selected = new ArrayList();
        vec.add( "#current_section_name#" );
        StringBuffer documentSectionNames = new StringBuffer();
        if ( 0 == documentSections.length ) {
            vec.add( imcref.getAdminTemplate( SECTION_MSG_TEMPLATE, user, null ) );
        } else {
            for ( int i = 0; i < documentSections.length; i++ ) {
                if ( 0 != i ) {
                    documentSectionNames.append( ", " );
                }
                String documentSectionId = documentSections[i][0];
                String documentSectionName = documentSections[i][1];
                selected.add( documentSectionId );
                documentSectionNames.append( documentSectionName );
            }
            vec.add( documentSectionNames.toString() );
        }

        //lets build the option list used when the admin whants to breake the inherit chain
        String[][] allSectionsArray = imcref.sqlProcedureMulti( "SectionGetAll", new String[0] );

        List allSections = new ArrayList();
        for ( int i = 0; i < allSectionsArray.length; i++ ) {
            allSections.add( allSectionsArray[i][0] );
            allSections.add( allSectionsArray[i][1] );
        }
        vec.add( "#section_option_list#" );
        vec.add( Html.createOptionList( allSections, selected ) );
    }

    /**
     * getRolesFromDb collects the information for a certain meta_id regarding the
     * rolesrights and parses the information into the assigned htmlFile.
     */
    private static void getRolesFromDb( String meta_id, UserDomainObject user, Vector vec ) {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        // Lets get the roles_rights_table_header template file
        StringBuffer roles_rights = new StringBuffer( imcref.getAdminTemplate( PERMISSIONS_TABLE_HEAD_TEMPLATE, user, null ) );

        // Get the info from the user object.
        // "temp_perm_settings" is an array containing a stringified meta-id, a hashtable of meta-info (column=value),
        // and a hashtable of roles and their corresponding set_id for this page (role_id=set_id).
        // This array comes from selecting the permissionpage. People set a lot of stuff in the page,
        // and then they forget to press "Save" before pressing another button.
        // If they press another button, this array will be put in the user-object, to remember their settings.
        Object[] temp_perm_settings = (Object[])user.remove( USER_HASH_KEY__TEMPORARY_PERMISSION_SETTINGS );

        Hashtable temp_perm_hash = null;
        String[] temp_default_templates = null;

        if ( temp_perm_settings != null && meta_id.equals( temp_perm_settings[0] ) ) {		// Make sure this is the right document.
            temp_perm_hash = (Hashtable)temp_perm_settings[2];
            temp_default_templates = (String[])temp_perm_settings[3];
        }


        // Hey, hey! Watch as i fetch the permission-set set (pun intended) for each role!
        String[][] role_permissions = imcref.sqlProcedureMulti( "GetUserRolesDocPermissions",
                                                                new String[]{meta_id, "" + user.getUserId()} );

        // Now watch as i fetch the permission_set for the user...
        String[] current_permissions = imcref.sqlProcedure( "GetUserPermissionSet",
                                                            new String[]{meta_id, "" + user.getUserId()} );
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
            if ( role_set_id == IMCConstants.DOC_PERM_SET_NONE ) {
                roles_no_rights.append( "<option value=\"" + role_id + "\">" + role_name + "</option>" );
                roles_rights.append( "<input type=\"hidden\" name=\"role_" + role_id + "\" value=\"4\">" );
                // So... it's put away for later... we don't need it now.
                continue;
            }
            Vector vec2 = new Vector();
            vec2.add( "#role_name#" );
            vec2.add( role_name );
            vec2.add( "#user_role#" );
            vec2.add( String.valueOf( IMCConstants.DOC_PERM_SET_FULL ).equals( role_permissions[i][3] ) ? "" : "*" );

            // // As we all know... 0 is full, 3 is read, 4 is none, and 1 and 2 are "other"
            // // Btw, 'none' doesn't really have a value, but is rather the absence of a value.
            // // I just use 4 here because i have to distinguish the absence of a value from a value that is about to be removed.
            // // FIXME: Hire the mafia to force me to put these as constants in an interface.

            // Update: Hey, hey! After finding a horse's head in my bed, i decided to create imcode.server.IMCConstants...

            for ( int j = IMCConstants.DOC_PERM_SET_FULL; j <= IMCConstants.DOC_PERM_SET_NONE; ++j ) { // From DOC_PERM_SET_FULL to DOC_PERM_SET_NONE (0 to 4)
                vec2.add( "#" + j + "#" );
                if ( user_set_id <= role_set_id		// User has more privileged set_id than role
                     && ( user_set_id <= j
                          && ( user_set_id != IMCConstants.DOC_PERM_SET_RESTRICTED_1
                               || j != IMCConstants.DOC_PERM_SET_RESTRICTED_2
                               || ( currentdoc_perms & IMCConstants.DOC_PERM_RESTRICTED_1_ADMINISTRATES_RESTRICTED_2 )
                                  != 0 ) )			// User has more privileged set_id than this set_id
                     && ( user_set_id != IMCConstants.DOC_PERM_SET_RESTRICTED_1
                          || role_set_id != IMCConstants.DOC_PERM_SET_RESTRICTED_2
                          || ( currentdoc_perms & IMCConstants.DOC_PERM_RESTRICTED_1_ADMINISTRATES_RESTRICTED_2 ) != 0 ) )	// User has set_id 1, and may modify set_id 2?
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

            Vector ftr = new Vector();

            int doc_type = imcref.getDocType( Integer.parseInt( meta_id ) );
            String default_templates = "";//the string containing default-templates-option-list

            if ( user_set_id == IMCConstants.DOC_PERM_SET_FULL ) {
                Vector perm_vec = new Vector();
                if ( ( currentdoc_perms & IMCConstants.DOC_PERM_RESTRICTED_1_ADMINISTRATES_RESTRICTED_2 ) != 0 ) {
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

            } else if ( ( currentdoc_perms & IMCConstants.DOC_PERM_RESTRICTED_1_ADMINISTRATES_RESTRICTED_2 ) != 0 ) {

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

    private static String getDefaultTemplateOptionList( IMCServiceInterface imcref,
                                                        String[] def_templates, String meta_id,
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
            Vector tempV = new Vector();
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
            Vector vect = new Vector();
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
                                             boolean for_new ) {
        final IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        // Lets get the langprefix
        final String lang_prefix = user.getLanguageIso639_2();

        String newstr = "";
        int doc_type = imcref.getDocType( meta_id );

        if ( for_new ) {		// This is the permissions for newly created documents.
            // Only applicable for text-docs (2)
            // since this is the only doc-type that can create new documents.
            // For new documents we set textdoc-permissions.too, since
            // text-doc is the only doc-type with multiple possible permissions.
            // Permission to create other doc-types gives permission to edit them.
            // It would be silly to be able to create, for example, an url, and not be able to change it.
            // FIXME: When we get more doc-types that have multiple permissions, (conference?) we need to change this,
            // to allow for setting permissions for all those doc-types for new documents.
            // We'll then have to output a permissionform for all the different doc-types.
            // In case we get other doc-types in which we can create documents we also need to change this.

            if ( doc_type != DocumentDomainObject.DOCTYPE_TEXT ) {
                return "";
            }
            newstr = "New";

        }

        // Here i fetch the current users set-id and the document-permissions for this document (Whether set-id 1 is more privileged than set-id 2.)
        String[] current_permissions = imcref.sqlProcedure( "GetUserPermissionSet",
                                                            new String[]{"" + meta_id, "" + user.getUserId()} );
        int user_set_id = Integer.parseInt( current_permissions[0] );
        int user_perm_set = Integer.parseInt( current_permissions[1] );
        int currentdoc_perms = Integer.parseInt( current_permissions[2] );

        // Create an anonymous adminbuttonparser that retrieves the file from the server instead of from the disk.
        AdminButtonParser adminButtonParser = new AdminButtonParser( "permissions/define_permission_" + doc_type + "_", ".html",
                                                       user_set_id, user_perm_set ) {
            protected StringBuffer getContent( String name ) {
                return new StringBuffer( imcref.getAdminTemplate( name, user, null ) );
            }
        };


        // Fetch all permissions this permissionset consists of.
        // Permission_id, Description, Value
        // One row for each permission on the system.
        // MAKE SURE the tables permissions and doc_permissions contain the permissions in use on this system!
        // FIXME: It is time to make an Interface that will define all permission-constants, doc-types, and such.
        // Remind me when i get a minute off some day.
        // Update! Check out imcode.server.IMCConstants
        String[] permissionset = imcref.sqlProcedure( "Get" + newstr + "PermissionSet",
                                                      new String[]{"" + meta_id, "" + set_id, lang_prefix} );

        final int ps_cols = 3;
        for ( int i = 0; i < permissionset.length; i += ps_cols ) {
            if ( !"0".equals( permissionset[i + 2] ) ) {
                adminButtonParser.put( "check_" + permissionset[i], "checked" );
            } else {
                adminButtonParser.put( "check_" + permissionset[i], "" );
            }
        }

        // Fetch all doctypes from the db and put them in an option-list
        // First, get the doc_types the current user may use.

        String[] user_dt = imcref.sqlProcedure( "GetDocTypesWith" + newstr + "Permissions",
                                                new String[]{"" + meta_id, "" + user_set_id, lang_prefix} );
        HashSet user_doc_types = new HashSet();

        // I'll fill a HashSet with all the doc-types the current user may use,
        // for easy retrieval.
        // A value of "-1" means the user may not use it.
        for ( int i = 0; i < user_dt.length; i += 3 ) {
            if ( !"-1".equals( user_dt[i + 2] ) ) {
                user_doc_types.add( user_dt[i] );
            }
        }

        // Now we get the doc_types the set-id we are editing may use.
        String[] doctypes = imcref.sqlProcedure( "GetDocTypesWith" + newstr + "Permissions",
                                                 new String[]{"" + meta_id, "" + set_id, lang_prefix} );
        // We allocate a string to contain the option-list
        String options_doctypes = "";
        for ( int i = 0; i < doctypes.length; i += 3 ) {
            // Check if the current user may set this doc-type for any set-id
            if ( user_set_id == 0			// If current user has full rights,
                 || ( user_set_id == 1	// or has set-id 1
                      && set_id == 2		// and is changing set-id 2
                      && user_doc_types.contains( doctypes[i] )	// and the user may use this doc-type.
                      && ( currentdoc_perms & 1 ) != 0// and set-id 1 is more privleged than set-id 2 for this document. (Bit 0)
                    ) ) {

                options_doctypes +=
                "<option value=\"8_"
                + doctypes[i]
                // Check if the set-id may currently use this doc-type
                + ( ( !"-1".equals( doctypes[i + 2] ) ) ? "\" selected>" : "\">" )
                + doctypes[i
                           + 1]
                + "</option>";
            }
        }
        adminButtonParser.put( "doctypes", options_doctypes );

        // Fetch all templategroups from the db and put them in an option-list
        // First we get the templategroups the current user may use
        String[] user_tg = imcref.sqlProcedure( "GetTemplateGroupsWith" + newstr + "Permissions",
                                                new String[]{"" + meta_id, "" + user_set_id} );

        HashSet user_templategroups = new HashSet();

        // I'll fill a HashSet with all the templategroups the current user may use,
        // for easy retrieval.
        for ( int i = 0; i < user_tg.length; i += 3 ) {
            if ( !"-1".equals( user_tg[i + 2] ) ) {
                user_templategroups.add( user_tg[i] );
            }
        }

        // Now we get the templategroups the set-id we are editing may use.
        String[] templategroups = imcref.sqlProcedure( "GetTemplateGroupsWith" + newstr + "Permissions",
                                                       new String[]{"" + meta_id, "" + set_id} );
        // We allocate a string to contain the option-list
        String options_templategroups = "";
        for ( int i = 0; i < templategroups.length; i += 3 ) {
            // Check if the current user may set this templategroup for any set-id (May he use it himself?)
            if ( user_set_id == 0			// If current user has full rights,
                 || ( user_set_id == 1	// or has set-id 1
                      && set_id == 2		// and is changing set-id 2
                      && user_templategroups.contains( templategroups[i] )	// and the user may use this group.
                      && ( currentdoc_perms & 1 ) != 0// and set-id 1 is more privleged than set-id 2 for this document. (Bit 0)
                    ) ) {
                options_templategroups +=
                "<option value=\"524288_"
                + templategroups[i]

                + ( ( !"-1".equals( templategroups[i + 2] ) ) ? "\" selected>" : "\">" )
                + templategroups[i
                                 + 1]
                + "</option>";
            }
        }
        adminButtonParser.put( "templategroups", options_templategroups );

        adminButtonParser.put( "set_id", String.valueOf( set_id ) );

        adminButtonParser.put( "meta_id", String.valueOf( meta_id ) );

        // Put the values for all the tags inserted in vec so far in the "define_permissions_"+doc_type+".html" file
        // That is, the doc-specific
        StringBuffer doc_specific = new StringBuffer(
                imcref.getAdminTemplate( "permissions/define_permissions_" + doc_type + ".html", user, null ) );

        Parser.parseTags( doc_specific, '#', " <>\"\n\r\t", adminButtonParser, true, 1 );

        adminButtonParser.put( "doc_rights", doc_specific.toString() );

        StringBuffer complete;
        if ( for_new ) {
            complete =
            new StringBuffer( imcref.getAdminTemplate( "permissions/define_new_permissions.html", user, null ) );
        } else {
            complete = new StringBuffer( imcref.getAdminTemplate( "permissions/define_permissions.html", user, null ) );
        }

        adminButtonParser.setPrefix( "permissions/define_permission_" );

        return Parser.parseTags( complete, '#', " <>\"\n\r\t", adminButtonParser, true, 1 ).toString();
    }

} // End of class
