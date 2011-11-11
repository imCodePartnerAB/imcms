<%@ page
	
	import="com.imcode.imcms.addon.imagearchive.util.treemenu.ImcmsTreeMenu,
	        com.imcode.imcms.addon.imagearchive.util.treemenu.TreeMenuItem,
	        java.util.List"
	
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	
%>
<%@ page import="com.imcode.imcms.api.*" %>
<%
String cp = request.getContextPath();
ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest(request) ;
DocumentService documentService     = imcmsSystem.getDocumentService() ;

TextDocumentViewing view = TextDocumentViewing.fromRequest(request) ;
String thisDocId = "";
    if(view != null){
        thisDocId = "" + view.getTextDocument().getId();
    }

TextDocument dataDoc = documentService.getTextDocument(1039) ;
TextDocument thisDoc = (null != view) ? view.getTextDocument() : dataDoc ;


/* *******************************************************************************************
 *         Get TreeMenu                                                                      *
 ******************************************************************************************* */

TextDocument.MenuItem[] leftMenuItems = null ;
try {
	leftMenuItems = dataDoc.getMenu(1).getVisibleMenuItems() ;
} catch (Exception ex) {}


ImcmsTreeMenu imcmsTreeMenu = new ImcmsTreeMenu(thisDoc, leftMenuItems) ;
List<TreeMenuItem> visibleTreeMenuItems = imcmsTreeMenu.getVisibleTreeMenuItems() ;

%>
				<a href="<%= cp %>/<%=thisDocId%>" class="leftMenuHeadingBg"><span>Startsida</span></a><%
		if (null != visibleTreeMenuItems) {
			int itemCount = 0 ;
			for (TreeMenuItem treeMenuItem : visibleTreeMenuItems) {
				TextDocument.MenuItem menuItem = treeMenuItem.getMenuItem() ;
				Document doc = menuItem.getDocument() ;
				boolean hasSubLevels        = treeMenuItem.hasSubLevels() ;
				boolean hasVisibleSubLevels = treeMenuItem.hasVisibleSubLevels() ;
				boolean isThisDoc           = treeMenuItem.isThisDoc() ;
				String href = cp + "/" + doc.getName() ;
				if (doc instanceof UrlDocument) {
					UrlDocument urlDoc = (UrlDocument) doc ;
					String urlDocUrl = urlDoc.getUrl() ;
					if (urlDocUrl.startsWith("/")) {
						href = cp + urlDocUrl ;
					}
				}
				TextDocument.MenuItem.TreeKey treeKey = menuItem.getTreeKey() ;
				int level = treeKey.getLevelCount() ; %>
				<a class="lev<%= level %><%
					%><%= hasSubLevels && hasVisibleSubLevels ? "  act_tree_lev" + level : hasSubLevels ? "  inact_tree_lev" + level : "" %><%
					%><%//= itemCount == 0 ? " first" : "" %><%
					%><%= isThisDoc ? " act_page" : "" %>"<%
					%> href="<%= href %>"<%
					%> target="<%= doc.getTarget() %>"><%
					%><span><%= doc.getHeadline().replaceAll("\\s+&\\s+", " &amp; ") %></span><%
				%></a><%
				itemCount++ ;
			}
		} %>