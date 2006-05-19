<%@ page import="com.imcode.util.HtmlBuilder, imcode.server.DocumentRequest, imcode.server.document.DocumentTypeDomainObject, imcode.server.document.TextDocumentPermissionSetDomainObject, imcode.server.document.textdocument.MenuDomainObject, imcode.server.document.textdocument.MenuItemDomainObject, imcode.server.document.textdocument.TextDocumentDomainObject, imcode.server.parser.ParserParameters, imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper, imcode.server.user.UserDomainObject, imcode.util.IdLocalizedNamePair, imcode.util.LocalizedMessage, org.apache.commons.collections.CollectionUtils, org.apache.commons.collections.Predicate, org.apache.commons.collections.Transformer, org.apache.commons.lang.StringEscapeUtils, org.apache.commons.lang.StringUtils, javax.servlet.http.HttpServletRequest, java.util.ArrayList, java.util.Arrays, java.util.List, java.util.Properties, java.util.Set, imcode.util.IdLocalizedNamePairToOptionTransformer, imcode.server.parser.MenuParser, imcode.server.parser.SimpleElement, imcode.server.parser.NodeList, imcode.server.parser.TagParser, org.apache.oro.text.regex.Perl5Matcher, java.io.StringWriter"%><% 
    ParserParameters parserParameters = (ParserParameters) request.getAttribute("parserParameters") ;
    MenuParser menuParser = (MenuParser) request.getAttribute("menuParser") ;
    int menuIndex = (Integer) request.getAttribute("menuIndex") ;
    Properties menuAttributes = (Properties) request.getAttribute("menuAttributes") ;
    TagParser tagParser = (TagParser) request.getAttribute("tagParser") ;
    String menuTemplate = (String) request.getAttribute("menuTemplate") ;

    int flags = parserParameters.getFlags() ;
    boolean menuMode = parserParameters.isMenuMode();
    String label = menuAttributes.getProperty("label") ;
    boolean editingThisMenu = menuMode && null != parserParameters.getEditingMenuIndex() && parserParameters.getEditingMenuIndex() == menuIndex ;
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

    if (editingThisMenu) { %>
<style type="text/css">
<!--
.imContent TABLE, .imContent TD, .imContent SPAN, .imContent INPUT, .imContent SELECT { font: 10px Verdana,sans-serif; }
INPUT.imcmsFormBtnSmall {
    background-color: #20568D;
    color: #ffffff;
    font: 10px Tahoma, Arial, sans-serif;
    border: 2px outset #668DB6;
    border-color: #668DB6 #000000 #000000 #668DB6;
    cursor:pointer;
    padding: 0 2;
}
.imMenuBtm { display:none; }
SPAN.imHeading  { font: bold 11px Tahoma,Verdana,sans-serif; color:#ffffff; }
SPAN.imText     { font: 11px Tahoma,Verdana,sans-serif; color:#000000; }
SELECT.imForm   { font: 11px Tahoma,Verdana,sans-serif; color:#000000; }
A.imLinkHelp    { font: bold 16px Arial, Tahoma,Verdana,sans-serif; color:#eeee00; text-decoration:none; }
-->
</style>
<form method="POST" action="<%= request.getContextPath() %>/servlet/ChangeMenu">
    <input TYPE="HIDDEN" name="meta_id" value="<%= document.getId() %>">
    <input type="HIDDEN" name="parent_meta_id" value="<%= document.getId() %>">
    <input type="HIDDEN" name="doc_menu_no" value="<%= menuIndex %>">
    <input type="HIDDEN" name="defaulttemplate" value="<%= StringEscapeUtils.escapeHtml( StringUtils.defaultString( menuAttributes.getProperty( "defaulttemplate" ) ) ) %>">
    <div class="imContent"><? templates/sv/textdoc/add_doc.html/2 ?>
        <table border="0" cellspacing="0" cellpadding="3" bgcolor="#f5f5f7" style="width:25em; border: 1px solid #e1ded9">
            <tr>
                <td bgcolor="#20568D">
                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td width="40">&nbsp;</td>
                    <td align="center"><span class="imHeading"><? templates/sv/textdoc/add_doc.html/2 ?></span></td>
                    <td width="40" align="right">
                    <td align="right"><input type="button" value="<? global/help ?>" title="<? global/openthehelppage ?>" class="imcmsFormBtnSmall" onClick="openHelpW('LinkAdmin')"></td>
                </tr>
                </table></td>
            </tr>
            <tr>
                <td>
                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td style="width:4.8em;"><span class="imText"><? global/create ?></span></td>
                    <td>
                    <select name="edit_menu" class="imForm">
                        <%  
                            TextDocumentPermissionSetDomainObject permissionSet = (TextDocumentPermissionSetDomainObject)user.getPermissionSetFor( document );
                            final Set allowedDocumentTypeIds = permissionSet.getAllowedDocumentTypeIds();
                            final List docTypeIdsOrder = new ArrayList(Arrays.asList(new IdLocalizedNamePair[] {
                                    DocumentTypeDomainObject.TEXT,
                                    new IdLocalizedNamePair( 0, new LocalizedMessage( "templates/sv/textdoc/existing_doc_name.html/1" ) ),
                                    DocumentTypeDomainObject.URL,
                                    DocumentTypeDomainObject.FILE,
                                    DocumentTypeDomainObject.BROWSER,
                                    DocumentTypeDomainObject.HTML,
                            }));
                            CollectionUtils.filter(docTypeIdsOrder, new Predicate() {
                                public boolean evaluate(Object object) {
                                    IdLocalizedNamePair pair = (IdLocalizedNamePair) object ;
                                    return 0 == pair.getId() || allowedDocumentTypeIds.contains(pair.getId()) ;
                                }
                            });
                            HtmlBuilder html = new HtmlBuilder(); %>
                           <%= html.options(docTypeIdsOrder, new IdLocalizedNamePairToOptionTransformer(user.getLanguageIso639_2())) %>
                    </select></td>
                    <td align="right"><input type="SUBMIT" value=" OK " name="submit" class="imcmsFormBtnSmall" style="width:3em;" title="<? templates/sv/textdoc/add_doc.html/2 ?>"></td>
                </tr>
                </table></td>
            </tr>
            <tr>
                <td><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/1x1_cccccc.gif" width="100%" height="1" alt=""></td>
            </tr>
            <tr>
                <td>
                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td style="width:4.8em;"><span class="imText"><? templates/sv/textdoc/sort_order.html/1 ?></span></td>
                    <td>
                    <select name="sort_order" class="imForm">
                        <%= html.options(Arrays.asList(new IdLocalizedNamePair[] {
                                new IdLocalizedNamePair(MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE, new LocalizedMessage("templates/sv/textdoc/sort_order.html/2")),
                                new IdLocalizedNamePair(MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED, new LocalizedMessage("templates/sv/textdoc/sort_order.html/3")),
                                new IdLocalizedNamePair(MenuDomainObject.MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED, new LocalizedMessage("templates/sv/textdoc/sort_order.html/4")),
                                new IdLocalizedNamePair(MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER, new LocalizedMessage("templates/sv/textdoc/sort_order.html/5")),
                        }), new IdLocalizedNamePairToOptionTransformer(user.getLanguageIso639_2()),
                            new Predicate() {
                                public boolean evaluate(Object object) {
                                    IdLocalizedNamePair pair = (IdLocalizedNamePair) object ;
                                    return pair.getId() == menu.getSortOrder() ;
                                }
                            }) %>
                    </select></td>
                    <td align="right"><input type="submit" NAME="sort" value="<? templates/sv/textdoc/sort_order.html/1 ?>" class="imcmsFormBtnSmall" style="padding-left:3px; padding-right:3px;" title="<? templates/sv/textdoc/sort_order.html/6 ?>"></td>
                </tr>
                </table></td>
            </tr>
        </table>
    </div>
    
    <%= content.toString() %>
    
    <div class="imContent">
        <table border="0" cellspacing="0" cellpadding="2" bgcolor="#f5f5f7" style="width:25em; border: 1px solid #e1ded9">
            <tr>
                <td>
                <input type="submit" value="<? templates/sv/textdoc/archive_del_button.html/2001 ?>" name="copy"
                       class="imcmsFormBtnSmall" width="59" style="width:7.8em;"
                       title="<? templates/sv/textdoc/archive_del_button.html/title_copy ?>"></td>
                <td>
                <input type="submit" value="<? templates/sv/textdoc/archive_del_button.html/2002 ?>" name="archive"
                       class="imcmsFormBtnSmall" width="59" style="width:7.8em;"
                       title="<? templates/sv/textdoc/archive_del_button.html/title_archive ?>"></td>
                <td>
                <input type="submit" value="<? templates/sv/textdoc/archive_del_button.html/2003 ?>" name="delete"
                       class="imcmsFormBtnSmall" style="width:7.8em;"
                       title="<? templates/sv/textdoc/archive_del_button.html/title_remove ?>"></td>
            </tr>
            <tr>
                <td colspan="3" bgcolor="#20568d" class="imMenuBtm">
                <img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/1x1.gif" width="1" height="1" alt=""></td>
            </tr>
        </table>
    </div>
</form>

<% } else if (menuMode) { %>
<a href="<%= request.getContextPath() %>/servlet/ChangeMenu?documentId=<%= document.getId() %>&menuIndex=<%= menuIndex %>" class="imcms_label"
        ><%= label %> [<%= defaultUserMenuItems.length %>/<%= menuItemsUserCanSee.length %>]&nbsp;<%-- 
    --%><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/red.gif" border="0" alt="edit menu <%= menuIndex%>" align="bottom"></a>
<%= content.toString() %>
    <a href="<%= request.getContextPath() %>/servlet/ChangeMenu?documentId=<%= document.getId() %>&menuIndex=<%= menuIndex %>"
            ><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/ico_txt.gif" border="0" alt="edit menu <%= menuIndex%>"></a>
<% } else { %>
<%= content.toString() %>
<% } %>
