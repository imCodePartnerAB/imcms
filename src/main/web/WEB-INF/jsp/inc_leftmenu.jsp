<%@ taglib prefix="imcms" uri="imcms" %>
<%@ page

	import="com.imcode.imcms.addon.imagearchive.util.treemenu.ImcmsTreeMenu,
	        com.imcode.imcms.addon.imagearchive.util.treemenu.TreeMenuItem,
	        com.imcode.imcms.api.*"

	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"

%>
<%@ page import="java.util.List" %>
<%
String cp = request.getContextPath();
TextDocumentViewing view = TextDocumentViewing.fromRequest(request) ;
TextDocument thisDoc = view.getTextDocument();

/* *******************************************************************************************
 *         Get TreeMenu                                                                      *
 ******************************************************************************************* */

TextDocument.MenuItem[] leftMenuItems = null ;
try {
	leftMenuItems = thisDoc.getMenu(1).getVisibleMenuItems() ;
} catch (Exception ex) {}


ImcmsTreeMenu imcmsTreeMenu = new ImcmsTreeMenu(thisDoc, leftMenuItems) ;
List<TreeMenuItem> visibleTreeMenuItems = imcmsTreeMenu.getVisibleTreeMenuItems() ;

%>
<imcms:text no="999" label="<br/>Archive start page<br/>" mode="write" formats="text" rows="1"/>
    <a href="<%= cp %>/<%=thisDoc.getId()%>?toArchiveSearchPage" class="leftMenuHeadingBg">
        <span>
            <imcms:text no="999" label="<br/>Archive start page<br/>" mode="read" formats="text" rows="1"/>
        </span>
    </a>
<%
    if (null != visibleTreeMenuItems) {
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
        }
    }
%>