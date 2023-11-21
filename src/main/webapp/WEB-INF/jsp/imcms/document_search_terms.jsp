<%@ page
        import="com.imcode.imcms.flow.OkCancelPage, com.imcode.imcms.servlet.superadmin.AdminSearchTerms, java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>

<% List<AdminSearchTerms.TermCount> termCounts = (List<AdminSearchTerms.TermCount>) request.getAttribute("termCounts"); %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<html>
<head>
    <title><fmt:message key="webapp/imcms/lang/jsp/document_search_terms.jsp/title"/></title>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/dist/imcms_admin.css">
    <link rel="stylesheet" type="text/css" media="all"
          href="<%=request.getContextPath()%>/js/jscalendar/skins/aqua/theme.css.jsp"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jscalendar/calendar.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jscalendar/lang/calendar-eng.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jscalendar/calendar-setup.js"></script>
</head>
<body>
<form action="<%= request.getContextPath() %>/servlet/AdminSearchTerms" method="POST">
    <ui:imcms_gui_outer_start/>
    <c:set var="heading">
        <fmt:message key="webapp/imcms/lang/jsp/document_search_terms.jsp/title"/>
    </c:set>
    <ui:imcms_gui_head heading="${heading}"/>
    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <input type="SUBMIT" class="imcmsFormBtn" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>"
                       value="<fmt:message key="global/back"/>">
            </td>
        </tr>
    </table>
    <ui:imcms_gui_mid/>
    <table border="0" cellspacing="0" cellpadding="0">
        <ui:labeled idref="from_date" key="webapp/imcms/lang/jsp/document_search_terms.jsp/daterange">
            &nbsp;
            <ui:datetime dateid="from_date" value="${fromDate}"/>-<ui:datetime dateid="to_date" value="${toDate}"/>
        </ui:labeled>
        <ui:separator/>
        <tr>
            <td colspan="2" align="right">
                <ui:submit name="search">
                    <jsp:attribute name="value"><fmt:message key="global/Search"/></jsp:attribute>
                </ui:submit>
            </td>
        </tr>
    </table>
    <%
        if (null != termCounts) {
    %>
    <ui:imcms_gui_hr wantedcolor="blue"/>
    <table border="0" width="100%">
        <tr>
            <th><fmt:message key="webapp/imcms/lang/jsp/document_search_terms.jsp/term"/></th>
            <th><fmt:message key="webapp/imcms/lang/jsp/document_search_terms.jsp/count"/></th>
        </tr>
        <%
            for (AdminSearchTerms.TermCount termCount : termCounts) {
        %>
        <tr>
            <td><%= termCount.getTerm() %>
            </td>
            <td><%= termCount.getCount() %>
            </td>
        </tr>
        <%
            }
        %></table>
    <%
        }
    %>

    <ui:imcms_gui_bottom/>
    <ui:imcms_gui_outer_end/>
</form>
</body>
</html>
