<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>
<title><? templates/sv/inPage_admin_no_template.html/1 ?></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css.jsp">
    <script src="${contextPath}/js/imcms/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body onLoad="focusField(1, 'change_group')">

<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="templates/sv/FileAdminCopyOverwriteWarning.html/1"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>
<table border="0" cellspacing="0" cellpadding="0">
<form method="post" action="SaveInPage">
<input type="HIDDEN" name="meta_id" value="#meta_id#">
<tr>
    <td><input type="submit" class="imcmsFormBtn" name="change_group" value="<? global/back ?>"></td>
    <td>&nbsp;</td>
    <td><input type="button" class="imcmsFormBtn" value="<? global/help ?>" title="<? global/openthehelppage ?>" onClick="openHelpW('Appearance')"></td>
</tr>
</form>
</table>
<ui:imcms_gui_mid/>

<table border="0" cellspacing="0" cellpadding="0" width="500">
<form method="post" action="SaveInPage">
<input type="HIDDEN" name="meta_id" value="#meta_id#">
<tr>
    <td>
        <c:set var="heading">
            <fmt:message key="templates/sv/inPage_admin_no_template.html/2"/>
        </c:set>
        <ui:imcms_gui_heading heading="${heading}"/>
    </td>
</tr>
<tr>
    <td><? templates/sv/inPage_admin_no_template.html/3 ?> </td>
</tr>
<tr>
    <td><ui:imcms_gui_hr wantedcolor="blue"/></td>
</tr>
<tr>
    <td align="right"><input type="submit" class="imcmsFormBtn" name="change_group" value="<? templates/sv/inPage_admin_no_template.html/2001 ?>"></td>
</tr>
</form>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>