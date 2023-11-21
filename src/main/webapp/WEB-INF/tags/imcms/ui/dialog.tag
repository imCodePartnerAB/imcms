<%@ tag import="com.imcode.imcms.flow.OkCancelPage, com.imcode.imcms.flow.Page" %>
<%@ tag import="org.apache.logging.log4j.LogManager" %>
<%@
        attribute name="titlekey" required="true" %>
<%@
        attribute name="helpid" required="true" %>
<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@
        taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@
        taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<html>
<%
    try {
%>
<head>
    <title><fmt:message key="${titlekey}"/></title>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/dist/imcms_admin.css">
    <link rel="stylesheet" type="text/css" media="all"
          href="<%=request.getContextPath()%>/js/jscalendar/skins/aqua/theme.css.jsp"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jscalendar/calendar.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jscalendar/lang/calendar-eng.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jscalendar/calendar-setup.js"></script>
</head>
<body>
<form action="<%= request.getContextPath() %>/servlet/PageDispatcher">
    <%= Page.htmlHidden(request) %>

    <ui:imcms_gui_outer_start/>
    <c:set var="heading">
        <fmt:message key="${titlekey}"/>
    </c:set>
    <ui:imcms_gui_head heading="${heading}"/>
    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><input type="SUBMIT" class="imcmsFormBtn"
                                   name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>"
                                   value="<fmt:message key="global/back"/>"></td>
                        <td>&nbsp;</td>
                        <td><input type="button" value="<fmt:message key="global/help"/>"
                                   title="<fmt:message key="global/openthehelppage"/>" class="imcmsFormBtn"
                                   onClick="openHelpW('<c:out value="${helpid}"/>')"></td>
                    </tr>
                </table>
            </td>
            <td>&nbsp;</td>
        </tr>
    </table>
    <ui:imcms_gui_mid/>

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

    <ui:imcms_gui_bottom/>
    <ui:imcms_gui_outer_end/>

</form>
</body>
</html>
<%
    } catch (Exception e) {
        LogManager.getLogger("jsp").error("Exception in dialog.tag.", e);
        throw e;
    }
%>
