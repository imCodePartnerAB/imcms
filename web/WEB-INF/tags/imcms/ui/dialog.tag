<%@ tag import="com.imcode.imcms.flow.OkCancelPage, com.imcode.imcms.flow.Page, org.apache.log4j.Logger"%><%@
        attribute name="titlekey" required="true" %><%@
        attribute name="helpid" required="true" %><%@
        taglib prefix="vel" uri="imcmsvelocity"%><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@
        taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %><html>
<%
    try {
%>
<head>
<title><fmt:message key="${titlekey}"/></title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/imcms/css/imcms_admin.css.jsp">
<link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/imcms/jscalendar/skins/aqua/theme.css.jsp" />
<script type="text/javascript" src="<%=request.getContextPath()%>/imcms/jscalendar/calendar.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/imcms/jscalendar/lang/calendar-eng.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/imcms/jscalendar/calendar-setup.js"></script>
</head>
<body>
<form action="<%= request.getContextPath() %>/servlet/PageDispatcher">
<%= Page.htmlHidden(request) %>
<vel:velocity>
#gui_outer_start()
#gui_head( "<fmt:message key="${titlekey}"/> " )
<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td>
        <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><input type="SUBMIT" class="imcmsFormBtn" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>" value="<fmt:message key="global/back"/>"></td>
            <td>&nbsp;</td>
            <td><input type="button" value="<fmt:message key="global/help"/>" title="<fmt:message key="global/openthehelppage"/>" class="imcmsFormBtn" onClick="openHelpW('<c:out value="${helpid}"/>')"></td>
        </tr>
        </table></td>
        <td>&nbsp;</td>
    </tr>
</table>
#gui_mid()
</vel:velocity>
<table border="0" cellspacing="0" cellpadding="0">
<jsp:doBody/>
<ui:separator/>
<tr>
    <td colspan="2" align="right">
        <ui:ok/>
        <ui:cancel/>
    </td>
</tr>
</table>
<vel:velocity>
#gui_bottom()
#gui_outer_end()
</vel:velocity>
</form>
</body>
</html>
<%
    } catch (Exception e) {
        Logger.getLogger("jsp").error("Exception in dialog.tag.", e);
        throw e;
    }
%>
