<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
#gui_start_of_page( "<? templates/sv/AdminRoles_Add.htm/1 ?>" "AdminRoles" "CANCEL_ROLE" "RoleAdd" "" )

<table width="400" border="0" cellspacing="0">
<form method="post" action="AdminRoles" name="addIP">
<tr>
    <td colspan="2">
        <c:set var="heading">
            <fmt:message key="templates/sv/AdminRoles_Add.htm/1"/>
        </c:set>
        <ui:imcms_gui_heading heading="${heading}"/>
    </td>
</tr>
<tr>
    <td colspan="2" class="imcmsAdmText">
        <? templates/sv/AdminRoles_Add.htm/3 ?></td>
</tr>
<tr>
<td colspan="2"><input type="text" name="ROLE_NAME" size="40" maxlength="60"></td>
</tr>
<tr>
    <td colspan="2">#ROLE_PERMISSIONS#</td>
</tr>
<tr>
    <td colspan="2"><ui:imcms_gui_hr wantedcolor="blue"/></td>
</tr>
<tr>
    <td colspan="2" align="right">
        <input type="submit" class="imcmsFormBtn" name="ADD_NEW_ROLE" value="<? global/save ?>">&nbsp;
        <input type="submit" class="imcmsFormBtn" name="CANCEL_ROLE" value="<? global/cancel ?>"> </td>
</tr>
</form>
</table>
<ui:imcms_gui_end_of_page/>