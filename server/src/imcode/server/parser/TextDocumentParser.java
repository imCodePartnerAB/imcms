package imcode.server.parser;

import imcode.readrunner.ReadrunnerFilter;
import imcode.server.DocumentRequest;
import imcode.server.IMCConstants;
import imcode.server.IMCService;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.TemplateDomainObject;
import imcode.server.document.TemplateMapper;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.DateHelper;
import imcode.util.FileCache;
import org.apache.log4j.Category;
import org.apache.oro.text.regex.*;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TextDocumentParser implements imcode.server.IMCConstants {

    private static Category log = Category.getInstance( "TextDocumentParser" );
    private FileCache fileCache = new FileCache();

    private final static org.apache.oro.text.perl.Perl5Util perl5util = new org.apache.oro.text.perl.Perl5Util(); // Internally synchronized

    static Pattern HASHTAG_PATTERN = null;
    private static Pattern MENU_PATTERN = null;
    private static Pattern IMCMS_TAG_PATTERN = null;
    private static Pattern HTML_TAG_PATTERN = null;

    private static Pattern READRUNNER_END_TITLE_PATTERN = null;
    private static Pattern READRUNNER_END_HEAD_PATTERN = null;
    private static Pattern READRUNNER_START_BODY_PATTERN = null;
    private static Pattern READRUNNER_END_BODY_PATTERN = null;

    static {
        Perl5Compiler patComp = new Perl5Compiler();
        try {
            // OK, so this pattern is simple, ugly, and prone to give a lot of errors.
            // Very good. Very good. Know something? NO SOUP FOR YOU!
            HTML_TAG_PATTERN = patComp.compile( "<[^>]+?>", Perl5Compiler.READ_ONLY_MASK );

            IMCMS_TAG_PATTERN = patComp.compile( "<\\?imcms:([-\\w]+)(.*?)\\?>", Perl5Compiler.SINGLELINE_MASK | Perl5Compiler.READ_ONLY_MASK );
            HASHTAG_PATTERN = patComp.compile( "#[^ #\"<>&;\\t\\r\\n]+#", Perl5Compiler.READ_ONLY_MASK );
            MENU_PATTERN = patComp.compile( "<\\?imcms:menu(.*?)\\?>(.*?)<\\?\\/imcms:menu\\?>", Perl5Compiler.SINGLELINE_MASK | Perl5Compiler.READ_ONLY_MASK );

            READRUNNER_END_TITLE_PATTERN = patComp.compile( "(</title>)", Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.CASE_INSENSITIVE_MASK );
            READRUNNER_END_HEAD_PATTERN = patComp.compile( "(</head>)", Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.CASE_INSENSITIVE_MASK );
            READRUNNER_START_BODY_PATTERN = patComp.compile( "(<body.*?)(>)", Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.CASE_INSENSITIVE_MASK );
            READRUNNER_END_BODY_PATTERN = patComp.compile( "(</body>)", Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.CASE_INSENSITIVE_MASK );

        } catch ( MalformedPatternException ignored ) {
            // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
            log.fatal( "Bad pattern.", ignored );
        }
    }

    private IMCServiceInterface serverObject;
    private File templatePath;
    private File includePath;
    private String imageUrl;

    public TextDocumentParser( IMCServiceInterface serverobject, File templatepath, File includepath, String imageurl ) {
        this.templatePath = templatepath;
        this.includePath = includepath;
        this.imageUrl = imageurl;
        this.serverObject = serverobject;
    }

    /*
       return a referens to IMCServerInterface used by TextDocumentParser
    */
    public IMCServiceInterface getServerObject() {
        return this.serverObject;
    }

    public String parsePage( DocumentRequest documentRequest, int flags, ParserParameters paramsToParse ) throws IOException {
        return parsePage( documentRequest, flags, 5, paramsToParse );
    }

    public String parsePage( DocumentRequest documentRequest, int flags, int includelevel, ParserParameters paramsToParse ) throws IOException {
        try {
            DocumentDomainObject myDoc = documentRequest.getDocument();
            int meta_id = myDoc.getMetaId();
            String meta_id_str = String.valueOf( meta_id );

            UserDomainObject user = documentRequest.getUser();
            int user_id = user.getUserId();
            String user_id_str = String.valueOf( user_id );


            //handles the extra parameters
            String template_name = paramsToParse.getTemplate();
            String param_value = paramsToParse.getParameter();
            String extparam_value = paramsToParse.getExternalParameter();

            String[] user_permission_set = ImcmsAuthenticatorAndUserMapper.sprocGetUserPermissionSet( serverObject, meta_id_str, user_id_str );
            if ( user_permission_set == null ) {
                log.error( "parsePage: GetUserPermissionset returned null" );
                return ( "GetUserPermissionset returned null" );
            }

            int user_set_id = Integer.parseInt( user_permission_set[0] );
            int user_perm_set = Integer.parseInt( user_permission_set[1] );

            boolean textmode = false;
            boolean imagemode = false;
            boolean menumode = false;
            boolean templatemode = false;
            boolean includemode = false;

            if ( flags > 0 ) {

                textmode = ( flags & PERM_DT_TEXT_EDIT_TEXTS ) != 0 && ( user_set_id == 0 || ( user_perm_set & PERM_DT_TEXT_EDIT_TEXTS ) != 0 );
                imagemode = ( flags & PERM_DT_TEXT_EDIT_IMAGES ) != 0 && ( user_set_id == 0 || ( user_perm_set & PERM_DT_TEXT_EDIT_IMAGES ) != 0 );
                menumode = ( flags & PERM_DT_TEXT_EDIT_MENUS ) != 0 && ( user_set_id == 0 || ( user_perm_set & PERM_DT_TEXT_EDIT_MENUS ) != 0 );
                templatemode = ( flags & PERM_DT_TEXT_CHANGE_TEMPLATE ) != 0 && ( user_set_id == 0 || ( user_perm_set & PERM_DT_TEXT_CHANGE_TEMPLATE ) != 0 );
                includemode = ( flags & PERM_DT_TEXT_EDIT_INCLUDES ) != 0 && ( user_set_id == 0 || ( user_perm_set & PERM_DT_TEXT_EDIT_INCLUDES ) != 0 );
            }

            String[] included_docs = DocumentMapper.sprocGetIncludes( serverObject, meta_id );

            TemplateDomainObject documentTemplate = myDoc.getTemplate();
            int documentTemplateId = documentTemplate.getId();
            String simple_name = documentTemplate.getSimple_name();
            int sort_order = myDoc.getMenuSortOrder();
            String group_id = "" + myDoc.getTemplateGroupId();

            if ( template_name != null ) {
                //lets validate that the template exists before we changes the original one
                String[] vectT = serverObject.sqlProcedure( "GetTemplateId", new String[]{template_name} );
                if ( vectT.length > 0 ) {
                    try {
                        int temp_template = Integer.parseInt( (String)vectT[0] );
                        if ( temp_template > 0 ) {
                            documentTemplateId = temp_template;
                            documentRequest.getDocument().setTemplate( TemplateMapper.getTemplate( (IMCService)serverObject, documentTemplateId ) );
                        }
                    } catch ( NumberFormatException nfe ) {
                        //do nothing, we keep the original template
                    }
                }
            }

            String lang_prefix = user.getLangPrefix();	// Find language

            String[] emp = (String[])user.get( "emphasize" );

            String[] metaIdUserIdPair = {meta_id_str, user_id_str};

            // Here we have the most timeconsuming part of parsing the page.
            // Selecting all the documents with permissions from the DB
            String[] childs = serverObject.sqlProcedure( "getChilds", metaIdUserIdPair );

            if ( childs == null ) {
                log.error( "parsePage: GetChilds returned null" );
                return ( "GetChilds returned null" );
            }

            // Get the images from the db
            // sqlStr = "select '#img'+convert(varchar(5), name)+'#',name,imgurl,linkurl,width,height,border,v_space,h_space,image_name,align,alt_text,low_scr,target,target_name from images where meta_id = " + meta_id ;
            //					0                    1    2      3       4     5      6      7       8       9          10    11       12      13     14
            String[] images = serverObject.sqlProcedure( "GetImgs", new String[]{"" + meta_id} );
            if ( images == null ) {
                log.error( "parsePage: GetImgs returned null" );
                return ( "GetImgs returned null" );
            }

            File admintemplate_path = new File( templatePath, "/" + lang_prefix + "/admin/" );

            String emphasize_string = fileCache.getCachedFileString( new File( admintemplate_path, "textdoc/emphasize.html" ) );

            Perl5Matcher patMat = new Perl5Matcher();

            Perl5Substitution emphasize_substitution = new Perl5Substitution( emphasize_string );

            Properties tags = new Properties();	// A properties object to hold the results from the db...
            Map textMap = serverObject.getTexts( meta_id );
            HashMap imageMap = new HashMap();

            Iterator imit = Arrays.asList( images ).iterator();
            // This is where we gather all images from the database and put them in our maps.
            while ( imit.hasNext() ) {
                imit.next();
                String imgnumber = (String)imit.next();
                String imgurl = (String)imit.next();
                String linkurl = (String)imit.next();
                String width = (String)imit.next();
                String height = (String)imit.next();
                String border = (String)imit.next();
                String vspace = (String)imit.next();
                String hspace = (String)imit.next();
                String image_name = (String)imit.next();
                String align = (String)imit.next();
                String alt = (String)imit.next();
                String lowscr = (String)imit.next();
                String target = (String)imit.next();
                String target_name = (String)imit.next();
                StringBuffer value = new StringBuffer( 96 );
                if ( !"".equals( imgurl ) ) {
                    if ( !"".equals( linkurl ) ) {
                        value.append( "<a href=\"" + linkurl + "\"" );
                        if ( target.equals( "_other" ) ) {
                            value.append( " target=\"" + target_name + "\">" );
                        } else if ( !"".equals( target ) ) {
                            value.append( " target=\"" + target + "\">" );
                        }
                    }

                    value.append( "<img src=\"" + imageUrl + imgurl + "\"" ); // FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
                    if ( !"0".equals( width ) ) {
                        value.append( " width=\"" + width + "\"" );
                    }
                    if ( !"0".equals( height ) ) {
                        value.append( " height=\"" + height + "\"" );
                    }
                    value.append( " border=\"" + border + "\"" );

                    if ( !"0".equals( vspace ) ) {
                        value.append( " vspace=\"" + vspace + "\"" );
                    }
                    if ( !"0".equals( hspace ) ) {
                        value.append( " hspace=\"" + hspace + "\"" );
                    }
                    if ( !"".equals( image_name ) ) {
                        value.append( " name=\"" + image_name + "\"" );
                    }
                    if ( !"".equals( alt ) ) {
                        value.append( " alt=\"" + alt + "\"" );
                    }
                    if ( !"".equals( lowscr ) ) {
                        value.append( " lowscr=\"" + lowscr + "\"" );
                    }
                    if ( !"".equals( align ) && !"none".equals( align ) ) {
                        value.append( " align=\"" + align + "\"" );
                    }
                    if ( !"".equals( linkurl ) || imagemode ) {
                        value.append( "></a>" );
                    } else {
                        value.append( ">" );
                    }
                }
                imageMap.put( imgnumber, value.toString() );
            }

            /*
              OK.. we will now make a LinkedList for the entire page.
              This LinkedList, menus, will contain one item for each menu on the page.
              These items will also be instances of LinkedList.
              These LinkedLists will in turn each hold one Properties for each item in each menu.
              These Properties will hold the tags, and the corresponding data, that will go in each menuitem.
            */
            HashMap menus = new HashMap();	// Map to contain all the menus on the page.
            Menu currentMenu = null;
            int old_menu = -1;
            SimpleDateFormat DATETIMEFORMAT = DateHelper.DATE_TIME_FORMAT_IN_DATABASE;

            Iterator childIt = Arrays.asList( childs ).iterator();

            while ( childIt.hasNext() ) {
                // The menuitemproperties are retrieved in the following order:
                // to_meta_id,
                // c.menu_sort,
                // manual_sort_order,
                // tree_sort_index,
                // doc_type,
                // archive,
                // target,
                // date_created,
                // date_modified,
                // meta_headline,
                // meta_text,
                // meta_image,
                // frame_name,
                // activated_date+activated_time,
                // archived_date+archived_time
                // 0 if admin
                // filename
                int childMetaId = Integer.parseInt( (String)childIt.next() );
                int menuno = Integer.parseInt( (String)childIt.next() );              // What menu in the page the child is in.
                if ( menuno != old_menu ) {	                                     // If we come upon a new menu...
                    old_menu = menuno;
                    currentMenu = new Menu( menuno, sort_order, menumode, imageUrl );	     // We make a new Menu,
                    menus.put( new Integer( menuno ), currentMenu );		     // and add it to the page.
                }
                MenuItem menuItem = new MenuItem( currentMenu );
                menuItem.setMetaId( childMetaId );                                    // The meta-id of the child
                menuItem.setSortKey( Integer.parseInt( (String)childIt.next() ) );      // What order the internalDocument is sorted in in the menu, using sort-order 2 (manual sort)
                menuItem.setTreeSortKey( (String)childIt.next() );
                menuItem.setDocumentType( Integer.parseInt( (String)childIt.next() ) ); // The doctype of the child.
                menuItem.setArchivedFlag( !"0".equals( (String)childIt.next() ) );          // Child is considered archived?
                menuItem.setTarget( (String)childIt.next() );                         // The target for this internalDocument.
                try {
                    menuItem.setCreatedDatetime( DATETIMEFORMAT.parse( (String)childIt.next() ) ); // The datetime the child was created.
                } catch ( java.text.ParseException ignored ) {
                }
                try {
                    menuItem.setModifiedDatetime( DATETIMEFORMAT.parse( (String)childIt.next() ) ); // The datetime the child was modified.
                } catch ( java.text.ParseException ignored ) {
                }
                menuItem.setHeadline( (String)childIt.next() );                       // The headline of the child.
                menuItem.setText( (String)childIt.next() );                           // The subtext for the child.
                menuItem.setImage( (String)childIt.next() );                          // An optional imageurl for this internalDocument.
                childIt.next();                                                     // Ignored. The target frame for this internalDocument. Replaced by 'target'.
                try {
                    menuItem.setActivatedDatetime( DATETIMEFORMAT.parse( (String)childIt.next() ) ); // The datetime the child will be/was activated
                } catch ( NullPointerException ignored ) {
                } catch ( ParseException ignored ) {
                }
                try {
                    menuItem.setArchivedDatetime( DATETIMEFORMAT.parse( (String)childIt.next() ) ); // The datetime the child will be/was archived
                } catch ( NullPointerException ignored ) {
                } catch ( ParseException ignored ) {
                }
                menuItem.setEditable( "0".equals( (String)childIt.next() ) );           // if the user may admin it.
                menuItem.setFilename( (String)childIt.next() );                       // The filename, if it is a file-doc.

                if ( ( !menuItem.isActive() || menuItem.isArchived() ) && !menumode ) { // if not menumode, and internalDocument is inactive or archived, don't include it.
                    continue;
                }

                currentMenu.add( menuItem );	// Add the Properties for this menuitem to the current menus list.
            }

            for ( Iterator menuIterator = menus.values().iterator(); menuIterator.hasNext(); ) {
                Menu menu = (Menu)menuIterator.next();
                sortMenu( menu, sort_order, lang_prefix );
            }

            // I need a list of tags that have numbers that need to be parsed in in their data.
            Properties numberedtags = new Properties();

            // I also need a list of files to load, and their corresponding tag...
            Properties toload = new Properties();

            // Oh! I need a set of tags to be replaced in the templatefiles we'll load...
            Properties temptags = new Properties();

            // Put tags and corresponding data in Properties
            tags.setProperty( "#userName#", user.getFullName() );
            tags.setProperty( "#session_counter#", String.valueOf( serverObject.getSessionCounter() ) );
            tags.setProperty( "#session_counter_date#", serverObject.getSessionCounterDate() );
            tags.setProperty( "#lastDate#", DATETIMEFORMAT.format( myDoc.getModifiedDatetime() ) );
            tags.setProperty( "#metaHeadline#", myDoc.getHeadline() );

            String meta_image = myDoc.getImage();
            if ( !"".equals( meta_image ) ) {
                meta_image = "<img src=\"" + meta_image + "\" border=\"0\">";
            }
            tags.setProperty( "#metaImage#", meta_image );
            tags.setProperty( "#sys_message#", serverObject.getSystemData().getSystemMessage() );
            tags.setProperty( "#webMaster#", serverObject.getSystemData().getWebMaster() );
            tags.setProperty( "#webMasterEmail#", serverObject.getSystemData().getWebMasterAddress() );
            tags.setProperty( "#serverMaster#", serverObject.getSystemData().getServerMaster() );
            tags.setProperty( "#serverMasterEmail#", serverObject.getSystemData().getServerMasterAddress() );

            tags.setProperty( "#addDoc*#", "" );
            tags.setProperty( "#saveSortStart*#", "" );
            tags.setProperty( "#saveSortStop*#", "" );

            tags.setProperty( "#param#", param_value );
            tags.setProperty( "#externalparam#", extparam_value );

            tags.setProperty( "#readrunner_quote_substitution_count#", "#readrunner_quote_substitution_count#" );

            // Give the user a row of buttons if he is privileged enough.
            if ( ( serverObject.checkDocAdminRights( meta_id, user ) || serverObject.checkUserAdminrole( user.getUserId(), 2 ) ) && flags >= 0 ) {
                tags.setProperty( "#adminMode#", serverObject.getMenuButtons( meta_id, user ) );
            }

            if ( templatemode ) {	//Templatemode! :)

                List groupnamevec = TemplateMapper.sqlSelectGroupName( serverObject, group_id );

                String group_name;
                if ( !groupnamevec.isEmpty() ) {
                    group_name = (String)groupnamevec.get( 0 );
                } else {
                    group_name = "";
                }

                List templategroups = new ArrayList( Arrays.asList( TemplateMapper.sprocGetTemplateGroupsForUser( serverObject, user, meta_id ) ) );

                int selected_group = user.getTemplateGroup();

                if ( selected_group == -1 ) {
                    selected_group = Integer.parseInt( group_id );
                }

                List templates = new ArrayList( Arrays.asList( TemplateMapper.sprocGetTemplatesInGroup( serverObject, selected_group ) ) );

                String templatelist = createTemplatesOptionList( templates, documentTemplateId );

                String grouplist = createTemplateGroupsOptionList( templategroups, selected_group );

                temptags.setProperty( "#getDocType#", "" );
                temptags.setProperty( "#DocMenuNo#", "" );
                temptags.setProperty( "#group#", group_name );
                temptags.setProperty( "#getTemplateGroups#", grouplist);
                temptags.setProperty( "#simple_name#", simple_name );
                temptags.setProperty( "#getTemplates#", templatelist );

                // Put templateadmintemplate in list of files to load.
                toload.setProperty( "#changePage#", ( new File( admintemplate_path, "textdoc/inPage_admin.html" ) ).getPath() );
            }  // if (templatemode)

            if ( menumode ) {

                String[] docTypes = serverObject.sqlProcedure( "GetDocTypesForUser", new String[]{"" + meta_id, "" + user.getUserId(), lang_prefix} );
                List docTypesList = new ArrayList( Arrays.asList( docTypes ) );

                String existing_doc_name = getExistingDocumentName( admintemplate_path );
                docTypesList.add( 0, "0" );
                docTypesList.add( 1, existing_doc_name );

                final int[] docTypesSortOrder = {DocumentDomainObject.DOCTYPE_TEXT, 0, // "Befintligt dokument"
                                                 DocumentDomainObject.DOCTYPE_FILE, DocumentDomainObject.DOCTYPE_URL, DocumentDomainObject.DOCTYPE_HTML, DocumentDomainObject.DOCTYPE_BROWSER, DocumentDomainObject.DOCTYPE_CONFERENCE, DocumentDomainObject.DOCTYPE_CHAT, DocumentDomainObject.DOCTYPE_BILLBOARD, DocumentDomainObject.DOCTYPE_DIAGRAM, DocumentDomainObject.DOCTYPE_CALENDER, };
                Map sortOrderMap = new HashMap();
                for ( int i = 0; i < docTypesSortOrder.length; i++ ) {
                    int docTypeId = docTypesSortOrder[i];
                    sortOrderMap.put( new Integer( docTypeId ), new Integer( i ) );
                }

                TreeMap sortedIds = new TreeMap();
                for ( Iterator iterator = docTypesList.iterator(); iterator.hasNext(); ) {
                    DocumentTypeIdNameTuple tempTuple = new DocumentTypeIdNameTuple();
                    tempTuple.id = new Integer( (String)iterator.next() );
                    tempTuple.name = (String)iterator.next();

                    Integer sortKey = (Integer)sortOrderMap.get( tempTuple.id );
                    if ( null != sortKey ) {
                        sortedIds.put( sortKey, tempTuple );
                    }
                }

                Collection sortedTuplesOfDocumentTypes = sortedIds.values();
                Iterator docTypesIter = sortedTuplesOfDocumentTypes.iterator();
                StringBuffer doc_types_sb = new StringBuffer( 256 );
                while ( docTypesIter.hasNext() ) {
                    DocumentTypeIdNameTuple temp = (DocumentTypeIdNameTuple)docTypesIter.next();
                    Integer documentTypeId = temp.id;
                    String documentTypeName = temp.name;
                    doc_types_sb.append( "<option value=\"" );
                    doc_types_sb.append( documentTypeId );
                    doc_types_sb.append( "\">" );
                    doc_types_sb.append( documentTypeName );
                    doc_types_sb.append( "</option>" );
                }

                // List of files to load, and tags to parse them into
                toload.setProperty( "addDoc", ( new File( admintemplate_path, "textdoc/add_doc.html" ) ).getPath() );
                toload.setProperty( "saveSortStart", ( new File( admintemplate_path, "textdoc/sort_order.html" ) ).getPath() );
                toload.setProperty( "saveSortStop", ( new File( admintemplate_path, "textdoc/archive_del_button.html" ) ).getPath() );
                toload.setProperty( "sort_button", ( new File( admintemplate_path, "textdoc/sort_button.html" ) ).getPath() );

                // Some tags to parse in the files we'll load.
                temptags.setProperty( "#doc_types#", doc_types_sb.toString() );	// The doc-types.
                temptags.setProperty( "#sortOrder" + sort_order + "#", "checked" );	// The sortorder for this internalDocument.
            } // if (menumode)

            temptags.setProperty( "#getMetaId#", String.valueOf( meta_id ) );


            // Now load the files specified in "toload", and place them in "tags"
            //System.out.println("Loading template-files.") ;

            imcode.server.parser.MapSubstitution temptagsmapsubstitution = new imcode.server.parser.MapSubstitution( temptags, false );

            try {
                StringBuffer templatebuffer = new StringBuffer();
                Enumeration propenum = toload.propertyNames();
                while ( propenum.hasMoreElements() ) {

                    String filetag = (String)propenum.nextElement();
                    String templatebufferfilename = toload.getProperty( filetag );
                    String templatebufferstring = fileCache.getCachedFileString( new File( templatebufferfilename ) );
                    // Humm... Now we must replace the tags in the loaded files too.
                    templatebufferstring = org.apache.oro.text.regex.Util.substitute( patMat, HASHTAG_PATTERN, temptagsmapsubstitution, templatebufferstring, org.apache.oro.text.regex.Util.SUBSTITUTE_ALL );

                    tags.setProperty( filetag, templatebufferstring );
                    templatebuffer.setLength( 0 );
                }
            } catch ( IOException e ) {
                log.error( "An error occurred reading file during parsing.", e );
                return ( "Error occurred reading file during parsing.\n" + e );
            }

            if ( menumode ) {	//Menumode! :)

                // Make a Properties of all tags that contain numbers, and what the number is supposed to replace
                // in the tag's corresponding data
                // I.e. "tags" contains the data to replace the numbered tag, but you probably want that number
                // to be inserted somewhere in that data.
                // BTW, "*" represents the number in the tag.
                //numberedtags.setProperty("#addDoc*#","#doc_menu_no#") ;
                //numberedtags.setProperty("#saveSortStart*#","#doc_menu_no#") ;

                String savesortstop = tags.getProperty( "saveSortStop" );
                // We must display the sortbutton, which we read into the tag "#sort_button#"
                savesortstop = tags.getProperty( "sort_button" ) + savesortstop;
                tags.setProperty( "saveSortStop", savesortstop );
            } else {	// Not menumode...
                tags.setProperty( "saveSortStop", "" );
            } // if (menumode)

            // Now... let's load the template!
            // Get templatedir and read the file.
            StringBuffer templatebuffer = new StringBuffer( fileCache.getCachedFileString( new File( templatePath, "text/" + documentTemplateId + ".html" ) ) );

            // Check file for tags
            String templateContents = templatebuffer.toString();
            StringBuffer result = new StringBuffer( templateContents.length() + 16384 ); // This value is the amount i expect the internalDocument to increase in size.

            ReadrunnerFilter readrunnerFilter = new ReadrunnerFilter();
            MenuParserSubstitution menuparsersubstitution = new imcode.server.parser.MenuParserSubstitution( documentRequest, menus, menumode, tags );
            HashTagSubstitution hashtagsubstitution = new imcode.server.parser.HashTagSubstitution( tags, numberedtags );
            ImcmsTagSubstitution imcmstagsubstitution = new imcode.server.parser.ImcmsTagSubstitution( this, documentRequest, templatePath, Arrays.asList( included_docs ), includemode, includelevel, includePath, textMap, textmode, imageMap, imagemode, paramsToParse, readrunnerFilter );

            LinkedList parse = new LinkedList();
            perl5util.split( parse, "/(<!--\\/?IMSCRIPT-->)/", templateContents );
            Iterator pit = parse.iterator();
            boolean parsing = false;

            // Well. Here we have it. The main parseloop.
            // The Inner Sanctum of imCMS. Have fun.
            while ( pit.hasNext() ) {
                // So, let's jump in and out of blocks delimited by <!--IMSCRIPT--> and <!--/IMSCRIPT-->
                String nextbit = (String)pit.next();
                if ( nextbit.equals( "<!--/IMSCRIPT-->" ) ) { // We matched <!--/IMSCRIPT-->
                    parsing = false;       // So, we're not parsing.
                    continue;
                } else if ( nextbit.equals( "<!--IMSCRIPT-->" ) ) { // We matched <!--IMSCRIPT-->
                    parsing = true;              // So let's get to parsing.
                    continue;
                }
                if ( !parsing ) {
                    result.append( nextbit );
                    continue;
                }

                // String nextbit now contains the bit to parse. (Within the imscript-tags.)

                // Parse the new-style menus.
                // Aah... the magic of OO...
                nextbit = org.apache.oro.text.regex.Util.substitute( patMat, MENU_PATTERN, menuparsersubstitution, nextbit, org.apache.oro.text.regex.Util.SUBSTITUTE_ALL );

                // Parse the <?imcms:tags?>
                nextbit = org.apache.oro.text.regex.Util.substitute( patMat, IMCMS_TAG_PATTERN, imcmstagsubstitution, nextbit, org.apache.oro.text.regex.Util.SUBSTITUTE_ALL );

                // Parse the hashtags
                nextbit = org.apache.oro.text.regex.Util.substitute( patMat, HASHTAG_PATTERN, hashtagsubstitution, nextbit, org.apache.oro.text.regex.Util.SUBSTITUTE_ALL );

                // So, append the result from this loop-iteration to the result.
                result.append( nextbit );
            } // end while (pit.hasNext()) // End of the main parseloop

            // Get the number of <q>tags</q> inserted by the readrunner filter.
            int readrunnerQuoteSubstitutionCount = readrunnerFilter.getReadrunnerQuoteSubstitutionCount();
            String returnresult = result.toString();

            if ( readrunnerQuoteSubstitutionCount > 0 ) {
                // We found a couple of readrunner-text-tags, and did a few substitutions

                Vector readrunnerSubstitutionCountVector = new Vector();
                readrunnerSubstitutionCountVector.add( "#readrunner_quote_substitution_count#" );
                readrunnerSubstitutionCountVector.add( "" + readrunnerQuoteSubstitutionCount );

                String readrunner_script_frag = serverObject.parseDoc( readrunnerSubstitutionCountVector, "readrunner/script.html.frag", lang_prefix );

                String readrunner_titlesuffix_frag = serverObject.parseDoc( null, "readrunner/titlesuffix.html.frag", lang_prefix );

                String readrunner_panel_frag = serverObject.parseDoc( null, "readrunner/panel.html.frag", lang_prefix );

                String readrunner_buffer_frag = serverObject.parseDoc( null, "readrunner/buffer.html.frag", lang_prefix );

                String readrunner_bodyevents_frag = serverObject.parseDoc( null, "readrunner/bodyevents.html.frag", lang_prefix );

                String readrunner_copyright_frag = serverObject.parseDoc( null, "readrunner/copyright.html.frag", lang_prefix );

                readrunner_titlesuffix_frag = escapeSubstitution( readrunner_titlesuffix_frag );
                readrunner_bodyevents_frag = escapeSubstitution( readrunner_bodyevents_frag );
                readrunner_panel_frag = escapeSubstitution( readrunner_panel_frag );
                readrunner_script_frag = escapeSubstitution( readrunner_script_frag );
                readrunner_buffer_frag = escapeSubstitution( readrunner_buffer_frag );

                // FIXME: Use a StringBuffer for all this crap instead.

                // Insert the copyright fragment at the top of the page
                returnresult = readrunner_copyright_frag + returnresult;

                // Insert the titlesuffix fragment at the end of the title
                returnresult = org.apache.oro.text.regex.Util.substitute( patMat, READRUNNER_END_TITLE_PATTERN, new Perl5Substitution( readrunner_titlesuffix_frag + "$1" ), returnresult );

                // Insert the script fragment at the end of the head
                returnresult = org.apache.oro.text.regex.Util.substitute( patMat, READRUNNER_END_HEAD_PATTERN, new Perl5Substitution( readrunner_script_frag + "$1" ), returnresult );

                // Insert the body-events and panel in and after the body tag.
                returnresult = org.apache.oro.text.regex.Util.substitute( patMat, READRUNNER_START_BODY_PATTERN, new Perl5Substitution( "$1" + readrunner_bodyevents_frag + "$2" + readrunner_panel_frag ), returnresult );

                // Insert the buffer fragment at the end of the body
                returnresult = org.apache.oro.text.regex.Util.substitute( patMat, READRUNNER_END_BODY_PATTERN, new Perl5Substitution( readrunner_buffer_frag + "$1" ), returnresult );

            }

            /*
              So, it is here i shall have to put my magical markupemphasizing code.
              First, i'll split the html (returnresult) on html-tags, and then go through every non-tag part and parse it for keywords to emphasize,
              and then i'll puzzle it together again. Whe-hey. This will be fun. Not to mention fast. Oh yes, siree.
            */
            if ( emp != null ) { // If we have something to emphasize...
                StringBuffer emphasized_result = new StringBuffer( returnresult.length() ); // A StringBuffer to hold the result
                PatternMatcherInput emp_input = new PatternMatcherInput( returnresult );    // A PatternMatcherInput to match on
                int last_html_offset = 0;
                int current_html_offset = 0;
                String non_html_tag_string = null;
                String html_tag_string = null;
                while ( patMat.contains( emp_input, HTML_TAG_PATTERN ) ) {
                    current_html_offset = emp_input.getMatchBeginOffset();
                    non_html_tag_string = result.substring( last_html_offset, current_html_offset );
                    last_html_offset = emp_input.getMatchEndOffset();
                    html_tag_string = result.substring( current_html_offset, last_html_offset );
                    non_html_tag_string = emphasizeString( non_html_tag_string, emp, emphasize_substitution, patMat );
                    // for each string to emphasize
                    emphasized_result.append( non_html_tag_string );
                    emphasized_result.append( html_tag_string );
                } // while
                non_html_tag_string = result.substring( last_html_offset );
                non_html_tag_string = emphasizeString( non_html_tag_string, emp, emphasize_substitution, patMat );
                emphasized_result.append( non_html_tag_string );
                returnresult = emphasized_result.toString();
            }
            return returnresult;
        } catch ( RuntimeException ex ) {
            log.error( "Error occurred during parsing.", ex );
            throw ex;
        }
    }

    private String createTemplateGroupsOptionList( List templategroups, int selected_group ) {
        StringBuffer grouplist = new StringBuffer();

        for ( Iterator iterator = templategroups.iterator(); iterator.hasNext(); ) {
            String[] templateGroupIdNamePair = (String[])iterator.next();
            int templateGroupId = Integer.parseInt(templateGroupIdNamePair[0]) ;
            String templateGroupName = templateGroupIdNamePair[1] ;
            grouplist.append( "<option value=\"" + templateGroupId );
            if ( selected_group == templateGroupId ) {
                grouplist.append( "\" selected>" + templateGroupName );
            } else {
                grouplist.append( "\">" + templateGroupName );
            }
            grouplist.append("</option>") ;
        }

        return grouplist.toString();
    }

    private String createTemplatesOptionList( List templates, int documentTemplateId ) {
        StringBuffer templatelist = new StringBuffer();
        // Make a HTML option-list of them...
        for ( Iterator iterator = templates.iterator(); iterator.hasNext(); ) {
            String[] templateIdNamePair = (String[])iterator.next();
            int templateId = Integer.parseInt(templateIdNamePair[0]) ;
            String templateName = templateIdNamePair[1] ;
            templatelist.append( "<option value=\"" + templateId );
            if ( templateId == documentTemplateId  ) {
                templatelist.append( "\" selected>" + templateName);
            } else {
                templatelist.append( "\">" + templateName);
            }
            templatelist.append( "</option>" ) ;
        }
        return templatelist.toString();
    }

    private String emphasizeString( String str, String[] emp, Substitution emphasize_substitution, PatternMatcher patMat ) {

        Perl5Compiler empCompiler = new Perl5Compiler();
        // for each string to emphasize
        for ( int i = 0; i < emp.length; ++i ) {
            try {
                Pattern empPattern = empCompiler.compile( "(" + Perl5Compiler.quotemeta( emp[i] ) + ")", Perl5Compiler.CASE_INSENSITIVE_MASK );
                str = org.apache.oro.text.regex.Util.substitute( // Threadsafe
                        patMat, empPattern, emphasize_substitution, str, org.apache.oro.text.regex.Util.SUBSTITUTE_ALL );
            } catch ( MalformedPatternException ex ) {
                log.warn( "Dynamic Pattern-compilation failed in IMCService.emphasizeString(). Suspected bug in jakarta-oro Perl5Compiler.quotemeta(). The String was '" + emp[i] + "'", ex );
            }
        }
        return str;
    }

    private String escapeSubstitution( String substitution ) {
        StringBuffer result = new StringBuffer();

        for ( int i = 0; i < substitution.length(); ++i ) {
            char c = substitution.charAt( i );
            switch ( c ) {
                case '\\':
                case '$':
                    result.append( '\\' );
            }
            result.append( c );
        }
        return result.toString();
    }

    class DocumentTypeIdNameTuple {

        Integer id;
        String name;
    }

    private String getExistingDocumentName( File admintemplate_path ) throws IOException {
        String existing_doc_filename = ( new File( admintemplate_path, "textdoc/existing_doc_name.html" ) ).getPath();
        String existing_doc_name = null;

        existing_doc_name = fileCache.getCachedFileString( new File( existing_doc_filename ) );
        return existing_doc_name;
    }

    private void sortMenu( Menu currentMenu, int sort_order, String lang_prefix ) {
        if ( null != currentMenu ) {

            Comparator childsComparator;
            switch ( sort_order ) {
                case IMCConstants.MENU_SORT_BY_DATETIME:
                    childsComparator = new ReverseComparator( new MenuItemModifiedDateComparator() );
                    break;
                case IMCConstants.MENU_SORT_BY_MANUAL_ORDER:
                    childsComparator = new ReverseComparator( new MenuItemManualSortOrderComparator() );
                    break;
                case IMCConstants.MENU_SORT_BY_MANUAL_TREE_ORDER:
                    childsComparator = new MenuItemManualTreeSortOrderComparator();
                    break;
                case IMCConstants.MENU_SORT_BY_HEADLINE:
                default:
                    childsComparator = new MenuItemHeadlineComparator( lang_prefix );
            }

            Collections.sort( currentMenu, childsComparator );
        }
    }

    class MenuItemHeadlineComparator implements Comparator {

        private Collator collator;

        private MenuItemHeadlineComparator( String lang ) {
            Locale locale;
            if ( "se".equalsIgnoreCase( lang ) ) {
                locale = new Locale( "sv" );
            } else {
                locale = Locale.ENGLISH;
            }
            collator = Collator.getInstance( locale );
        }

        public int compare( Object o1, Object o2 ) {
            String headline1 = ( (MenuItem)o1 ).getHeadline();
            String headline2 = ( (MenuItem)o2 ).getHeadline();
            return collator.compare( headline1, headline2 );
        }
    }

    static class MenuItemModifiedDateComparator implements Comparator {

        public int compare( Object o1, Object o2 ) {
            Date modifiedDate1 = ( (MenuItem)o1 ).getModifiedDatetime();
            Date modifiedDate2 = ( (MenuItem)o2 ).getModifiedDatetime();
            return modifiedDate1.compareTo( modifiedDate2 );
        }
    }

    private class MenuItemManualSortOrderComparator implements Comparator {

        public int compare( Object o1, Object o2 ) {
            int sortKey1 = ( (MenuItem)o1 ).getSortKey();
            int sortKey2 = ( (MenuItem)o2 ).getSortKey();
            return sortKey1 - sortKey2;
        }
    }

    static class MenuItemManualTreeSortOrderComparator implements Comparator {

        private final static Pattern FIRST_NUMBER_PATTERN;
        private final PatternMatcher perl5Matcher = new Perl5Matcher();

        private final Comparator dateComparator = new ReverseComparator( new MenuItemModifiedDateComparator() );

        static {
            PatternCompiler perl5Compiler = new Perl5Compiler();
            Pattern firstNumberPattern = null;
            try {
                firstNumberPattern = perl5Compiler.compile( "^(\\d+)\\.?(.*)" );
            } catch ( MalformedPatternException ignored ) {
                log.fatal( "Bad pattern.", ignored );
            }
            FIRST_NUMBER_PATTERN = firstNumberPattern;
        }

        public int compare( Object o1, Object o2 ) {
            String treeSortKey1 = ( (MenuItem)o1 ).getTreeSortKey();
            String treeSortKey2 = ( (MenuItem)o2 ).getTreeSortKey();

            int difference = compareTreeSortKeys( treeSortKey1, treeSortKey2 );
            if ( 0 == difference ) {
                return dateComparator.compare( o1, o2 );
            }
            return difference;
        }

        private int compareTreeSortKeys( String treeSortKey1, String treeSortKey2 ) {

            boolean key1Matches = perl5Matcher.matches( treeSortKey1, FIRST_NUMBER_PATTERN );
            MatchResult match1 = perl5Matcher.getMatch();
            boolean key2Matches = perl5Matcher.matches( treeSortKey2, FIRST_NUMBER_PATTERN );
            MatchResult match2 = perl5Matcher.getMatch();

            if ( key1Matches && key2Matches ) {
                int firstNumber1 = Integer.parseInt( match1.group( 1 ) );
                String tail1 = match1.group( 2 );

                int firstNumber2 = Integer.parseInt( match2.group( 1 ) );
                String tail2 = match2.group( 2 );

                if ( firstNumber1 != firstNumber2 ) {
                    return firstNumber1 - firstNumber2;
                }
                return compareTreeSortKeys( tail1, tail2 );
            } else if ( !key1Matches && !key2Matches ) {
                return treeSortKey1.compareTo( treeSortKey2 );
            } else if ( key2Matches ) {
                return -1;
            } else {
                return +1;
            }
        }
    }

    private static class ReverseComparator implements Comparator {

        private Comparator comparator;

        ReverseComparator( Comparator comparator ) {
            this.comparator = comparator;
        }

        public int compare( Object o1, Object o2 ) {
            return -comparator.compare( o1, o2 );
        }
    }
}
