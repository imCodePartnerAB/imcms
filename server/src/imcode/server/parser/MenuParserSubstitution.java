package imcode.server.parser ;

import java.util.* ;
import org.apache.oro.text.regex.* ;
import imcode.util.log.* ;
import imcode.util.* ;

public class MenuParserSubstitution implements Substitution {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private static Pattern HASHTAG_PATTERN  = null ;
    private static Pattern MENU_NO_PATTERN  = null ;

    private FileCache fileCache = new FileCache() ;

    Log log = Log.getLog("server") ;

    static {

	Perl5Compiler patComp = new Perl5Compiler() ;

	try {
	    HASHTAG_PATTERN = patComp.compile("#[^#\"<> \\t\\r\\n]+#",Perl5Compiler.READ_ONLY_MASK) ;
	    MENU_NO_PATTERN = patComp.compile("#doc_menu_no#",Perl5Compiler.READ_ONLY_MASK) ;
	} catch (MalformedPatternException ignored) {
	    // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
	    Log log = Log.getLog("server") ;
	    log.log(Log.CRITICAL, "Danger, Will Robinson!") ;
	}
    }

    Map menus ;
    boolean menumode ;
    Properties tags ;
    int[] implicitMenus = {1} ;

    public MenuParserSubstitution (Map menus,boolean menumode, Properties tags ) {
	this.menumode = menumode ;
	this.menus = menus ;
	this.tags = tags ;
    }

    private List getMenuById(int id) {
	return (LinkedList)menus.get(new Integer(id)) ;
    }

    private String getMenuModePrefix(PatternMatcher patMat, int menu_id) {
	String temp = tags.getProperty("addDoc") +
	    tags.getProperty("saveSortStart") ;

	return org.apache.oro.text.regex.Util.substitute(patMat,MENU_NO_PATTERN,new StringSubstitution(""+menu_id),temp,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
    }

    private String getMenuModeSuffix() {
	return tags.getProperty("saveSortStop") ;
    }

    private String nodeMenuParser (int menuId, PatternMatcher patMat) {
	List currentMenu = getMenuById(menuId) ; 	// Get the LinkedList of Properties that is the menu
	if (currentMenu == null) {
	    currentMenu = new LinkedList() ;
	}
	StringBuffer result = new StringBuffer() ; // Allocate a buffer for building our return-value in.
	String menutemplate = patMat.getMatch().group(2) ;
	NodeList menuNodes = new NodeList(menutemplate) ; // Build a tree-structure of nodes in memory, which "only" needs to be traversed. (Vood)oo-magic.
	nodeMenu(new SimpleElement("menu",null,menuNodes),result,currentMenu, patMat) ; // Create an artificial root-node of this tree. An "imcms:menu"-element.
	if (menumode) { // If in menumode, make sure to include all the stuff from the proper admintemplates.
	    result.append(getMenuModeSuffix()) ;
	    result.insert(0,getMenuModePrefix(patMat,menuId)) ;
	}
	return result.toString() ;
    }

    /**
       Handle an imcms:menu element.
    **/
    private void nodeMenu(Element menuNode, StringBuffer result, List currentMenu, PatternMatcher patMat) {
	if (menuNode.getChildElement("menuloop") == null) {
	    nodeMenuLoop(new SimpleElement("menuloop",null,menuNode.getChildren()), result, currentMenu, patMat) ; // The imcms:menu contained no imcms:menuloop, so let's create one.
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
			nodeMenuLoop((Element)menuNodeChild,result,currentMenu,patMat) ;
		    } else {
			result.append(((Element)menuNodeChild).toString()) ;  // No? Just print it verbatim into the source.
		    }
		    break ;
		}
	    }
	}
    }

    /**
       Handle an imcms:menuloop element.
    **/
    private void nodeMenuLoop(Element menuLoopNode, StringBuffer result, List menuItems, PatternMatcher patMat) {
	MapSubstitution tagMapSubstitution = new MapSubstitution() ;
	Iterator menuItemsIterator = menuItems.iterator() ;
	if (menuLoopNode.getChildElement("menuitem") == null) {
	    Element menuItemNode = new SimpleElement("menuitem",null,menuLoopNode.getChildren()) ;  // The imcms:menuloop contained no imcms:menuitem, so let's create one.
	    while (menuItemsIterator.hasNext()) {
		Properties menuItem = (Properties)menuItemsIterator.next() ;
		nodeMenuItem(menuItemNode, result, menuItem, tagMapSubstitution, patMat) ; // Parse one menuitem through the only imcms:menuitem-element.
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
			    Properties menuItem = (menuItemsIterator.hasNext() ? (Properties)menuItemsIterator.next() : null) ; // If there are more menuitems from the db, put the next in 'menuItem', otherwise put null.
			    nodeMenuItem((Element)menuLoopChild,result,menuItem,tagMapSubstitution,patMat) ; // Parse one menuitem.
			} else {
			    result.append(menuLoopChild.toString()) ;  // No? Just append the elements verbatim into the result.
			}
			break ;
		    }
		}
	    }
	}
    }
    
    /** Parse one menuitem **/
    private void nodeMenuItem(Element menuItemNode, StringBuffer result, Properties menuItem, MapSubstitution tagMapSubstitution, PatternMatcher patMat) {
	Iterator menuItemChildrenIterator = menuItemNode.getChildren().iterator() ;
	while (menuItemChildrenIterator.hasNext()) {
	    Node menuItemChild = (Node)menuItemChildrenIterator.next() ;
	    switch(menuItemChild.getNodeType()) { // Check the type of the child-node.
	    case Node.ELEMENT_NODE : // A element-node
		if (!"menuitemhide".equals(((Element)menuItemChild).getName()) || menuItem != null ) {
		    tagMapSubstitution.setMap((menuItem != null ? menuItem : new Properties()), true) ;
		    result.append(org.apache.oro.text.regex.Util.substitute(patMat,HASHTAG_PATTERN,tagMapSubstitution,((Element)menuItemChild).getTextContent(),org.apache.oro.text.regex.Util.SUBSTITUTE_ALL)) ;
		}
		break ;
	    case Node.TEXT_NODE : // A text-node
		tagMapSubstitution.setMap((menuItem != null ? menuItem : new Properties()), true) ;
		result.append(org.apache.oro.text.regex.Util.substitute(patMat,HASHTAG_PATTERN,tagMapSubstitution,((Text)menuItemChild).getContent(),org.apache.oro.text.regex.Util.SUBSTITUTE_ALL)) ;
		break ;
	    }
	}
    }

    public void appendSubstitution( StringBuffer sb, MatchResult matres, int sc, String originalInput, PatternMatcher patMat, Pattern pat) {
	MatchResult menuMatres = patMat.getMatch() ;
	// Get the id of the menu
	int menuId = 0 ;
	try {
	    menuId = Integer.parseInt(menuMatres.group(1)) ;
	} catch (NumberFormatException ex) {
	    menuId = implicitMenus[0]++ ;
	}
	sb.append(nodeMenuParser(menuId,patMat)) ;
    }

}
