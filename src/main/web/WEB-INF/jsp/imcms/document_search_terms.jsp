<%@ page import="com.imcode.imcms.servlet.superadmin.AdminSearchTerms, java.util.List, java.util.Date, imcode.util.Utility, com.imcode.imcms.flow.OkCancelPage"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %><%
    List<AdminSearchTerms.TermCount> termCounts = (List<AdminSearchTerms.TermCount>) request.getAttribute("termCounts");
%><%@taglib prefix="vel" uri="imcmsvelocity"%><%@
     taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
     taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@
     taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %><html>
<head>
<title><fmt:message key="web/imcms/lang/jsp/document_search_terms.jsp/title"/></title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/imcms/css/imcms_admin.css.jsp">
<link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/imcms/jscalendar/skins/aqua/theme.css.jsp" />
<script type="text/javascript" src="<%=request.getContextPath()%>/imcms/jscalendar/calendar.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/imcms/jscalendar/lang/calendar-eng.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/imcms/jscalendar/calendar-setup.js"></script>
</head>
<body>
<form action="<%= request.getContextPath() %>/servlet/AdminSearchTerms" method="POST">
<vel:velocity>
#gui_outer_start()
#gui_head( "<fmt:message key="web/imcms/lang/jsp/document_search_terms.jsp/title"/>" )
<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td>
            <input type="SUBMIT" class="imcmsFormBtn" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>" value="<fmt:message key="global/back"/>">
        </td>
    </tr>
</table>
#gui_mid()
</vel:velocity>
<table border="0" cellspacing="0" cellpadding="0">
<ui:labeled idref="from_date" key="web/imcms/lang/jsp/document_search_terms.jsp/daterange">
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
        %><vel:velocity>
        #gui_hr( "blue" )
        </vel:velocity>
        <table border="0" width="100%">
            <tr><th><fmt:message key="web/imcms/lang/jsp/document_search_terms.jsp/term"/></th><th><fmt:message key="web/imcms/lang/jsp/document_search_terms.jsp/count"/></th></tr><%
        for ( AdminSearchTerms.TermCount termCount : termCounts ) {
            %>
                <tr>
                    <td><%= termCount.getTerm() %></td>
                    <td><%= termCount.getCount() %></td>
                </tr>
            <%
        }
        %></table><%
    }
%>

<vel:velocity>
#gui_bottom()
#gui_outer_end()
</vel:velocity>
</form>
</body>
</html>
