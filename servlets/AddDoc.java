
import java.io.*;
import java.util.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.*;
import imcode.external.diverse.*;
import imcode.server.*;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;

/**
 Adds a new document to a menu.
 Shows an empty metadata page, which calls SaveNewMeta
 */
public class AddDoc extends HttpServlet {

    /**
     init()
     */
    public void init( ServletConfig config ) throws ServletException {
        super.init( config );
    }

    /**
     doPost()
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {


        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        String start_url = imcref.getStartUrl();

        res.setContentType( "text/html" );
        Writer out = res.getWriter();

        String meta_id = req.getParameter( "meta_id" );
        int meta_id_int = Integer.parseInt( meta_id );

        String item_selected = req.getParameter( "edit_menu" );
        String doc_menu_no = req.getParameter( "doc_menu_no" );
        String doc_type = "2";

        // Check if user logged on
        UserDomainObject user;
        if( (user = Check.userLoggedOn( req, res, start_url )) == null ) {
            return;
        }
        String lang_prefix = user.getLangPrefix();

        boolean userHasRights = DocumentMapper.checkUsersRights( imcref, user, meta_id, lang_prefix, doc_type );

        if( !"0".equals( item_selected ) && !userHasRights ) {
            String output = AdminDoc.adminDoc( meta_id_int, meta_id_int, user, req, res );
            if( output != null ) {
                out.write( output );
            }
            return;
        }

        // Lets detect the doctype were gonna add
        if( item_selected.equals( "2" ) ) {
            doc_type = "2";
        } else if( item_selected.equals( "8" ) ) {
            doc_type = "8";
        } else if( item_selected.equals( "6" ) ) {
            doc_type = "6";
        } else if( item_selected.equals( "7" ) ) {
            doc_type = "7";
        } else if( item_selected.equals( "0" ) ) { // its an existing document
            Vector vec = new Vector();
            vec.add( "#meta_id#" );
            vec.add( meta_id );
            vec.add( "#doc_menu_no#" );
            vec.add( doc_menu_no );

            // Lets get todays date
            SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );
            Date toDay = new Date();
            vec.add( "#start_date#" );
            vec.add( formatter.format( toDay ) );
            vec.add( "#end_date#" );
            vec.add( formatter.format( toDay ) );

            vec.add( "#searchstring#" );
            vec.add( "" );

            vec.add( "#searchResults#" );
            vec.add( "" );

            // Lets fix the sortby list, first get the displaytexts from the database
            String[] sortOrder = imcref.sqlProcedure( "SortOrder_GetExistingDocs '" + lang_prefix + "'" );
            Vector sortOrderV = this.convert2Vector( sortOrder );
            sortOrderV.copyInto( sortOrder );
            Html htm = new Html();
            String sortOrderStr = htm.createHtmlOptionList( "", sortOrderV );
            vec.add( "#sortBy#" );
            vec.add( sortOrderStr );

            // Lets set all the the documenttypes as selected in the html file
            String[] allDocTypesArray = imcref.getDocumentTypesInList( lang_prefix );
            for( int i = 0; i < allDocTypesArray.length; i += 2 ) {
                vec.add( "#checked_" + allDocTypesArray[i] + "#" );
                vec.add( "checked" );
            }

            // Lets set the create/ change types as selected in the html file
            String[] allPossibleIncludeDocsValues = {"created", "changed"};
            for( int i = 0; i < allPossibleIncludeDocsValues.length; i++ ) {
                vec.add( "#include_check_" + allPossibleIncludeDocsValues[i] + "#" );
                vec.add( "checked" );
            }

            // Lets set the and / or search preposition
            String[] allPossibleSearchPreps = {"and", "or"};
            for( int i = 0; i < allPossibleSearchPreps.length; i++ ) {
                vec.add( "#search_prep_check_" + allPossibleSearchPreps[i] + "#" );
                if( i == 0 ) {
                    vec.add( "checked" );
                } else {
                    vec.add( "" );
                }
            }
            // Lets parse the html page which consists of the add an existing doc
            out.write( imcref.parseDoc( vec, "existing_doc.html", lang_prefix ) );
            return;

        } else if( item_selected.equals( "5" ) ) {
            doc_type = "5";
        } else {
            doc_type = item_selected;
        }

        final int NORMAL = 0;
        final int CHECKBOX = 1;
        final int OPTION = 2;
        final int OTHER = 3;

        String[] metatable = {/*  Nullable			Nullvalue */
            "shared", "0",
            "disable_search", "0",
            "archive", "0",
            "show_meta", "0",
            "permissions", "1",
            "meta_image", null,
            "frame_name", null,
            "target", null,
            "lang_prefix", null};

        int metatabletype[] = {CHECKBOX,
                               CHECKBOX,
                               CHECKBOX,
                               CHECKBOX,
                               NORMAL,
                               NORMAL,
                               NORMAL,
                               OPTION,
                               OTHER};

        // Lets get the meta information
        String sqlStr = "select * from meta where meta_id = " + meta_id;
        Hashtable hash = imcref.sqlQueryHash( sqlStr );

        // Lets get the html template file

        String htmlStr;

        String advanced = "";

        if( imcref.checkDocAdminRights( meta_id_int, user, 2 ) ) {
            advanced = "adv_";
        }

        if( item_selected.equals( "2" ) ) {
            htmlStr = imcref.parseDoc( null, advanced + "new_meta_text.html", lang_prefix );
        } else {
            htmlStr = imcref.parseDoc( null, advanced + "new_meta.html", lang_prefix );
        }

        Vector vec = new Vector();
        String checks = "";
        for( int i = 0; i < metatable.length; i += 2 ) {
            String value = ((String[])hash.get( metatable[i] ))[0];
            value = escapeForHtml( value );
            String tag = "#" + metatable[i] + "#";
            if( metatabletype[i / 2] == NORMAL ) {			// This is not a checkbox or an optionbox
                if( htmlStr.indexOf( tag ) == -1 ) {
                    checks += "<input type=hidden name=\"" + metatable[i] + "\" value=\"" + value + "\">";
                } else {
                    vec.add( tag );							// Replace its corresponding tag
                    vec.add( value );
                }
            } else if( metatabletype[i / 2] == CHECKBOX ) {	// This is a checkbox
                if( !value.equals( metatable[i + 1] ) ) {	// If it is equal to the nullvalue, it must not appear (i.e. equal null)
                    if( htmlStr.indexOf( tag ) == -1 ) {
                        checks += "<input type=hidden name=\"" + metatable[i] + "\" value=\"" + value + "\">";
                    } else {
                        vec.add( tag );
                        vec.add( "checked" );
                    }
                }
            } else if( metatabletype[i / 2] == OPTION ) {	// This is an optionbox
                if( htmlStr.indexOf( "#" + value + "#" ) == -1 ) {	// There is no tag equal to the value of this
                    if( htmlStr.indexOf( tag ) == -1 ) {
                        checks += "<input type=hidden name=\"" + metatable[i] + "\" value=\"" + value + "\">";
                    } else {
                        vec.add( tag );							// Replace its corresponding tag
                        vec.add( value );
                    }
                } else {
                    vec.add( "#" + value + "#" );
                    vec.add( "checked" );
                }
            }
        }

        // Lets add the standard meta information
        vec.add( "#parent_meta_id#" );
        vec.add( ((String[])hash.get( "meta_id" ))[0] );

        // Lets get the permission stuff and put it into hidden fields, we'll need them later
        /*
          hash = imcref.sqlQueryHash( "select role_id, set_id from roles_rights where meta_id = "+meta_id ) ;
          if (hash != null) {
          String[] role_id = (String[])hash.get("role_id") ;
          String[] permission_id = (String[])hash.get("permission_id") ;
          if ( role_id != null ) {
          for ( int i=0 ; i<role_id.length ; i++ ) {
          checks += "<input type=hidden name=\"roles_rights\" value=\""+role_id[i]+"_"+permission_id[i]+"\">" ;
          }
          }
          }
        */
        // Here i'll select all classification-strings and
        // concatenate them into one semicolon-separated string.
        String classification = DocumentMapper.getClassificationsAsOneString( imcref, Integer.parseInt(meta_id) );

        vec.add( "#classification#" );
        vec.add( classification );

        // Lets fix the date information (date_created, modified etc)
        Date dt = imcref.getCurrentDate();
        SimpleDateFormat dateformat = new SimpleDateFormat( "yyyy-MM-dd" );
        //		checks += "<input type=hidden name=\"date_created\" value=\""+DATE_FORMAT.format(dt)+"\">" ;
        //		checks += "<input type=hidden name=\"date_modified\" value=\""+DATE_FORMAT.format(dt)+"\">" ;
        //		checks += "<input type=hidden name=\"activated_date\" value=\""+DATE_FORMAT.format(dt)+"\">" ;

        vec.add( "#activated_date#" );
        vec.add( dateformat.format( dt ) );
        dateformat = new SimpleDateFormat( "HH:mm" );
        vec.add( "#activated_time#" );
        vec.add( dateformat.format( dt ) );

        //		checks += "<input type=hidden name=\"activated_time\" value=\""+DATE_FORMAT.format(dt)+"\">"	;

        vec.add( "#checks#" );
        vec.add( checks );

        // Lets add the document informtion, the creator etc
        vec.add( "#doc_menu_no#" );
        vec.add( doc_menu_no );
        vec.add( "#doc_type#" );
        vec.add( doc_type );
        //		vec.add("#owner#") ;
        //		vec.add(user.getString("first_name")+" "+user.getString("last_name")) ;


        //**************** section index word stuff *****************
        //lets get the section stuff from db
        String[] parent_section = DocumentMapper.sprocSectionGetInheritId( imcref, Integer.parseInt(meta_id) );
        //lets add the stuff that ceep track of the inherit section id and name
        if( parent_section == null || 0 == parent_section.length ) {
            vec.add( "#current_section_id#" );
            vec.add( "-1" );
            vec.add( "#current_section_name#" );
            vec.add( imcref.parseDoc( null, MetaDataParser.SECTION_MSG_TEMPLATE, lang_prefix ) );
        } else {
            vec.add( "#current_section_id#" );
            vec.add( parent_section[0] );
            vec.add( "#current_section_name#" );
            vec.add( parent_section[1] );
        }

        MetaDataParser.addLanguageRelatedTagsForDocInfoPageToParseList(vec,hash,imcref,user);
        
        //lets build the option list used when the admin whants to breake the inherit chain
        String[] all_sections = imcref.sqlProcedure( "SectionGetAll" );
        Vector onlyTemp = new Vector();
        String option_list = "";
        String selected = "-1";
        if( all_sections != null ) {
            for( int i = 0; i < all_sections.length; i++ ) {
                onlyTemp.add( all_sections[i] );
            }
            if( parent_section != null && parent_section.length > 0) {
                    selected = parent_section[0];
            }

            option_list = Html.createHtmlOptionList( selected, onlyTemp );
        }
        vec.add( "#section_option_list#" );
        vec.add( option_list );
        //**************** end section index word stuff *************



        // Lets parse the information and send it to the browser
        if( item_selected.equals( "2" ) ) {
            out.write( imcref.parseDoc( vec, advanced + "new_meta_text.html", lang_prefix ) );
        } else {
            out.write( imcref.parseDoc( vec, advanced + "new_meta.html", lang_prefix ) );
        }

    }

    private static String escapeForHtml( String text ) {
        String[] pd = {"<", "&lt;",
                       ">", "&gt;",
                       "\"", "&quot;",
                       "&", "&amp;"};
        text = Parser.parseDoc( text, pd );
        return text;
    }

    /**
     * Convert array to vector
     */

    private static Vector convert2Vector( String[] arr ) {
        if( arr == null )
            return new Vector();

        Vector v = new Vector( arr.length );
        for( int i = 0; i < arr.length; i++ )
            v.add( arr[i] );
        return v;
    }

}
