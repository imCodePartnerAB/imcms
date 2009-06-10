<%@ page contentType="text/html; charset=UTF-8"
    import="org.apache.commons.lang.StringEscapeUtils,
          imcode.server.Imcms,
          imcode.server.document.FileDocumentDomainObject,
          org.apache.commons.lang.ObjectUtils,
          imcode.util.*,
          com.imcode.imcms.flow.*,
          org.apache.commons.lang.StringUtils,
          java.util.*,
          com.imcode.imcms.servlet.GetDoc,
          com.imcode.util.HumanReadable"
%><%@ page import="com.imcode.imcms.mapping.DocumentMapper, com.imcode.imcms.util.l10n.LocalizedMessage"%><%@taglib prefix="vel" uri="imcmsvelocity"%>
<vel:velocity>
<html>
<head>
<title><? install/htdocs/sv/jsp/docadmin/file_document.jsp/1 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'file')">

#gui_outer_start()
#gui_head("<? global/imcms_administration ?>")
<table border="0" cellspacing="0" cellpadding="0">
<form method="POST" enctype="multipart/form-data" action="DocumentPageFlowDispatcher">
<input type="hidden" name="meta_id" value="#getMetaId#">
<input type="hidden" name="new_meta_id" value="#new_meta_id#">
<tr>
	<td><input type="submit" class="imcmsFormBtn" name="cancel" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2001 ?>" name="cancel"></td>
	<td>&nbsp;</td>
	<td><input type="button" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2002 ?>" title="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2003 ?>" class="imcmsFormBtn" onClick="openHelpW('LinkFile')"></td>
</tr>
</table>
#gui_mid()
<table border="0" cellspacing="0" cellpadding="2" width="400"><%

    EditFileDocumentPageFlow.FileDocumentEditPage editPage = EditFileDocumentPageFlow.FileDocumentEditPage.fromRequest(request) ;

    DocumentPageFlow httpFlow = DocumentPageFlow.fromRequest(request) ;
    FileDocumentDomainObject document = (FileDocumentDomainObject)httpFlow.getDocument() ;
    FileDocumentDomainObject.FileDocumentFile selectedFile = editPage.getSelectedFile() ;
    boolean creatingNewDocument = 0==document.getId();
    EditFileDocumentPageFlow.MimeTypeRestriction mimeTypeRestriction = editPage.getPageMimeTypeRestriction() ;

    String selectedFileId = editPage.getSelectedFileId();
    Map files = document.getFiles();
    boolean allowChoiceOfDefault = files.size() > 1; %>
<input type="hidden" name="<%= PageFlow.REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW %>" value="<%=
	HttpSessionUtils.getSessionAttributeNameFromRequest(request,PageFlow.REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW) %>">
<input type="hidden" name="<%= PageFlow.REQUEST_PARAMETER__PAGE %>" value="<%=
	EditDocumentPageFlow.PAGE__EDIT %>">
<tr>
	<td><%
	if (StringUtils.isNotBlank(selectedFile.getFilename())) {
		%>#gui_heading( "<? install/htdocs/sv/jsp/docadmin/file_document.jsp/4/2 ?> &nbsp; &quot;<%= selectedFile.getFilename() %>&quot;" )<%
	} else {
		%>#gui_heading( "<? install/htdocs/sv/jsp/docadmin/file_document.jsp/4/1 ?>" )<%
	} %></td>
</tr>
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0" width="656"><%
LocalizedMessage localizedErrorMessage = editPage.getErrorMessage() ;
if (null != localizedErrorMessage) { %>
	<tr>
		<td colspan="2"><div style="color: red"><%= localizedErrorMessage.toLocalizedString(request) %></div></td>
	</tr><%
} %>
	<tr>
		<td width="85" height="22"><? install/htdocs/sv/jsp/docadmin/file_document.jsp/filename_label ?></td>
		<td><%
