<%@ page import="com.imcode.imcms.servlet.superadmin.ListDocuments,
                 imcode.server.document.DocumentDomainObject,
                 org.apache.commons.lang.StringEscapeUtils,
                 imcode.server.Imcms,
                 imcode.util.Utility,
                 imcode.server.user.UserDomainObject,
                 java.util.*,
                 imcode.util.Html" %>
<%@ page import="com.imcode.imcms.mapping.DocumentMapper" %>
<%@ page import="com.imcode.imcms.domain.dto.export.DocumentExportHistory" %>
<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="vel" uri="imcmsvelocity" %>
<% final DocumentExportHistory history = (DocumentExportHistory) request.getAttribute("history");%>
<vel:velocity>

	#gui_start_of_page( "<? imcms/lang/jsp/export/export_summary.jsp/title ?>" "AdminManager" "" "ListDocument" "" )

	<table border="0" cellspacing="0" cellpadding="2" width="680" id="documentsTable">
		<tr>
			<td style="width: 20%"><b><? global/Page_alias ?>&nbsp;</b></td>
			<td style="width: 5%"><b><? web/imcms/lang/jsp/heading_status ?>&nbsp;</b></td>
			<td style="width: 5%"><b><? web/imcms/lang/jsp/heading_type ?></b></td>
			<td style="width: 64%"><b><? web/imcms/lang/jsp/heading_adminlink ?></b></td>
			<td style="width: 1%"><b></b></td>
		</tr>
		<%

			final DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
			final UserDomainObject user = Utility.getLoggedOnUser(request);
			final Map documentTypes = documentMapper.getAllDocumentTypeIdsAndNamesInUsersLanguage(user);
			Iterator iterator;
			if (history.getDocumentList() != null) {
				iterator = documentMapper.getDocumentsIteratorIn(history.getDocumentList());
			} else {
				iterator = documentMapper.getDocumentsIteratorInRange(history.getRange());
			}

			while (iterator.hasNext()) {
				final DocumentDomainObject document = (DocumentDomainObject) iterator.next();
		%>
		<tr>
			<td colspan="6"><img src="$contextPath/imcms/$language/images/admin/1x1_cccccc.gif" width="100%" height="1">
			</td>
		</tr>
		<tr valign="top" id="<%= document.getId() %>"><%
			String alias = document.getAlias();
			if (alias != null) {
		%>
			<td><a name="alias"
			       href="$contextPath/<%= document.getAlias() %>"><%= StringEscapeUtils.escapeHtml(document.getAlias()) %>
			</a></td>
			<% } else { %>
			<td>&nbsp;</td>
			<%}%>
			<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="2"><br>
				<%= Html.getLinkedStatusIconTemplate(document, user, request) %>
			</td>
			<td nowrap><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="2"><br>
				<%= StringEscapeUtils.escapeHtml((String) documentTypes.get(document.getDocumentTypeId()))%>&nbsp;
			</td>
			<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="2"><br>
				<a name="<%= document.getId() %>"
				   href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>"><%=
				document.getId() %> - <%= StringEscapeUtils.escapeHtml(document.getHeadline()) %>
				</a>
			</td>
			<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="2"><br>
				<%
					switch (history.getStatus(document.getId())) {
						case SUCCESS:
							out.write("<span title=\"Exported\" class=\"checkmark\"></span>");
							break;
						case FAILED:
							out.write("<span title=\"Failed\" class=\"close\"></span>");
							break;
						case SKIPPED:
							out.write("<span style=\"padding-left:3px;\">" +
									"<svg width=\"16px\" height=\"16px\" viewBox=\"-1.6 -1.6 19.20 19.20\" xmlns=\"http://www.w3.org/2000/svg\"\n" +
									"\t\t\t\t     fill=\"none\" stroke=\"grey\">\n" +
									"<title> Skipped </title>" +
									"\t\t\t\t\t<g id=\"SVGRepo_bgCarrier\" stroke-width=\"0\"></g>\n" +
									"\t\t\t\t\t<g id=\"SVGRepo_iconCarrier\">\n" +
									"\t\t\t\t\t\t<path fill=\"#20568d\" fill-rule=\"evenodd\"\n" +
									"\t\t\t\t\t\t      d=\"M8 0a8 8 0 100 16A8 8 0 008 0zM1.5 8a6.5 6.5 0 0110.535-5.096l-9.131 9.131A6.472 6.472 0 011.5 8zm2.465 5.096a6.5 6.5 0 009.131-9.131l-9.131 9.131z\"\n" +
									"\t\t\t\t\t\t      clip-rule=\"evenodd\"></path>\n" +
									"\t\t\t\t\t</g>\n" +
									"\t\t\t\t</svg>" +
									"</span>");
							break;
					}
				%>
			</td>
		</tr>
		<%
			}%>
		<tfoot>
		<tr>
			<td colspan="6">#gui_hr( "blue" )</td>
		</tr>
		<tr>
			<td colspan="5" align="right">
				<div style="display: inline-block;">
					<a class="imcmsFormBtn" name="" style="text-decoration:none;"
					   href="/servlet/ExportDocuments?download=true"><? global/download ?></a>
				</div>
			</td>
		</tr>
		</tfoot>
	</table>
	<%--	<%--%>
	<%--		}%>--%>
	#gui_end_of_page()
</vel:velocity>
