<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>

<ui:imcms_gui_start_of_page titleAndHeading="templates/sv/AdminRoles_Delete2.htm/1"/>

<form method="post" action="AdminRoles" name="adminRoles">
    <table width="400" border="0" cellspacing="0">
        <tr>
            <td colspan="2" class="imcmsAdmText"><span style='color:red'><b><? templates/sv/AdminRoles_Delete2.htm/2 ?></b></span>
                <input type="HIDDEN" name="ROLE_ID" value="#CURRENT_ROLE_ID#"></td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td colspan="2" class="imcmsAdmText"><font face="Verdana, Arial, Helvetica, sans-serif">
                <? templates/sv/AdminRoles_Delete2.htm/3 ?></font></td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td colspan="2"><ui:imcms_gui_hr wantedcolor="blue"/></td>
        </tr>
        <tr>
            <td colspan="2" align="right">
                <input type="submit" name="DELETE_ROLE" class="imcmsFormBtn" value="<? global/OK ?>">&nbsp;
                <input type="submit" name="CANCEL_ROLE" class="imcmsFormBtn" value="<? global/cancel ?>"></td>
        </tr>
    </table>
</form>
<ui:imcms_gui_end_of_page/>