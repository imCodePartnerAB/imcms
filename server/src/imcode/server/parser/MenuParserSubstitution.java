package imcode.server.parser;

import imcode.server.DocumentRequest;
import org.apache.oro.text.regex.*;
import java.util.*;

import org.apache.oro.text.regex.*;
import imcode.util.*;
import imcode.server.*;
import imcode.server.user.UserDomainObject;

import java.text.SimpleDateFormat;
import java.net.URLEncoder;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

class MenuParserSubstitution implements Substitution {


    private Substitution NULLSUBSTITUTION = new StringSubstitution( "" );

    private Map menus;
    private boolean menumode;
    private Properties tags;
    private int[] implicitMenus = {1};
    private DocumentRequest documentRequest;

    public MenuParserSubstitution( DocumentRequest documentRequest, Map menus, boolean menumode, Properties tags ) {
        this.documentRequest = documentRequest;
        this.menumode = menumode;
        this.menus = menus;
        this.tags = tags;
    }

    private Menu getMenuById( int id ) {
        return (Menu)menus.get( new Integer( id ) );
    }

    private String getMenuModePrefix(int menu_id, String labelAttribute) {
        String temp = tags.getProperty("addDoc") +
                tags.getProperty("saveSortStart");

        String[] parseTags = new String[]{
            "#doc_menu_no#", "" + menu_id,
            "#label#", labelAttribute
        };

        return Parser.parseDoc(temp, parseTags);
    }

    private String getMenuModeSuffix() {
        return tags.getProperty("saveSortStop");
    }

