<%@ page import="com.imcode.imcms.servlet.superadmin.ListDocuments,
                 imcode.server.document.DocumentDomainObject,
                 org.apache.commons.lang.StringEscapeUtils,
                 imcode.server.Imcms,
                 imcode.util.Utility,
                 imcode.server.user.UserDomainObject,
                 java.util.*,
                 imcode.util.Html,
                 com.imcode.imcms.mapping.DocumentMapper" %>
<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="vel" uri="imcmsvelocity" %>
<% ListDocuments.FormData formData = (ListDocuments.FormData) request.getAttribute(ListDocuments.REQUEST_ATTRIBUTE__FORM_DATA);%>
<vel:velocity>
	#gui_start_of_page( "<? imcms/lang/jsp/export/export_document_list.jsp/title ?>" "AdminManager" "" "ListDocument" "" )

	<table border="0" cellspacing="0" cellpadding="2" width="680">
		<tr>
			<td>
				<table border="0" cellspacing="0" cellpadding="0">
					<form method="GET" action="ListDocuments" id="form">
						<%
							if (formData.list != null) {
								for (int metaId : formData.list) {
									out.println(String.format("<input hidden value='%s' id='%s' name='documentsList'>", metaId, metaId));
								}
							}
						%>
						<tr>
							<td><button type="button" id="changeInput" class="imcmsFormBtnSmall" style="margin-bottom: 5px;">Change input type</button></td>
						</tr>
						<tr id="documentsList" <%=formData.list == null ? "style='display: none'" : "" %>>
							<td>
								<div>
									<select name="<%= ListDocuments.PARAMETER__LIST %>"
											id="documentsListSelect" multiple
											style="text-align: center;
											min-width: 100px;
											min-height: 130px;">
										<%
											if (formData.list != null) {
												for (int metaId : formData.list) {
													out.println(String.format("<option value='%s' id='%s' name='documentsList'>%s</option>", metaId, metaId, metaId));
												}
											}
										%>
									</select>
								</div>
							</td>
							<td style="display: block">
								<div style="margin-left: 10px;">
									<input type="text" id="metaIdInput" style="margin-bottom: 5px;">
									<span>&nbsp;&nbsp;</span>
									<div>
										<button class="imcmsFormBtnSmall" type="button" id="addMetaIdBtn">Add</button>
										<span>&nbsp;&nbsp;</span>
										<button class="imcmsFormBtnSmall" type="button" id="deleteMetaIdBtn">Delete
										</button>
									</div>
								</div>
							</td>
						</tr>
						<tr id="documentsRange" <%=formData.list != null ? "style='display: none'" : "" %>>
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
						</tr>
						<tr style="display: block;margin-top: 5px">
						<td>
							<input type="hidden" name="<%=ListDocuments.PARAMETER__EXPORT_DOCUMENTS%>"
								   value="true">
						</td>
						<td>
							<input type="submit" class="imcmsFormBtnSmall"
								   id="listBtn"
								   name="<%= ListDocuments.PARAMETER_BUTTON__LIST %>"
								   value=" <? imcms/lang/jsp/document_list.jsp/2002 ?> ">
						</td>
						<%
							if ((request.getParameter("start") != null && request.getParameter("end") != null) || request.getParameter("documentsList") != null) {
						%>
						<td>
							<input type="checkbox" name="skipExported" id="skipExported" checked>
						</td>
						<td>
							<label for="skipExported"><? imcms/lang/jsp/export/export_document_list.jsp/skip_exported ?></label>
						</td>
						</tr>
						<%
							}
						%>
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
		<tr valign="top" data-id="<%= document.getId() %>" data-exported="<%= document.isExported() %>"><%
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
	const $form = document.getElementById("form");
	const $documentsTable = document.getElementById("documentsTable");
	const $cancelBtn = document.getElementById("cancelBtn");
	const $exportBtn = document.getElementById("exportBtn");
	const $listStartInput = document.getElementById("start");
	const $listEndInput = document.getElementById("end");
	const $skipExportedInput = document.getElementById("skipExported");
	const $tbody = $documentsTable?.querySelector("tbody");
	const $spinner = document.getElementById("spinner");

	const $documentsListSelect = document.getElementById("documentsListSelect");
	const $metaIdInput = document.getElementById("metaIdInput");
	const $addMetaIdBtn = document.getElementById("addMetaIdBtn");
	const $deleteMetaIdBtn = document.getElementById("deleteMetaIdBtn");

	const $documentsList = document.getElementById("documentsList");
	const $documentsRange = document.getElementById("documentsRange");
	const $changeInput=document.getElementById("changeInput");

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

	$exportBtn?.addEventListener('click', (e) => {
		e.preventDefault();
		disableButtons();

		const documentsId = Array.from($documentsListSelect.options).map(option => option.value);
		const url = new URLSearchParams();

		if ($skipExportedInput.checked) {
			url.append("skipExported", "true");
		}

		url.append("start", $listStartInput.value);
		url.append("end", $listEndInput.value);
		url.append("documentsList", documentsId.toString());

		const exportRequest = new Request("/servlet/ExportDocuments?" + url.toString(), {
			cache: "no-cache",
			method: "POST",
		});

		$spinner.style.display = "inline-block";
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

	$addMetaIdBtn.addEventListener('click', (e) => {
		e.preventDefault();
		const metaId = $metaIdInput.value;

		if (isNaN(parseInt(metaId))) {
			alert("Input only meta id");
			return;
		}

		if (document.getElementById(metaId)){
			alert("Duplicate meta id");
			return;
		}

		const $option = document.createElement("option");
		$option.value = metaId;
		$option.innerHTML = metaId;

		$documentsListSelect.appendChild($option);
		$metaIdInput.value = "";

		const $input = document.createElement("input");
		$input.value = metaId;
		$input.id = metaId;
		$input.hidden = true;
		$input.name = "documentsList";
		$form.appendChild($input);
	})

	$deleteMetaIdBtn.addEventListener('click', (e) => {
		e.preventDefault();
		const selectedOptions = Array.from($documentsListSelect.selectedOptions);

		if (!selectedOptions.length) {
			alert("Select option/s first")
		}

		//iterate in reverse order to prevent index updating in select
		for (let i = selectedOptions.length - 1; i >= 0; i--) {
			$documentsListSelect.remove(selectedOptions[i].index);
			document.getElementById(selectedOptions[i].value).remove();
		}
	})

	const hideEvent = new Event("hide-element-event");
	const showEvent = new Event("show-element-event");

	$documentsList.addEventListener("show-element-event", (e) => {
		$documentsList.style.display = "table-row";
	})

	$documentsList.addEventListener("hide-element-event", (e) => {
		$documentsList.style.display = "none";
	})

	$documentsRange.addEventListener("show-element-event", (e) => {
		$documentsRange.style.display = "table-row";
	})

	$documentsRange.addEventListener("hide-element-event", (e) => {
		$documentsRange.style.display = "none";
	})

	$changeInput.addEventListener("click", (e) => {
		if ($documentsRange.style.display !== "none") {
			$documentsList.dispatchEvent(showEvent);
			$documentsRange.dispatchEvent(hideEvent);
		} else {
			$documentsRange.dispatchEvent(showEvent);
			$documentsList.dispatchEvent(hideEvent);
		}
	})

</script>
