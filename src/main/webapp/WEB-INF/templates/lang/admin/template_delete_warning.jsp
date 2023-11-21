<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title><? templates/sv/template_delete_warning.html/1 ?></title>


    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">
    <script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>


</head>
<body bgcolor="#FFFFFF">

<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="templates/sv/template_delete_warning.html/1"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>
<table border="0" cellpadding="0" cellspacing="0">
    <form>
        <tr>
            <td><input type="button" value="<? templates/sv/template_delete_warning.html/2001 ?>" class="imcmsFormBtn"
                       onClick="history.go(-1); return false"></td>
            <td>&nbsp;</td>
            <td><input type="button" value="<? templates/sv/template_delete_warning.html/2002 ?>"
                       title="<? templates/sv/template_delete_warning.html/2003 ?>" class="imcmsFormBtn"
                       onClick="openHelpW('TemplateRemoveAlert')"></td>
        </tr>
    </form>
</table>
<ui:imcms_gui_mid/>

<table border="0" cellspacing="0" cellpadding="2" width="660">
    <form method="post" action="TemplateChange">
        <input type="HIDDEN" name="template" value="${template}">
        <input type="HIDDEN" name="language" value="${language}">
        <tr>
            <td class="imcmsAdmText">
                <? templates/sv/template_delete_warning.html/1001 ?>&nbsp;
            </td>
        </tr>
        <tr>
            <td class="imcmsAdmText">
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr valign="top">
                        <td class="imcmsAdmText" nowrap><? templates/sv/template_delete_warning.html/1002 ?> &nbsp;</td>
                        <td>
                            <select name="select" size="7">
                                ${docs}
                            </select></td>
                        <td nowrap>&nbsp; &nbsp;</td>
                        <td class="imcmsAdmText" nowrap><? templates/sv/template_delete_warning.html/1003 ?> &nbsp;</td>
                        <td>
                            <select name="new_template">
                                ${templates}
                            </select></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td><ui:imcms_gui_hr wantedcolor="blue"/></td>
        </tr>
        <tr>
            <td align="right">
                <input type="submit" class="imcmsFormBtn" name="template_delete"
                       value="  <? templates/sv/template_delete_warning.html/2004 ?>  ">
                <input type="submit" class="imcmsFormBtn" name="template_delete_cancel"
                       value="<? templates/sv/template_delete_warning.html/2005 ?>"></td>
        </tr>
    </form>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>


</body>
</html>
