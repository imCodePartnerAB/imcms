package imcode.server.parser;

import imcode.server.DocumentRequest;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.util.Html;
import imcode.util.IdNamePair;
import imcode.util.Utility;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.StringSubstitution;
import org.apache.oro.text.regex.Substitution;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

class MenuParser {

    private Substitution NULLSUBSTITUTION = new StringSubstitution( "" );

    private Map menus;
    private int[] implicitMenus = {1};
    private ParserParameters parserParameters;
    private static final int EXISTING_DOCTYPE_ID = 0;

    MenuParser( ParserParameters parserParameters ) {
        this.parserParameters = parserParameters;
        TextDocumentDomainObject textDocument = (TextDocumentDomainObject)parserParameters.getDocumentRequest().getDocument();
        this.menus = textDocument.getMenus();
    }

    private MenuDomainObject getMenuByIndex( int id ) {
        return (MenuDomainObject)menus.get( new Integer( id ) );
    }

    private String getMenuModePrefix( int menuIndex, List parseTags ) {
        ImcmsServices services = parserParameters.getDocumentRequest().getServices();
        UserDomainObject user = parserParameters.getDocumentRequest().getUser();
        Integer editingMenuIndex = parserParameters.getEditingMenuIndex();
        String result = services.getAdminTemplate( "textdoc/menulabel.frag", user, parseTags );
        if ( null != editingMenuIndex && editingMenuIndex.intValue() == menuIndex ) {
            result += services.getAdminTemplate( "textdoc/add_doc.html", user, parseTags )
                      +
                      services.getAdminTemplate( "textdoc/sort_order.html", user, parseTags );
        }
        return result;
    }

    private List createParseTags( int menuIndex, Properties menuattributes ) {
        MenuDomainObject menu = getMenuByIndex( menuIndex );

        DocumentRequest documentRequest = parserParameters.getDocumentRequest();
        MenuItemDomainObject[] menuItems = menu.getMenuItemsUserCanSee(documentRequest.getUser() ) ;
        ImcmsAuthenticatorAndUserAndRoleMapper userMapper = documentRequest.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        UserDomainObject defaultUser = userMapper.getDefaultUser() ;
        MenuItemDomainObject[] defaultUserMenuItems = menu.getMenuItemsUserCanSee( defaultUser );
        List parseTags = Arrays.asList( new String[]{
            "#menuindex#", "" + menuIndex,
            "#menuitemcount#", ""+menuItems.length,
            "#defaultusermenuitemcount#", ""+defaultUserMenuItems.length,
            "#label#", menuattributes.getProperty( "label" ),
            "#flags#", "" + parserParameters.getFlags(),
            "#sortOrder" + ( null != menu ? menu.getSortOrder() : MenuDomainObject.MENU_SORT_ORDER__DEFAULT ) + "#", " selected",
            "#doc_types#", createDocumentTypesOptionList(),
            "#meta_id#", "" + documentRequest.getDocument().getId(),
            "#defaulttemplate#", URLEncoder.encode( StringUtils.defaultString( menuattributes.getProperty( "defaulttemplate" ) ) )
        } );
        return parseTags;
    }

    private String getMenuModeSuffix( int menuIndex, List parseTags ) {
        Integer editingMenuIndex = parserParameters.getEditingMenuIndex();
        ImcmsServices services = parserParameters.getDocumentRequest().getServices();
        if ( null != editingMenuIndex && editingMenuIndex.intValue() == menuIndex ) {
            return services.getAdminTemplate( "textdoc/archive_del_button.html", parserParameters.getDocumentRequest().getUser(), null );
        } else {
            return services.getAdminTemplate( "textdoc/admin_menu.frag", parserParameters.getDocumentRequest().getUser(), parseTags );
        }
    }

