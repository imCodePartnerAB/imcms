package imcode.server.parser;

import imcode.server.DocumentRequest;
import imcode.server.IMCConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Parser;
import org.apache.oro.text.regex.*;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

class MenuParserSubstitution implements Substitution {

    private Substitution NULLSUBSTITUTION = new StringSubstitution( "" );

    private Map menus;
    private boolean menumode;
    private int[] implicitMenus = {1};
    private DocumentRequest documentRequest;

    public static final String TEMPLATE__STATUS_NEW = "textdoc/status/new.frag";
    public static final String TEMPLATE__STATUS_DISAPPROVED = "textdoc/status/disapproved.frag";
    public static final String TEMPLATE__STATUS_PUBLISHED = "textdoc/status/published.frag";
    public static final String TEMPLATE__STATUS_UNPUBLISHED = "textdoc/status/unpublished.frag";
    public static final String TEMPLATE__STATUS_ARCHIVED = "textdoc/status/archived.frag";
    public static final String TEMPLATE__STATUS_APPROVED = "textdoc/status/approved.frag";

    public MenuParserSubstitution( DocumentRequest documentRequest, Map menus, boolean menumode ) {
        this.documentRequest = documentRequest;
        this.menumode = menumode;
        this.menus = menus;
    }

    private Menu getMenuByIndex( int id ) {
        return (Menu)menus.get( new Integer( id ) );
    }

    private String getMenuModePrefix( int menuIndex, String labelAttribute ) {
        String temp = documentRequest.getServerObject().getAdminTemplate( "textdoc/add_doc.html", documentRequest.getUser(), null )
                      +
                      documentRequest.getServerObject().getAdminTemplate( "textdoc/sort_order.html", documentRequest.getUser(), null );

        String[] parseTags = new String[]{
            "#doc_menu_no#", "" + menuIndex,
            "#label#", labelAttribute,
            "#sortOrder" + getMenuByIndex( menuIndex ).getSortOrder() + "#", "checked",
            "#doc_types#", createDocumentTypesOptionList(),
            "#getMetaId#", ""+documentRequest.getDocument().getId()
        };

        return Parser.parseDoc( temp, parseTags );
    }

    class DocumentTypeIdNamePair {

        Integer id;
        String name;
    }

    private String createDocumentTypesOptionList() {
        int documentId = documentRequest.getDocument().getId();
        String[] docTypes = documentRequest.getServerObject().sqlProcedure( "GetDocTypesForUser", new String[]{
            "" + documentId, "" + documentRequest.getUser().getUserId(),
            documentRequest.getUser().getLanguageIso639_2()
        } );
        List docTypesList = new ArrayList( Arrays.asList( docTypes ) );

        String existing_doc_name = documentRequest.getServerObject().getAdminTemplate( "textdoc/existing_doc_name.html", documentRequest.getUser(), null );
        docTypesList.add( 0, "0" );
        docTypesList.add( 1, existing_doc_name );

        final int[] docTypesSortOrder = {
            DocumentDomainObject.DOCTYPE_TEXT,
            0, // "Existing document"
            DocumentDomainObject.DOCTYPE_URL,
            DocumentDomainObject.DOCTYPE_FILE,
            DocumentDomainObject.DOCTYPE_BROWSER,
            DocumentDomainObject.DOCTYPE_HTML,
            DocumentDomainObject.DOCTYPE_CHAT,
            DocumentDomainObject.DOCTYPE_BILLBOARD,
            DocumentDomainObject.DOCTYPE_CONFERENCE,
            DocumentDomainObject.DOCTYPE_DIAGRAM,
        };
        Map sortOrderMap = new HashMap();
        for ( int i = 0; i < docTypesSortOrder.length; i++ ) {
            int docTypeId = docTypesSortOrder[i];
            sortOrderMap.put( new Integer( docTypeId ), new Integer( i ) );
        }

        TreeMap sortedIds = new TreeMap();
        for ( Iterator iterator = docTypesList.iterator(); iterator.hasNext(); ) {
            DocumentTypeIdNamePair documentTypeIdNamePair = new DocumentTypeIdNamePair();
            documentTypeIdNamePair.id = new Integer( (String)iterator.next() );
            documentTypeIdNamePair.name = (String)iterator.next();

            Integer sortKey = (Integer)sortOrderMap.get( documentTypeIdNamePair.id );
            if ( null != sortKey ) {
                sortedIds.put( sortKey, documentTypeIdNamePair );
            }
        }

        Collection sortedTuplesOfDocumentTypes = sortedIds.values();
        Iterator docTypesIter = sortedTuplesOfDocumentTypes.iterator();
        StringBuffer doc_types_sb = new StringBuffer( 256 );
        while ( docTypesIter.hasNext() ) {
            DocumentTypeIdNamePair temp = (DocumentTypeIdNamePair)docTypesIter.next();
            Integer documentTypeId = temp.id;
            String documentTypeName = temp.name;
            doc_types_sb.append( "<option value=\"" );
            doc_types_sb.append( documentTypeId );
            doc_types_sb.append( "\">" );
            doc_types_sb.append( documentTypeName );
            doc_types_sb.append( "</option>" );
        }

        String doctypesOptionList = doc_types_sb.toString();
        return doctypesOptionList;
    }

