<%@ page import="com.imcode.imcms.servlet.admin.DocumentComposer,
                 imcode.server.document.DocumentDomainObject,
                 org.apache.commons.lang.StringEscapeUtils,
                 org.apache.commons.lang.ObjectUtils,
                 imcode.server.document.UrlDocumentDomainObject" contentType="text/html"%>
<%
    UrlDocumentDomainObject document = (UrlDocumentDomainObject)DocumentComposer.getObjectFromSessionWithKeyInRequest( request, DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME ) ;
%>
<html>
<head>

<title><? install/htdocs/sv/jsp/docadmin/url_document.jsp/1 ?></title>

<link rel="stylesheet" href="@imcmscssurl@/imcms_admin_ns.css" type="text/css">
<script src="@imcmsscripturl@/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'url_ref')">

#gui_outer_start()
#gui_head("<? global/imcms_administration ?>")

<table border="0" cellspacing="0" cellpadding="0">
<form action="DocumentComposer">
<tr>
	<td><input type="submit" name="cancel" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2001 ?>"></td>
	<td>&nbsp;</td>
	<td><input type="button" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2002 ?>" title="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2003 ?>" class="imcmsFormBtn" onClick="openHelpW(82)"></td>
</tr>
</table>
#gui_mid()

<table border="0" cellspacing="0" cellpadding="2" width="400">
<%
    DocumentComposer.NewDocumentParentInformation newDocumentParentInformation = (DocumentComposer.NewDocumentParentInformation)DocumentComposer.getObjectFromSessionWithKeyInRequest(request, DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME);
    boolean creatingNewDocument = null != newDocumentParentInformation;

    if (creatingNewDocument) { %>
        <input type="hidden"
            name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__ACTION %>"
            value="<%= DocumentComposer.ACTION__CREATE_NEW_URL_DOCUMENT %>">
        <input type="hidden"
            name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME %>"
            value="<%= request.getAttribute(DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME) %>">
<%  } else { %>
        <input type="hidden"
            name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__ACTION %>"
            value="<%= DocumentComposer.ACTION__PROCESS_EDITED_URL_DOCUMENT %>">
<% } %>
        <input type="hidden"
            name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME %>"
            value="<%= request.getAttribute(DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME) %>">
<tr>
	<td colspan="2">#gui_heading( "<? install/htdocs/sv/jsp/docadmin/url_document.jsp/4/1 ?>" )</td>
</tr>
<tr>
	<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/url_document.jsp/1001 ?>&nbsp;</td>
	<td><input type="text" name="<%= DocumentComposer.PARAMETER__URL_DOC__URL %>" size="62" maxlength="255"
                value="<%= StringEscapeUtils.escapeHtml( (String)ObjectUtils.defaultIfNull( document.getUrlDocumentUrl(), "" )) %>"></td>
</tr>
<tr>
	<td colspan="2">#gui_hr( "cccccc" )</td>
</tr>
<tr>
	<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/url_document.jsp/1002 ?><img src="@imcmsimageurl@/admin/1x1.gif" width="1" height="22"></td>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
        <% String target = document.getTarget() ; %>
		<td><input type="radio" name="target" value="_self"<% if ("_self".equalsIgnoreCase( target ) || "".equals( target )) {%> checked<% target = null; }%>></td>
		<td class="imcmsAdmText" nowrap>&nbsp;<? install/htdocs/sv/jsp/docadmin/url_document.jsp/1003 ?> &nbsp;</td>
		<td><input type="radio" name="target" value="_blank"<% if ("_blank".equalsIgnoreCase( target )) {%> checked<% target = null; }%>></td>
		<td class="imcmsAdmText" nowrap>&nbsp;<? install/htdocs/sv/jsp/docadmin/url_document.jsp/1004 ?> &nbsp;</td>
		<td><input type="radio" name="target" value="_top"<% if ("_top".equalsIgnoreCase( target )) {%> checked<% target = null; }%>></td>
		<td class="imcmsAdmText" nowrap>&nbsp;<? install/htdocs/sv/jsp/docadmin/url_document.jsp/1005 ?> &nbsp;</td>
	</tr>
	<tr>
		<td><input type="radio" name="target"<% if (null != target) {%> checked<%}%>></td>
		<td class="imcmsAdmText" nowrap>&nbsp;<? install/htdocs/sv/jsp/docadmin/url_document.jsp/1006 ?> &nbsp;</td>
		<td colspan="4">
            <input type="text" name="target" size="17" maxlength="50"
                value="<% if (null != target) {%><%= StringEscapeUtils.escapeHtml( target ) %><%}%>">
        </td>
	</tr>
	</table></td>
</tr>
<tr>
	<td colspan="2">#gui_hr( "blue" )</td>
</tr>
<tr>
	<td colspan="2" align="right">
	<input type="submit" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2004 ?>" name="ok">
	<input type="reset" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2005 ?>">
	<input type="submit" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2006 ?>" name="cancel"></td>
</tr>
</form>
</table>
#gui_bottom()
#gui_outer_end()


</body>
</html>