    private String createDocumentTypesOptionList() {
        DocumentDomainObject document = parserParameters.getDocumentRequest().getDocument();

        UserDomainObject user = parserParameters.getDocumentRequest().getUser();
        IdNamePair[] docTypes = parserParameters.getDocumentRequest().getServices().getDocumentMapper().getCreatableDocumentTypeIdsAndNamesInUsersLanguage( document, user );
        Map docTypesMap = new HashMap();
        for ( int i = 0; i < docTypes.length; i++ ) {
            IdNamePair docType = docTypes[i];
            docTypesMap.put( new Integer( docType.getId() ), docType );
        }

        String existing_doc_name = parserParameters.getDocumentRequest().getServices().getAdminTemplate( "textdoc/existing_doc_name.html", parserParameters.getDocumentRequest().getUser(), null );
        docTypesMap.put( new Integer( EXISTING_DOCTYPE_ID ), new IdNamePair( EXISTING_DOCTYPE_ID, existing_doc_name ) );

        final int[] docTypeIdsOrder = {
            DocumentDomainObject.DOCTYPE_ID_TEXT,
            EXISTING_DOCTYPE_ID,
            DocumentDomainObject.DOCTYPE_ID_URL,
            DocumentDomainObject.DOCTYPE_ID_FILE,
            DocumentDomainObject.DOCTYPE_ID_BROWSER,
            DocumentDomainObject.DOCTYPE_ID_HTML,
            DocumentDomainObject.DOCTYPE_ID_CHAT,
            DocumentDomainObject.DOCTYPE_ID_BILLBOARD,
            DocumentDomainObject.DOCTYPE_ID_CONFERENCE,
        };

        StringBuffer documentTypesHtmlOptionList = new StringBuffer();
        for ( int i = 0; i < docTypeIdsOrder.length; i++ ) {
            int docTypeId = docTypeIdsOrder[i];
            IdNamePair temp = (IdNamePair)docTypesMap.get( new Integer( docTypeId ) );
            if ( null != temp ) {
                int documentTypeId = temp.getId();
                String documentTypeName = temp.getName();
                documentTypesHtmlOptionList.append( "<option value=\"" + documentTypeId + "\">" + documentTypeName
                                                    + "</option>" );
            }
        }

        return documentTypesHtmlOptionList.toString();
    }

    private String parseMenuNode( int menuIndex, String menutemplate, Properties menuattributes,
                                  PatternMatcher patMat, TagParser tagParser ) {
        String modeAttribute = menuattributes.getProperty( "mode" );
        boolean modeIsRead = "read".equalsIgnoreCase( modeAttribute );
        boolean modeIsWrite = "write".equalsIgnoreCase( modeAttribute );
        boolean menuMode = parserParameters.isMenuMode();
        if ( menuMode && modeIsRead || !menuMode && modeIsWrite ) {
            return "";
        }
        MenuDomainObject currentMenu = getMenuByIndex( menuIndex ); 	// Get the menu
        StringBuffer result = new StringBuffer(); // Allocate a buffer for building our return-value in.
        NodeList menuNodes = new NodeList( menutemplate, parserParameters.getDocumentRequest().getHttpServletRequest() ); // Build a tree-structure of nodes in memory, which "only" needs to be traversed. (Vood)oo-magic.
        nodeMenu( new SimpleElement( "menu", menuattributes, menuNodes ), result, currentMenu, patMat, menuIndex, tagParser ); // Create an artificial root-node of this tree. An "imcms:menu"-element.
        if ( menuMode ) { // If in menuMode, make sure to include all the stuff from the proper admintemplates.
            List parseTags = createParseTags( menuIndex, menuattributes );
            result.append( getMenuModeSuffix( menuIndex, parseTags ) );
            result.insert( 0, getMenuModePrefix( menuIndex, parseTags ) );
        }
        return result.toString();
    }

    /**
     * Handle an imcms:menu element.
     */
    private void nodeMenu( Element menuNode, StringBuffer result, MenuDomainObject currentMenu,
                           PatternMatcher patMat, int menuIndex, TagParser tagParser ) {
        if ( currentMenu == null || 0 == currentMenu.getMenuItems().length ) {
            return; // Don't output anything
        }
        Properties menuAttributes = menuNode.getAttributes(); // Get the attributes from the imcms:menu-element. This will be passed down, to allow attributes of the imcms:menu-element to affect the menuitems.
        if ( menuNode.getChildElement( "menuloop" ) == null ) {
            nodeMenuLoop( new SimpleElement( "menuloop", null, menuNode.getChildren() ), result, currentMenu, menuAttributes, patMat, menuIndex, tagParser ); // The imcms:menu contained no imcms:menuloop, so let's create one, passing the children from the imcms:menu
        } else {
            // The imcms:menu contained at least one imcms:menuloop.
            Iterator menuNodeChildrenIterator = menuNode.getChildren().iterator();
            while ( menuNodeChildrenIterator.hasNext() ) {
                Node menuNodeChild = (Node)menuNodeChildrenIterator.next();
                switch ( menuNodeChild.getNodeType() ) { // Check the type of the child-node.
                    case Node.TEXT_NODE: // A text-node
                        result.append( tagParser.replaceTags( patMat,( (Text)menuNodeChild ).getContent() ) ); // Append the contents to our result.
                        break;
                    case Node.ELEMENT_NODE: // An element-node
                        if ( "menuloop".equals( ( (Element)menuNodeChild ).getName() ) ) { // Is it an imcms:menuloop?
                            nodeMenuLoop( (Element)menuNodeChild, result, currentMenu, menuAttributes, patMat, menuIndex, tagParser );
                        } else {
                            result.append( tagParser.replaceTags( patMat, menuNodeChild.toString() ) );  // No? Just append it (almost)verbatim.
                        }
                        break;
                }
            }
        }
    }

