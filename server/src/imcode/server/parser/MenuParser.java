package imcode.server.parser;

import imcode.server.DocumentRequest;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.util.Utility;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.StringSubstitution;
import org.apache.oro.text.regex.Substitution;
import org.apache.oro.text.regex.Util;
import org.apache.oro.text.regex.Perl5Matcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.io.Writer;
import java.io.IOException;
import java.io.StringWriter;

public class MenuParser {

    private final static Substitution NULLSUBSTITUTION = new StringSubstitution( "" );

    private int[] implicitMenus = {1};
    private ParserParameters parserParameters;

    MenuParser( ParserParameters parserParameters ) {
        this.parserParameters = parserParameters;
    }

    private String parseMenuNode(int menuIndex, String menuTemplate, Properties menuAttributes,
                                 TagParser tagParser) {
        String modeAttribute = menuAttributes.getProperty( "mode" );
        boolean modeIsRead = "read".equalsIgnoreCase( modeAttribute );
        boolean modeIsWrite = "write".equalsIgnoreCase( modeAttribute );
        boolean menuMode = parserParameters.isMenuMode();
        if ( menuMode && modeIsRead || !menuMode && modeIsWrite ) {
            return "";
        }

        try {
            NodeList menuNodes = new NodeList( menuTemplate, parserParameters.getDocumentRequest().getHttpServletRequest(), tagParser );
            DocumentRequest documentRequest = parserParameters.getDocumentRequest();
            TextDocumentDomainObject document = (TextDocumentDomainObject) documentRequest.getDocument() ;
            final MenuDomainObject menu = document.getMenu(menuIndex) ;
            StringWriter contentWriter = new StringWriter();
            nodeMenu( new SimpleElement( "menu", menuAttributes, menuNodes ), contentWriter, menu, new Perl5Matcher(), menuIndex, tagParser );
            String content = contentWriter.toString();
            return addMenuAdmin(menuIndex, menuMode, content, menu, documentRequest.getHttpServletRequest(), documentRequest.getHttpServletResponse(), menuAttributes.getProperty("label"));
        } catch ( Exception e ) {
            throw new UnhandledException(e);
        }
    }

    public static String addMenuAdmin(int menuIndex, boolean menuMode, String content,
                                MenuDomainObject menu,
                                HttpServletRequest request, HttpServletResponse response, String label) throws ServletException, IOException {
        if (!menuMode) {
            return content;
        }
        ImcmsAuthenticatorAndUserAndRoleMapper userMapper = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        UserDomainObject defaultUser = userMapper.getDefaultUser() ;
        MenuItemDomainObject[] defaultUserMenuItems = menu.getPublishedMenuItemsUserCanSee( defaultUser );
        UserDomainObject user = Utility.getLoggedOnUser(request);
        MenuItemDomainObject[] menuItemsUserCanSee = menu.getMenuItemsUserCanSee(user ) ;
        request.setAttribute("content", content);
        request.setAttribute("label", label);
        request.setAttribute("defaultUserCount", defaultUserMenuItems.length) ;
        request.setAttribute("userCount", menuItemsUserCanSee.length);
        request.setAttribute("menuIndex", new Integer(menuIndex));
        return Utility.getContents("/imcms/"+user.getLanguageIso639_2()+"/jsp/docadmin/text/edit_menu.jsp",
                                   request, response) ;
    }