    private String getMenuModeSuffix() {
        return documentRequest.getServerObject().getAdminTemplate( "textdoc/archive_del_button.html", documentRequest.getUser(), null );
    }

    private String nodeMenuParser( int menuId, String menutemplate, Properties menuattributes, PatternMatcher patMat ) {
        String modeAttribute = menuattributes.getProperty( "mode" );
        boolean modeIsRead = "read".equalsIgnoreCase( modeAttribute );
        boolean modeIsWrite = "write".equalsIgnoreCase( modeAttribute );
        if ( menumode && modeIsRead || !menumode && modeIsWrite ) {
            return "";
        }
        Menu currentMenu = getMenuByIndex( menuId ); 	// Get the menu
        StringBuffer result = new StringBuffer(); // Allocate a buffer for building our return-value in.
        NodeList menuNodes = new NodeList( menutemplate ); // Build a tree-structure of nodes in memory, which "only" needs to be traversed. (Vood)oo-magic.
        nodeMenu( new SimpleElement( "menu", menuattributes, menuNodes ), result, currentMenu, patMat ); // Create an artificial root-node of this tree. An "imcms:menu"-element.
        if ( menumode ) { // If in menumode, make sure to include all the stuff from the proper admintemplates.
            String labelAttribute = menuattributes.getProperty( "label" );
            result.append( getMenuModeSuffix() );
            result.insert( 0, getMenuModePrefix( menuId, labelAttribute ) );
        }
        return result.toString();
    }

