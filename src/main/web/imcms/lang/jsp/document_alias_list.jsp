<%@ page import="com.imcode.imcms.api.ContentManagementSystem,
                 com.imcode.imcms.api.UserService,
                 com.imcode.imcms.mapping.DocumentMapper,
                 com.imcode.imcms.servlet.admin.ListDocumentAliasPage,
                 imcode.server.Imcms,
                 imcode.server.document.DocumentDomainObject,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Html"%>
<%@ page import="imcode.util.Utility"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.imcode.imcms.flow.OkCancelPage"%>
<%@page contentType="text/html; charset=UTF-8"%><%@taglib prefix="vel" uri="imcmsvelocity"%>
<%  ListDocumentAliasPage listDocumentAliasPage = (ListDocumentAliasPage) ListDocumentAliasPage.fromRequest(request) ;
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    UserService userService = imcmsSystem.getUserService();
    DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
	UserDomainObject user = Utility.getLoggedOnUser(request);
	Map documentTypes = documentMapper.getAllDocumentTypeIdsAndNamesInUsersLanguage(user) ;%>
<vel:velocity>
<html>
<head>

<title><? imcms/lang/jsp/document_list.jsp/title ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF">

#gui_outer_start()
#gui_head( "<? imcms/lang/jsp/document_list.jsp/title ?>" )

<form method="POST" action="<%= request.getContextPath() %>/servlet/PageDispatcher">
<table border="0" cellspacing="0" cellpadding="0">
<%= ListDocumentAliasPage.htmlHidden( request ) %>
<tr>
	<td><input type="submit" class="imcmsFormBtn" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>" value="<? global/cancel ?>"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? global/help ?>" class="imcmsFormBtn" onClick="openHelpW('')"></td>
</tr>
</table>

#gui_mid()

<table border="0" cellspacing="0" cellpadding="2" width="680">
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
    <tr>
		<td><? imcms/lang/jsp/document_alias_list.jsp/title ?></td>
		<td>&nbsp;&nbsp;</td>
		<td><input type="text" name="<%= ListDocumentAliasPage.REQUEST_PARAMETER__LIST_START %>" value="<%= listDocumentAliasPage.startString %>" size="1" maxlength="1"></td>
		<td>&nbsp;&nbsp;</td>
		<td><? imcms/lang/jsp/document_list.jsp/1004 ?></td>
		<td>&nbsp;&nbsp;</td>
		<td><input type="text" name="<%= ListDocumentAliasPage.REQUEST_PARAMETER__LIST_END %>" value="<%= listDocumentAliasPage.endString %>" size="1" maxlength="1"></td>
		<td>&nbsp;&nbsp;</td>
		<td><input type="submit" class="imcmsFormBtnSmall" name="<%= ListDocumentAliasPage.REQUEST_PARAMETER_BUTTON__LIST %>" value=" <? imcms/lang/jsp/document_list.jsp/2002 ?> "></td>
    </tr>
	</table></td>
</tr>
<tr>
	<td>#gui_hr( "blue" )</td>
</tr>
</table><%

if (null != listDocumentAliasPage.aliasInSelectedRange) { %>

<table border="0" cellspacing="0" cellpadding="2" width="680">
<tr>
    <td><b><? global/Page_alias ?>&nbsp;</b></td>
    <td><b><? web/imcms/lang/jsp/heading_status ?>&nbsp;</b></td>
	<td><b><? web/imcms/lang/jsp/heading_type ?></b></td>
	<td><b><? web/imcms/lang/jsp/heading_adminlink ?></b></td>
	<td><b><? global/Created_by ?></b></td>
    <td><b><? global/Publisher ?></b></td>
</tr><%
    Iterator aliasIter = listDocumentAliasPage.aliasInSelectedRange.iterator();
	while ( aliasIter.hasNext() ) {
		DocumentDomainObject document = documentMapper.getDocument( aliasIter.next().toString() ); %>
<tr>
	<td colspan="6"><img src="$contextPath/imcms/$language/images/admin/1x1_cccccc.gif" width="100%" height="1"></td>
</tr>
<tr valign="top">
    <td><a name="alias" href="$contextPath/<%= document.getAlias() %>"><%= StringEscapeUtils.escapeHtml(document.getAlias()) %></a></td>
	<td align="center"><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="2"><br>
    <%= Html.getLinkedStatusIconTemplate( document, user, request ) %></td>
	<td nowrap><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="2"><br>
	<%= StringEscapeUtils.escapeHtml((String)documentTypes.get(new Integer( document.getDocumentTypeId() )))%>&nbsp;</td>
	<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="2"><br>
	<a name="<%= document.getId() %>" href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>"><%=
		document.getId() %> - <%= StringEscapeUtils.escapeHtml( document.getHeadline() ) %></a></td>
	<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="2"><br>
	<%= StringEscapeUtils.escapeHtml(userService.getUser(document.getCreatorId()).getFirstName()) + " " + StringEscapeUtils.escapeHtml(userService.getUser(document.getCreatorId()).getLastName()) %></td>
	<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="2"><br>
	<%= document.getPublisherId() != null ? StringEscapeUtils.escapeHtml(userService.getUser(document.getPublisherId()).getFirstName()) + " " + StringEscapeUtils.escapeHtml(userService.getUser(document.getPublisherId()).getLastName()) : "" %></td>
</tr><%
	} %>
<tr>
	<td colspan="6">#gui_hr( "blue" )</td>
</tr>
<tr>
	<td colspan="6" align="right"><input type="submit" class="imcmsFormBtn" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>" value="<? global/cancel ?>"></td>
</tr>
</table>
<%}%>
</form>

#gui_bottom()
#gui_outer_end()

</body>
</html>
</vel:velocity>
