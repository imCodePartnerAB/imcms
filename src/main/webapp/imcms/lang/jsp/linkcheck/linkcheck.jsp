<%@ page import="com.imcode.imcms.mapping.DefaultDocumentMapper,
                 com.imcode.imcms.servlet.admin.AdminDoc,
                 com.imcode.imcms.servlet.superadmin.LinkCheck,
                 imcode.server.ImcmsConstants,
                 imcode.server.document.DocumentDomainObject,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility,
                 org.apache.commons.text.StringEscapeUtils,
                 java.util.Iterator" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%

LinkCheck.LinkCheckPage linkCheckPage = (LinkCheck.LinkCheckPage) request.getAttribute(LinkCheck.LinkCheckPage.REQUEST_ATTRIBUTE__PAGE) ;
boolean doCheckLinks = linkCheckPage.isDoCheckLinks();
    String language = Utility.getLoggedOnUser(request).getLanguage();

%>

<c:set var="heading">
    <fmt:message key="webapp/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading"/>
</c:set>
<ui:imcms_gui_start_of_page titleAndHeading="${heading}"/>

<form method="GET" action="LinkCheck">
<table border="0" cellspacing="0" cellpadding="2" width="100%">
<tr>
    <td>
        <c:set var="heading">
            <fmt:message key="webapp/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading"/>
        </c:set>
        <ui:imcms_gui_heading heading="${heading}"/>
    </td>
</tr>
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="120"><? webapp/imcms/lang/jsp/linkcheck/linkcheck.jsp/only_broken ?></td>
		<td><input type="checkbox" name="<%= LinkCheck.REQUEST_PARAMETER__BROKEN_ONLY %>"  value="0"  <%= linkCheckPage.isBrokenOnly() ? "checked" : "" %> ></td>
	</tr>
	<tr>
		<td><? webapp/imcms/lang/jsp/linkcheck/linkcheck.jsp/start_id ?></td>
		<td><input type="text" name="<%= LinkCheck.REQUEST_PARAMETER__START_ID %>"  size="5" value="<%= linkCheckPage.getStartId() %>"></td>
	</tr>
	<tr>
		<td><? webapp/imcms/lang/jsp/linkcheck/linkcheck.jsp/end_id ?></td>
		<td><input type="text" name="<%= LinkCheck.REQUEST_PARAMETER__END_ID %>" size="5" value="<%= linkCheckPage.getEndId() %>"></td>
	</tr>
    </table>
        <img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="396" height="1"></td>
</tr>
<tr>
    <td><ui:imcms_gui_hr wantedcolor="blue"/></td>
</tr>
<tr>
	<td align="right">
	<input type="submit" name="<%= LinkCheck.REQUEST_PARAMETER__START_BUTTON %>" value="<? webapp/imcms/lang/jsp/linkcheck/linkcheck.jsp/start_check ?>" class="imcmsFormBtn"></td>
</tr>
</table>
</form>
<%