    /**
     * Handle an imcms:menuloop element.
     *
     * @param menuLoopNode   The imcms:menuloop-element
     * @param result         The StringBuffer to which to append the result
     * @param menu           The current menu
     * @param menuAttributes The attributes passed down from the imcms:menu-element.
     * @param patMat         The patternmatcher used for pattern matching.
     * @param tagParser
     */
    private void nodeMenuLoop( Element menuLoopNode, StringBuffer result, MenuDomainObject menu,
                               Properties menuAttributes, PatternMatcher patMat, int menuIndex, TagParser tagParser ) {
        if ( null == menu ) {
            return;
        }
        List menuLoopNodeChildren = menuLoopNode.getChildren();
        if ( null == menuLoopNode.getChildElement( "menuitem" ) ) {
            Element menuItemNode = new SimpleElement( "menuitem", null, menuLoopNodeChildren );  // The imcms:menuloop contained no imcms:menuitem, so let's create one.
            menuLoopNodeChildren = new ArrayList( 1 );
            menuLoopNodeChildren.add( menuItemNode );
        }
        // The imcms:menuloop contained at least one imcms:menuitem.
        loopOverMenuItemsAndMenuItemTemplateElementsAndAddToResult( menu, menuLoopNodeChildren,
                                                                    result, menuAttributes, patMat, menuIndex, tagParser );
    }

    private boolean editingMenu( int menuIndex ) {
        Integer editingMenuIndex = parserParameters.getEditingMenuIndex();
        boolean editingThisMenu = null != editingMenuIndex && editingMenuIndex.intValue() == menuIndex
                                  && parserParameters.isMenuMode();
        return editingThisMenu;
    }

    private void loopOverMenuItemsAndMenuItemTemplateElementsAndAddToResult( MenuDomainObject menu,
                                                                             final List menuLoopNodeChildren,
                                                                             StringBuffer result,
                                                                             Properties menuAttributes,
                                                                             PatternMatcher patMat,
                                                                             final int menuIndex, TagParser tagParser ) {
        Iterator menuItemsIterator = new FilterIterator( Arrays.asList( menu.getMenuItems() ).iterator(), new Predicate() {
            public boolean evaluate( Object o ) {
                DocumentDomainObject document = ( (MenuItemDomainObject)o ).getDocument();
                UserDomainObject user = parserParameters.getDocumentRequest().getUser();
                if ( user.canSeeDocumentInMenus( document ) ) {
                    if ( editingMenu( menuIndex ) || document.isPublishedAndNotArchived() ) {
                        return true;
                    }
                }
                return false;
            }
        } );

        int menuItemIndexStart = 0;
        try {
            menuItemIndexStart = Integer.parseInt( menuAttributes.getProperty( "indexstart" ) );
        } catch ( NumberFormatException nfe ) {
            // already set
        }
        int menuItemIndexStep = 1;
        try {
            menuItemIndexStep = Integer.parseInt( menuAttributes.getProperty( "indexstep" ) );
        } catch ( NumberFormatException nfe ) {
            // already set
        }
        int menuItemIndex = menuItemIndexStart;
        while ( menuItemsIterator.hasNext() ) { // While there still are more menuitems from the db.
            Iterator menuLoopChildrenIterator = menuLoopNodeChildren.iterator();
            while ( menuLoopChildrenIterator.hasNext() ) {  // While there still are more imcms:menuloop-children
                Node menuLoopChild = (Node)menuLoopChildrenIterator.next();
                switch ( menuLoopChild.getNodeType() ) { // Check the type of the child-node.
                    case Node.TEXT_NODE: // A text-node
                        result.append( tagParser.replaceTags( patMat, ( (Text)menuLoopChild ).getContent() ) ); // Append the contents to our result.
                        break;
                    case Node.ELEMENT_NODE: // An element-node
                        if ( "menuitem".equals( ( (Element)menuLoopChild ).getName() ) ) { // Is it an imcms:menuitem?
                            MenuItemDomainObject menuItem = menuItemsIterator.hasNext()
                                                            ? (MenuItemDomainObject)menuItemsIterator.next()
                                                            : null; // If there are more menuitems from the db, put the next in 'menuItem', otherwise put null.
                            nodeMenuItem( (Element)menuLoopChild, result, menuItem,
                                          menuAttributes, patMat, menuItemIndex, menu, menuIndex, tagParser ); // Parse one menuitem.
                            menuItemIndex += menuItemIndexStep;
                        } else {
                            result.append( tagParser.replaceTags( patMat, menuLoopChild.toString() ) );  // No? Just append the elements verbatim into the result.
                        }
                        break;
                }
            }
        }
    }

