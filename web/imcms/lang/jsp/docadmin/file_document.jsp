<%@ page contentType="text/html" import="com.imcode.imcms.servlet.admin.DocumentComposer,
                                         imcode.server.document.DocumentMapper,
                                         org.apache.commons.lang.StringEscapeUtils,
                                         imcode.server.ApplicationServer,
                                         imcode.server.document.FileDocumentDomainObject,
                                         org.apache.commons.lang.ObjectUtils,
                                         imcode.server.document.HtmlDocumentDomainObject,
                                         imcode.util.*,
                                         com.imcode.imcms.flow.*,
                                         org.apache.commons.lang.ArrayUtils,
                                         org.apache.commons.lang.StringUtils,
                                         org.apache.commons.collections.Transformer,
                                         java.util.*"%>
<%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<vel:velocity>
<html>
<head>
<title><? install/htdocs/sv/jsp/docadmin/file_document.jsp/1 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'file')">
#gui_outer_start()
#gui_head("<? global/imcms_administration ?>")
<table border="0" cellspacing="0" cellpadding="0">
<form method="POST" enctype="multipart/form-data" action="DocumentComposer" charset>
<input type="hidden" name="meta_id" value="#getMetaId#">
<input type="hidden" name="new_meta_id" value="#new_meta_id#">
<tr>
	<td><input type="submit" class="imcmsFormBtn" name="cancel" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2001 ?>" name="cancel"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2002 ?>" title="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2003 ?>" class="imcmsFormBtn" onClick="openHelpW(74)"></td>
</tr>
</table>
#gui_mid()
<table border="0" cellspacing="0" cellpadding="2" width="400">
<%
    EditFileDocumentPageFlow.FileDocumentEditPage editPage = EditFileDocumentPageFlow.FileDocumentEditPage.fromRequest(request) ;

    DocumentPageFlow httpFlow = DocumentPageFlow.fromRequest(request) ;
    FileDocumentDomainObject document = (FileDocumentDomainObject)httpFlow.getDocument() ;
    FileDocumentDomainObject.FileDocumentFile fileDocumentFile = editPage.getFileVariant() ;
    boolean creatingNewDocument = httpFlow instanceof CreateDocumentPageFlow ;
    EditFileDocumentPageFlow.MimeTypeRestriction mimeTypeRestriction = editPage.getMimeTypeRestriction() ;

    String selectedFileVariantName = editPage.getFileVariantName();
    Map fileVariants = document.getFileVariants();
%>
<input type="hidden" name="<%= HttpPageFlow.REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW %>"
    value="<%= HttpSessionUtils.getSessionAttributeNameFromRequest(request,HttpPageFlow.REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW) %>">
<input type="hidden" name="<%= HttpPageFlow.REQUEST_PARAMETER__PAGE %>"
    value="<%= EditDocumentPageFlow.PAGE__EDIT %>">
<tr>
	<td>
        #gui_heading( "<? install/htdocs/sv/jsp/docadmin/file_document.jsp/4/1 ?>" )
    </td>
