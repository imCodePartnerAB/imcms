<%@ page import="com.imcode.imcms.servlet.admin.DocumentComposer,
                 org.apache.commons.lang.StringEscapeUtils,
                 imcode.server.document.HtmlDocumentDomainObject,
                 org.apache.commons.lang.ObjectUtils" contentType="text/html"%>
<%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<vel:velocity>
<html>
<head>
<title><? install/htdocs/sv/jsp/docadmin/html_document.jsp/1 ?></title>
<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'frame_set')">
#gui_outer_start()
#gui_head("<? global/imcms_administration ?>")
<table border="0" cellspacing="0" cellpadding="0">
<form method="POST" action="DocumentComposer">
<tr>
	<td><input type="submit" class="imcmsFormBtn" name="cancel" value="<? install/htdocs/sv/jsp/docadmin/html_document.jsp/2001 ?>"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? install/htdocs/sv/jsp/docadmin/html_document.jsp/2002 ?>" title="<? install/htdocs/sv/jsp/docadmin/html_document.jsp/2003 ?>" class="imcmsFormBtn" onClick="openHelpW(76)"></td>
</tr>
</form>
</table>
#gui_mid()
<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<form method="POST" action="DocumentComposer">
<%
    HtmlDocumentDomainObject document = (HtmlDocumentDomainObject)DocumentComposer.getObjectFromSessionWithKeyInRequest(request, DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME);
    DocumentComposer.NewDocumentParentInformation newDocumentParentInformation = (DocumentComposer.NewDocumentParentInformation)DocumentComposer.getObjectFromSessionWithKeyInRequest(request, DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME);
    boolean creatingNewDocument = null != newDocumentParentInformation;

    if (creatingNewDocument) { %>
        <input type="hidden"
                name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__ACTION %>"
                value="<%= DocumentComposer.ACTION__CREATE_NEW_HTML_DOCUMENT %>">
        <input type="hidden"
                name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME %>"
                value="<%= request.getAttribute(DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME) %>">
    <% } else { %>
        <input type="hidden"
                name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__ACTION %>"
                value="<%= DocumentComposer.ACTION__PROCESS_EDITED_HTML_DOCUMENT %>">
    <% } %>
        <input type="hidden"
                name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME %>"
                value="<%= request.getAttribute(DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME) %>">
<tr>
	<td>
        #gui_heading( "<? install/htdocs/sv/jsp/docadmin/html_document.jsp/6 ?>" )
    </td>
</tr>
<tr>
	<td><? install/htdocs/sv/jsp/docadmin/html_document.jsp/5 ?></td>
</tr>
<tr>
	<td><textarea name="<%= DocumentComposer.PARAMETER__HTML_DOC__HTML %>" cols="57" rows="16" wrap="virtual" style="width:100%; overflow:auto">
<%= StringEscapeUtils.escapeHtml( (String)ObjectUtils.defaultIfNull( document.getHtmlDocumentHtml(), "") ) %></textarea></td>
</tr>
<tr>
	<td>
        #gui_hr( "blue" )
    </td>
</tr>
<tr>
	<td align="right">
	<input type="submit" class="imcmsFormBtn" name="ok" value="<? install/htdocs/sv/jsp/docadmin/html_document.jsp/2004 ?>">
	<input type="reset" class="imcmsFormBtn" name="reset" value="<? install/htdocs/sv/jsp/docadmin/html_document.jsp/2005 ?>">
	<input type="submit" class="imcmsFormBtn" name="cancel" value="<? install/htdocs/sv/jsp/docadmin/html_document.jsp/2006 ?>"></td>
</tr>
</form>
</table>
#gui_bottom()
#gui_outer_end()
</vel:velocity>
</body>
</html>
