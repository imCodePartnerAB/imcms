<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>


    <title><? templates/sv/AdminManager_adminTask_element.htm/10 ?></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css">
    <script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(2,'templategroup')">

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
                       onClick="openHelpW('TemplateGroupRemove')"></td>
        </tr>
    </form>
    <tr>
        <td class="imcmsAdmText"><? templates/sv/templategroup_delete.html/3 ?></td>
    </tr>
    <form name="TemplateAdmin" action="TemplateAdmin" method="post">
        <input type="HIDDEN" name="language" value="<? templates/default_lang ?>">
        <tr>
            <td>
                <input type="submit" class="imcmsFormBtnSub" name="add_template"
                       value="<? templates/sv/templategroup_delete.html/2001 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="delete_template"
                       value="<? templates/sv/templategroup_delete.html/2002 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="rename_template"
                       value="<? templates/sv/templategroup_delete.html/2003 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="get_template"
                       value="<? templates/sv/templategroup_delete.html/2004 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="edit_template"
                       value="<? templates/sv/templategroup_delete.html/2005 ?>"><br>
                <img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="2"><br>
                <input type="submit" class="imcmsFormBtnSub" name="add_demotemplate"
                       value="<? templates/sv/templategroup_delete.html/2006 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="show_templates"
                       value="<? templates/sv/templategroup_delete.html/2007 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="change_availability_template"
                       value="<? templates/sv/template_admin.html/2012 ?>"></td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><? templates/sv/templategroup_delete.html/4 ?></td>
        </tr>
        <tr>
            <td>
                <input type="submit" class="imcmsFormBtnSub" name="add_group"
                       value="<? templates/sv/templategroup_delete.html/2008 ?>">
                <input type="submit" class="imcmsFormBtnSubDisabled" name="delete_group"
                       value="<? templates/sv/templategroup_delete.html/2009 ?>" disabled="disabled">
                <input type="submit" class="imcmsFormBtnSub" name="rename_group"
                       value="<? templates/sv/templategroup_delete.html/2010 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="assign_group"
                       value="<? templates/sv/templategroup_delete.html/2011 ?>"></td>
        </tr>
    </form>
</table>
<ui:imcms_gui_mid/>

<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
    <form name="TemplateChange" action="TemplateChange" method="post">
        <input type="hidden" name="language" value="${language}">
        <tr>
            <td colspan="2">
                <c:set var="heading">
                    <fmt:message key="templates/sv/templategroup_delete.html/6/1"/>
                </c:set>
                <ui:imcms_gui_heading heading="${heading}"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <table border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td width="80" height="24" class="imcmsAdmText"
                            nowrap><? templates/sv/templategroup_delete.html/1001 ?></td>
                        <td>
                            <select name="templategroup">
                                ${templategroups}
                            </select></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan="2"><ui:imcms_gui_hr wantedcolor="blue"/></td>
        </tr>
        <tr>
            <td colspan="2" align="right">
                <input type="submit" class="imcmsFormBtn" name="group_delete_check"
                       value="<? templates/sv/templategroup_delete.html/2014 ?>">
                <input type="submit" class="imcmsFormBtn" name="cancel"
                       value="<? templates/sv/templategroup_delete.html/2015 ?>"></td>
        </tr>
    </form>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>


</body>
</html>
