<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>


    <title><? templates/sv/AdminManager_adminTask_element.htm/10 ?></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">
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
                       onClick="openHelpW('TemplateAssign')"></td>
        </tr>
    </form>
    <form name="TemplateAdmin" action="TemplateAdmin" method="post">
        <input type="HIDDEN" name="language" value="<? templates/default_lang ?>">
        <tr>
            <td class="imcmsAdmText"><? templates/sv/template_assign.html/4 ?></td>
        </tr>
        <tr>
            <td>
                <input type="submit" class="imcmsFormBtnSub" name="add_group"
                       value="<? templates/sv/template_assign.html/2008 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="delete_group"
                       value="<? templates/sv/template_assign.html/2009 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="rename_group"
                       value="<? templates/sv/template_assign.html/2010 ?>">
                <input type="submit" class="imcmsFormBtnSubDisabled" name="assign_group"
                       value="<? templates/sv/template_assign.html/2011 ?>" disabled="disabled"></td>
        </tr>
    </form>
</table>
<ui:imcms_gui_mid/>

<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
    <form name="TemplateChange" action="TemplateChange" method="post">
        <input type="hidden" name="group_id" value="${group_id}">
        <input type="hidden" name="language" value="${language}">
        <tr>
            <td colspan="2">
                <c:set var="heading">
                    <fmt:message key="templates/sv/template_assign.html/6/1"/>
                </c:set>
                <ui:imcms_gui_heading heading="${heading}"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <table border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td height="24" nowrap><? templates/sv/template_assign.html/1001 ?>&nbsp;&nbsp;</td>
                        <td>
                            <select name="templategroup">
                                ${templategroups}
                            </select></td>
                        <td>&nbsp;&nbsp;<input type="submit" class="imcmsFormBtnSmall" name="show_assigned"
                                               value="<? templates/sv/template_assign.html/2014 ?>"></td>
                    </tr>
                    <tr>
                        <td height="24" class="imcmsAdmText"><? templates/sv/template_assign.html/8 ?></td>
                        <td colspan="2" class="imcmsAdmText"><b><i>${group}</i></b></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                        <td width="45%" class="imcmsAdmText"><? templates/sv/template_assign.html/11 ?></td>
                    </tr>
                    <tr>
                        <td align="right">
                            <select name="unassigned" size="7" style="width:100%" multiple>
                                ${unassigned}
                            </select></td>
                        <td align="center">
                            &nbsp;<input type="submit" class="imcmsFormBtnSmall" style="width:60px" name="assign"
                                         value="<? templates/sv/template_assign.html/2015 ?>">&nbsp;<br>
                            <img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="2"><br>
                            &nbsp;<input type="submit" class="imcmsFormBtnSmall" style="width:60px" name="deassign"
                                         value="<? templates/sv/template_assign.html/2016 ?>">&nbsp;
                        </td>
                        <td>
                            <select name="assigned" size="7" style="width:100%" multiple>
                                ${assigned}
                            </select></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan="2"><ui:imcms_gui_hr wantedcolor="blue"/></td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><? templates/sv/template_assign.html/1002 ?></td>
            <td align="right">
                <input type="submit" class="imcmsFormBtn" name="cancel"
                       value="<? templates/sv/template_assign.html/2017 ?>"></td>
        </tr>
    </form>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>


</body>
</html>
