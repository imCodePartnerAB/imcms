<%@ page
	
	import="com.imcode.imcms.flow.OkCancelPage,
	        com.imcode.imcms.flow.Page,
	        com.imcode.imcms.servlet.admin.MenuEditPage,
	        com.imcode.imcms.util.l10n.LocalizedMessage,
	        com.imcode.util.HtmlBuilder,
	        imcode.server.document.DocumentDomainObject,
	        imcode.server.document.DocumentTypeDomainObject,
	        imcode.server.document.TextDocumentPermissionSetDomainObject,
	        imcode.server.document.textdocument.MenuDomainObject,
	        imcode.server.document.textdocument.MenuItemDomainObject,
	        imcode.server.document.textdocument.TextDocumentDomainObject,
	        imcode.server.document.textdocument.TreeSortKeyDomainObject,
	        imcode.server.user.UserDomainObject,
	        imcode.util.Html,
	        imcode.util.IdLocalizedNamePair,
	        imcode.util.IdLocalizedNamePairToOptionTransformer,
	        imcode.util.Utility,
	        org.apache.commons.collections.CollectionUtils,
	        org.apache.commons.collections.Predicate,
	        org.apache.commons.lang.StringEscapeUtils,
	        org.apache.commons.lang.StringUtils,
	        java.util.ArrayList,
	        java.util.Arrays,
	        java.util.List,
	        java.util.Set,
	        com.imcode.imcms.servlet.AjaxServlet"
	
	contentType="text/html; charset=UTF-8"
	
%><%@ taglib prefix="vel" uri="imcmsvelocity"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%
    
MenuEditPage menuEditPage = (MenuEditPage) Page.fromRequest(request);
final MenuDomainObject menu = menuEditPage.getMenu();
UserDomainObject user = Utility.getLoggedOnUser(request);
MenuItemDomainObject[] menuItemsUserCanSee = menu.getMenuItemsUserCanSee(user);
TextDocumentDomainObject textDocument = menuEditPage.getTextDocument();

String cp = request.getContextPath() ;

%><vel:velocity><html>
<head>

<link rel="stylesheet" type="text/css" href="<%= cp %>/imcms/css/imcms_admin.css.jsp">
<script type="text/javascript" src="<%= cp %>/imcms/$language/scripts/imcms_admin.js.jsp"></script>

</head>
<body>


<form action="<%= cp %>/servlet/PageDispatcher" method="POST">
<%= Page.htmlHidden(request) %>

#gui_outer_start()
#gui_head( "<fmt:message key="global/imcms_administration" />" )
<input type="submit" tabindex="10" value="<fmt:message key="global/back" />" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>" class="imcmsFormBtn">
#gui_mid()
		
	<div>
		<fmt:message key="templates/sv/textdoc/add_doc.html/2" />
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
		<input type="submit" name="<%= MenuEditPage.CREATE %>" value="<fmt:message key="global/create" />" class="imcmsFormBtnSmall">
	</div>
#gui_hr("cccccc")
	<div>
		<select name="<%= MenuEditPage.SORT_ORDER %>">
			<%= html.options(Arrays.asList(new IdLocalizedNamePair[] {
					new IdLocalizedNamePair(MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE, new LocalizedMessage("templates/sv/textdoc/sort_order.html/by_headline")),
					new IdLocalizedNamePair(MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED, new LocalizedMessage("templates/sv/textdoc/sort_order.html/by_manual_key")),
					new IdLocalizedNamePair(MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER, new LocalizedMessage("templates/sv/textdoc/sort_order.html/by_manual_tree_key")),
					new IdLocalizedNamePair(MenuDomainObject.MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED, new LocalizedMessage("templates/sv/textdoc/sort_order.html/by_modified_datetime")),
					new IdLocalizedNamePair(MenuDomainObject.MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED, new LocalizedMessage("templates/sv/textdoc/sort_order.html/by_published_datetime")),
			}), new IdLocalizedNamePairToOptionTransformer(user.getLanguageIso639_2()),
				new Predicate() {
					public boolean evaluate(Object object) {
						IdLocalizedNamePair pair = (IdLocalizedNamePair) object ;
						return pair.getId() == menu.getSortOrder() ;
					}
				}) %>
		</select>
		<input type="submit" name="<%= MenuEditPage.SORT %>" value="<fmt:message key="templates/sv/textdoc/archive_del_button.html/1" />" class="imcmsFormBtnSmall">
	</div>
