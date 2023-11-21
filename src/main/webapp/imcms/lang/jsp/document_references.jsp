<%@ page import="com.imcode.imcms.mapping.DefaultDocumentMapper,
                 com.imcode.imcms.servlet.admin.AdminDoc,
                 com.imcode.imcms.servlet.superadmin.DocumentReferences,
                 imcode.server.ImcmsConstants,
                 imcode.server.document.textdocument.TextDocumentDomainObject,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility,
                 org.apache.commons.lang3.StringEscapeUtils" %>
<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    UserDomainObject user = Utility.getLoggedOnUser(request);
    if (!user.isSuperAdmin()) {
        return;
    }
    DefaultDocumentMapper.TextDocumentMenuIndexPair[] documentMenuPairs = (DefaultDocumentMapper.TextDocumentMenuIndexPair[]) request.getAttribute(DocumentReferences.REQUEST_ATTRIBUTE__DOCUMENT_MENU_PAIRS);
%>
<html>
<head>
    <title><fmt:message key="webapp/imcms/lang/jsp/document_references.jsp/heading"/></title>
    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">
    <%--<script type="text/javascript" src="${contextPath}/imcms/scripts/imcms_api.js"></script>--%>
</head>
<body>
<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="webapp/imcms/lang/jsp/document_references.jsp/heading"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>
<table border="0" cellspacing="0" cellpadding="0">
    <form method="GET" action="DocumentReferences">
        <input type="hidden" name="<%= DocumentReferences.REQUEST_PARAMETER__RETURNURL %>"
               value="<%= request.getParameter(DocumentReferences.REQUEST_PARAMETER__RETURNURL) %>">
        <tr>
            <td><input type="submit" class="imcmsFormBtn"
                       name="<%= DocumentReferences.REQUEST_PARAMETER__BUTTON_RETURN %>"
                       value="<fmt:message key="global/back"/>"></td>
        </tr>
    </form>
</table>
<ui:imcms_gui_mid/>
<table border="0" cellspacing="0" cellpadding="2" width="400">
    <c:set var="heading">
        <fmt:message key="webapp/imcms/lang/jsp/document_references.jsp/explanation"/>
    </c:set>
    <tr>
        <td colspan="2"><ui:imcms_gui_heading heading="${heading}"/></td>
    </tr>
    <tr>
        <td width="15%" align="center"><b><fmt:message key="webapp/imcms/lang/jsp/heading_status"/>&nbsp;</b></td>
        <td width="85%"><b><fmt:message key="webapp/imcms/lang/jsp/heading_adminlink"/></b></td>
    </tr>
    <%
        for (int i = 0; i < documentMenuPairs.length; i++) {
            DefaultDocumentMapper.TextDocumentMenuIndexPair textDocumentMenuIndexPair = documentMenuPairs[i];
            TextDocumentDomainObject textDocument = textDocumentMenuIndexPair.getDocument();
            int menuIndex = textDocumentMenuIndexPair.getMenuIndex();
    %>
    <tr>
        <td colspan="2"><img
                src="${contextPath}/imcms/${language}/images/admin/1x1_cccccc.gif" width="100%" height="1" vspace="4">
        </td>
    </tr>
    <tr>
        <td align="center"><ui:statusIcon lifeCyclePhase="<%=textDocument.getLifeCyclePhase()%>"/></td>
        <td>
            <a href="<%= request.getContextPath() %>/servlet/AdminDoc?meta_id=<%= textDocument.getId() %>&<%= AdminDoc.PARAMETER__DISPATCH_FLAGS %>=<%= ImcmsConstants.DISPATCH_FLAG__EDIT_MENU %>&editmenu=<%= menuIndex %>">
                <%= textDocument.getId() %>: "<%= StringEscapeUtils.escapeHtml4(textDocument.getHeadline()) %>"
                -
                <fmt:message key="webapp/imcms/lang/jsp/document_references.jsp/heading_menu"/> <%= menuIndex %>
            </a>
        </td>
    </tr>
    <% } %>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>
</body>
</html>
