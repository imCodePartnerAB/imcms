<%@ page import="com.imcode.imcms.flow.Page,
                 com.imcode.imcms.servlet.DocumentFinder,
                 com.imcode.imcms.servlet.SearchDocumentsPage,
                 imcode.util.jscalendar.JSCalendar,
                 org.apache.commons.lang.StringEscapeUtils"
        contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%
    SearchDocumentsPage searchDocumentsPage = Page.fromRequest(request);
    DocumentFinder documentFinder = searchDocumentsPage.getDocumentFinder() ;
    JSCalendar jsCalendar = searchDocumentsPage.getJSCalender(request);

%>
<html style="height:100%;">
<head>
<title><? templates/sv/search/search_documents.html/1 ?></title>

    <link rel="stylesheet" href="${contextPath}/dist/imcms_admin.css" type="text/css">
    <script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>
<%= jsCalendar.getHeadTagScripts() %>

<style type="text/css">
BODY {
	margin: 0 !important;
	padding: 0 !important;
}
#container {
	margin: 0;
	padding: 30px 10px;
}
</style>

<script type="text/javascript">
function addScrolling() {
	if (window.opener) {
		var obj = document.getElementById("container") ;
		obj.style.height = "100%" ;
		obj.style.overflow = "scroll" ;
	}
}
</script>

</head>

<body bgcolor="#FFFFFF" onload="addScrolling(); document.forms[1].<%= StringEscapeUtils.escapeJavaScript(SearchDocumentsPage.REQUEST_PARAMETER__QUERY_STRING) %>.focus()">
<div id="container">
    <ui:imcms_gui_outer_start/>
    <c:set var="heading">
        <fmt:message key="templates/sv/search/search_documents.html/1"/>
    </c:set>
    <ui:imcms_gui_head heading="${heading}"/>

<form method="GET" action="<%= request.getContextPath() %>/servlet/SearchDocuments">
<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <% if (documentFinder.isCancelable()) { %>
            <td><input class="imcmsFormBtn" type="submit" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__CANCEL_BUTTON %>" value="<? global/back ?>">&nbsp;</td>
        <% } %>
        <td><input type="button" value="<? global/help ?>" title="<? global/openthehelppage ?>" class="imcmsFormBtn" onClick="openHelpW('MyPagesSearch')"></td>
    </tr>
</table>
    <ui:imcms_gui_mid/>
<jsp:include page="search_documents_form.jsp" />
</form>


<jsp:include page="search_documents_results.jsp" />
    <ui:imcms_gui_bottom/>
    <ui:imcms_gui_outer_end/>
	<div>&nbsp;</div>
</div>
</body>
</html>
