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
                                         org.apache.commons.lang.StringUtils"%>
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

    DocumentPageFlow httpFlow = (DocumentPageFlow)DocumentComposer.getDocumentPageFlowFromRequest(request) ;
    FileDocumentDomainObject document = (FileDocumentDomainObject)httpFlow.getDocument() ;
    boolean creatingNewDocument = httpFlow instanceof CreateDocumentPageFlow ;
    EditFileDocumentPageFlow.MimeTypeRestriction mimeTypeRestriction = editPage.getMimeTypeRestriction() ;
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
                <td colspan="2"><span style="color: red"><%= StringEscapeUtils.escapeHtml(localizedErrorMessage.toLocalizedString(request)) %></span></td>
            </tr><%
        }
    %>
    <tr>
		<td width="20%" height="22"><? install/htdocs/sv/jsp/docadmin/file_document.jsp/5 ?></td>
		<td width="80%">
            <% if (!creatingNewDocument) { %>
                <%= StringEscapeUtils.escapeHtml( (String)ObjectUtils.defaultIfNull( document.getFilename(), "" ) ) %><br>
            <% } %>
            <input type="file" name="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__FILE_DOC__FILE %>" size="45">
        </td>
	</tr>
	<tr>
		<td height="22"><? install/htdocs/sv/jsp/docadmin/file_document.jsp/7 ?></td>
		<td>
		<select name="<%= EditFileDocumentPageFlow.REQUEST_PARAMETER__FILE_DOC__MIME_TYPE %>">
			<option value=""<% if (StringUtils.isBlank( document.getMimeType() ) ) { %> selected<% } %>>
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
                    if (mimeType.equals( document.getMimeType() )) {
                        selected = true ;
                        documentMimeTypeFoundInDropDown = true ;
                    }
                    %><option value="<%= mimeType %>"<% if (selected) { %> selected<% } %>>
                        <%= StringEscapeUtils.escapeHtml( mimeTypeDescriptionInUsersLanguage ) %>
                    </option><%
                }
            %>
		</select></td>
	</tr>
	<tr>
		<td height="22"><? install/htdocs/sv/jsp/docadmin/file_document.jsp/10 ?></td>
		<td>
            <input type="text" name="mimetype" size="30" maxlength="50" value="<% if (!documentMimeTypeFoundInDropDown) { %><%= StringEscapeUtils.escapeHtml( (String)ObjectUtils.defaultIfNull( document.getMimeType(), "" ) ) %><% } %>">
        </td>
	</tr>
	</table></td>
</tr>
<tr>
	<td>#gui_hr( "blue" )</td>
</tr>
<tr>
	<td align="right">
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