    /**
     * Handle an imcms:menu element.
     */
    public void nodeMenu( Element menuNode, Writer result, MenuDomainObject currentMenu,
                          PatternMatcher patMat, int menuIndex, TagParser tagParser ) throws IOException {
        if ( currentMenu == null || 0 == currentMenu.getMenuItems().length ) {
            return; // Don't output anything
        }
        Properties menuAttributes = menuNode.getAttributes(); // Get the attributes from the imcms:menu-element. This will be passed down, to allow attributes of the imcms:menu-element to affect the menuitems.
        if ( menuNode.getChildElement( "menuloop" ) == null ) {
            nodeMenuLoop( new SimpleElement( "menuloop", null, menuNode.getChildren() ), result, currentMenu, menuAttributes, patMat, tagParser ); // The imcms:menu contained no imcms:menuloop, so let's create one, passing the children from the imcms:menu
        } else {
            // The imcms:menu contained at least one imcms:menuloop.
            Iterator menuNodeChildrenIterator = menuNode.getChildren().iterator();
            while ( menuNodeChildrenIterator.hasNext() ) {
                Node menuNodeChild = (Node)menuNodeChildrenIterator.next();
                switch ( menuNodeChild.getNodeType() ) { // Check the type of the child-node.
                    case Node.TEXT_NODE: // A text-node
                        result.write( tagParser.replaceTags(( (Text)menuNodeChild ).getContent(), false) ); // Append the contents to our result.
                        break;
                    case Node.ELEMENT_NODE: // An element-node
                        if ( "menuloop".equals( ( (Element)menuNodeChild ).getName() ) ) { // Is it an imcms:menuloop?
                            nodeMenuLoop( (Element)menuNodeChild, result, currentMenu, menuAttributes, patMat, tagParser );
                        } else {
                            result.append( tagParser.replaceTags(menuNodeChild.toString(), false) );  // No? Just append it (almost)verbatim.
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
    private void nodeMenuLoop(Element menuLoopNode, Writer result, MenuDomainObject menu,
                              Properties menuAttributes, PatternMatcher patMat, TagParser tagParser) throws IOException {
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
                                                                    result, menuAttributes, patMat, tagParser );
    }

    private void loopOverMenuItemsAndMenuItemTemplateElementsAndAddToResult(MenuDomainObject menu,
                                                                            final List menuLoopNodeChildren,
                                                                            Writer result,
                                                                            Properties menuAttributes,
                                                                            PatternMatcher patMat,
                                                                            TagParser tagParser) throws IOException {
        final UserDomainObject user = parserParameters.getDocumentRequest().getUser();
        Iterator menuItemsIterator = new FilterIterator( Arrays.asList( menu.getMenuItems() ).iterator(), new UserCanSeeMenuItemPredicate(user) );

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
                        result.write( tagParser.replaceTags(( (Text)menuLoopChild ).getContent(), false) ); // Append the contents to our result.
                        break;
                    case Node.ELEMENT_NODE: // An element-node
                        if ( "menuitem".equals( ( (Element)menuLoopChild ).getName() ) ) { // Is it an imcms:menuitem?
                            MenuItemDomainObject menuItem = menuItemsIterator.hasNext()
                                                            ? (MenuItemDomainObject)menuItemsIterator.next()
                                                            : null; // If there are more menuitems from the db, put the next in 'menuItem', otherwise put null.
                            nodeMenuItem( (Element)menuLoopChild, result, menuItem,
                                          menuAttributes, patMat, menuItemIndex, tagParser ); // Parse one menuitem.
                            menuItemIndex += menuItemIndexStep;
                        } else {
                            result.append( tagParser.replaceTags(menuLoopChild.toString(), false) );  // No? Just append the elements verbatim into the result.
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
    private void nodeMenuItem(Element menuItemNode, Writer result, MenuItemDomainObject menuItem,
                              Properties menuAttributes, PatternMatcher patMat, int menuItemIndex,
                              TagParser tagParser) throws IOException {
        Substitution menuItemSubstitution;
        if ( menuItem != null ) {
            Properties menuItemAttributes = new Properties( menuAttributes ); // Make a copy of the menuAttributes, so we don't override them permanently.
            menuItemAttributes.putAll( menuItemNode.getAttributes() ); // Let all attributes of the menuItemNode override the attributes of the menu.
            menuItemSubstitution = getMenuItemSubstitution(menuItem, menuItemAttributes, menuItemIndex);
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

    private void parseMenuItem( Writer result, String template, Substitution substitution,
                                PatternMatcher patMat, TagParser tagParser ) throws IOException {
        String tagsReplaced = tagParser.replaceTags(template, false) ;
        result.write( Util.substitute( patMat, TextDocumentParser.hashtagPattern, substitution,
                                       tagsReplaced,
                                       Util.SUBSTITUTE_ALL ) );
    }

    public String tag( Properties menuattributes, String menutemplate,
                       PatternMatcher patMat, TagParser tagParser ) {
        int menuIndex;
        try {
            menuIndex = Integer.parseInt( menuattributes.getProperty( "no" ) );
        } catch ( NumberFormatException ex ) {
            menuIndex = implicitMenus[0]++;
        }
        return parseMenuNode( menuIndex, menutemplate, menuattributes, tagParser );
    }

    /**
     * Create a substitution for parsing this menuitem into a template with the correct tags.
     */
    private Substitution getMenuItemSubstitution(MenuItemDomainObject menuItem,
                                                 Properties parameters, int menuItemIndex) {

        DocumentDomainObject document = menuItem.getDocument();
        DocumentRequest documentRequest = parserParameters.getDocumentRequest();
        String contextPath = documentRequest.getHttpServletRequest().getContextPath();
        String imageUrl = document.getMenuImage();
        final String imagesRoot = contextPath + Imcms.getServices().getConfig().getImageUrl();

        String imageTag = imageUrl != null && imageUrl.length() > 0
                          ? "<img src=\"" + imagesRoot + StringEscapeUtils.escapeHtml( imageUrl ) + "\" border=\"0\">" : "";
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

        String template = parameters.getProperty( "template" );
        HttpServletRequest request = documentRequest.getHttpServletRequest();
        String href = getPathToDocument(request, document, template);

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

        tags.setProperty( "#menuitemlink#", a_href );
        tags.setProperty( "#/menuitemlink#", "</a>" );

        return new MapSubstitution( tags, true );
    }

    public static String getPathToDocument(HttpServletRequest request, DocumentDomainObject document, String template) {
        String href = Utility.getAbsolutePathToDocument( request, document );
        if (StringUtils.isNotBlank(template)) {
            href += -1 != href.indexOf( '?' ) ? '&' : '?' ;
            href += "template=" + URLEncoder.encode( template ) ;
        }
        return href;
    }

    public static class UserCanSeeMenuItemPredicate implements Predicate {

        private final UserDomainObject user;

        public UserCanSeeMenuItemPredicate(UserDomainObject user) {
            this.user = user;
        }

        public boolean evaluate( Object o ) {
            DocumentDomainObject document = ( (MenuItemDomainObject)o ).getDocument();
            return user.canSeeDocumentInMenus(document);
        }
    }
}
