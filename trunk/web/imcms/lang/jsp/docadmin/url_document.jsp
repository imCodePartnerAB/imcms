<%@ page import="com.imcode.imcms.flow.DocumentPageFlow,
                 com.imcode.imcms.flow.EditDocumentPageFlow,
                 com.imcode.imcms.flow.EditUrlDocumentPageFlow,
                 com.imcode.imcms.flow.PageFlow,
                 imcode.server.document.UrlDocumentDomainObject,
                 imcode.util.HttpSessionUtils,
                 org.apache.commons.lang.ObjectUtils,
                 org.apache.commons.lang.StringEscapeUtils" contentType="text/html; charset=UTF-8"%>
<%@taglib prefix="vel" uri="imcmsvelocity"%><%
    DocumentPageFlow httpFlow = DocumentPageFlow.fromRequest(request) ;
    UrlDocumentDomainObject document = (UrlDocumentDomainObject)httpFlow.getDocument() ;
%>
<vel:velocity>
<html>
<head>
<title><? install/htdocs/sv/jsp/docadmin/url_document.jsp/1 ?></title>
<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'url_ref')">
#gui_outer_start()
#gui_head("<? global/imcms_administration ?>")
<table border="0" cellspacing="0" cellpadding="0">
<form action="DocumentPageFlowDispatcher">
<tr>
	<td><input type="submit" name="cancel" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2001 ?>"></td>
	<td>&nbsp;</td>
	<td><input type="button" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2002 ?>" title="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2003 ?>" class="imcmsFormBtn" onClick="openHelpW('LinkExternal')"></td>
</tr>
</table>
#gui_mid()
<table border="0" cellspacing="0" cellpadding="2" width="400">
<input type="hidden" name="<%= PageFlow.REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW %>"
    value="<%= HttpSessionUtils.getSessionAttributeNameFromRequest(request,PageFlow.REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW) %>">
<input type="hidden" name="<%= PageFlow.REQUEST_PARAMETER__PAGE %>"
    value="<%= EditDocumentPageFlow.PAGE__EDIT %>">
<tr>
	<td colspan="2">
        #gui_heading( "<? install/htdocs/sv/jsp/docadmin/url_document.jsp/4/1 ?>" )
    </td>
</tr>
<tr>
	<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/url_document.jsp/1001 ?>&nbsp;</td>
	<td><input type="text" name="<%= EditUrlDocumentPageFlow.REQUEST_PARAMETER__URL_DOC__URL %>" size="62" maxlength="255"
                value="<%= StringEscapeUtils.escapeHtml( (String)ObjectUtils.defaultIfNull( document.getUrl(), "" )) %>"></td>
</tr>
<tr>
	<td colspan="2">
        #gui_hr( "cccccc" )
    </td>
</tr>
<tr>
	<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/url_document.jsp/1002 ?><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="22"></td>
	<td>
        <% request.setAttribute( "target", document.getTarget() );%>
        <jsp:include page="target.jsp"/>
    </td>
</tr>
<tr>
	<td colspan="2">
        #gui_hr( "blue" )
    </td>
</tr>
<tr>
	<td colspan="2" align="right">
	<input type="submit" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2004 ?>" name="ok" onClick="return singleclicked();">
	<input type="reset" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2005 ?>">
	<input type="submit" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/url_document.jsp/2006 ?>" name="cancel"></td>
</tr>
</form>
</table>
#gui_bottom()
#gui_outer_end()
</vel:velocity>
</body>
</html>
