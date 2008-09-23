<%@ page import="com.imcode.imcms.mapping.DocumentMapper,
                 com.imcode.imcms.servlet.admin.AdminDoc,
                 com.imcode.imcms.servlet.superadmin.DocumentReferences,
                 com.imcode.imcms.servlet.superadmin.LinkCheck,
                 imcode.server.ImcmsConstants,
                 imcode.server.document.DocumentDomainObject,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Html,
                 imcode.util.Utility,
                 org.apache.commons.lang.StringEscapeUtils"%><%@ page import="java.util.Iterator"%>
<%@page contentType="text/html; charset=UTF-8"%><%@taglib prefix="vel" uri="imcmsvelocity"%><%

LinkCheck.LinkCheckPage linkCheckPage = (LinkCheck.LinkCheckPage) request.getAttribute(LinkCheck.LinkCheckPage.REQUEST_ATTRIBUTE__PAGE) ;
boolean doCheckLinks = linkCheckPage.isDoCheckLinks();
String language = Utility.getLoggedOnUser( request ).getLanguageIso639_2() ;

%>
<vel:velocity>
#gui_start_of_page( "<? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading ?>" "AdminManager" "" "LinksValidate" "" )

<form method="GET" action="LinkCheck">
<table border="0" cellspacing="0" cellpadding="2" width="100%">
<tr>
	<td>#gui_heading( "<? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading ?>" )</td>
</tr>
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="120"><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/only_broken ?></td>
		<td><input type="checkbox" name="<%= LinkCheck.REQUEST_PARAMETER__BROKEN_ONLY %>"  value="0"  <%= linkCheckPage.isBrokenOnly() ? "checked" : "" %> ></td>
	</tr>
	<tr>
		<td><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/start_id ?></td>
		<td><input type="text" name="<%= LinkCheck.REQUEST_PARAMETER__START_ID %>"  size="5" value="<%= linkCheckPage.getStartId() %>"></td>
	</tr>
	<tr>
		<td><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/end_id ?></td>
		<td><input type="text" name="<%= LinkCheck.REQUEST_PARAMETER__END_ID %>" size="5" value="<%= linkCheckPage.getEndId() %>"></td>
	</tr>
	</table><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="396" height="1"></td>
</tr>
<tr>
	<td>#gui_hr( "blue" )</td>
</tr>
<tr>
	<td align="right">
	<input type="submit" name="<%= LinkCheck.REQUEST_PARAMETER__START_BUTTON %>" value="<? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/start_check ?>" class="imcmsFormBtn"></td>
</tr>
</table>
</form>
</vel:velocity><%

