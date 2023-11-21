<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title><fmt:message key="templates/sv/AdminIpAccess_Add.htm/2"/></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">
    <script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>

</head>

<body>

<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="global/imcms_administration"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>
<ui:imcms_gui_mid/>

<form method="post" action="AdminIpAccess" name="addIP">
    <table border="0" cellspacing="0" cellpadding="2" width="400">
        <tr>
            <td colspan="2">
                <c:set var="heading">
                    <fmt:message key="templates/sv/AdminIpAccess_Add.htm/2"/>
                </c:set>
                <ui:imcms_gui_heading heading="${heading}"/>
            </td>
        </tr>
        <tr>
            <td colspan="2"><? templates/sv/AdminIpAccess_Add.htm/3 ?></td>
        </tr>
        <tr>
            <td colspan="2"><ui:imcms_gui_hr wantedcolor="cccccc"/></td>
        </tr>
        <tr>
            <td><? templates/sv/AdminIpAccess_Add.htm/4 ?></td>
            <td><select name="USER_ID" size="1">${USERS_LIST}</select></td>
        </tr>
        <tr>
            <td><? templates/sv/AdminIpAccess_Add.htm/6 ?></td>
            <td><input type="text" name="IP_START" size="15" maxlength="15"></td>
        </tr>
        <tr>
            <td><? templates/sv/AdminIpAccess_Add.htm/7 ?></td>
            <td><input type="text" name="IP_END" size="15" maxlength="15"></td>
        </tr>
        <tr>
            <td colspan="2"><ui:imcms_gui_hr wantedcolor="cccccc"/></td>
        </tr>
        <tr>
            <td colspan="2"><? templates/sv/AdminIpAccess_Add.htm/8 ?></td>
        </tr>
        <tr>
            <td colspan="2"><ui:imcms_gui_hr wantedcolor="blue"/></td>
        </tr>
        <tr>
            <td colspan="2" align="right">
                <input type="submit" class="imcmsFormBtn" name="ADD_NEW_IP_ACCESS"
                       value="<? templates/sv/AdminIpAccess_Add.htm/2001 ?>">
                <input type="submit" class="imcmsFormBtn" name="CANCEL_ADD_IP"
                       value="<? templates/sv/AdminIpAccess_Add.htm/2002 ?>"></td>
        </tr>
    </table>
</form>

<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>

</body>
</html>
