<%@ page import="imcode.server.DocumentRequest, imcode.server.document.textdocument.MenuDomainObject, imcode.server.document.textdocument.MenuItemDomainObject, imcode.server.document.textdocument.TextDocumentDomainObject, imcode.server.parser.MenuParser, imcode.server.parser.NodeList, imcode.server.parser.ParserParameters, imcode.server.parser.SimpleElement, imcode.server.parser.TagParser, imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper, imcode.server.user.UserDomainObject, org.apache.oro.text.regex.Perl5Matcher, java.io.StringWriter, java.util.Properties"%><% 
    ParserParameters parserParameters = (ParserParameters) request.getAttribute("parserParameters") ;
    MenuParser menuParser = (MenuParser) request.getAttribute("menuParser") ;
    int menuIndex =  ((Integer) request.getAttribute("menuIndex") ).intValue() ;
    Properties menuAttributes = (Properties) request.getAttribute("menuAttributes") ;
    TagParser tagParser = (TagParser) request.getAttribute("tagParser") ;
    String menuTemplate = (String) request.getAttribute("menuTemplate") ;

    boolean menuMode = parserParameters.isMenuMode();
    String label = menuAttributes.getProperty("label") ;
    DocumentRequest documentRequest = parserParameters.getDocumentRequest();
    UserDomainObject user = documentRequest.getUser();
    TextDocumentDomainObject document = (TextDocumentDomainObject) documentRequest.getDocument() ;
    final MenuDomainObject menu = document.getMenu(menuIndex) ;
    MenuItemDomainObject[] menuItemsUserCanSee = menu.getMenuItemsUserCanSee(documentRequest.getUser() ) ;
    ImcmsAuthenticatorAndUserAndRoleMapper userMapper = documentRequest.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
    UserDomainObject defaultUser = userMapper.getDefaultUser() ;
    MenuItemDomainObject[] defaultUserMenuItems = menu.getPublishedMenuItemsUserCanSee( defaultUser );

    NodeList menuNodes = new NodeList( menuTemplate, parserParameters.getDocumentRequest().getHttpServletRequest(), tagParser );
    StringWriter content = new StringWriter();
    menuParser.nodeMenu( new SimpleElement( "menu", menuAttributes, menuNodes ), content, menu, new Perl5Matcher(), menuIndex, tagParser );

     if (menuMode) { %>
<a href="<%= request.getContextPath() %>/servlet/ChangeMenu?documentId=<%= document.getId() %>&menuIndex=<%= menuIndex %>" class="imcms_label"
        ><%= label %> [<%= defaultUserMenuItems.length %>/<%= menuItemsUserCanSee.length %>]&nbsp;<%-- 
    --%><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/red.gif" border="0" alt="edit menu <%= menuIndex%>" align="bottom"></a>
<%= content.toString() %>
    <a href="<%= request.getContextPath() %>/servlet/ChangeMenu?documentId=<%= document.getId() %>&menuIndex=<%= menuIndex %>"
            ><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/ico_txt.gif" border="0" alt="edit menu <%= menuIndex%>"></a>
<% } else { %>
<%= content.toString() %>
<% } %>
