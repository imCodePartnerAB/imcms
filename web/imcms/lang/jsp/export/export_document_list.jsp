<%@ page import="com.imcode.imcms.servlet.superadmin.ListDocuments,
                 imcode.server.document.DocumentDomainObject,
                 org.apache.commons.lang.StringEscapeUtils,
                 imcode.server.Imcms,
                 imcode.util.Utility,
                 imcode.server.user.UserDomainObject,
                 java.util.*,
                 imcode.util.Html" %>
<%@ page import="com.imcode.imcms.mapping.DocumentMapper" %>
<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="vel" uri="imcmsvelocity" %>
<% ListDocuments.FormData formData = (ListDocuments.FormData) request.getAttribute(ListDocuments.REQUEST_ATTRIBUTE__FORM_DATA);%>
<vel:velocity>
	#gui_start_of_page( "<? imcms/lang/jsp/export/export_document_list.jsp/title ?>" "AdminManager" "" "ListDocument" "" )

	<table border="0" cellspacing="0" cellpadding="2" width="680">
		<tr>
			<td>
				<table border="0" cellspacing="0" cellpadding="0">
					<form method="GET" action="ListDocuments">
						<tr>
							<td><? imcms/lang/jsp/document_list.jsp/1003 ?></td>
							<td>&nbsp;&nbsp;</td>
							<td>
								<input type="text" id="<%= ListDocuments.PARAMETER__LIST_START %>"
								       name="<%= ListDocuments.PARAMETER__LIST_START %>"
								       value="<%= formData.selectedRange.getMinimumInteger() %>" size="6">
							</td>
							<td>&nbsp;&nbsp;</td>
							<td><? imcms/lang/jsp/document_list.jsp/1004 ?></td>
							<td>&nbsp;&nbsp;</td>
							<td>
								<input type="text" id="<%= ListDocuments.PARAMETER__LIST_END %>"
								       name="<%= ListDocuments.PARAMETER__LIST_END %>"
								       value="<%= formData.selectedRange.getMaximumInteger() %>" size="6">
							</td>
							<td>&nbsp;&nbsp;</td>
							<td>
								<input type="hidden" name="<%=ListDocuments.PARAMETER__EXPORT_DOCUMENTS%>"
								       value="true">
							</td>
							<td>
								<input type="submit" class="imcmsFormBtnSmall"
								       name="<%= ListDocuments.PARAMETER_BUTTON__LIST %>"
								       value=" <? imcms/lang/jsp/document_list.jsp/2002 ?> ">
							</td>
							<%
								if (request.getParameter("start") != null && request.getParameter("end") != null) {
							%>
							<td>
								<input type="checkbox" name="skipExported" id="skipExported" checked>
							</td>
							<td>
								<label for="skipExported"><? imcms/lang/jsp/export/export_document_list.jsp/skip_exported ?></label>
							</td>
							<%
								}
							%>
						</tr>
					</form>
				</table>
			</td>
		</tr>
		<tr>
			<td>#gui_hr( "blue" )</td>
		</tr>
	</table>
	<%
		if (null != formData.documentsIterator) { %>

	<table border="0" cellspacing="0" cellpadding="2" width="680" id="documentsTable">
		<tr>
			<td><b><? global/Page_alias ?>&nbsp;</b></td>
			<td><b><? web/imcms/lang/jsp/heading_status ?>&nbsp;</b></td>
			<td><b><? web/imcms/lang/jsp/heading_type ?></b></td>
			<td><b><? web/imcms/lang/jsp/heading_adminlink ?></b></td>
			<td><b><? web/imcms/lang/jsp/export_allowed ?></b></td>
				<%--			<td><b><? web/imcms/lang/jsp/exported ?></b></td>--%>
			<td><b></b></td>
		</tr>
		<%

			DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
			UserDomainObject user = Utility.getLoggedOnUser(request);
			Map documentTypes = documentMapper.getAllDocumentTypeIdsAndNamesInUsersLanguage(user);

			while (formData.documentsIterator.hasNext()) {
				DocumentDomainObject document = (DocumentDomainObject) formData.documentsIterator.next();
		%>
		<tr>
			<td colspan="6"><img src="$contextPath/imcms/$language/images/admin/1x1_cccccc.gif" width="100%" height="1">
			</td>
		</tr>
		<tr valign="top" id="<%= document.getId() %>" data-exported="<%= document.isExported() %>"><%
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
				<input <%= document.isPublished() ? "" : "disabled" %> name="allowedToExport"
				                                                       onclick="onAllowedToExportCheckboxClick.call(this,<%=document.getId()%>)"
				                                                       type="checkbox" <%= document.isExportAllowed() && document.isPublished() ? "checked":"" %> >
			</td>
			<% if (document.isExported()) {
			%>
			<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="2"><br>
				<span class="checkmark" title="<? web/imcms/lang/jsp/exported ?>"></span>
			</td>
			<%
				}
			%>
		</tr>
		<%
			}%>
		<tr>
			<td colspan="6">#gui_hr( "blue" )</td>
		</tr>
		<tfoot>
		<tr>
			<td colspan="6" align="right">
				<div class="loading-animation" style="display: none;float:left;" id="spinner"></div>
				<form method="get" action="AdminManager" style="display: inline-block;">
					<input type="submit" class="imcmsFormBtn" name="" id="cancelBtn" value="<? global/cancel ?>">
				</form>
				<div style="display: inline-block;">
					<input type="submit" class="imcmsFormBtn" name="" id="exportBtn" value="<? global/export ?>">
				</div>
			</td>
		</tr>
		</tfoot>
	</table>
	<%
		}%>
	#gui_end_of_page()
</vel:velocity>

<script type="application/javascript">
	const $documentsTable = document.getElementById("documentsTable");
	const $cancelBtn = document.getElementById("cancelBtn");
	const $exportBtn = document.getElementById("exportBtn");
	const $listStartInput = document.getElementById("start");
	const $listEndInput = document.getElementById("end");
	const $skipExportedInput = document.getElementById("skipExported");
	const $tbody = $documentsTable.querySelector("tbody");
	const $spinner = document.getElementById("spinner");

	function onAllowedToExportCheckboxClick(docId) {
		const $row = this.closest("tr");
		$row.style.opacity = "0.5";
		const data = {
			docId: docId,
			allowedToExport: this.checked
		};

		const saveRequest = new Request("/servlet/UpdateDocumentExportStatusServlet", {
			cache: "no-cache",
			method: "POST",
			body: JSON.stringify(data),
		});

		fetch(saveRequest)
			.then(response => {
				if (response.ok) {
					$row.style.opacity = "1";
				}
			})
	}

	$exportBtn.addEventListener('click', (e) => {
		e.preventDefault();
		disableButtons();

		$spinner.style.display = "inline-block";
		const exportRequest = new Request("/servlet/ExportDocuments" + ($skipExportedInput.checked ? "?skipExported=true" : ""), {
			cache: "no-cache",
			method: "POST",
			body: JSON.stringify({
				start: $listStartInput.value,
				end: $listEndInput.value
			}),
		});

		fetch(exportRequest)
			.then(response => {
				if (response.ok) {
					window.location.replace("/servlet/ExportDocuments")

				}
				$spinner.style.display = "none";
			})
	})

	function disableButtons() {
		const $buttons = [$cancelBtn, $exportBtn];

		$tbody.style.opacity = "0.5";
		$buttons.forEach(button => {
			button.disabled = true;
			button.style.cursor = "not-allowed"
		})
	}

</script>
