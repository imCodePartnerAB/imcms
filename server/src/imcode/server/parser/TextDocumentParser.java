package imcode.server.parser;

import imcode.server.DocumentRequest;
import imcode.server.IMCConstants;
import imcode.server.IMCServiceInterface;
import imcode.server.LanguageMapper;
import imcode.server.document.*;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.oro.text.regex.*;
import org.apache.oro.text.perl.Perl5Util;

import java.io.IOException;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TextDocumentParser implements imcode.server.IMCConstants {

    private final static Logger log = Logger.getLogger( TextDocumentParser.class );

    private final static Perl5Util perl5util = new Perl5Util();

    static Pattern HASHTAG_PATTERN = null;
    private static Pattern MENU_PATTERN = null;
    private static Pattern IMCMS_TAG_PATTERN = null;
    private static Pattern HTML_TAG_PATTERN = null;

    static {
        Perl5Compiler patComp = new Perl5Compiler();
        try {
            // OK, so this pattern is simple, ugly, and prone to give a lot of errors.
            // Very good. Very good. Know something? NO SOUP FOR YOU!
            HTML_TAG_PATTERN = patComp.compile( "<[^>]+?>", Perl5Compiler.READ_ONLY_MASK );

            IMCMS_TAG_PATTERN = patComp.compile( "<\\?imcms:([-\\w]+)(.*?)\\?>", Perl5Compiler.SINGLELINE_MASK
                                                                                 | Perl5Compiler.READ_ONLY_MASK );
            HASHTAG_PATTERN = patComp.compile( "#[^ #\"<>&;\\t\\r\\n]+#", Perl5Compiler.READ_ONLY_MASK );
            MENU_PATTERN = patComp.compile( "<\\?imcms:menu(.*?)\\?>(.*?)<\\?\\/imcms:menu\\?>", Perl5Compiler.SINGLELINE_MASK
                                                                                                 | Perl5Compiler.READ_ONLY_MASK );

        } catch ( MalformedPatternException ignored ) {
            // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
            log.fatal( "Bad pattern.", ignored );
        }
    }

    private IMCServiceInterface service;
    public static final int EXPECTED_CONTENT_BLOAT = 16384;

    public TextDocumentParser( IMCServiceInterface serverobject ) {
        this.service = serverobject;
    }

    /*
       return a referens to IMCServerInterface used by TextDocumentParser
    */
    public IMCServiceInterface getService() {
        return this.service;
    }

    public String parsePage( ParserParameters paramsToParse ) throws IOException {
        NDC.push( "parsePage" );
        String page = parsePage( paramsToParse, 5 );
        NDC.pop();
        return page;
    }

    public String parsePage( ParserParameters parserParameters, int includelevel ) throws IOException {
        DocumentRequest documentRequest = parserParameters.getDocumentRequest() ;
        int flags = parserParameters.getFlags() ;
        try {
            TextDocumentDomainObject document = (TextDocumentDomainObject)documentRequest.getDocument();
            int meta_id = document.getId();
            String meta_id_str = String.valueOf( meta_id );

            UserDomainObject user = documentRequest.getUser();
            int user_id = user.getId();
            String user_id_str = String.valueOf( user_id );

            DocumentMapper documentMapper = service.getDocumentMapper();

            boolean textmode = false;
            boolean imagemode = false;
            boolean menumode = false;
            boolean templatemode = false;
            boolean includemode = false;

            if ( flags > 0 ) {
                int user_set_id = documentMapper.getUsersMostPrivilegedPermissionSetIdOnDocument( user, document );
                int user_perm_set = documentMapper.getUsersPermissionBitsOnDocumentIfRestricted( user_set_id, document );

                textmode = ( flags & PERM_DT_TEXT_EDIT_TEXTS ) != 0
                           && ( user_set_id == 0 || ( user_perm_set & PERM_DT_TEXT_EDIT_TEXTS ) != 0 );
                imagemode = ( flags & PERM_DT_TEXT_EDIT_IMAGES ) != 0
                            && ( user_set_id == 0 || ( user_perm_set & PERM_DT_TEXT_EDIT_IMAGES ) != 0 );
                menumode = ( flags & PERM_DT_TEXT_EDIT_MENUS ) != 0
                           && ( user_set_id == 0 || ( user_perm_set & PERM_DT_TEXT_EDIT_MENUS ) != 0 );
                templatemode = ( flags & PERM_DT_TEXT_CHANGE_TEMPLATE ) != 0
                               && ( user_set_id == 0 || ( user_perm_set & PERM_DT_TEXT_CHANGE_TEMPLATE ) != 0 );
                includemode = ( flags & PERM_DT_TEXT_EDIT_INCLUDES ) != 0
                              && ( user_set_id == 0 || ( user_perm_set & PERM_DT_TEXT_EDIT_INCLUDES ) != 0 );
            }

            TemplateDomainObject documentTemplate = document.getTemplate();
            int documentTemplateId = documentTemplate.getId();

            String template_name = parserParameters.getTemplate();
            if ( template_name != null ) {
                TemplateMapper templateMapper = service.getTemplateMapper();
                TemplateDomainObject template = templateMapper.getTemplateByName( template_name );
                if ( null != template ) {
                    documentTemplateId = template.getId();
                }
            }

            String[] metaIdUserIdPair = {meta_id_str, user_id_str};

            Perl5Matcher patMat = new Perl5Matcher();

            SimpleDateFormat datetimeFormatWithSeconds = new SimpleDateFormat( DateConstants.DATETIME_SECONDS_FORMAT_STRING );

            Map menus = getMenus( metaIdUserIdPair, menumode, datetimeFormatWithSeconds, parserParameters );

            StringBuffer templatebuffer = new StringBuffer( service.getTemplateData( documentTemplateId ) );

            String templateContents = templatebuffer.toString();
            StringBuffer result = new StringBuffer( templateContents.length() + EXPECTED_CONTENT_BLOAT );

            String imcmsMessage = service.getAdminTemplate( "textdoc/imcms_message.html", user, null );
            result.append( imcmsMessage );

            Properties hashTags = getHashTags( user, datetimeFormatWithSeconds, document, templatemode, parserParameters );
            MapSubstitution hashtagsubstitution = new imcode.server.parser.MapSubstitution( hashTags, true );
            MenuParserSubstitution menuparsersubstitution = new imcode.server.parser.MenuParserSubstitution( parserParameters, menus, menumode );
            ImcmsTagSubstitution imcmstagsubstitution = new imcode.server.parser.ImcmsTagSubstitution( this, parserParameters, includemode, includelevel, textmode, imagemode );

            LinkedList parse = new LinkedList();
            perl5util.split( parse, "/(<!--\\/?IMSCRIPT-->)/", templateContents );
            Iterator parseIterator = parse.iterator();
            boolean parsing = false;

            // Well. Here we have it. The main parseloop.
            // The Inner Sanctum of imCMS. Have fun.
            while ( parseIterator.hasNext() ) {
                // So, let's jump in and out of blocks delimited by <!--IMSCRIPT--> and <!--/IMSCRIPT-->
                String nextbit = (String)parseIterator.next();
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

                nextbit = replaceTags( nextbit, patMat, menuparsersubstitution, imcmstagsubstitution, hashtagsubstitution );

                result.append( nextbit );
            }

            String returnresult = result.toString();

            returnresult = applyEmphasis( documentRequest, user, returnresult, patMat, result );
            return returnresult;
        } catch ( RuntimeException ex ) {
            log.error( "Error occurred during parsing.", ex );
            throw ex;
        }
    }

    private String applyEmphasis( DocumentRequest documentRequest, UserDomainObject user, String returnresult,
                                  Perl5Matcher patMat, StringBuffer result ) {
        String[] emp = documentRequest.getEmphasize();
        if ( emp != null ) { // If we have something to emphasize...
            String emphasize_string = service.getAdminTemplate( "textdoc/emphasize.html", user, null );
            Perl5Substitution emphasize_substitution = new Perl5Substitution( emphasize_string );
            StringBuffer emphasized_result = new StringBuffer( returnresult.length() ); // A StringBuffer to hold the result
            PatternMatcherInput emp_input = new PatternMatcherInput( returnresult );    // A PatternMatcherInput to match on
            int last_html_offset = 0;
            int current_html_offset;
            String non_html_tag_string;
            String html_tag_string;
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
    }

    private String replaceTags( String nextbit, Perl5Matcher patMat, MenuParserSubstitution menuparsersubstitution,
                                ImcmsTagSubstitution imcmstagsubstitution, MapSubstitution hashtagsubstitution ) {
        // Menus.
        nextbit = Util.substitute( patMat, MENU_PATTERN, menuparsersubstitution, nextbit, Util.SUBSTITUTE_ALL );
        // <?imcms:tags?>
        nextbit = Util.substitute( patMat, IMCMS_TAG_PATTERN, imcmstagsubstitution, nextbit, Util.SUBSTITUTE_ALL );
        // #hashtags#
        nextbit = Util.substitute( patMat, HASHTAG_PATTERN, hashtagsubstitution, nextbit, Util.SUBSTITUTE_ALL );
        return nextbit;
    }

    private Properties getHashTags( UserDomainObject user, SimpleDateFormat datetimeFormatWithSeconds,
                                    TextDocumentDomainObject document,
                                    boolean templatemode, ParserParameters parserParameters ) {

        Properties tags = new Properties();	// A properties object to hold the results from the db...
        // Put tags and corresponding data in Properties
        tags.setProperty( "#userName#", user.getFullName() );
        tags.setProperty( "#session_counter#", String.valueOf( service.getSessionCounter() ) );
        tags.setProperty( "#session_counter_date#", service.getSessionCounterDate() );
        tags.setProperty( "#lastDate#", datetimeFormatWithSeconds.format( document.getModifiedDatetime() ) );
        tags.setProperty( "#metaHeadline#", document.getHeadline() );
        tags.setProperty( "#metaText#", document.getMenuText() );

        String meta_image = document.getMenuImage();
        if ( !"".equals( meta_image ) ) {
            meta_image = "<img src=\"" + meta_image + "\" border=\"0\">";
        }
        tags.setProperty( "#metaImage#", meta_image );
        tags.setProperty( "#sys_message#", service.getSystemData().getSystemMessage() );
        tags.setProperty( "#webMaster#", service.getSystemData().getWebMaster() );
        tags.setProperty( "#webMasterEmail#", service.getSystemData().getWebMasterAddress() );
        tags.setProperty( "#serverMaster#", service.getSystemData().getServerMaster() );
        tags.setProperty( "#serverMasterEmail#", service.getSystemData().getServerMasterAddress() );

        tags.setProperty( "#param#", parserParameters.getParameter() );
        tags.setProperty( "#externalparam#", parserParameters.getExternalParameter() );

        if (parserParameters.getFlags() >= 0) {
            tags.setProperty( "#adminMode#", service.getMenuButtons( user, document ) );
        }

        String changeTemplateUi = createChangeTemplateUi( templatemode, user, document );
        tags.setProperty( "#changePage#", changeTemplateUi );
        return tags;
    }

    private String createChangeTemplateUi( boolean templatemode, UserDomainObject user,
                                           TextDocumentDomainObject document ) {
        String changeTemplateUi = "";
        if ( templatemode ) {	//Templatemode! :)

            TemplateMapper templateMapper = service.getTemplateMapper();

            TemplateGroupDomainObject selected_group = user.getTemplateGroup();
            if ( null == selected_group ) {
                selected_group = templateMapper.getTemplateGroupById( document.getTemplateGroupId() );
            }

            String group_name = selected_group.getName();

            TemplateDomainObject[] templates = templateMapper.getTemplatesInGroup( selected_group );

            String templatelist = templateMapper.createHtmlOptionListOfTemplates( templates, document.getTemplate() );

            String grouplist = templateMapper.createHtmlOptionListOfTemplateGroups( selected_group );

            // Oh! I need a set of tags to be replaced in the templatefiles we'll load...
            List temptags = new ArrayList();

            temptags.add( "#getMetaId#" );
            temptags.add( "" + document.getId() );
            temptags.add( "#group#" );
            temptags.add( group_name );
            temptags.add( "#getTemplateGroups#" );
            temptags.add( grouplist );
            temptags.add( "#simple_name#" );
            temptags.add( document.getTemplate().getName() );
            temptags.add( "#getTemplatesInGroup#" );
            temptags.add( templatelist );

            // Put templateadmintemplate in list of files to load.
            changeTemplateUi = service.getAdminTemplate( "textdoc/inPage_admin.html", user, temptags );
        }  // if (templatemode)
        return changeTemplateUi;
    }

    private Map getMenus( String[] metaIdUserIdPair, boolean menumode, SimpleDateFormat datetimeFormatWithSeconds,
                          ParserParameters parserParameters ) {
        /*
          OK.. we will now make a LinkedList for the entire page.
          This LinkedList, menus, will contain one item for each menu on the page.
          These items will also be instances of LinkedList.
          These LinkedLists will in turn each hold one Properties for each item in each menu.
          These Properties will hold the tags, and the corresponding data, that will go in each menuitem.
        */
        HashMap menus = new HashMap();	// Map to contain all the menus on the page.
        Menu currentMenu = null;
        int previousMenuIndex = -1;

        // Here we have the most timeconsuming part of parsing the page.
        // Selecting all the documents with permissions from the DB
        String[][] childs = service.sqlProcedureMulti( "GetChilds", metaIdUserIdPair );

        for ( int i = 0; i < childs.length; ++i ) {
            String[] childRow = childs[i];

            int menuIndex = Integer.parseInt( childRow[1] );              // What menu in the page the child is in.
            int menuSortOrder = Integer.parseInt( childRow[2] );
            if ( menuIndex != previousMenuIndex ) {	                                     // If we come upon a new menu...
                previousMenuIndex = menuIndex;
                currentMenu = new Menu( menuIndex, menumode, menuSortOrder );	     // We make a new Menu,
                menus.put( new Integer( menuIndex ), currentMenu );		     // and add it to the page.
            }
            MenuItem menuItem = createMenuItemFromSprocGetChildsRow( currentMenu, childRow, datetimeFormatWithSeconds );

            Integer editingMenuIndex = parserParameters.getEditingMenuIndex();
            boolean editingThisMenu = menumode && null != editingMenuIndex && editingMenuIndex.intValue() == menuIndex;
            if ( !menuItem.getDocument().isPublishedAndNotArchived() && !editingThisMenu ) { // if not menumode, and document is inactive or archived, don't include it.
                continue;
            }
            currentMenu.add( menuItem );	// Add the Properties for this menuitem to the current menus list.
        }

        for ( Iterator menuIterator = menus.values().iterator(); menuIterator.hasNext(); ) {
            Menu menu = (Menu)menuIterator.next();
            sortMenu( menu );
        }
        return menus;
    }

    private MenuItem createMenuItemFromSprocGetChildsRow( Menu currentMenu, String[] childRow,
                                                          SimpleDateFormat datetimeFormatWithSeconds ) {
        MenuItem menuItem = new MenuItem( currentMenu );
        DocumentDomainObject menuItemDocument = DocumentDomainObject.fromDocumentTypeId( Integer.parseInt( childRow[5] ) );
        menuItemDocument.setId( Integer.parseInt( childRow[0] ) );
        menuItem.setSortKey( Integer.parseInt( childRow[3] ) );      // What order the document is sorted in in the menu, using sort-order 2 (manual sort)
        menuItem.setTreeSortKey( childRow[4] );
        menuItemDocument.setTarget( childRow[6] );
        try {
            menuItemDocument.setCreatedDatetime( datetimeFormatWithSeconds.parse( childRow[7] ) );
        } catch ( ParseException ignored ) {
        }
        try {
            menuItemDocument.setModifiedDatetime( datetimeFormatWithSeconds.parse( childRow[8] ) );
        } catch ( ParseException ignored ) {
        }
        menuItemDocument.setHeadline( childRow[9] );
        menuItemDocument.setMenuText( childRow[10] );
        menuItemDocument.setMenuImage( childRow[11] );
        try {
            menuItemDocument.setPublicationStartDatetime( datetimeFormatWithSeconds.parse( childRow[12] ) );
        } catch ( NullPointerException ignored ) {
        } catch ( ParseException ignored ) {
        }
        try {
            menuItemDocument.setArchivedDatetime( datetimeFormatWithSeconds.parse( childRow[13] ) );
        } catch ( NullPointerException ignored ) {
        } catch ( ParseException ignored ) {
        }
        try {
            menuItemDocument.setPublicationEndDatetime( datetimeFormatWithSeconds.parse( childRow[14] ) );
        } catch ( NullPointerException ignored ) {
        } catch ( ParseException ignored ) {
        }
        menuItem.setEditable( "0".equals( childRow[15] ) );           // if the user may admin it.
        menuItemDocument.setStatus( Integer.parseInt( childRow[16] ) );
        menuItem.setDocument( menuItemDocument );
        return menuItem;
    }

    private String emphasizeString( String str, String[] emp, Substitution emphasize_substitution,
                                    PatternMatcher patMat ) {

        Perl5Compiler empCompiler = new Perl5Compiler();
        // for each string to emphasize
        for ( int i = 0; i < emp.length; ++i ) {
            try {
                Pattern empPattern = empCompiler.compile( "(" + Perl5Compiler.quotemeta( emp[i] ) + ")", Perl5Compiler.CASE_INSENSITIVE_MASK );
                str = org.apache.oro.text.regex.Util.substitute( patMat, empPattern, emphasize_substitution, str, org.apache.oro.text.regex.Util.SUBSTITUTE_ALL );
            } catch ( MalformedPatternException ex ) {
                log.warn( "Dynamic Pattern-compilation failed in IMCService.emphasizeString(). Suspected bug in jakarta-oro Perl5Compiler.quotemeta(). The String was '"
                          + emp[i]
                          + "'", ex );
            }
        }
        return str;
    }

    private void sortMenu( Menu currentMenu ) {
        int sort_order = currentMenu.getSortOrder();
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
                childsComparator = new MenuItemHeadlineComparator( service.getDefaultLanguageAsIso639_2() );
        }

        Collections.sort( currentMenu, childsComparator );
    }

    class MenuItemHeadlineComparator implements Comparator {

        private Collator collator;

        private MenuItemHeadlineComparator( String lang ) {
            Locale locale = Locale.ENGLISH ;
            try {
                locale = new Locale( LanguageMapper.convert639_2to639_1( lang ) );
            } catch ( LanguageMapper.LanguageNotSupportedException ignored ) {}
            collator = Collator.getInstance( locale );
        }

        public int compare( Object o1, Object o2 ) {
            String headline1 = ( (MenuItem)o1 ).getDocument().getHeadline();
            String headline2 = ( (MenuItem)o2 ).getDocument().getHeadline();
            return collator.compare( headline1, headline2 );
        }
    }

    static class MenuItemModifiedDateComparator implements Comparator {

        public int compare( Object o1, Object o2 ) {
            Date modifiedDate1 = ( (MenuItem)o1 ).getDocument().getModifiedDatetime();
            Date modifiedDate2 = ( (MenuItem)o2 ).getDocument().getModifiedDatetime();
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

    /**
     * @deprecated Remove usage of this when removeing MenuItem instead -> MenuItemDomainObject
     */
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