    /**
     * Handle one imcms:menuitem
     *
     * @param menuItemNode   The imcms:menuitem-element
     * @param result         The StringBuffer to which to append the result
     * @param menuItem       The current menuitem
     * @param menuAttributes The attributes passed down from the imcms:menu-element. Any attributes in the imcms:menuitem-element will override these.
     * @param patMat         The patternmatcher used for pattern matching.
     * @param tagParser
     */
    private void nodeMenuItem( Element menuItemNode, StringBuffer result, MenuItemDomainObject menuItem,
                               Properties menuAttributes, PatternMatcher patMat, int menuItemIndex,
                               MenuDomainObject menu, int menuIndex, TagParser tagParser ) {
        Substitution menuItemSubstitution;
        if ( menuItem != null ) {
            Properties menuItemAttributes = new Properties( menuAttributes ); // Make a copy of the menuAttributes, so we don't override them permanently.
            menuItemAttributes.putAll( menuItemNode.getAttributes() ); // Let all attributes of the menuItemNode override the attributes of the menu.
            menuItemSubstitution = getMenuItemSubstitution( menu, menuItem, menuItemAttributes, menuItemIndex, menuIndex );
        } else {
            menuItemSubstitution = NULLSUBSTITUTION;
        }
        Iterator menuItemChildrenIterator = menuItemNode.getChildren().iterator();
        while ( menuItemChildrenIterator.hasNext() ) { // For each node that is a child of this imcms:menuitem-element
            Node menuItemChild = (Node)menuItemChildrenIterator.next();
            switch ( menuItemChild.getNodeType() ) { // Check the type of the child-node.
                case Node.ELEMENT_NODE: // An element-node
                    Element menuItemChildElement = (Element)menuItemChild;
                    if ( !"menuitemhide".equals( menuItemChildElement.getName() )
                         || menuItem != null ) { // if the child-element isn't a imcms:menuitemhide-element or there is a child...
                        parseMenuItem( result, menuItemChildElement.getTextContent(),
                                       menuItemSubstitution, patMat, tagParser ); // parse it
                    }
                    break;
                case Node.TEXT_NODE: // A text-node
                    parseMenuItem( result, ( (Text)menuItemChild ).getContent(), menuItemSubstitution,
                                   patMat, tagParser ); // parse it
                    break;
            }
        }
    }

    private void parseMenuItem( StringBuffer result, String template, Substitution substitution,
                                PatternMatcher patMat, TagParser tagParser ) {
        String tagsReplaced = tagParser.replaceTags( patMat, template ) ;
        result.append( org.apache.oro.text.regex.Util.substitute( patMat, TextDocumentParser.HASHTAG_PATTERN, substitution,
                                                                  tagsReplaced,
                                                                  org.apache.oro.text.regex.Util.SUBSTITUTE_ALL ) );
    }

    public String tag( Properties menuattributes, String menutemplate,
                       PatternMatcher patMat, TagParser tagParser ) {
        int menuIndex;
        try {
            menuIndex = Integer.parseInt( menuattributes.getProperty( "no" ) );
        } catch ( NumberFormatException ex ) {
            menuIndex = implicitMenus[0]++;
        }
        return parseMenuNode( menuIndex, menutemplate, menuattributes, patMat, tagParser );
    }