    /**
     * Handle an imcms:menu element.
     */
    private void nodeMenu( Element menuNode, StringBuffer result, Menu currentMenu, PatternMatcher patMat ) {
        if ( currentMenu == null || currentMenu.isEmpty() ) {
            return; // Don't output anything
        }
        Properties menuAttributes = menuNode.getAttributes(); // Get the attributes from the imcms:menu-element. This will be passed down, to allow attributes of the imcms:menu-element to affect the menuitems.
        if ( menuNode.getChildElement( "menuloop" ) == null ) {
            nodeMenuLoop( new SimpleElement( "menuloop", null, menuNode.getChildren() ), result, currentMenu, menuAttributes, patMat ); // The imcms:menu contained no imcms:menuloop, so let's create one, passing the children from the imcms:menu
        } else {
            // The imcms:menu contained at least one imcms:menuloop.
            Iterator menuNodeChildrenIterator = menuNode.getChildren().iterator();
            while ( menuNodeChildrenIterator.hasNext() ) {
                Node menuNodeChild = (Node)menuNodeChildrenIterator.next();
                switch ( menuNodeChild.getNodeType() ) { // Check the type of the child-node.
                    case Node.TEXT_NODE: // A text-node
                        result.append( ( (Text)menuNodeChild ).getContent() ); // Append the contents to our result.
                        break;
                    case Node.ELEMENT_NODE: // An element-node
                        if ( "menuloop".equals( ( (Element)menuNodeChild ).getName() ) ) { // Is it an imcms:menuloop?
                            nodeMenuLoop( (Element)menuNodeChild, result, currentMenu, menuAttributes, patMat );
                        } else {
                            result.append( ( menuNodeChild ).toString() );  // No? Just append it (almost)verbatim.
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
     * @param menuItems      The current menu
     * @param menuAttributes The attributes passed down from the imcms:menu-element.
     * @param patMat         The patternmatcher used for pattern matching.
     */
    private void nodeMenuLoop( Element menuLoopNode, StringBuffer result, Menu menuItems,
                               Properties menuAttributes, PatternMatcher patMat ) {
        if ( null == menuItems ) {
            return;
        }
        ListIterator menuItemsIterator = menuItems.listIterator();
        List menuLoopNodeChildren = menuLoopNode.getChildren();
        if ( null == menuLoopNode.getChildElement( "menuitem" ) ) {
            Element menuItemNode = new SimpleElement( "menuitem", null, menuLoopNodeChildren );  // The imcms:menuloop contained no imcms:menuitem, so let's create one.
            menuLoopNodeChildren = new ArrayList( 1 );
            menuLoopNodeChildren.add( menuItemNode );
        }
        // The imcms:menuloop contained at least one imcms:menuitem.
        loopOverMenuItemsAndMenuItemTemplateElementsAndAddToResult( menuItemsIterator, menuLoopNodeChildren,
                                                                    result, menuAttributes, patMat );
    }

    private void loopOverMenuItemsAndMenuItemTemplateElementsAndAddToResult( ListIterator menuItemsIterator,
                                                                             final List menuLoopNodeChildren,
                                                                             StringBuffer result,
                                                                             Properties menuAttributes,
                                                                             PatternMatcher patMat ) {
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
                        result.append( ( (Text)menuLoopChild ).getContent() ); // Append the contents to our result.
                        break;
                    case Node.ELEMENT_NODE: // An element-node
                        if ( "menuitem".equals( ( (Element)menuLoopChild ).getName() ) ) { // Is it an imcms:menuitem?
                            MenuItem menuItem = menuItemsIterator.hasNext()
                                                ? (MenuItem)menuItemsIterator.next()
                                                : null; // If there are more menuitems from the db, put the next in 'menuItem', otherwise put null.
                            nodeMenuItem( (Element)menuLoopChild, result, menuItem,
                                          menuAttributes, patMat, menuItemIndex ); // Parse one menuitem.
                            menuItemIndex += menuItemIndexStep;
                        } else {
                            result.append( menuLoopChild.toString() );  // No? Just append the elements verbatim into the result.
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
     */
    private void nodeMenuItem( Element menuItemNode, StringBuffer result, MenuItem menuItem,
                               Properties menuAttributes, PatternMatcher patMat, int menuItemIndex ) {
        Substitution menuItemSubstitution;
        if ( menuItem != null ) {
            Properties menuItemAttributes = new Properties( menuAttributes ); // Make a copy of the menuAttributes, so we don't override them permanently.
            menuItemAttributes.putAll( menuItemNode.getAttributes() ); // Let all attributes of the menuItemNode override the attributes of the menu.
            menuItemSubstitution = getMenuItemSubstitution( menuItem, menuItemAttributes, menuItemIndex );
        } else {
            menuItemSubstitution = NULLSUBSTITUTION;
        }
        Iterator menuItemChildrenIterator = menuItemNode.getChildren().iterator();
        while ( menuItemChildrenIterator.hasNext() ) { // For each node that is a child of this imcms:menuitem-element
            Node menuItemChild = (Node)menuItemChildrenIterator.next();
            switch ( menuItemChild.getNodeType() ) { // Check the type of the child-node.
                case Node.ELEMENT_NODE: // An element-node
                    Element menuItemChildElement = (Element)menuItemChild;
                    if ( ( !"menuitemhide".equals( menuItemChildElement.getName() ) )
                         || menuItem != null ) { // if the child-element isn't a imcms:menuitemhide-element or there is a child...
                        parseMenuItem( result, menuItemChildElement.getTextContent(),
                                       menuItemSubstitution, patMat ); // parse it
                    }
                    break;
                case Node.TEXT_NODE: // A text-node
                    parseMenuItem( result, ( (Text)menuItemChild ).getContent(), menuItemSubstitution,
                                   patMat ); // parse it
                    break;
            }
        }
    }

    private void parseMenuItem( StringBuffer result, String template, Substitution substitution,
                                PatternMatcher patMat ) {
        result.append( org.apache.oro.text.regex.Util.substitute( patMat, TextDocumentParser.HASHTAG_PATTERN, substitution,
                                                                  template,
                                                                  org.apache.oro.text.regex.Util.SUBSTITUTE_ALL ) );
    }

    public void appendSubstitution( StringBuffer sb, MatchResult matres, int sc,
                                    PatternMatcherInput originalInput, PatternMatcher patMat,
                                    Pattern pat ) {
        MatchResult menuMatres = patMat.getMatch();
        String attributes_string = menuMatres.group( 1 );
        String menutemplate = menuMatres.group( 2 );
        Properties menuattributes = NodeList.createAttributes( attributes_string, patMat );
        // Get the id of the menu
        int menuId;
        try {
            menuId = Integer.parseInt( menuattributes.getProperty( "no" ) );
        } catch ( NumberFormatException ex ) {
            menuId = implicitMenus[0]++;
        }
        sb.append( nodeMenuParser( menuId, menutemplate, menuattributes, patMat ) );
    }

    /**
     * Create a substitution for parsing this menuitem into a template with the correct tags.
     */
    private Substitution getMenuItemSubstitution( MenuItem menuItem, Properties parameters, int menuItemIndex ) {

        DocumentDomainObject document = menuItem.getDocument();
        String imageUrl = document.getMenuImage();
        String imageTag = ( imageUrl != null && imageUrl.length() > 0 )
                          ? ( "<img src=\"" + imageUrl + "\" border=\"0\">" ) : "";
        String headline = document.getHeadline();
        if ( headline.length() == 0 ) {
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
        tags.setProperty( "#menuitemtreesortkey#", menuItem.getTreeSortKey() );
        tags.setProperty( "#menuitemmetaid#", "" + document.getId() );
        tags.setProperty( "#menuitemheadline#", headline );
        tags.setProperty( "#menuitemtext#", document.getMenuText() );
        tags.setProperty( "#menuitemimage#", imageTag );
        tags.setProperty( "#menuitemimageurl#", imageUrl );
        tags.setProperty( "#menuitemtarget#", document.getTarget() );
        tags.setProperty( "#menuitemdatecreated#", createdDate );
        tags.setProperty( "#menuitemdatemodified#", modifiedDate );

        String template = parameters.getProperty( "template" );
        String href = "GetDoc?meta_id=" + document.getId()
                      + ( template != null ? "&template=" + URLEncoder.encode( template ) : "" );

        List menuItemAHref = new ArrayList( 4 );
        menuItemAHref.add( "#href#" );
        menuItemAHref.add( href );
        menuItemAHref.add( "#target#" );
        menuItemAHref.add( document.getTarget() );

        UserDomainObject user = documentRequest.getUser();

        String a_href = documentRequest.getServerObject().getAdminTemplate( "textdoc/menuitem_a_href.frag", user, menuItemAHref );

        tags.setProperty( "#menuitemlinkonly#", a_href );
        tags.setProperty( "#/menuitemlinkonly#", "</a>" );

        if ( menuItem.getParentMenu().isMenuMode() ) {
            final int sortOrder = menuItem.getParentMenu().getSortOrder();
            if ( IMCConstants.MENU_SORT_BY_MANUAL_ORDER == sortOrder
                 || IMCConstants.MENU_SORT_BY_MANUAL_TREE_ORDER == sortOrder ) {
                String sortKey = "";
                String sortKeyTemplate = null;
                if ( IMCConstants.MENU_SORT_BY_MANUAL_ORDER == sortOrder ) {
                    sortKey = "" + menuItem.getSortKey();
                    sortKeyTemplate = "textdoc/admin_menuitem_manual_sortkey.frag";

                } else if ( IMCConstants.MENU_SORT_BY_MANUAL_TREE_ORDER == sortOrder ) {
                    String key = menuItem.getTreeSortKey();
                    sortKey = key == null ? "" : key;
                    sortKeyTemplate = "textdoc/admin_menuitem_treesortkey.frag";
                }
                List menuItemSortKeyTags = new ArrayList( 4 );
                menuItemSortKeyTags.add( "#meta_id#" );
                menuItemSortKeyTags.add( "" + document.getId() );
                menuItemSortKeyTags.add( "#sortkey#" );
                menuItemSortKeyTags.add( sortKey );

                a_href = documentRequest.getServerObject().getAdminTemplate( sortKeyTemplate, user, menuItemSortKeyTags )
                         + a_href;
            }

            List menuItemCheckboxTags = new ArrayList( 2 );
            menuItemCheckboxTags.add( "#meta_id#" );
            menuItemCheckboxTags.add( "" + document.getId() );

            a_href = documentRequest.getServerObject().getAdminTemplate( "textdoc/admin_menuitem_checkbox.frag", user, menuItemCheckboxTags )
                     + a_href;

            a_href = getStatusIconTemplate(menuItem.getDocument()) + a_href;
        }

        tags.setProperty( "#menuitemlink#", a_href );
        tags.setProperty( "#/menuitemlink#",
                          menuItem.getParentMenu().isMenuMode() && menuItem.isEditable()
                          ? "</a>"
                            + documentRequest.getServerObject().getAdminTemplate( "textdoc/admin_menuitem.frag", user, Arrays.asList( new String[]{
                                "#meta_id#", ""
                                             + document.getId()
                            } ) )
                          : "</a>" );

        return new MapSubstitution( tags, true );
    }

    private String getStatusIconTemplate(DocumentDomainObject document) {
        String statusIconTemplateName = null;
        if ( DocumentDomainObject.STATUS_NEW == document.getStatus() ) {
            statusIconTemplateName = TEMPLATE__STATUS_NEW;
        } else if ( DocumentDomainObject.STATUS_PUBLICATION_DISAPPROVED == document.getStatus() ) {
            statusIconTemplateName = TEMPLATE__STATUS_DISAPPROVED;
        } else if ( document.isPublishedAndNotArchived() ) {
            statusIconTemplateName = TEMPLATE__STATUS_PUBLISHED;
        } else if ( document.isNoLongerPublished() ) {
            statusIconTemplateName = TEMPLATE__STATUS_UNPUBLISHED;
        } else if ( document.isArchived() ) {
            statusIconTemplateName = TEMPLATE__STATUS_ARCHIVED;
        } else {
            statusIconTemplateName = TEMPLATE__STATUS_APPROVED;
        }
        String statusIconTemplate = documentRequest.getServerObject().getAdminTemplate( statusIconTemplateName, documentRequest.getUser(), null );
        return statusIconTemplate;
    }

}
