<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title><? templates/sv/templategroup_delete_warning.html/1 ?></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">
    <script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF">

<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="templates/sv/templategroup_delete_warning.html/1"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>
<table border="0" cellpadding="0" cellspacing="0">
    <form>
        <tr>
            <td><input type="button" value="<? templates/sv/templategroup_delete_warning.html/2001 ?>"
                       class="imcmsFormBtn" onClick="history.go(-1); return false"></td>
            <td>&nbsp;</td>
            <td><input type="button" value="<? templates/sv/templategroup_delete_warning.html/2002 ?>"
                       title="<? templates/sv/templategroup_delete_warning.html/2003 ?>" class="imcmsFormBtn"
                       onClick="openHelpW('TemplateGroupRemove')"></td>
        </tr>
    </form>
</table>
<ui:imcms_gui_mid/>

<table border="0" cellspacing="0" cellpadding="2" width="310">
    <form method="post" action="TemplateChange">
        <input type="HIDDEN" name="templategroup" value="${templategroup}">
        <input type="HIDDEN" name="group_delete_cancel" value="Avbryt">
        <tr>
            <td align="center" class="imcmsAdmText">
                <b><? templates/sv/templategroup_delete_documents_assigned_warning.html/1 ?></b>
                <blockquote>${templates}</blockquote>
            </td>
        </tr>
        <tr>
            <td><ui:imcms_gui_hr wantedcolor="blue"/></td>
        </tr>
        <tr>
            <td align="right">
                <input type="submit" class="imcmsFormBtn" name="group_delete_cancel"
                       value="<? templates/sv/templategroup_delete_warning.html/2005 ?>"></td>
        </tr>

    </form>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>


</body>
</html>

