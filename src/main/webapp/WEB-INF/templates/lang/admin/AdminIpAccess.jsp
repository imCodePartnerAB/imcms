<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<c:set var="heading">
    <fmt:message key="templates/sv/AdminManager_adminTask_element.htm/7"/>
</c:set>
<ui:imcms_gui_start_of_page titleAndHeading="${heading}"/>

<form method="post" action="AdminIpAccess" name="argumentForm">
    <table border="0" cellspacing="0" cellpadding="2" width="400">
        <tr>
            <td>
                <c:set var="heading">
                    <fmt:message key="templates/sv/AdminIpAccess.htm/3000"/>
                </c:set>
                <ui:imcms_gui_heading heading="${heading}"/>
            </td>
        </tr>
        <tr>
            <td><? templates/sv/AdminIpAccess.htm/3 ?><br>
                &nbsp;
            </td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="2" width="100%">
                    <tr>
                        <td><b><? templates/sv/AdminIpAccess.htm/3001 ?></b></td>
                        <td><b><? templates/sv/AdminIpAccess.htm/3002 ?></b></td>
                        <td><b><? templates/sv/AdminIpAccess.htm/3003 ?></b></td>
                        <td>&nbsp;&nbsp;&nbsp;</td>
                        <td><b><? templates/sv/AdminIpAccess.htm/3004 ?></b></td>
                    </tr>
                    <tr>
                        <td colspan="5"><ui:imcms_gui_hr wantedcolor="cccccc"/></td>
                    </tr>
                    ${ALL_IP_ACCESSES}
                </table>
            </td>
        </tr>
        <tr>
            <td><ui:imcms_gui_hr wantedcolor="blue"/></td>
        </tr>
        <tr>
            <td align="right">
                <input type="submit" class="imcmsFormBtn" name="ADD_IP_ACCESS"
                       value="<? templates/sv/AdminIpAccess.htm/2001 ?>">
                <input type="submit" class="imcmsFormBtn" name="RESAVE_IP_ACCESS"
                       value="<? templates/sv/AdminIpAccess.htm/2002 ?>">
                <input type="submit" class="imcmsFormBtn" name="IP_WARN_DELETE"
                       value="<? templates/sv/AdminIpAccess.htm/2003 ?>"></td>
        </tr>
    </table>
</form>

<ui:imcms_gui_end_of_page/>
