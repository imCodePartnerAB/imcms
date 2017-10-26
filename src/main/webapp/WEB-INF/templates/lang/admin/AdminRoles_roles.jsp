<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<ui:imcms_gui_start_of_page titleAndHeading="templates/sv/AdminRoles_roles.htm/1"/>


<form method="post" action="AdminRoles" name="UserInfo">
<table border="0" cellspacing="0" cellpadding="2" width="400">
<tr>
    <td>
        <c:set var="heading">
            <fmt:message key="templates/sv/AdminRoles_roles.htm/1"/>
        </c:set>
        <ui:imcms_gui_heading heading="${heading}"/>
    </td>
</tr>
<tr>
    <td>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr valign="top">
                <td class="imcmsAdmText" nowrap><? templates/sv/AdminRoles_roles.htm/2 ?> &nbsp;</td>
                <td>
                    <select name="ROLE_ID" size="10">
                        #ROLES_MENU#
                    </select></td>
            </tr>
        </table></td>
</tr>
<tr>
    <td><ui:imcms_gui_hr wantedcolor="blue"/></td>
</tr>
<tr>
    <td>
        <input type="submit" class="imcmsFormBtnSmall" name="VIEW_ADD_NEW_ROLE" value="<? templates/sv/AdminRoles_roles.htm/2001 ?>">
        <input type="submit" class="imcmsFormBtnSmall" name="VIEW_RENAME_ROLE" value="<? templates/sv/AdminRoles_roles.htm/2002 ?>">
        <input type="submit" class="imcmsFormBtnSmall" name="VIEW_EDIT_ROLE" value="<? templates/sv/AdminRoles_roles.htm/2003 ?>">
        <input type="submit" class="imcmsFormBtnSmall" name="VIEW_DELETE_ROLE" value="<? templates/sv/AdminRoles_roles.htm/2004 ?>"></td>
</tr>
</table>
</form>

<ui:imcms_gui_end_of_page/>
<ui:imcms_gui_end_of_page/>


