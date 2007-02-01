<%@ tag import="com.imcode.imcms.flow.OkCancelPage, com.imcode.imcms.flow.Page"%><%@
        attribute name="title"%><%@
        attribute name="helpid"%><%@
        taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@
        taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %><html>
<head>
<title><c:out value="${title}"/></title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/imcms/css/imcms_admin.css.jsp">
</head>
<body>
<form action="<%= request.getContextPath() %>/servlet/PageDispatcher">
<%= Page.htmlHidden(request) %>
<vel:velocity>
#gui_outer_start()
#gui_head( "<c:out value="${title}"/> " )
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
