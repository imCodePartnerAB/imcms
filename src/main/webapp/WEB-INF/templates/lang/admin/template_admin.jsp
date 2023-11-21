<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>

    <title><? templates/sv/AdminManager_adminTask_element.htm/10 ?></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">
    <script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF">

<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="templates/sv/AdminManager_adminTask_element.htm/10"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>

<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
    <form action="AdminManager">
        <tr>
            <td>
                <input type="submit" value="<? global/back ?>" title="<? global/back ?>" class="imcmsFormBtn">
                <input type="button" value="<? global/help ?>" title="<? global/help ?>" class="imcmsFormBtn"
                       onClick="openHelpW('Templates')"></td>
        </tr>
    </form>
    <form name="TemplateAdmin" action="TemplateAdmin" method="post">
        <input TYPE="HIDDEN" name="language" value="<? templates/default_lang ?>">
        <tr>
            <td class="imcmsAdmText"><? templates/sv/template_admin.html/4 ?></td>
        </tr>
        <tr>
            <td>
                <input type="submit" class="imcmsFormBtnSub" name="add_group"
                       value="<? templates/sv/template_admin.html/2008 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="delete_group"
                       value="<? templates/sv/template_admin.html/2009 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="rename_group"
                       value="<? templates/sv/template_admin.html/2010 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="assign_group"
                       value="<? templates/sv/template_admin.html/2011 ?>"></td>
        </tr>
</table>
<ui:imcms_gui_mid/>

<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
    <tr>
        <td>
            <c:set var="heading">
                <fmt:message key="templates/sv/template_admin.html/6/1"/>
            </c:set>
            <ui:imcms_gui_heading heading="${heading}"/>
        </td>
    </tr>
    <tr>
        <td height="28" class="imcmsAdmText"><? templates/sv/template_admin.html/7 ?></td>
    </tr>
    <tr>
        <td colspan="2"><ui:imcms_gui_hr wantedcolor="blue"/></td>
    </tr>
    <tr>
        <td align="right">
            <input type="submit" class="imcmsFormBtn" name="cancel" value="<? global/cancel ?>"></td>
    </tr>
    </form>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>


</body>
</html>