if (StringUtils.isNotBlank(selectedFile.getFilename())) {
	%><%= StringEscapeUtils.escapeHtml( StringUtils.defaultString( selectedFile.getFilename()) ) %><br><%
} %>
		<input type="file" name="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__FILE_DOC__FILE %>" size="45"></td>
	</tr>
	<input type="hidden" name="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__FILE_DOC__SELECTED_FILE_ID %>" value="<%=
	StringEscapeUtils.escapeHtml( StringUtils.defaultString( selectedFileId ) )%>">
	<tr>
		<td height="22"><? install/htdocs/sv/jsp/docadmin/file_document.jsp/mime_type_label ?></td>
		<td>
		<select id="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__FILE_DOC__MIME_TYPE %>" name="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__FILE_DOC__MIME_TYPE %>">
			<option value=""<% if (StringUtils.isBlank( selectedFile.getMimeType() ) ) { %> selected<% } %>>
			<? install/htdocs/sv/jsp/docadmin/file_document.jsp/autodetect_or_fill_in_below ?></option><%

    final DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
    String[][] mimeTypes = documentMapper.getAllMimeTypesWithDescriptions(Utility.getLoggedOnUser( request ));
    boolean documentMimeTypeFoundInDropDown = false ;

    for ( int i = 0; i < mimeTypes.length; i++ ) {
        String mimeType = mimeTypes[i][0];
        if (!mimeTypeRestriction.allows(mimeType)) {
            continue;
        }
        String mimeTypeDescriptionInUsersLanguage = mimeTypes[i][1] ;
        boolean selected = false ;
        if (mimeType.equals( selectedFile.getMimeType() )) {
            selected = true ;
            documentMimeTypeFoundInDropDown = true ;
        } %>
                <option value="<%= mimeType %>"<% if (selected) { %> selected<% } %>><%=
            StringEscapeUtils.escapeHtml( mimeTypeDescriptionInUsersLanguage ) %> (<%= mimeType %>)</option><%
    } %>
		</select></td>
	</tr>
	<tr>
		<td height="22"><? install/htdocs/sv/jsp/docadmin/file_document.jsp/other_mime_type_label ?></td>
	<td>
	<input type="text" size="30" maxlength="50" name="mimetype" value="<%
