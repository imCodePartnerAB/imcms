<%@ page import="com.imcode.imcms.servlet.superadmin.ListDocuments,
                 org.apache.commons.lang.math.IntRange,
                 imcode.server.document.DocumentDomainObject,
                 com.imcode.imcms.servlet.superadmin.DocumentReferences,
                 org.apache.commons.lang.StringEscapeUtils,
                 imcode.server.Imcms,
                 imcode.server.document.DocumentMapper,
                 imcode.util.Utility,
                 imcode.server.user.UserDomainObject,
                 imcode.server.document.textdocument.TextDocumentDomainObject,
                 imcode.server.document.textdocument.MenuDomainObject,
                 java.util.*,
                 java.net.URLEncoder,
                 org.apache.commons.lang.ObjectUtils,
                 imcode.util.Html"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<% ListDocuments.FormData formData = (ListDocuments.FormData)request.getAttribute( ListDocuments.REQUEST_ATTRIBUTE__FORM_DATA ) ;%>
<vel:velocity>

#gui_start_of_page( "<? imcms/lang/jsp/document_list.jsp/title ?>" "AdminManager" "" "37" "" )

<table border="0" cellspacing="0" cellpadding="2" width="680">
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<form method="GET" action="ListDocuments">
	<tr>
		<td><? imcms/lang/jsp/document_list.jsp/1003 ?></td>
		<td>&nbsp;</td>
		<td><input type="text" name="<%= ListDocuments.PARAMETER__LIST_START %>" value="<%= formData.selectedRange.getMinimumInteger() %>" size="6"></td>
		<td>&nbsp;</td>
		<td><? imcms/lang/jsp/document_list.jsp/1004 ?></td>
		<td>&nbsp;</td>
		<td><input type="text" name="<%= ListDocuments.PARAMETER__LIST_END %>" value="<%= formData.selectedRange.getMaximumInteger() %>" size="6"></td>
		<td>&nbsp;</td>
		<td><input type="submit" class="imcmsFormBtnSmall" name="<%= ListDocuments.PARAMETER_BUTTON__LIST %>" value=" <? imcms/lang/jsp/document_list.jsp/2002 ?> "></td>
	</tr>
	</form>
	</table></td>
</tr>
</table><%

if (null != formData.documentsIterator) { %>

<table border="0" cellspacing="0" cellpadding="2" width="680">
<tr>
	<td><b><? web/imcms/lang/jsp/heading_type ?></b></td>
	<td><b><? web/imcms/lang/jsp/heading_adminlink ?></b></td>
	<td><b><? web/imcms/lang/jsp/heading_references ?></b></td>
	<td><b><? web/imcms/lang/jsp/heading_status ?></b></td>
	<td>&nbsp; <b><? imcms/lang/jsp/document_list.jsp/heading_child_documents ?></b></td>
</tr><%

	DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
	UserDomainObject user = Utility.getLoggedOnUser(request);
	Map documentTypes = documentMapper.getAllDocumentTypeIdsAndNamesInUsersLanguage(user) ;

	while ( formData.documentsIterator.hasNext() ) {
		DocumentDomainObject document = (DocumentDomainObject)formData.documentsIterator.next();
		DocumentMapper.TextDocumentMenuIndexPair[] documentMenuPairsContainingDocument = documentMapper.getDocumentMenuPairsContainingDocument( document ); %>
<tr>
	<td colspan="5"><img src="$contextPath/imcms/$language/images/admin/1x1_cccccc.gif" width="100%" height="1"></td>
</tr>
<tr valign="top">
	<td nowrap><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="2"><br>
	<%= StringEscapeUtils.escapeHtml((String)documentTypes.get(new Integer( document.getDocumentTypeId() )))%>&nbsp;</td>
	<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="2"><br>
	<a name="<%= document.getId() %>" href="AdminDoc?meta_id=<%= document.getId() %>"><%=
		document.getId() %> - <%= StringEscapeUtils.escapeHtml( document.getHeadline() ) %></a></td>
	<td nowrap><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="2"><br><%
		if (documentMenuPairsContainingDocument.length > 0 ) {
			String backUrl = "ListDocuments?" + ObjectUtils.defaultIfNull(request.getQueryString(),"") ;
			String escapedBackUrl = URLEncoder.encode(backUrl); %>
	<a href="<%= request.getContextPath() %>/servlet/DocumentReferences?<%= DocumentReferences.REQUEST_PARAMETER__RETURNURL %>=<%= escapedBackUrl %>&<%= DocumentReferences.REQUEST_PARAMETER__REFERENCED_DOCUMENT_ID %>=<%= document.getId() %>"><%
		} %><%= documentMenuPairsContainingDocument.length %> <? web/imcms/lang/jsp/parent_count_unit ?><%
		if (documentMenuPairsContainingDocument.length > 0 ) {
			%></a><%
		} %></td>
	<td align="center"><%= Html.getLinkedStatusIconTemplate( document, user ) %></td>
	<td><%
		if (document instanceof TextDocumentDomainObject) {
			TextDocumentDomainObject textDocument = (TextDocumentDomainObject)document ;
			List childDocuments = new ArrayList(textDocument.getChildDocuments());
			if (!childDocuments.isEmpty()) { %>
	<table border="0" cellpadding="2" cellspacing="0"><%
				Collections.sort(childDocuments, DocumentDomainObject.DocumentComparator.ID) ;
				for ( Iterator iterator = childDocuments.iterator(); iterator.hasNext(); ) {
					DocumentDomainObject childDocument = (DocumentDomainObject)iterator.next(); %>
  <tr valign="top">
		<td>&nbsp;<b>&#149;</b>&nbsp;</td>
		<td><a href="<%="ListDocuments?"+ListDocuments.PARAMETER__LIST_START + "=" + childDocument.getId() + "&" + ListDocuments.PARAMETER__LIST_END +"=" + childDocument.getId()%>"><%=
					childDocument.getId() %> - <%=
					StringEscapeUtils.escapeHtml(childDocument.getHeadline()) %></a></td>
	</tr><%
				} %>
	</table><%
			}
		} %></td>
</tr><%
	} %>
</table><%
} %>
#gui_end_of_page()
</vel:velocity>