</tr>
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0" width="396">
    <%  LocalizedMessage localizedErrorMessage = editPage.getErrorMessage() ;
        if (null != localizedErrorMessage) {
            %><tr>
                <td colspan="2"><div style="color: red"><%= localizedErrorMessage.toLocalizedString(request) %></div></td>
            </tr><%
        }
    %>
    <% if (fileVariants.size() > 1) { %>
    <tr>
		<td width="20%" height="22"><? install/htdocs/sv/jsp/docadmin/file_document.jsp/variant_name ?></td>
		<td width="80%">
            <input type="text" size="30" maxlength="50"
                name="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__FILE_DOC__NEW_VARIANT_NAME %>"
                value="<%= StringEscapeUtils.escapeHtml( selectedFileVariantName ) %>">
        </td>
	</tr>
    <% } %>
    <tr>
		<td width="20%" height="22"><? install/htdocs/sv/jsp/docadmin/file_document.jsp/5 ?></td>
		<td width="80%">
            <% if (StringUtils.isNotBlank(fileDocumentFile.getFilename())) { %>
                <%= StringEscapeUtils.escapeHtml( StringUtils.defaultString( fileDocumentFile.getFilename()) ) %><br>
            <% } %>
            <input type="file" name="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__FILE_DOC__FILE %>" size="45">
            <input type="hidden" name="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__FILE_DOC__OLD_VARIANT_NAME %>" value="<%= StringEscapeUtils.escapeHtml( StringUtils.defaultString( selectedFileVariantName ) )%>">
        </td>
	</tr>
	<tr>
		<td height="22"><? install/htdocs/sv/jsp/docadmin/file_document.jsp/7 ?></td>
		<td>
		<select id="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__FILE_DOC__MIME_TYPE %>" name="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__FILE_DOC__MIME_TYPE %>">
			<option value=""<% if (StringUtils.isBlank( fileDocumentFile.getMimeType() ) ) { %> selected<% } %>>
                <? install/htdocs/sv/jsp/docadmin/file_document.jsp/autodetect_or_fill_in_below ?>
            </option>
			<%
                final DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
                String[][] mimeTypes = documentMapper.getAllMimeTypesWithDescriptions(Utility.getLoggedOnUser( request ));
                boolean documentMimeTypeFoundInDropDown = false ;
                for ( int i = 0; i < mimeTypes.length; i++ ) {
                    String mimeType = mimeTypes[i][0];
                    if (!mimeTypeRestriction.allows(mimeType)) {
                        continue;
                    }
                    String mimeTypeDescriptionInUsersLanguage = mimeTypes[i][1] ;
                    boolean selected = false ;
                    if (mimeType.equals( fileDocumentFile.getMimeType() )) {
                        selected = true ;
                        documentMimeTypeFoundInDropDown = true ;
                    }
                    %><option value="<%= mimeType %>"<% if (selected) { %> selected<% } %>>
                        <%= StringEscapeUtils.escapeHtml( mimeTypeDescriptionInUsersLanguage ) %> (<%= mimeType %>)
                    </option><%
                }
            %>
		</select></td>
	</tr>
	<tr>
		<td height="22"><? install/htdocs/sv/jsp/docadmin/file_document.jsp/10 ?></td>
		<td>
            <input type="text" size="30" maxlength="50"
                name="mimetype"
                value="<% if (!documentMimeTypeFoundInDropDown) { %><%= StringEscapeUtils.escapeHtml( (String)ObjectUtils.defaultIfNull( fileDocumentFile.getMimeType(), "" ) ) %><% } %>"
                onkeypress="if (document.getElementById) document.getElementById('<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__FILE_DOC__MIME_TYPE %>').selectedIndex = 0;">
        </td>
	</tr>
    <%
        if (fileVariants.size() > 1) { %>
    <tr>
    	<td colspan="2">#gui_hr( "blue" )</td>
    </tr>
    <tr>
		<td colspan="2">
            <table>
            <%
            Set fileVariantNames = new TreeSet(String.CASE_INSENSITIVE_ORDER);
            fileVariantNames.addAll(fileVariants.keySet()) ;
            for ( Iterator iterator = fileVariantNames.iterator(); iterator.hasNext(); ) {
                    String fileVariantName = (String)iterator.next();
                    boolean isSelectedFileVariantName = fileVariantName.equals(selectedFileVariantName) ;
                    boolean isDefaultFileVariantName = fileVariantName.equals(document.getDefaultFileVariantName());
                    %><tr<% if (isSelectedFileVariantName) { %> bgcolor="#DDDDDD"<% } %>>
                        <td><%
                            String escapedFileVariantName = StringEscapeUtils.escapeHtml(fileVariantName) ;
                            if (isDefaultFileVariantName) {
                                %><b><%
                            }
                            %><%= escapedFileVariantName %><%
                            if (isDefaultFileVariantName) {
                                %></b><%
                            } %>
                        </td>
                        <td><input type="submit" class="imcmsFormBtnSmall"
                                name="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__EDIT_VARIANT_BUTTON_PREFIX +"_"+StringEscapeUtils.escapeHtml(fileVariantName)%>"
                                value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/select_variant_for_edit_button ?>"></td>
                        <td><input type="submit" class="imcmsFormBtnSmall"
                                name="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__DELETE_VARIANT_BUTTON_PREFIX +"_"+StringEscapeUtils.escapeHtml(fileVariantName)%>"
                                value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/delete_variant_button ?>"></td>
                        <td align="center"><% if (!isDefaultFileVariantName) { %>
                            <input type="submit" class="imcmsFormBtnSmall"
                                name="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__SELECT_DEFAULT_VARIANT_BUTTON_PREFIX +"_"+StringEscapeUtils.escapeHtml(fileVariantName) %>"
                                value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/select_default_variant_button ?>">
                            <% } else { %><b><? install/htdocs/sv/jsp/docadmin/file_document.jsp/default_variant ?></b><% } %>
                            </td>
                    </tr><%
                }
            %>
            </table>
        </td>
	</tr>
    <% } %>
	</table></td>
</tr>
<tr>
	<td>#gui_hr( "blue" )</td>
</tr>
<tr>
	<td align="right">
	<input type="submit" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/new_variant_button ?>" name="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__NEW_VARIANT_BUTTON %>">
	<input type="submit" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2004 ?>" name="<%= HttpPageFlow.REQUEST_PARAMETER__OK_BUTTON %>">
	<input type="reset" class="imcmsFormBtn" name="reset" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2005 ?>">
	<input type="submit" class="imcmsFormBtn" name="cancel" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2006 ?>"></td>
</tr>
</form>
</table>
#gui_bottom()
#gui_outer_end()
</body>
</html>
</vel:velocity>
