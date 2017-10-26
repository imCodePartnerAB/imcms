<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<html>
<head>
    <title><fmt:message key="templates/sv/AdminIpAccess_Delete2.htm/1"/></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css.jsp">
    <script src="${contextPath}/js/imcms/imcms_admin.js.jsp" type="text/javascript"></script>

</head>

<body>

<ui:imcms_gui_outer_start/>
#gui_head( "<? global/imcms_administration ?>" )
#gui_mid()

<form method="post" action="AdminIpAccess" name="AdminIPAccess">
    <table border="0" cellspacing="0" cellpadding="2" width="400">
        <tr>
            <td>#gui_heading( "<? global/warning ?>" )</td>
        </tr>
        <tr>
            <td><? templates/sv/AdminIpAccess_Delete2.htm/3 ?></td>
        </tr>
        <tr>
            <td>#gui_hr( "blue" )</td>
        </tr>
        <tr>
            <td align="right">
                <input type="submit" class="imcmsFormBtn" name="DEL_IP_ACCESS"
                       value="<? templates/sv/AdminIpAccess_Delete2.htm/2001 ?>">
                <input type="submit" class="imcmsFormBtn" name="IP_CANCEL_DELETE" value="<? global/cancel ?>"></td>
        </tr>
    </table>
</form>

<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>

</body>
</html>
