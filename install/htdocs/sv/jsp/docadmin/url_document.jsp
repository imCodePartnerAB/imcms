<%@ page import="com.imcode.imcms.servlet.admin.DocumentInformation,
                 imcode.server.document.DocumentDomainObject,
                 org.apache.commons.lang.StringEscapeUtils" contentType="text/html"%>
<%
    DocumentDomainObject document = (DocumentDomainObject)DocumentInformation.getObjectFromSessionWithKeyInRequest( request, DocumentInformation.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME ) ;
%>
<html>
<head>

<title><? install/htdocs/sv/jsp/docadmin/url_document.jsp/1 ?></title>

<link rel="stylesheet" href="@imcmscssurl@/imcms_admin_ns.css" type="text/css">
<script src="@imcmsscripturl@/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'url_ref')">

<script>
imcmsGui("outer_start", null);
imcmsGui("head", null);
</script>
<table border="0" cellspacing="0" cellpadding="0">
<form action="DocumentInformation">
<tr>
	<td><input type="submit" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2001 ?>"></td>
	<td>&nbsp;</td>
	<td><input type="button" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2002 ?>" title="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2003 ?>" class="imcmsFormBtn" onClick="openHelpW(82)"></td>
</tr>
</form>
</table>
<script>
imcmsGui("mid", null);
</script>
<table border="0" cellspacing="0" cellpadding="2" width="400">
<form method="POST" action="DocumentInformation">
<input type="hidden" name="<%= DocumentInformation.PARAMETER__ACTION %>" value="<%= DocumentInformation.ACTION__CREATE_NEW_URL_DOCUMENT %>">
<input type="hidden" name="<%= DocumentInformation.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME %>" value="<%= request.getAttribute(DocumentInformation.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME) %>">
<input type="hidden" name="<%= DocumentInformation.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME %>" value="<%= request.getAttribute(DocumentInformation.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME) %>">
<tr>
	<td colspan="2"><script>imcHeading("<? install/htdocs/sv/jsp/docadmin/url_document.jsp/4/1 ?>",396);</script></td>
</tr>
<tr>
	<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/url_document.jsp/1001 ?>&nbsp;</td>
	<td><input type="text" name="<%= DocumentInformation.PARAMETER__URL_DOC__URL %>" size="62" maxlength="255" value=""></td>
</tr>
<tr>
	<td colspan="2"><script>hr("100%",396,"cccccc");</script></td>
</tr>
<tr>
	<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/url_document.jsp/1002 ?><img src="@imcmsimageurl@/admin/1x1.gif" width="1" height="22"></td>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
        <% String target = document.getTarget() ; %>
		<td><input type="radio" name="target" value="_self"<% if ("_self".equalsIgnoreCase( target )) {%> checked<% target = null; }%>></td>
		<td class="imcmsAdmText" nowrap>&nbsp;<? install/htdocs/sv/jsp/docadmin/url_document.jsp/1003 ?> &nbsp;</td>
		<td><input type="radio" name="target" value="_blank"<% if ("_blank".equalsIgnoreCase( target )) {%> checked<% target = null; }%>></td>
		<td class="imcmsAdmText" nowrap>&nbsp;<? install/htdocs/sv/jsp/docadmin/url_document.jsp/1004 ?> &nbsp;</td>
		<td><input type="radio" name="target" value="_top"<% if ("_top".equalsIgnoreCase( target )) {%> checked<% target = null; }%>></td>
		<td class="imcmsAdmText" nowrap>&nbsp;<? install/htdocs/sv/jsp/docadmin/url_document.jsp/1005 ?> &nbsp;</td>
	</tr>
	<tr>
		<td><input type="radio" name="target" value="_other"<% if (null != target) {%> checked<%}%>></td>
		<td class="imcmsAdmText" nowrap>&nbsp;<? install/htdocs/sv/jsp/docadmin/url_document.jsp/1006 ?> &nbsp;</td>
		<td colspan="4">
            <input type="text" name="target" size="17" maxlength="50"
                value="<% if (null != target) {%><%= StringEscapeUtils.escapeHtml( target ) %><%}%>">
        </td>
	</tr>
	</table></td>
</tr>
<tr>
	<td colspan="2"><script>hr("100%",396,"blue");</script></td>
</tr>
<tr>
	<td colspan="2" align="right">
	<input type="submit" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2004 ?>" name="ok">
	<input type="reset" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2005 ?>">
	<input type="submit" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2006 ?>" name="cancel"></td>
</tr>
</form>
</table>
<script>
imcmsGui("bottom", null);
imcmsGui("outer_end", null);
</script>

</body>
</html>
