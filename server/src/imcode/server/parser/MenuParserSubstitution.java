package imcode.server.parser ;

import java.util.* ;
import org.apache.oro.text.regex.* ;
import imcode.util.* ;

import org.apache.log4j.Category;

public class MenuParserSubstitution implements Substitution {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private static Pattern HASHTAG_PATTERN  = null ;
    private static Pattern MENU_NO_PATTERN  = null ;

    private FileCache fileCache = new FileCache() ;

    private static Category log = Category.getRoot() ;

    static {

	Perl5Compiler patComp = new Perl5Compiler() ;

	try {
	    HASHTAG_PATTERN = patComp.compile("#[^#\"<>\\s]+#",Perl5Compiler.READ_ONLY_MASK) ;
	    MENU_NO_PATTERN = patComp.compile("#doc_menu_no#",Perl5Compiler.READ_ONLY_MASK) ;
	} catch (MalformedPatternException ignored) {
	    // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
	    log.fatal("Danger, Will Robinson!",ignored) ;
	}
    }

    private Substitution NULLSUBSTITUTION = new StringSubstitution("") ;

    Map menus ;
    boolean menumode ;
    Properties tags ;
    int[] implicitMenus = {1} ;

    public MenuParserSubstitution (Map menus,boolean menumode, Properties tags ) {
	this.menumode = menumode ;
	this.menus = menus ;
	this.tags = tags ;
    }

    private Menu getMenuById(int id) {
	return (Menu)menus.get(new Integer(id)) ;
    }