    private String nodeMenuParser( int menuId, String menutemplate, Properties menuattributes, PatternMatcher patMat ) {
        String modeAttribute = menuattributes.getProperty( "mode" );
        boolean modeIsRead = "read".equalsIgnoreCase( modeAttribute );
        boolean modeIsWrite = "write".equalsIgnoreCase( modeAttribute );
        if ( menumode && modeIsRead || !menumode && modeIsWrite ) {
            return "";
        }
        Menu currentMenu = getMenuById( menuId ); 	// Get the menu
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
    private void nodeMenuLoop(Element menuLoopNode, StringBuffer result, Menu menuItems,
                              Properties menuAttributes, PatternMatcher patMat) {
        if (null == menuItems) {
            return;
        }
        ListIterator menuItemsIterator = menuItems.listIterator();
        List menuLoopNodeChildren = menuLoopNode.getChildren();
        if (null == menuLoopNode.getChildElement("menuitem")) {
            Element menuItemNode = new SimpleElement("menuitem", null, menuLoopNodeChildren);  // The imcms:menuloop contained no imcms:menuitem, so let's create one.
            menuLoopNodeChildren = new ArrayList(1);
            menuLoopNodeChildren.add(menuItemNode);
        }
        // The imcms:menuloop contained at least one imcms:menuitem.
        loopOverMenuItemsAndMenuItemTemplateElementsAndAddToResult(menuItemsIterator, menuLoopNodeChildren,
                result, menuAttributes, patMat);
    }

    private void loopOverMenuItemsAndMenuItemTemplateElementsAndAddToResult(
            ListIterator menuItemsIterator, final List menuLoopNodeChildren, StringBuffer result,
            Properties menuAttributes, PatternMatcher patMat) {
        int menuItemIndexStart = 0;
        try {
            menuItemIndexStart = Integer.parseInt(menuAttributes.getProperty("indexstart"));
        } catch (NumberFormatException nfe) {
            // already set
        }
        int menuItemIndexStep = 1;
        try {
            menuItemIndexStep = Integer.parseInt(menuAttributes.getProperty("indexstep"));
        } catch (NumberFormatException nfe) {
            // already set
        }
        int menuItemIndex = menuItemIndexStart;
        while (menuItemsIterator.hasNext()) { // While there still are more menuitems from the db.
            Iterator menuLoopChildrenIterator = menuLoopNodeChildren.iterator();
            while (menuLoopChildrenIterator.hasNext()) {  // While there still are more imcms:menuloop-children
                Node menuLoopChild = (Node) menuLoopChildrenIterator.next();
                switch (menuLoopChild.getNodeType()) { // Check the type of the child-node.
                    case Node.TEXT_NODE: // A text-node
                        result.append(((Text) menuLoopChild).getContent()); // Append the contents to our result.
                        break;
                    case Node.ELEMENT_NODE: // An element-node
                        if ("menuitem".equals(((Element) menuLoopChild).getName())) { // Is it an imcms:menuitem?
                            MenuItem menuItem = menuItemsIterator.hasNext()
                                    ? (MenuItem) menuItemsIterator.next()
                                    : null; // If there are more menuitems from the db, put the next in 'menuItem', otherwise put null.
                            nodeMenuItem((Element) menuLoopChild, result, menuItem,
                                    menuAttributes, patMat, menuItemIndex); // Parse one menuitem.
                            menuItemIndex += menuItemIndexStep;
                        } else {
                            result.append(menuLoopChild.toString());  // No? Just append the elements verbatim into the result.
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
    private void nodeMenuItem(Element menuItemNode, StringBuffer result, MenuItem menuItem,
                              Properties menuAttributes, PatternMatcher patMat, int menuItemIndex) {
        Substitution menuItemSubstitution;
        if (menuItem != null) {
            Properties menuItemAttributes = new Properties(menuAttributes); // Make a copy of the menuAttributes, so we don't override them permanently.
            menuItemAttributes.putAll(menuItemNode.getAttributes()); // Let all attributes of the menuItemNode override the attributes of the menu.
            menuItemSubstitution = getMenuItemSubstitution(menuItem, menuItemAttributes, menuItemIndex);
        } else {
            menuItemSubstitution = NULLSUBSTITUTION;
        }
        Iterator menuItemChildrenIterator = menuItemNode.getChildren().iterator();
        while (menuItemChildrenIterator.hasNext()) { // For each node that is a child of this imcms:menuitem-element
            Node menuItemChild = (Node) menuItemChildrenIterator.next();
            switch (menuItemChild.getNodeType()) { // Check the type of the child-node.
                case Node.ELEMENT_NODE: // An element-node
                    Element menuItemChildElement = (Element) menuItemChild;
                    if ((!"menuitemhide".equals(menuItemChildElement.getName()))
                            || menuItem != null) { // if the child-element isn't a imcms:menuitemhide-element or there is a child...
                        parseMenuItem(result, menuItemChildElement.getTextContent(),
                                menuItemSubstitution, patMat); // parse it
                    }
                    break;
                case Node.TEXT_NODE: // A text-node
                    parseMenuItem(result, ((Text) menuItemChild).getContent(), menuItemSubstitution,
                            patMat); // parse it
                    break;
            }
        }
    }

    private void parseMenuItem(StringBuffer result, String template, Substitution substitution,
                               PatternMatcher patMat) {
        result.append(
                org.apache.oro.text.regex.Util.substitute(patMat, TextDocumentParser.HASHTAG_PATTERN, substitution,
                        template,
                        org.apache.oro.text.regex.Util.SUBSTITUTE_ALL));
    }

    public void appendSubstitution(StringBuffer sb, MatchResult matres, int sc,
                                   PatternMatcherInput originalInput, PatternMatcher patMat,
                                   Pattern pat) {
        MatchResult menuMatres = patMat.getMatch();
        String attributes_string = menuMatres.group(1);
        String menutemplate = menuMatres.group(2);
        Properties menuattributes = NodeList.createAttributes(attributes_string, patMat);
        // Get the id of the menu
        int menuId;
        try {
            menuId = Integer.parseInt(menuattributes.getProperty("no"));
        } catch (NumberFormatException ex) {
            menuId = implicitMenus[0]++;
        }
        sb.append(nodeMenuParser(menuId, menutemplate, menuattributes, patMat));
    }

    /**
     * Create a substitution for parsing this menuitem into a template with the correct tags.
     */
    private Substitution getMenuItemSubstitution( MenuItem menuItem, Properties parameters, int menuItemIndex ) {

        String imageUrl = menuItem.getImage();
        String imageTag = ( imageUrl != null && imageUrl.length() > 0 ) ? ( "<img src=\"" + imageUrl + "\" border=\"0\">" ) : "";
        String headline = menuItem.getHeadline();
        if ( headline.length() == 0 ) {
            headline = "&nbsp;";
        } else {
            if ( !menuItem.isActivated() ) {
                headline = "<em><i>" + headline;
                headline += "</i></em>";
            }
            if ( menuItem.isArchived() ) {
                headline = "<strike>" + headline;
                headline += "</strike>";
            }
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        String createdDate = dateFormat.format( menuItem.getCreatedDatetime() );
        String modifiedDate = dateFormat.format( menuItem.getModifiedDatetime() );

        Properties tags = new Properties();
        tags.setProperty( "#childMetaId#", "" + menuItem.getMetaId() );
        tags.setProperty( "#childMetaHeadline#", headline );
        tags.setProperty( "#childMetaText#", menuItem.getText() );
        tags.setProperty( "#childMetaImage#", imageTag );
        tags.setProperty( "#childCreatedDate#", createdDate );
        tags.setProperty( "#childModifiedDate#", modifiedDate );
        tags.setProperty( "#menuitemindex#", "" + menuItemIndex);
        tags.setProperty( "#menuitemtreesortkey#", menuItem.getTreeSortKey());
        tags.setProperty( "#menuitemmetaid#", "" + menuItem.getMetaId());
        tags.setProperty( "#menuitemheadline#", headline );
        tags.setProperty( "#menuitemtext#", menuItem.getText() );
        tags.setProperty( "#menuitemimage#", imageTag );
        tags.setProperty( "#menuitemimageurl#", imageUrl );
        tags.setProperty( "#menuitemtarget#", menuItem.getTarget() );
        tags.setProperty( "#menuitemdatecreated#", createdDate );
        tags.setProperty( "#menuitemdatemodified#", modifiedDate );

        // If this doc is a file, we'll want to put in the filename
        // as an escaped translated path
        // For example: /servlet/GetDoc/filename.ext?meta_id=1234
        //                             ^^^^^^^^^^^^^
        String template = parameters.getProperty( "template" );
        String href = "GetDoc"
                + (menuItem.getFilename() == null || menuItem.getFilename().length() == 0
                ? "" : "/" + URLEncoder.encode(menuItem.getFilename()))
                + "?meta_id="
                + menuItem.getMetaId()
                + (template != null ? "&template=" + URLEncoder.encode(template) : "");

        List menuItemAHref = new ArrayList(4);
        menuItemAHref.add("#href#");
        menuItemAHref.add(href);
        menuItemAHref.add("#target#");
        menuItemAHref.add(menuItem.getTarget());

        UserDomainObject user = documentRequest.getUser() ;

        String a_href = documentRequest.getServerObject().parseDoc(
                menuItemAHref,
                "textdoc/menuitem_a_href.frag", user);

        tags.setProperty( "#menuitemlinkonly#", a_href );
        tags.setProperty( "#/menuitemlinkonly#", "</a>" );

        if ( menuItem.getParentMenu().isMenuMode() ) {
            final int sortOrder = menuItem.getParentMenu().getSortOrder();
            if (IMCConstants.MENU_SORT_BY_MANUAL_ORDER == sortOrder || IMCConstants.MENU_SORT_BY_MANUAL_TREE_ORDER == sortOrder) {
                String sortKey = "";
                String sortKeyTemplate = null;
                if (IMCConstants.MENU_SORT_BY_MANUAL_ORDER == sortOrder) {
                    sortKey = "" + menuItem.getSortKey();
                    sortKeyTemplate = "textdoc/admin_menuitem_manual_sortkey.frag";

                } else if (IMCConstants.MENU_SORT_BY_MANUAL_TREE_ORDER == sortOrder) {
                    String key = menuItem.getTreeSortKey();
                    sortKey = key == null ? "" : key;
                    sortKeyTemplate = "textdoc/admin_menuitem_treesortkey.frag";
            }
                List menuItemSortKeyTags = new ArrayList(4);
                menuItemSortKeyTags.add("#meta_id#");
                menuItemSortKeyTags.add("" + menuItem.getMetaId());
                menuItemSortKeyTags.add("#sortkey#");
                menuItemSortKeyTags.add(sortKey);

                a_href = documentRequest.getServerObject().parseDoc(
                        menuItemSortKeyTags,
                        sortKeyTemplate, user) + a_href;
            }

            List menuItemCheckboxTags = new ArrayList(2);
            menuItemCheckboxTags.add("#meta_id#");
            menuItemCheckboxTags.add("" + menuItem.getMetaId());

            a_href = documentRequest.getServerObject().parseDoc(
                    menuItemCheckboxTags,
                    "textdoc/admin_menuitem_checkbox.frag", user) + a_href;
        }

        tags.setProperty( "#menuitemlink#", a_href );
        tags.setProperty( "#/menuitemlink#",
                menuItem.getParentMenu().isMenuMode() && menuItem.isEditable()
                ? "</a>"
                + documentRequest.getServerObject().parseDoc(
                        Arrays.asList(new String[]{"#meta_id#", ""
                + menuItem.getMetaId()}),
                        "textdoc/admin_menuitem.frag", user)
                : "</a>");


        return new MapSubstitution( tags, true );
    }

}
