<%@ page import="com.imcode.imcms.mapping.DefaultDocumentMapper,
                 com.imcode.imcms.mapping.DocumentMapper,
                 com.imcode.imcms.servlet.superadmin.ListDocuments,
                 imcode.server.Imcms,
                 imcode.server.document.DocumentComparators,
                 imcode.server.document.DocumentDomainObject,
                 imcode.server.document.textdocument.TextDocumentDomainObject,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility,
                 org.apache.commons.lang3.ObjectUtils,
                 org.apache.commons.text.StringEscapeUtils,
                 java.net.URLEncoder" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<% ListDocuments.FormData formData = (ListDocuments.FormData) request.getAttribute(ListDocuments.REQUEST_ATTRIBUTE__FORM_DATA);%>

<c:set var="heading">
    <fmt:message key="imcms/lang/jsp/document_list.jsp/title"/>
</c:set>
<ui:imcms_gui_start_of_page titleAndHeading="${heading}"/>

<table border="0" cellspacing="0" cellpadding="2" width="680">
    <tr>
        <td>
            <table border="0" cellspacing="0" cellpadding="0">
                <form method="GET" action="ListDocuments">
                    <tr>
                        <td><fmt:message key="imcms/lang/jsp/document_list.jsp/1003"/></td>
                        <td>&nbsp;&nbsp;</td>
                        <td><input type="text" name="<%= ListDocuments.PARAMETER__LIST_START %>"
                                   value="<%= formData.selectedRange.getMinimumInteger() %>" size="6"></td>
                        <td>&nbsp;&nbsp;</td>
                        <td><fmt:message key="imcms/lang/jsp/document_list.jsp/1004"/></td>
                        <td>&nbsp;&nbsp;</td>
                        <td><input type="text" name="<%= ListDocuments.PARAMETER__LIST_END %>"
                                   value="<%= formData.selectedRange.getMaximumInteger() %>" size="6"></td>
                        <td>&nbsp;&nbsp;</td>
                        <td><input type="submit" class="imcmsFormBtnSmall"
                                   name="<%= ListDocuments.PARAMETER_BUTTON__LIST %>"
                                   value=" <fmt:message key="imcms/lang/jsp/document_list.jsp/2002"/> "></td>
                    </tr>
                </form>
            </table>
        </td>
    </tr>
    <tr>
        <td><ui:imcms_gui_hr wantedcolor="blue"/></td>
    </tr>
</table>
<%
    if (null != formData.documentsIterator) { %>

<table border="0" cellspacing="0" cellpadding="2" width="680">
    <tr>
        <td><b><fmt:message key="global/Page_alias"/>&nbsp;</b></td>
        <td><b><fmt:message key="webapp/imcms/lang/jsp/heading_status"/>&nbsp;</b></td>
        <td><b><fmt:message key="webapp/imcms/lang/jsp/heading_type"/></b></td>
        <td><b><fmt:message key="webapp/imcms/lang/jsp/heading_adminlink"/></b></td>
        <td><b><fmt:message key="webapp/imcms/lang/jsp/heading_references"/></b></td>
        <td>&nbsp; <b><fmt:message key="imcms/lang/jsp/document_list.jsp/heading_child_documents"/></b></td>
    </tr>
    <%

        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        UserDomainObject user = Utility.getLoggedOnUser(request);
        Map documentTypes = documentMapper.getAllDocumentTypeIdsAndNamesInUsersLanguage(user);

        while (formData.documentsIterator.hasNext()) {
            DocumentDomainObject document = (DocumentDomainObject) formData.documentsIterator.next();
            DefaultDocumentMapper.TextDocumentMenuIndexPair[] documentMenuPairsContainingDocument = documentMapper.getDocumentMenuPairsContainingDocument(document); %>
    <tr>
        <td colspan="6"><img src="${contextPath}/imcms/${language}/images/admin/1x1_cccccc.gif" width="100%" height="1">
        </td>
    </tr>
    <tr valign="top"><%
        String alias = document.getAlias();
        if (alias != null) {
    %>
        <td><a name="alias"
               href="${contextPath}/<%= document.getAlias() %>"><%= StringEscapeUtils.escapeHtml4(document.getAlias()) %>
        </a></td>
        <% } else { %>
        <td>&nbsp;</td>
        <%}%>
        <td><ui:statusIcon lifeCyclePhase="<%=document.getLifeCyclePhase()%>"/></td>
        <td nowrap><%= StringEscapeUtils.escapeHtml4((String) documentTypes.get(document.getDocumentTypeId()))%>&nbsp;</td>
        <td>
            <a name="<%= document.getId() %>" href="${contextPath}/servlet/AdminDoc?meta_id=<%= document.getId() %>"><%=
            document.getId() %> - <%= StringEscapeUtils.escapeHtml4(document.getHeadline()) %>
            </a>
        </td>
        <td nowrap><%
            if (documentMenuPairsContainingDocument.length > 0) {
                String backUrl = "ListDocuments?" + ObjectUtils.defaultIfNull(request.getQueryString(), "");
                String escapedBackUrl = URLEncoder.encode(backUrl, Imcms.UTF_8_ENCODING); %>
            <a href="<%= request.getContextPath() %>/servlet/DocumentReferences?returnurl=<%= escapedBackUrl %>&id=<%= document.getId() %>"><%
                } %><%= documentMenuPairsContainingDocument.length %> <fmt:message
                    key="webapp/imcms/lang/jsp/parent_count_unit"/><%
                if (documentMenuPairsContainingDocument.length > 0) {
            %></a><%
                } %></td>
        <td><%
            if (document instanceof TextDocumentDomainObject) {
                TextDocumentDomainObject textDocument = (TextDocumentDomainObject) document;
                List<DocumentDomainObject> childDocuments = documentMapper.getDocuments(textDocument.getChildDocumentIds());
                if (!childDocuments.isEmpty()) { %>
            <table border="0" cellpadding="2" cellspacing="0"><%
                Collections.sort(childDocuments, DocumentComparators.ID);
                for (DocumentDomainObject childDocument : childDocuments) {
            %>
                <tr valign="top">
                    <td>&nbsp;<b>&#149;</b>&nbsp;</td>
                    <td>
                        <a href="<%="ListDocuments?"+ListDocuments.PARAMETER__LIST_START + "=" + childDocument.getId() + "&" + ListDocuments.PARAMETER__LIST_END +"=" + childDocument.getId()%>"><%=
                        childDocument.getId() %> - <%=StringEscapeUtils.escapeHtml4(childDocument.getHeadline()) %>
                        </a></td>
                </tr>
                <%
                    } %>
            </table>
            <%
                    }
                } %></td>
    </tr>
    <%
        } %>
    <tr>
        <td colspan="6"><ui:imcms_gui_hr wantedcolor="blue"/></td>
    </tr>
    <form method="get" action="AdminManager">
        <tr>
            <td colspan="6" align="right"><input type="submit" class="imcmsFormBtn" name=""
                                                 value="<fmt:message key="global/cancel"/>">
            </td>
        </tr>
    </form>
</table>
<%
    } %>
<ui:imcms_gui_end_of_page/>
