<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<c:set var="heading">
    <fmt:message key="templates/sv/AdminRoles_Rename.htm/1"/>
</c:set>
<ui:imcms_gui_start_of_page titleAndHeading="${heading}"/>

<table width="400" border="0" cellspacing="0">
    <form method="post" action="AdminRoles" name="addIP">
        <input type="HIDDEN" name="ROLE_ID" value="${CURRENT_ROLE_ID}">
        <tr>
            <td class="imcmsAdmText">
                <c:set var="heading">
                    <fmt:message key="templates/sv/AdminRoles_Rename.htm/3"/> &nbsp; &nbsp; <i>${CURRENT_ROLE_NAME}</i>
                </c:set>
                <ui:imcms_gui_heading heading="${heading}"/>
            </td>
        </tr>
        <tr>
            <td>
                <input type="text" name="ROLE_NAME" value="${CURRENT_ROLE_NAME}" size="40" maxlength="60"></td>
        </tr>
        <tr>
            <td><ui:imcms_gui_hr wantedcolor="blue"/></td>
        </tr>
        <tr>
            <td align="right">
                <input type="submit" class="imcmsFormBtn" name="RENAME_ROLE" value="<? global/save ?>">&nbsp;
                <input type="submit" class="imcmsFormBtn" name="CANCEL_ROLE" value="<? global/cancel ?>"></td>
        </tr>
    </form>
</table>

<ui:imcms_gui_end_of_page/>