if (doCheckLinks) {
	UserDomainObject user = Utility.getLoggedOnUser( request ) ;
	Iterator linksIterator = (Iterator)linkCheckPage.getLinksIterator() ;
	while ( linksIterator.hasNext() ) { %>
<table border="0" cellspacing="2" cellpadding="2" width="100%">
<tr>
	<td colspan="9"><vel:velocity><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="15"></vel:velocity></td>
</tr>
<tr>
    <td><b><? global/Page_alias ?>&nbsp;</b></td>
    <td style="width: 50px"><b><? web/imcms/lang/jsp/heading_status ?></b></td>
	<td style="width: 70px"><b><? web/imcms/lang/jsp/heading_type ?></b></td>
	<td><b><? web/imcms/lang/jsp/heading_adminlink ?></b></td>
	<td><b><? web/imcms/lang/jsp/heading_references ?></b></td>
	<td><b><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_url ?></b></td>
	<td align="center" style="width: 5em;"><b><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_host_found ?></b></td>
	<td align="center" style="width: 5em;"><b><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_host_reachable ?></b></td>
	<td align="center" style="width: 5em;"><b><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_ok ?></b></td>
</tr>
<tr>
	<td colspan="9"><vel:velocity>#gui_hr( "cccccc" )</vel:velocity></td>
</tr><%
		for (int i = 0; linksIterator.hasNext() && i < 10; ++i) {
			response.flushBuffer();
			LinkCheck.Link link = (LinkCheck.Link)linksIterator.next();
			if ( (!link.isCheckable() || link.isOk()) && linkCheckPage.isBrokenOnly() ) {
				--i ;
				continue;
			}
			DocumentDomainObject document = link.getDocument() ; %>
<tr><%String alias = document.getAlias();
    if ( alias != null ) { %>
    <td><a name="alias" href="<%= request.getContextPath() + "/" + document.getAlias() %>"><%= StringEscapeUtils.escapeHtml(document.getAlias()) %></a></td>
    <% }else { %>
    <td>&nbsp;</td> <%}%>
    <td nowrap><%= Html.getLinkedStatusIconTemplate( document, user, request ) %></td><%
			if (link instanceof LinkCheck.UrlDocumentLink) {
				LinkCheck.UrlDocumentLink urlDocumentLink = (LinkCheck.UrlDocumentLink)link ;
				DocumentMapper.TextDocumentMenuIndexPair[] documentMenuPairsContainingUrlDocument = urlDocumentLink.getDocumentMenuPairsContainingUrlDocument(); %>
	<td nowrap><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/url_document ?></td>
	<td nowrap><a href="<%= request.getContextPath() %>/servlet/AdminDoc?meta_id=<%=
				document.getId() %>&<%=
				AdminDoc.PARAMETER__DISPATCH_FLAGS%>=<%=
				ImcmsConstants.DISPATCH_FLAG__EDIT_URL_DOCUMENT %>"><%=
				document.getId() %> - <%= StringEscapeUtils.escapeHtml( document.getHeadline() ) %></a></td>
	<td nowrap><%
				if (documentMenuPairsContainingUrlDocument.length > 0) {
					%><a href="<%= request.getContextPath() %>/servlet/DocumentReferences?<%=
					DocumentReferences.REQUEST_PARAMETER__REFERENCED_DOCUMENT_ID %>=<%=
					document.getId() %>&<%=
					DocumentReferences.REQUEST_PARAMETER__RETURNURL %>=LinkCheck"><%
				}
				%><%= documentMenuPairsContainingUrlDocument.length %> <? web/imcms/lang/jsp/parent_count_unit ?><%
				if (documentMenuPairsContainingUrlDocument.length > 0) {
					%></a><%
				} %></td><%
			} else {
				LinkCheck.TextDocumentElementLink textDocumentElementLink = (LinkCheck.TextDocumentElementLink)link ; %>
	<td><%
				if (link instanceof LinkCheck.TextLink) { %>
	<? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/text ?><%
				} else { %>
	<? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/image ?><%
				} %></td>
	<td><%
				if (link instanceof LinkCheck.TextLink) {
					%><a href="<%=
					request.getContextPath() %>/servlet/ChangeText?meta_id=<%=
					document.getId() %>&txt=<%=
					textDocumentElementLink.getIndex()%>"><%
				} else {
					%><a href="<%=
					request.getContextPath() %>/servlet/ChangeImage?meta_id=<%=
					document.getId() %>&img=<%=
					textDocumentElementLink.getIndex()%>"><%
				}
				%><%= document.getId() %> - <%= textDocumentElementLink.getIndex() %> - <%= StringEscapeUtils.escapeHtml( document.getHeadline() ) %></a></td>
	<td>&nbsp;</td><%
			} %>
	<td><a href="<%= StringEscapeUtils.escapeHtml( link.fixSchemeLessUrl() ) %>"><%= StringEscapeUtils.escapeHtml( link.getUrl() ) %></a></td>
    <% if (link.isCheckable()) { %>
	<td align="center"><img src="<%= request.getContextPath() %>/imcms/<%= language %>/images/admin/<%
			%>btn_checked_<%= (link.isHostFound()) ? "1" : "0" %>.gif"></td>
	<td align="center"><img src="<%= request.getContextPath() %>/imcms/<%= language %>/images/admin/<%
			%>btn_checked_<%= (link.isHostReachable()) ? "1" : "0" %>.gif"></td>
	<td align="center"><img src="<%= request.getContextPath() %>/imcms/<%= language %>/images/admin/<%
			%>btn_checked_<%= (link.isOk() ) ? "1" : "0" %>.gif"></td>
    <% } else { %>
    <td align="center" colspan="3"><? web/imcms/lang/jsp/linkcheck/linkcheck.jsp/uncheckable ?></td>
    <% } %>
</tr><%
			response.flushBuffer();
		} %>
</table><%
	}
} %>
<vel:velocity>
#gui_end_of_page()
</vel:velocity>
