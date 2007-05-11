<%@ page import="com.imcode.imcms.flow.DocumentPageFlow,
                 com.imcode.imcms.flow.EditDocumentPageFlow,
                 com.imcode.imcms.flow.EditHtmlDocumentPageFlow,
                 com.imcode.imcms.flow.PageFlow,
                 imcode.server.document.HtmlDocumentDomainObject,
                 imcode.util.HttpSessionUtils,
                 org.apache.commons.lang.ObjectUtils,
                 org.apache.commons.lang.StringEscapeUtils"
                contentType="text/html; charset=UTF-8"%>
<%@taglib prefix="vel" uri="imcmsvelocity"%>
<vel:velocity>
<html>
<head>
<title><? install/htdocs/sv/jsp/docadmin/html_document.jsp/1 ?></title>
<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'frame_set')">
#gui_outer_start()
#gui_head("<? global/imcms_administration ?>")
<table border="0" cellspacing="0" cellpadding="0">
<form method="POST" action="DocumentPageFlowDispatcher">
<tr>
	<td><input type="submit" class="imcmsFormBtn" name="cancel" value="<? install/htdocs/sv/jsp/docadmin/html_document.jsp/2001 ?>"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? install/htdocs/sv/jsp/docadmin/html_document.jsp/2002 ?>" title="<? install/htdocs/sv/jsp/docadmin/html_document.jsp/2003 ?>" class="imcmsFormBtn" onClick="openHelpW('LinkHTMLPage')"></td>
</tr>
</form>
</table>
#gui_mid()
<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<form method="POST" action="DocumentPageFlowDispatcher">
<%
    DocumentPageFlow httpFlow = DocumentPageFlow.fromRequest(request) ;
    HtmlDocumentDomainObject document = (HtmlDocumentDomainObject)httpFlow.getDocument() ;
%>
<input type="hidden" name="<%= PageFlow.REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW %>"
    value="<%= HttpSessionUtils.getSessionAttributeNameFromRequest(request,PageFlow.REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW) %>">
<input type="hidden" name="<%= PageFlow.REQUEST_PARAMETER__PAGE %>"
    value="<%= EditDocumentPageFlow.PAGE__EDIT %>">
<tr>
	<td>
        #gui_heading( "<? install/htdocs/sv/jsp/docadmin/html_document.jsp/6 ?>" )
    </td>
</tr>
<tr>
	<td><? install/htdocs/sv/jsp/docadmin/html_document.jsp/5 ?></td>
</tr>
<tr>
	<td><textarea name="<%= EditHtmlDocumentPageFlow.REQUEST_PARAMETER__HTML_DOC__HTML %>" cols="57" rows="16" wrap="virtual" style="width:100%; overflow:auto">
<%= StringEscapeUtils.escapeHtml( (String)ObjectUtils.defaultIfNull( document.getHtml(), "") ) %></textarea></td>
</tr>
<tr>
	<td>
        #gui_hr( "blue" )
    </td>
</tr>
<tr>
	<td align="right">
	<input type="submit" class="imcmsFormBtn" name="ok" value="<? install/htdocs/sv/jsp/docadmin/html_document.jsp/2004 ?>" onClick="return singleclicked();">
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
