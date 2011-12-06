<%@ page
	
	import="com.imcode.imcms.api.*"
	
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	
%>
<%
String cp = request.getContextPath();
ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest(request) ;
TextDocumentViewing viewing = TextDocumentViewing.fromRequest(request) ;
TextDocument doc = viewing.getTextDocument() ;
DocumentPermissionSet permSet = doc.getDocumentPermissionSetForUser() ;
User currentUser = imcmsSystem.getCurrentUser() ;

boolean hasRightToEditMenus = permSet.getEditMenusPermission() ;
boolean isSuperAdmin        = currentUser.isSuperAdmin() ;
int archiveDocId = doc.getId();
boolean showPanel = false ;


if (hasRightToEditMenus) {
	showPanel = true ;
} else if (isSuperAdmin) {
	showPanel = true ;
}

if (showPanel) { %>
<div id="adminLinksDiv">
	<form action="">
	<table border="0" cellspacing="0" cellpadding="5" class="adminLinksTable">
	<tr>
		<td class="imcmsAdmBgHead" style="vertical-align:middle;" nowrap>
		<span style="font: bold 11px Verdana,Geneva,sans-serif; color:#ddddff; letter-spacing:-1px;">imCMS</span></td>
		<td class="imcmsAdmBgCont" style="vertical-align:middle;" nowrap>
		<span style="font: bold 11px Verdana,Geneva,sans-serif; color:#000000;">&nbsp;Admin:</span></td>
		<td class="imcmsAdmBgCont" style="vertical-align:middle;" nowrap><%
		if (hasRightToEditMenus) { %>
		<button onclick="document.location='<%= cp %>/servlet/ChangeMenu?documentId=<%= archiveDocId %>&amp;menuIndex=<%= 1 %>'; return false"<%
			%> class="imcmsFormBtnPanel">Vänstermenyn</button>&nbsp;<%
		} %></td>
	</tr>
	</table>
	</form>
</div><%
} %>