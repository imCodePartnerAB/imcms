<%@ page import="com.imcode.imcms.servlet.SearchDocuments,
                 imcode.server.Imcms,
                 imcode.server.document.SectionDomainObject,
                 org.apache.commons.lang.StringEscapeUtils,
                 imcode.util.Html,
                 org.apache.commons.collections.Transformer,
                 imcode.server.document.DocumentDomainObject,
                 imcode.util.Utility,
                 imcode.server.user.UserDomainObject,
                 org.apache.commons.lang.StringUtils,
                 java.util.*,
                 org.apache.commons.collections.set.ListOrderedSet,
                 java.net.URLEncoder,
                 imcode.util.HttpSessionUtils,
                 com.imcode.imcms.servlet.DocumentFinder,
                 com.imcode.imcms.servlet.SearchDocumentsPage,
                 imcode.util.Html,
                 imcode.server.document.DocumentMapper,
                 com.imcode.imcms.servlet.superadmin.AdminManager,
                 com.imcode.imcms.flow.Page"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<%
    SearchDocumentsPage searchDocumentsPage = (SearchDocumentsPage) Page.fromRequest(request) ;
    DocumentFinder documentFinder = searchDocumentsPage.getDocumentFinder() ;
%>
<vel:velocity>
<html>
<head>
<title><? templates/sv/search/search_documents.html/1 ?></title>

<link rel="stylesheet" href="$contextPath/imcms/css/imcms_admin.css.jsp" type="text/css">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js" type="text/javascript"></script>

</head>

<body bgcolor="#FFFFFF" onLoad="document.forms[0].<%= StringEscapeUtils.escapeJavaScript(SearchDocumentsPage.REQUEST_PARAMETER__QUERY_STRING) %>.focus()">
#gui_outer_start()
#gui_head( "<? templates/sv/search/search_documents.html/1 ?>" )

<form method="GET" action="SearchDocuments">

<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <% if (documentFinder.isCancelable()) { %>
            <td><input class="imcmsFormBtn" type="submit" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__CANCEL_BUTTON %>" value="<? global/back ?>">&nbsp;</td>
        <% } %>
        <td><input type="button" value="<? global/help ?>" title="<? global/openthehelppage ?>" class="imcmsFormBtn" onClick="openHelpW(101)"></td>
    </tr>
</table>

#gui_mid()
<jsp:include page="search_documents_form.jsp" />
</form>


<jsp:include page="search_documents_results.jsp" />
#gui_end_of_page()
</vel:velocity>