#gui_hr("cccccc")
		<div id="handleHeading" style="display:none;">
			<fmt:message key="templates/sv/textdoc/archive_del_button.html/drag_drop" />
		</div>
		<%= "<div id=\"menuItems\" style=\"width:" + (menu.getSortOrder() == MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER ? 600 : 500) + "px;\">" %><%
			for ( MenuItemDomainObject menuItem : menuItemsUserCanSee ) {
				DocumentDomainObject menuItemDocument = menuItem.getDocument();
				String headline = menuItemDocument.getHeadline();
				if ( StringUtils.isBlank(headline) ) {
					headline = "_" ;
				} %>
			<div class="menuItem" id="menuItem<%= menuItemDocument.getId() %>">
				<table border="0" cellspacing="0" cellpadding="3">
				<tr><%
				if (menu.getSortOrder() == MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER || menu.getSortOrder() == MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED ) {
					TreeSortKeyDomainObject treeSortKey = menuItem.getTreeSortKey();
					String sortKey = treeSortKey.toString() ;
					if (menu.getSortOrder() == MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED) {
						sortKey =  menuItem.getSortKey() == null ? "" : menuItem.getSortKey().toString() ;
					} %>
					<td><input type="text" class="sortKeyField" id="<%=
					MenuEditPage.SORT_KEY %><%= menuItemDocument.getId() %>" name="<%=
					MenuEditPage.SORT_KEY %><%= menuItemDocument.getId() %>" size="<%=
					(menu.getSortOrder() == MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER) ? 25 : 5 %>" value="<%=
					StringEscapeUtils.escapeHtml(sortKey) %>" />
					<div class="handle"></div></td><%
				} %>
					<td><%= Html.getLinkedStatusIconTemplate( menuItem.getDocument(), user, request ) %></td>
					<td><input type="checkbox" name="<%= MenuEditPage.SELECTED %>" value="<%= menuItemDocument.getId() %>" /></td>
					<td<%= (menu.getSortOrder() == MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER) ? " style=\"padding-left:" + ((menuItem.getTreeSortKey().getLevelCount() * 10) - 7) + "px\"" : "" %>>
					<a href="<%= cp %>/<%= menuItemDocument.getName() %>"><%= StringEscapeUtils.escapeHtml(headline) %></a><%
				if (user.canEdit(menuItemDocument)) {
					%>&nbsp;<a href="<%= cp %>/servlet/AdminDoc?meta_id=<%= menuItemDocument.getId() %>"><img src="<%=
					cp %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/ico_txt.gif" border="0" alt="" /></a><%
				} %></td>
				</tr>
				</table>
			</div><%
			} %>
		<%= "<div>" %>
#gui_hr("cccccc")
	<div>
		<input type="submit" name="<%= MenuEditPage.COPY %>" value="<fmt:message key="templates/sv/textdoc/archive_del_button.html/2001" />" class="imcmsFormBtnSmall">
		<input type="submit" name="<%= MenuEditPage.ARCHIVE %>" value="<fmt:message key="templates/sv/textdoc/archive_del_button.html/2002" />" class="imcmsFormBtnSmall">
		<input type="submit" name="<%= MenuEditPage.REMOVE %>" value="<fmt:message key="templates/sv/textdoc/archive_del_button.html/2003" />" class="imcmsFormBtnSmall">
	</div>
#gui_hr("cccccc")
	<div style="text-align: right;">
		<input type="submit" name="<%= OkCancelPage.REQUEST_PARAMETER__OK %>" value="<fmt:message key="global/OK" />" class="imcmsFormBtn">
		<input type="submit" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>" value="<fmt:message key="global/cancel" />" class="imcmsFormBtn">
	</div>
#gui_bottom()
#gui_outer_end()
</form>
</vel:velocity>
<%
if (menu.getSortOrder() == MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED) { %>
<%--
/* *******************************************************************************************
 *         Drag & Drop sorting                                                               *
 ******************************************************************************************* */
--%>
<script type="text/javascript">
jQ(document).ready(function($) {
	$('#menuItems .menuItem .sortKeyField').hide(0, function() {
		$('#menuItems .menuItem .handle').addClass('ui-sortable-handle').show(0) ;
		$('#handleHeading').show(0) ;
	}) ;
	$('#menuItems').sortable(
		{
			items:   '.menuItem',
			handle:  '.handle',
			helper:  'clone',
			axis : 	 'y',
			opacity: 0.5,
			update : function() {
				var $this = $(this) ;
				$this.sortable('option', 'disabled', true) ;
				getSortData($this) ;
			}
		}
	) ;
}) ;
function getSortData($this) {
	var arrNewOrderMetaIds = $this.sortable('toArray').join(',').replace(/menuItem/g, '').replace(/,$/, '').split(',') ;
	if (null != arrNewOrderMetaIds && arrNewOrderMetaIds.length > 0) {
		var sortKey = 500 ;
		for (var i = arrNewOrderMetaIds.length - 1; i >= 0 ; i--) {
			$('#<%= MenuEditPage.SORT_KEY %>' + arrNewOrderMetaIds[i]).val(sortKey) ;
			sortKey += 10 ;
		}
	}
	$this.sortable('option', 'disabled', false) ;
}
</script><%
} %>

</body>
</html>
