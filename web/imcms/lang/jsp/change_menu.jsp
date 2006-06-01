<%@ page import="com.imcode.imcms.flow.Page, com.imcode.imcms.servlet.admin.MenuEditPage, imcode.server.document.textdocument.MenuDomainObject, imcode.server.document.textdocument.MenuItemDomainObject, imcode.util.Utility, imcode.server.user.UserDomainObject, imcode.server.document.DocumentDomainObject, org.apache.commons.lang.StringEscapeUtils, imcode.server.document.TextDocumentPermissionSetDomainObject, java.util.Set, java.util.List, imcode.server.document.DocumentTypeDomainObject, imcode.util.IdLocalizedNamePair, java.util.ArrayList, java.util.Arrays, imcode.util.LocalizedMessage, org.apache.commons.collections.Predicate, org.apache.commons.collections.CollectionUtils, com.imcode.util.HtmlBuilder, imcode.util.IdLocalizedNamePairToOptionTransformer, imcode.server.document.textdocument.TextDocumentDomainObject, imcode.server.document.textdocument.TreeSortKeyDomainObject, org.apache.commons.lang.StringUtils, imcode.util.Html, com.imcode.imcms.flow.OkCancelPage"%><%@ page contentType="text/html"%><%
    
    MenuEditPage menuEditPage = (MenuEditPage) Page.fromRequest(request);
    final MenuDomainObject menu = menuEditPage.getMenu();
    UserDomainObject user = Utility.getLoggedOnUser(request);
    MenuItemDomainObject[] menuItemsUserCanSee = menu.getMenuItemsUserCanSee(user);
    TextDocumentDomainObject textDocument = menuEditPage.getTextDocument();

%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld" %><vel:velocity><html>
    <head>
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms_admin.css.jsp">
    </head>
    <body>
        <form action="<%= request.getContextPath() %>/servlet/PageDispatcher" method="POST">
            <%= Page.htmlHidden(request) %>

        #gui_outer_start()
        #gui_head( "<? global/imcms_administration ?>" )
		<input type="submit" tabindex="10" value="<? global/back ?>" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>" class="imcmsFormBtn">

        #gui_mid()
        
            <div>
                <? templates/sv/textdoc/add_doc.html/2 ?>
                <select name="<%= MenuEditPage.DOCUMENT_TYPE_ID %>">
                            <%
                                TextDocumentPermissionSetDomainObject permissionSet = (TextDocumentPermissionSetDomainObject)user.getPermissionSetFor( textDocument );
                                final Set allowedDocumentTypeIds = permissionSet.getAllowedDocumentTypeIds();
                                final List docTypeIdsOrder = new ArrayList(Arrays.asList(new IdLocalizedNamePair[] {
                                        DocumentTypeDomainObject.TEXT,
                                        new IdLocalizedNamePair( 0, new LocalizedMessage( "templates/sv/textdoc/existing_doc_name.html/internal_link" ) ),
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
                </select>
                <input type="submit" name="<%= MenuEditPage.CREATE %>" value="<? global/create ?>" class="imcmsFormBtnSmall">
            </div>
            #gui_hr("cccccc")
            <div>
                <select name="<%= MenuEditPage.SORT_ORDER %>">
                    <%= html.options(Arrays.asList(new IdLocalizedNamePair[] {
                            new IdLocalizedNamePair(MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE, new LocalizedMessage("templates/sv/textdoc/sort_order.html/by_headline")),
                            new IdLocalizedNamePair(MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED, new LocalizedMessage("templates/sv/textdoc/sort_order.html/by_manual_key")),
                            new IdLocalizedNamePair(MenuDomainObject.MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED, new LocalizedMessage("templates/sv/textdoc/sort_order.html/by_modified_datetime")),
                            new IdLocalizedNamePair(MenuDomainObject.MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED, new LocalizedMessage("templates/sv/textdoc/sort_order.html/by_published_datetime")),
                            new IdLocalizedNamePair(MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER, new LocalizedMessage("templates/sv/textdoc/sort_order.html/by_manual_tree_key")),
                    }), new IdLocalizedNamePairToOptionTransformer(user.getLanguageIso639_2()),
                        new Predicate() {
                            public boolean evaluate(Object object) {
                                IdLocalizedNamePair pair = (IdLocalizedNamePair) object ;
                                return pair.getId() == menu.getSortOrder() ;
                            }
                        }) %>
                </select>
                <input type="submit" name="<%= MenuEditPage.SORT %>" value="<? templates/sv/textdoc/archive_del_button.html/1 ?>" class="imcmsFormBtnSmall">
            </div>
            #gui_hr("cccccc")
            <div>
            <%
                for ( MenuItemDomainObject menuItem : menuItemsUserCanSee ) {
                    DocumentDomainObject menuItemDocument = menuItem.getDocument();
                    String headline = menuItemDocument.getHeadline();
                    if ( StringUtils.isBlank(headline) ) {
                        headline = "_" ;
                    }
                    %>
                    <div>
                        <%
                            if (menu.getSortOrder() == MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER || menu.getSortOrder() == MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED ) {
                                TreeSortKeyDomainObject treeSortKey = menuItem.getTreeSortKey();
                                String sortKey = treeSortKey.toString() ;
                                if (menu.getSortOrder() == MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED) {
                                    sortKey =  menuItem.getSortKey() == null ? "" : menuItem.getSortKey().toString() ;
                                }
                                %><input type="text" name="<%= MenuEditPage.SORT_KEY %><%= menuItemDocument.getId() %>" size="5" value="<%= StringEscapeUtils.escapeHtml(sortKey) %>"><%
                            }
                        %><%= Html.getLinkedStatusIconTemplate( menuItem.getDocument(), user, request ) %> 
                        <input type="checkbox" name="<%= MenuEditPage.SELECTED %>" value="<%= menuItemDocument.getId() %>">
                        <a href="<%= request.getContextPath() %>/<%= menuItemDocument.getId() %>"><%= StringEscapeUtils.escapeHtml(headline) %></a>
                        <% if (user.canEdit(menuItemDocument)) {
                            %>&nbsp;<a href="<%= request.getContextPath() %>/servlet/AdminDoc?meta_id=<%= menuItemDocument.getId() %>"><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/ico_txt.gif" border="0"></a><%
                        } %>
                    </div>
                    <%
                }
            %>
            </div>
            #gui_hr("cccccc")
            <div>
                <input type="submit" name="<%= MenuEditPage.COPY %>" value="<? templates/sv/textdoc/archive_del_button.html/2001 ?>" class="imcmsFormBtnSmall">
                <input type="submit" name="<%= MenuEditPage.ARCHIVE %>" value="<? templates/sv/textdoc/archive_del_button.html/2002 ?>" class="imcmsFormBtnSmall">
                <input type="submit" name="<%= MenuEditPage.REMOVE %>" value="<? templates/sv/textdoc/archive_del_button.html/2003 ?>" class="imcmsFormBtnSmall">
            </div>
            #gui_hr("cccccc")
            <div style="text-align: right;">
                <input type="submit" name="<%= OkCancelPage.REQUEST_PARAMETER__OK %>" value="<? global/OK ?>" class="imcmsFormBtn">
                <input type="submit" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>" value="<? global/cancel ?>" class="imcmsFormBtn">
            </div>

        #gui_bottom()
        #gui_outer_end()

        </form>

    </body>
</html></vel:velocity>