if (!documentMimeTypeFoundInDropDown) {
	%><%= StringEscapeUtils.escapeHtml( (String)ObjectUtils.defaultIfNull( selectedFile.getMimeType(), "" ) ) %><%
} %>" onkeypress="if (document.getElementById) document.getElementById('<%=
	EditFileDocumentPageFlow.REQUEST_PARAMETER__FILE_DOC__MIME_TYPE %>').selectedIndex = 0;"></td>
	</tr>
	<tr>
		<td colspan="2">#gui_hr( "blue" )</td>
	</tr>
	<tr>
		<td colspan="2">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td><? install/htdocs/sv/jsp/docadmin/file_document.jsp/target_label ?></td>
			<td><%
			request.setAttribute( "target", document.getTarget() );%>
			<jsp:include page="target.jsp" /></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td colspan="2">#gui_hr( "blue" )</td>
	</tr>
	<tr>
		<td colspan="2" align="right">
		<input type="submit" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/save_file_button ?>" name="<%=
	EditFileDocumentPageFlow.REQUEST_PARAMETER__SAVE_FILE_BUTTON %>"></td>
	</tr><%
if (!files.isEmpty()) { %>
	<tr>
		<td colspan="2">&nbsp;<br>#gui_heading( "<? install/htdocs/sv/jsp/docadmin/file_document.jsp/heading_added_files ?>" )</td>
	</tr>
	<tr>
		<td colspan="2">
		<table border="0" cellspacing="0" cellpadding="4" width="100%">
		<tr align="left">
			<td><b><? install/htdocs/sv/jsp/docadmin/file_document.jsp/file_id_label ?></b></td>
			<td width="40%"><b><? install/htdocs/sv/jsp/docadmin/file_document.jsp/filename_label ?></b></td>
			<td align="right"><b><? install/htdocs/sv/jsp/docadmin/file_document.jsp/size_label ?></b>&nbsp;</td>
			<td><b><? install/htdocs/sv/jsp/docadmin/file_document.jsp/mime_type_label ?></b></td><%
	if (allowChoiceOfDefault) { %>
			<td align="center"><b><? install/htdocs/sv/jsp/docadmin/file_document.jsp/default_label ?></b></td><%
	} %>
			<td width="55">&nbsp;</td>
			<td width="55">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="<%= (allowChoiceOfDefault) ? "7" : "6" %>"><img src="$contextPath/imcms/$language/images/admin/1x1_cccccc.gif" width="100%" height="1"></td>
		</tr><%
	Set fileIds = files.keySet();

	for ( Iterator iterator = fileIds.iterator(); iterator.hasNext(); ) {

		String fileId = (String)iterator.next();
		String escapedFileId = StringEscapeUtils.escapeHtml(fileId) ;
		boolean isSelectedFileId = fileId.equals(selectedFileId) ;
		boolean isDefaultFileId = fileId.equals(document.getDefaultFileId());
		FileDocumentDomainObject.FileDocumentFile file = (FileDocumentDomainObject.FileDocumentFile)files.get(fileId) ;
		%>
		<tr<% if (isSelectedFileId) { %> bgcolor="#DDDDFF"<% } %>>
			<td><input type="text" size="10" maxlength="50" name="<%=
		EditFileDocumentPageFlow.REQUEST_PARAMETER__FILE_DOC__NEW_FILE_ID_PREFIX + escapedFileId %>" value="<%= escapedFileId %>"></td><%
		if (!creatingNewDocument) { %>
			<td><a href="$contextPath/servlet/GetDoc?meta_id=<%= document.getId() %>&<%= GetDoc.REQUEST_PARAMETER__FILE_ID %>=<%= escapedFileId %>" target="_blank"><%=
			StringEscapeUtils.escapeHtml(file.getFilename()) %></a></td><%
		} else { %>
			<td><%= StringEscapeUtils.escapeHtml(file.getFilename()) %></td><%
		} %>
			<td align="right" nowrap><%= HumanReadable.getHumanReadableByteSize( file.getInputStreamSource().getSize() ).replaceAll( " ", "&nbsp;" ) %>&nbsp;</td>
			<td><%= StringEscapeUtils.escapeHtml(file.getMimeType()) %></td><%
		if (allowChoiceOfDefault) { %>
			<td align="center"><input type="radio" name="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__DEFAULT_FILE %>" value="<%=
			escapedFileId %>"<% if (isDefaultFileId) { %> checked<% } %>></td><%
		} %>
			<td><input type="submit" class="imcmsFormBtnSmall" name="<%=
			EditFileDocumentPageFlow.REQUEST_PARAMETER__SELECT_FILE_BUTTON_PREFIX +escapedFileId%>" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/select_file_button ?>"></td>
			<td><input type="submit" class="imcmsFormBtnSmall" name="<%=
			EditFileDocumentPageFlow.REQUEST_PARAMETER__DELETE_FILE_BUTTON_PREFIX +escapedFileId%>" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/delete_file_button ?>"></td>
		</tr><%
	} %>
		</table></td>
	</tr><%
} %>
	</table></td>
</tr>
<tr>
	<td>#gui_hr( "blue" )</td>
</tr>
<tr>
	<td align="right"><%
	if (!files.isEmpty()) { %>
	<input type="submit" class="imcmsFormBtn" value="<? global/OK ?>" name="<%= PageFlow.REQUEST_PARAMETER__OK_BUTTON %>" onClick="return singleclicked();"><%
	} %>
	<input type="submit" class="imcmsFormBtn" name="<%= PageFlow.REQUEST_PARAMETER__CANCEL_BUTTON %>" value="<? global/cancel ?>"></td>
</tr>
</form>
</table>
#gui_bottom()
#gui_outer_end()

</body>
</html>
</vel:velocity>
