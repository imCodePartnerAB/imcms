<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<table width="100%" border="0">
<tr>
    <td colspan="2">&nbsp;<br>
        <c:set var="heading">
            <fmt:message key="templates/sv/AdminRoles_Edit_Permissions_List.html/1"/>
        </c:set>
        <ui:imcms_gui_heading heading="${heading}"/>
    </td>
</tr>
#PERMISSION_ROWS#
</table>
