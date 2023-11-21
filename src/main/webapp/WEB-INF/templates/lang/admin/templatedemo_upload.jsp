<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>


    <title><? templates/sv/AdminManager_adminTask_element.htm/10 ?></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">
    <script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>

    <script language="JavaScript">
        <!--
        function setSize() {
            document.getElementById("file").size = 85;
        }

        //-->
    </script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(2,'file'); setSize();">

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
                       onClick="openHelpW('TemplateDemoUpload')"></td>
        </tr>
    </form>
    <form name="TemplateAdmin" action="TemplateAdmin" method="post">
        <input type="HIDDEN" name="language" value="<? templates/default_lang ?>">
        <tr>
            <td class="imcmsAdmText"><? templates/sv/templatedemo_upload.html/4 ?></td>
        </tr>
        <tr>
            <td>
                <input type="submit" class="imcmsFormBtnSub" name="add_group"
                       value="<? templates/sv/templatedemo_upload.html/2008 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="delete_group"
                       value="<? templates/sv/templatedemo_upload.html/2009 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="rename_group"
                       value="<? templates/sv/templatedemo_upload.html/2010 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="assign_group"
                       value="<? templates/sv/templatedemo_upload.html/2011 ?>"></td>
        </tr>
    </form>
</table>
<ui:imcms_gui_mid/>

<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
    <form method="post" action="TemplateAdd" enctype="multipart/form-data">
        <input type="hidden" name="language" value="${language}">
        <input type="hidden" name="demo" value="true">
        <tr>
            <td colspan="2">
                <c:set var="heading">
                    <fmt:message key="templates/sv/templatedemo_upload.html/6/1"/>
                </c:set>
                <ui:imcms_gui_heading heading="${heading}"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                        <td width="20%" class="imcmsAdmText" nowrap><? templates/sv/templatedemo_upload.html/1001 ?>
                            &nbsp;
                        </td>
                        <td><input type="file" name="file" size="35"></td>
                    </tr>
                    <tr>
                        <td height="24" class="imcmsAdmText" nowrap><? templates/sv/templatedemo_upload.html/1002 ?>
                            &nbsp;
                        </td>
                        <td>
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td>
                                        <select name="template">
                                            ${templates}
                                        </select></td>
                                    <td class="imcmsAdmText" nowrap>
                                        &nbsp; <? templates/sv/templatedemo_upload.html/1003 ?></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td class="imcmsAdmText" height="22"><? templates/sv/templatedemo_upload.html/9 ?></td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td>
                            <table border="0" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td><input type="submit" class="imcmsFormBtnSmall" name="ok"
                                               value="<? templates/sv/templatedemo_upload.html/2014 ?>"
                                               style="width:120"></td>
                                    <td>&nbsp;</td>
                                    <td><input type="submit" class="imcmsFormBtnSmall" name="view_demo"
                                               value="<? templates/sv/templatedemo_upload.html/2015 ?>"
                                               style="width:120"></td>
                                    <td>&nbsp;</td>
                                    <td><input type="submit" class="imcmsFormBtnSmall" name="delete_demo"
                                               value="<? templates/sv/templatedemo_upload.html/2016 ?>"
                                               style="width:120"></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan="2"><ui:imcms_gui_hr wantedcolor="blue"/></td>
        </tr>
        <tr>
            <td colspan="2" align="right">
                <input type="submit" class="imcmsFormBtn" name="cancel"
                       value="<? templates/sv/templatedemo_upload.html/2017 ?>"></td>
        </tr>
    </form>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>


</body>
</html>