    /**
     * Create a substitution for parsing this menuitem into a template with the correct tags.
     */
    private Substitution getMenuItemSubstitution( MenuDomainObject menu, MenuItemDomainObject menuItem,
                                                  Properties parameters, int menuItemIndex, int menuIndex ) {

        DocumentDomainObject document = menuItem.getDocument();
        String imageUrl = document.getMenuImage();
        String imageTag = imageUrl != null && imageUrl.length() > 0
                          ? "<img src=\"" + StringEscapeUtils.escapeHtml( imageUrl ) + "\" border=\"0\">" : "";
        String headline = document.getHeadline() ;
        if ( StringUtils.isBlank( headline ) ) {
            headline = "&nbsp;";
        } else {
            if ( !document.isPublished() ) {
                headline = "<em><i>" + headline;
                headline += "</i></em>";
            }
            if ( document.isArchived() ) {
                headline = "<strike>" + headline;
                headline += "</strike>";
            }
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        String createdDate = dateFormat.format( document.getCreatedDatetime() );
        String modifiedDate = dateFormat.format( document.getModifiedDatetime() );

        Properties tags = new Properties();
        tags.setProperty( "#childMetaId#", "" + document.getId() );
        tags.setProperty( "#childMetaHeadline#", headline );
        tags.setProperty( "#childMetaText#", document.getMenuText() );
        tags.setProperty( "#childMetaImage#", imageTag );
        tags.setProperty( "#childCreatedDate#", createdDate );
        tags.setProperty( "#childModifiedDate#", modifiedDate );
        tags.setProperty( "#menuitemindex#", "" + menuItemIndex );
        tags.setProperty( "#menuitemtreesortkey#", menuItem.getTreeSortKey().toString() );
        tags.setProperty( "#menuitemmetaid#", "" + document.getId() );
        tags.setProperty( "#menuitemheadline#", headline );
        tags.setProperty( "#menuitemtext#", document.getMenuText() );
        tags.setProperty( "#menuitemimage#", imageTag );
        tags.setProperty( "#menuitemimageurl#", StringEscapeUtils.escapeHtml( imageUrl ) );
        tags.setProperty( "#menuitemtarget#", document.getTarget() );
        tags.setProperty( "#menuitemdatecreated#", createdDate );
        tags.setProperty( "#menuitemdatemodified#", modifiedDate );

        DocumentRequest documentRequest = parserParameters.getDocumentRequest();

        String template = parameters.getProperty( "template" );
        String href = Utility.getAbsolutePathToDocument( documentRequest.getHttpServletRequest(), document );
        if (null != template) {
            href += -1 != href.indexOf( '?' ) ? '&' : '?' ;
            href += "template=" + URLEncoder.encode( template ) ;
        }

        List menuItemAHref = new ArrayList( 4 );
        menuItemAHref.add( "#href#" );
        menuItemAHref.add( href );
        menuItemAHref.add( "#target#" );
        menuItemAHref.add( document.getTarget() );

        UserDomainObject user = documentRequest.getUser();

        ImcmsServices serverObject = documentRequest.getServices();
        String a_href = serverObject.getAdminTemplate( "textdoc/menuitem_a_href.frag", user, menuItemAHref );

        tags.setProperty( "#menuitemlinkonly#", a_href );
        tags.setProperty( "#/menuitemlinkonly#", "</a>" );

        boolean editingThisMenu = editingMenu( menuIndex );
        if ( editingThisMenu ) {
            final int sortOrder = menu.getSortOrder();
            if ( MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED == sortOrder
                 || MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER == sortOrder ) {
                String sortKey ;
                String sortKeyTemplate ;
                if ( MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED == sortOrder ) {
                    sortKey = "" + menuItem.getSortKey();
                    sortKeyTemplate = "textdoc/admin_menuitem_manual_sortkey.frag";

                } else {
                    String key = menuItem.getTreeSortKey().toString();
                    sortKey = key == null ? "" : key;
                    sortKeyTemplate = "textdoc/admin_menuitem_treesortkey.frag";
                }
                List menuItemSortKeyTags = new ArrayList( 4 );
                menuItemSortKeyTags.add( "#meta_id#" );
                menuItemSortKeyTags.add( "" + document.getId() );
                menuItemSortKeyTags.add( "#sortkey#" );
                menuItemSortKeyTags.add( sortKey );

                a_href = serverObject.getAdminTemplate( sortKeyTemplate, user, menuItemSortKeyTags )
                         + a_href;
            }

            List menuItemCheckboxTags = new ArrayList( 2 );
            menuItemCheckboxTags.add( "#meta_id#" );
            menuItemCheckboxTags.add( "" + document.getId() );

            a_href = serverObject.getAdminTemplate( "textdoc/admin_menuitem_checkbox.frag", user, menuItemCheckboxTags )
                     + a_href;
            a_href = Html.getLinkedStatusIconTemplate( menuItem.getDocument(), user, documentRequest.getHttpServletRequest() ) + a_href;
        }

        tags.setProperty( "#menuitemlink#", a_href );
        tags.setProperty( "#/menuitemlink#",
                          editingThisMenu && user.canEdit( menuItem.getDocument() )
                          ? "</a>"
                            + serverObject.getAdminTemplate( "textdoc/admin_menuitem.frag", user, Arrays.asList( new String[]{
                                "#meta_id#", ""
                                             + document.getId()
                            } ) )
                          : "</a>" );

        return new MapSubstitution( tags, true );
    }

}