    private String getMenuModePrefix(PatternMatcher patMat, int menu_id) {
	String temp = tags.getProperty("addDoc") +
	    tags.getProperty("saveSortStart") ;

	return org.apache.oro.text.regex.Util.substitute(patMat,MENU_NO_PATTERN,new StringSubstitution(""+menu_id),temp,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
    }

    private String getMenuModeSuffix() {
	return tags.getProperty("saveSortStop") ;
    }

    private String nodeMenuParser (int menuId, String menutemplate, Properties menuattributes, PatternMatcher patMat) {
	Menu currentMenu = getMenuById(menuId) ; 	// Get the menu
	StringBuffer result = new StringBuffer() ; // Allocate a buffer for building our return-value in.
	NodeList menuNodes = new NodeList(menutemplate) ; // Build a tree-structure of nodes in memory, which "only" needs to be traversed. (Vood)oo-magic.
	nodeMenu(new SimpleElement("menu",menuattributes,menuNodes),result,currentMenu, patMat) ; // Create an artificial root-node of this tree. An "imcms:menu"-element.
	if (menumode) { // If in menumode, make sure to include all the stuff from the proper admintemplates.
	    result.append(getMenuModeSuffix()) ;
	    result.insert(0,getMenuModePrefix(patMat,menuId)) ;
	}
	return result.toString() ;
    }
    /**
       Handle an imcms:menu element.
    **/
    private void nodeMenu(Element menuNode, StringBuffer result, Menu currentMenu, PatternMatcher patMat) {
	if (currentMenu == null || currentMenu.isEmpty()) {
	    return ; // Don't output anything
	}
	Properties menuAttributes = menuNode.getAttributes() ; // Get the attributes from the imcms:menu-element. This will be passed down, to allow attributes of the imcms:menu-element to affect the menuitems.
	if (menuNode.getChildElement("menuloop") == null) {
	    nodeMenuLoop(new SimpleElement("menuloop",null,menuNode.getChildren()), result, currentMenu, menuAttributes, patMat) ; // The imcms:menu contained no imcms:menuloop, so let's create one, passing the children from the imcms:menu
	} else {
	    // The imcms:menu contained at least one imcms:menuloop.
	    Iterator menuNodeChildrenIterator = menuNode.getChildren().iterator() ;
	    while (menuNodeChildrenIterator.hasNext()) {
		Node menuNodeChild = (Node)menuNodeChildrenIterator.next() ;
		switch(menuNodeChild.getNodeType()) { // Check the type of the child-node.
		case Node.TEXT_NODE : // A text-node
		    result.append(((Text)menuNodeChild).getContent()) ; // Append the contents to our result.
		    break ;
		case Node.ELEMENT_NODE : // An element-node
		    if ("menuloop".equals(((Element)menuNodeChild).getName())) { // Is it an imcms:menuloop?
			nodeMenuLoop((Element)menuNodeChild,result,currentMenu,menuAttributes,patMat) ;
		    } else {
			result.append(((Element)menuNodeChild).toString()) ;  // No? Just append it (almost)verbatim.
		    }
		    break ;
		}
	    }
	}
    }

    /**
       Handle an imcms:menuloop element.
       @param menuLoopNode   The imcms:menuloop-element
       @param result         The StringBuffer to which to append the result
       @param menuItems      The current menu
       @param menuAttributes The attributes passed down from the imcms:menu-element.
       @param patMat         The patternmatcher used for pattern matching.
    **/
    private void nodeMenuLoop(Element menuLoopNode, StringBuffer result, Menu menuItems, Properties menuAttributes, PatternMatcher patMat) {
	if (menuItems == null) {
	    return ;
	}
	Iterator menuItemsIterator = menuItems.iterator() ;
	if (menuLoopNode.getChildElement("menuitem") == null) {
	    Element menuItemNode = new SimpleElement("menuitem",null,menuLoopNode.getChildren()) ;  // The imcms:menuloop contained no imcms:menuitem, so let's create one.
	    while (menuItemsIterator.hasNext()) {
		MenuItem menuItem = (MenuItem)menuItemsIterator.next() ;
		nodeMenuItem(menuItemNode, result, menuItem, menuAttributes, patMat) ; // Parse one menuitem through the only imcms:menuitem-element.
	    }
	} else {
	    // The imcms:menuloop contained at least one imcms:menuitem.
	    while (menuItemsIterator.hasNext()) { // While there still are more menuitems from the db.
		Iterator menuLoopChildrenIterator = menuLoopNode.getChildren().iterator() ;
		while (menuLoopChildrenIterator.hasNext()) {  // While there still are more imcms:menuloop-children
		    Node menuLoopChild = (Node)menuLoopChildrenIterator.next() ;
		    switch(menuLoopChild.getNodeType()) { // Check the type of the child-node.
		    case Node.TEXT_NODE : // A text-node
			result.append(((Text)menuLoopChild).getContent()) ; // Append the contents to our result.
			break ;
		    case Node.ELEMENT_NODE : // An element-node
			if ("menuitem".equals(((Element)menuLoopChild).getName())) { // Is it an imcms:menuitem?
			    MenuItem menuItem = (menuItemsIterator.hasNext() ? (MenuItem)menuItemsIterator.next() : null) ; // If there are more menuitems from the db, put the next in 'menuItem', otherwise put null.
			    nodeMenuItem((Element)menuLoopChild,result,menuItem,menuAttributes,patMat) ; // Parse one menuitem.
			} else {
			    result.append(menuLoopChild.toString()) ;  // No? Just append the elements verbatim into the result.
			}
			break ;
		    }
		}
	    }
	}
    }

    /**
       Handle one imcms:menuitem
       @param menuItemNode       The imcms:menuitem-element
       @param result             The StringBuffer to which to append the result
       @param menuItem           The current menuitem
       @param menuItemAttributes The attributes passed down from the imcms:menu-element. Any attributes in the imcms:menuitem-element will override these.
       @param patMat             The patternmatcher used for pattern matching.
    **/
    private void nodeMenuItem(Element menuItemNode, StringBuffer result, MenuItem menuItem, Properties menuAttributes, PatternMatcher patMat) {
	Substitution menuItemSubstitution = null ;
	if ( menuItem != null ) {
	    Properties menuItemAttributes = new Properties(menuAttributes) ; // Make a copy of the menuAttributes, so we don't override them permanently.
	    menuItemAttributes.putAll(menuItemNode.getAttributes()) ; // Let all attributes of the menuItemNode override the attributes of the menu.
	    menuItemSubstitution = menuItem.getSubstitution(menuItemAttributes) ;
	} else {
	    menuItemSubstitution = NULLSUBSTITUTION ;
	}
	Iterator menuItemChildrenIterator = menuItemNode.getChildren().iterator() ;
	while (menuItemChildrenIterator.hasNext()) { // For each node that is a child of this imcms:menuitem-element
	    Node menuItemChild = (Node)menuItemChildrenIterator.next() ;
	    switch(menuItemChild.getNodeType()) { // Check the type of the child-node.
	    case Node.ELEMENT_NODE : // An element-node
		Element menuItemChildElement = (Element)menuItemChild ;
		if ( (!"menuitemhide".equals(menuItemChildElement.getName())) || menuItem != null ) { // if the child-element isn't a imcms:menuitemhide-element or there is a child...
		    parseMenuItem(result,menuItemChildElement.getTextContent(),menuItemSubstitution,patMat) ; // parse it
		}
		break ;
	    case Node.TEXT_NODE : // A text-node
		parseMenuItem(result,((Text)menuItemChild).getContent(),menuItemSubstitution,patMat) ; // parse it
		break ;
	    }
	}
    }

    private void parseMenuItem(StringBuffer result, String template, Substitution substitution, PatternMatcher patMat) {
	result.append(org.apache.oro.text.regex.Util.substitute(patMat,HASHTAG_PATTERN,substitution,template,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL)) ;
    }

    public void appendSubstitution( StringBuffer sb, MatchResult matres, int sc, PatternMatcherInput originalInput, PatternMatcher patMat, Pattern pat) {
	MatchResult menuMatres = patMat.getMatch() ;
	String attributes_string = menuMatres.group(1) ;
	String menutemplate = menuMatres.group(2) ;
	Properties menuattributes = NodeList.createAttributes(attributes_string,patMat) ;
	// Get the id of the menu
	int menuId = 0 ;
	try {
	    menuId = Integer.parseInt(menuattributes.getProperty("no")) ;
	} catch (NumberFormatException ex) {
	    menuId = implicitMenus[0]++ ;
	}
	sb.append(nodeMenuParser(menuId,menutemplate, menuattributes,patMat)) ;
    }

}
