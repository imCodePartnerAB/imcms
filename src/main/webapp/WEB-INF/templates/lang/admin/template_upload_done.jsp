<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title><? templates/sv/template_upload_done.html/1 ?></title>


    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">


</head>
<body bgcolor="#FFFFFF">


<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="global/message"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>

<table border="0" cellpadding="0" cellspacing="0">
    <form>
        <tr>
            <td><input type="button" value="<? templates/sv/template_upload_done.html/2001 ?>" class="imcmsFormBtn"
                       onClick="history.go(-1); return false"></td>
            <td>&nbsp;</td>
            <td><input type="button" value="<? templates/sv/template_upload_done.html/2002 ?>"
                       title="<? templates/sv/template_upload_done.html/2003 ?>" class="imcmsFormBtn"
                       onClick="openHelpW('TemplateUploadNew')"></td>
        </tr>
        <input type="HIDDEN" name="add_template" value="Tillbaka">
        <input type="HIDDEN" name="language" value="${language}">
    </form>
</table>
<ui:imcms_gui_mid/>

<table border="0" cellspacing="0" cellpadding="2" width="310">
    <tr>
        <td align="center" class="imcmsAdmText">
            <? templates/sv/template_upload_done.html/4 ?></td>
    </tr>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>


</body>
</html>