if (doCheckLinks) {
	UserDomainObject user = Utility.getLoggedOnUser( request ) ;
    Iterator linksIterator = linkCheckPage.getLinksIterator();
	while ( linksIterator.hasNext() ) { %>
<table border="0" cellspacing="2" cellpadding="2" width="100%">
<tr>
    <td colspan="9"><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="15"></td>
</tr>
<tr>
    <td><b><? global/Page_alias ?>&nbsp;</b></td>
    <td style="width: 50px"><b><? webapp/imcms/lang/jsp/heading_status ?></b></td>
	<td style="width: 70px"><b><? webapp/imcms/lang/jsp/heading_type ?></b></td>
	<td><b><? webapp/imcms/lang/jsp/heading_adminlink ?></b></td>
	<td><b><? webapp/imcms/lang/jsp/heading_references ?></b></td>
	<td><b><? webapp/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_url ?></b></td>
	<td align="center" style="width: 5em;"><b><? webapp/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_host_found ?></b></td>
	<td align="center" style="width: 5em;"><b><? webapp/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_host_reachable ?></b></td>
	<td align="center" style="width: 5em;"><b><? webapp/imcms/lang/jsp/linkcheck/linkcheck.jsp/heading_ok ?></b></td>
</tr>
<tr>
    <td colspan="9"><ui:imcms_gui_hr wantedcolor="cccccc"/></td>
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
    <td><a name="alias" href="<%= request.getContextPath() + "/" + document.getAlias() %>"><%= StringEscapeUtils.escapeHtml4(document.getAlias()) %></a></td>
    <% }else { %>
    <td>&nbsp;</td> <%}%>
    <td nowrap><ui:statusIcon lifeCyclePhase="<%=document.getLifeCyclePhase()%>"/></td>
    <%
			if (link instanceof LinkCheck.UrlDocumentLink) {
				LinkCheck.UrlDocumentLink urlDocumentLink = (LinkCheck.UrlDocumentLink)link ;
                DefaultDocumentMapper.TextDocumentMenuIndexPair[] documentMenuPairsContainingUrlDocument = urlDocumentLink.getDocumentMenuPairsContainingUrlDocument(); %>
	<td nowrap><? webapp/imcms/lang/jsp/linkcheck/linkcheck.jsp/url_document ?></td>
	<td nowrap><a href="<%= request.getContextPath() %>/servlet/AdminDoc?meta_id=<%=
				document.getId() %>&<%=
				AdminDoc.PARAMETER__DISPATCH_FLAGS%>=<%=
				ImcmsConstants.DISPATCH_FLAG__EDIT_URL_DOCUMENT %>"><%=
				document.getId() %> - <%= StringEscapeUtils.escapeHtml4( document.getHeadline() ) %></a></td>
	<td nowrap><%
				if (documentMenuPairsContainingUrlDocument.length > 0) {
    %><a href="<%= request.getContextPath() %>/servlet/DocumentReferences?id=<%=
					document.getId() %>&returnurl=LinkCheck"><%
				}
				%><%= documentMenuPairsContainingUrlDocument.length %> <? webapp/imcms/lang/jsp/parent_count_unit ?><%
				if (documentMenuPairsContainingUrlDocument.length > 0) {
					%></a><%
				} %></td><%
			} else {
				LinkCheck.TextDocumentElementLink textDocumentElementLink = (LinkCheck.TextDocumentElementLink)link ; %>
	<td><%
				if (link instanceof LinkCheck.TextLink) { %>
	<? webapp/imcms/lang/jsp/linkcheck/linkcheck.jsp/text ?><%
				} else { %>
	<? webapp/imcms/lang/jsp/linkcheck/linkcheck.jsp/image ?><%
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
				%><%= document.getId() %> - <%= textDocumentElementLink.getIndex() %> - <%= StringEscapeUtils.escapeHtml4( document.getHeadline() ) %></a></td>
	<td>&nbsp;</td><%
			} %>
	<td><a href="<%= StringEscapeUtils.escapeHtml4( link.fixSchemeLessUrl() ) %>"><%= StringEscapeUtils.escapeHtml4( link.getUrl() ) %></a></td>
    <% if (link.isCheckable()) { %>
	<td align="center"><img src="<%= request.getContextPath() %>/imcms/<%= language %>/images/admin/<%
			%>btn_checked_<%= (link.isHostFound()) ? "1" : "0" %>.gif"></td>
	<td align="center"><img src="<%= request.getContextPath() %>/imcms/<%= language %>/images/admin/<%
			%>btn_checked_<%= (link.isHostReachable()) ? "1" : "0" %>.gif"></td>
	<td align="center"><img src="<%= request.getContextPath() %>/imcms/<%= language %>/images/admin/<%
			%>btn_checked_<%= (link.isOk() ) ? "1" : "0" %>.gif"></td>
    <% } else { %>
    <td align="center" colspan="3"><? webapp/imcms/lang/jsp/linkcheck/linkcheck.jsp/uncheckable ?></td>
    <% } %>
</tr><%
			response.flushBuffer();
		} %>
</table><%
	}
} %>
    <ui:imcms_gui_end_of_page/>
