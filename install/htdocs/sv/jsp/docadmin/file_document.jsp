<%@ page contentType="text/html" import="com.imcode.imcms.servlet.admin.DocumentComposer,
                                         imcode.server.document.DocumentMapper,
                                         imcode.util.Utility,
                                         org.apache.commons.lang.StringEscapeUtils,
                                         imcode.server.ApplicationServer"%>
<html>
<head>
<title><? install/htdocs/sv/jsp/docadmin/file_document.jsp/1 ?></title>

<link rel="stylesheet" href="@imcmscssurl@/imcms_admin_ns.css" type="text/css">
<script src="@imcmsscripturl@/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'file')">

<script>
imcmsGui("outer_start", null);
imcmsGui("head", null);
</script>
<table border="0" cellspacing="0" cellpadding="0">
<form method="POST" enctype="multipart/form-data" action="DocumentComposer" charset>
<input type="hidden" name="meta_id" value="#getMetaId#">
<input type="hidden" name="new_meta_id" value="#new_meta_id#">
<tr>
	<td><input type="submit" class="imcmsFormBtn" name="cancel" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2001 ?>" name="cancel"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2002 ?>" title="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2003 ?>" class="imcmsFormBtn" onClick="openHelpW(74)"></td>
</tr>
</form>
</table>
<script>
imcmsGui("mid", null);
</script>
<table border="0" cellspacing="0" cellpadding="2" width="400">
<form method="POST" enctype="multipart/form-data" action="DocumentComposer">
<input type="hidden" name="<%= DocumentComposer.PARAMETER__ACTION %>" value="<%= DocumentComposer.ACTION__CREATE_NEW_FILE_DOCUMENT %>">
<input type="hidden" name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME %>" value="<%= request.getAttribute(DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME) %>">
<input type="hidden" name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME %>" value="<%= request.getAttribute(DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME) %>">
<tr>
	<td><script>imcHeading("<? install/htdocs/sv/jsp/docadmin/file_document.jsp/4/1 ?>",396);</script></td>
</tr>
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0" width="396">
	<tr>
		<td width="20%" height="22"><? install/htdocs/sv/jsp/docadmin/file_document.jsp/5 ?></td>
		<td width="80%">
            <input type="file" name="<%= DocumentComposer.PARAMETER__FILE_DOC__FILE %>" size="45"/>
        </td>
	</tr>
	<tr>
		<td height="22"><? install/htdocs/sv/jsp/docadmin/file_document.jsp/7 ?></td>
		<td>
		<select name="<%= DocumentComposer.PARAMETER__FILE_DOC__MIME_TYPE %>">
			<option value=""><? install/htdocs/sv/jsp/docadmin/file_document.jsp/autodetect_or_fill_in_below ?></option>
			<%
                final DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
                String[][] mimeTypes = documentMapper.getAllMimeTypesWithDescriptions(Utility.getLoggedOnUser( request ));
                for ( int i = 0; i < mimeTypes.length; i++ ) {
                    String mimeType = mimeTypes[i][0];
                    String mimeTypeDescriptionInUsersLanguage = mimeTypes[i][1] ;
                    %><option value="<%= mimeType %>"><%= StringEscapeUtils.escapeHtml( mimeTypeDescriptionInUsersLanguage ) %></option><%
                }
            %>
		</select></td>
	</tr>
	<tr>
		<td height="22"><? install/htdocs/sv/jsp/docadmin/file_document.jsp/10 ?></td>
		<td>
            <input type="text" name="mimetype" size="30" maxlength="50" value="">
        </td>
	</tr>
	</table></td>
</tr>
<tr>
	<td><script>hr("100%",396,"blue");</script></td>
</tr>
<tr>
	<td align="right">
	<input type="submit" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2004 ?>" name="ok">
	<input type="reset" class="imcmsFormBtn" name="reset" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2005 ?>">
	<input type="submit" class="imcmsFormBtn" name="cancel" value="<? install/htdocs/sv/jsp/docadmin/file_document.jsp/2006 ?>"></td>
</tr>
</form>
</table>
<script>
imcmsGui("bottom", null);
imcmsGui("outer_end", null);
</script>

</body>
</html>