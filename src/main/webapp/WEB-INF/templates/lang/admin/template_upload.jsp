<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>


    <title><? templates/sv/AdminManager_adminTask_element.htm/10 ?></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css">
    <script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>

    <SCRIPT LANGUAGE="JavaScript">
        <!--
        function checkForName() {
            var theFile = document.forms.TemplateAdd.file.value;
            var sFileName = '';
            var re0 = /.(php|html?|jspx?)$/gi;
            var re1 = /(\w+)\.(\w+)$/i;
            var re2 = /\"/g; //"
            sFileName = theFile.replace(re2, '');
            if (re0.test(sFileName)) {
                sFileName = sFileName.replace(re1, "$1");
                arrFileName = sFileName.split("\/");
                sFileName = arrFileName[arrFileName.length - 1];
                arrFileName = sFileName.split(":");
                sFileName = arrFileName[arrFileName.length - 1];
                arrFileName = sFileName.split("\\");
                sFileName = arrFileName[arrFileName.length - 1];
                if (!/[:;\/\.\\]+/gi.test(sFileName)) {
                    document.forms.TemplateAdd.name.value = sFileName;
                    document.forms.TemplateAdd.overwrite.checked = 1;
                }
            }
        }

        function setSize() {
            document.getElementById("sfile").size = 85;
            document.getElementById("sname").size = 65;
        }

        //-->
    </SCRIPT>

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
                       onClick="openHelpW('TemplateUploadNew')"></td>
        </tr>
    </form>
    <tr>
        <td class="imcmsAdmText"><? templates/sv/template_upload.html/3 ?></td>
    </tr>
    <form name="TemplateAdmin" action="TemplateAdmin" method="post">
        <input type="HIDDEN" name="language" value="<? templates/default_lang ?>">
        <tr>
            <td>
                <input type="submit" class="imcmsFormBtnSubDisabled" name="add_template"
                       value="<? templates/sv/template_upload.html/2001 ?>" disabled="disabled">
                <input type="submit" class="imcmsFormBtnSub" name="delete_template"
                       value="<? templates/sv/template_upload.html/2002 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="rename_template"
                       value="<? templates/sv/template_upload.html/2003 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="get_template"
                       value="<? templates/sv/template_upload.html/2004 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="edit_template"
                       value="<? templates/sv/template_upload.html/2005 ?>"><br>
                <img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="2"><br>
                <input type="submit" class="imcmsFormBtnSub" name="add_demotemplate"
                       value="<? templates/sv/template_upload.html/2006 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="show_templates"
                       value="<? templates/sv/template_upload.html/2007 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="change_availability_template"
                       value="<? templates/sv/template_admin.html/2012 ?>"></td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><? templates/sv/template_upload.html/4 ?></td>
        </tr>
        <tr>
            <td>
                <input type="submit" class="imcmsFormBtnSub" name="add_group"
                       value="<? templates/sv/template_upload.html/2008 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="delete_group"
                       value="<? templates/sv/template_upload.html/2009 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="rename_group"
                       value="<? templates/sv/template_upload.html/2010 ?>">
                <input type="submit" class="imcmsFormBtnSub" name="assign_group"
                       value="<? templates/sv/template_upload.html/2011 ?>"></td>
        </tr>
    </form>
</table>
<ui:imcms_gui_mid/>

<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
    <form name="TemplateAdd" method="post" action="TemplateAdd" enctype="multipart/form-data">
        <input type="HIDDEN" name="language" value="${language}">
        <tr>
            <td colspan="2">
                <c:set var="heading">
                    <fmt:message key="templates/sv/template_upload.html/6/1"/>
                </c:set>
                <ui:imcms_gui_heading heading="${heading}"/>
            </td>
        </tr>
        <tr>
            <td width="20%" class="imcmsAdmText" nowrap><? templates/sv/template_upload.html/1001 ?> &nbsp;</td>
            <td><input type="file" name="file" id="sfile" size="35"></td>
        </tr>
        <tr>
            <td class="imcmsAdmText" nowrap><? templates/sv/template_upload.html/1002 ?> &nbsp;</td>
            <td>
                <table border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td><input type="text" name="name" id="sname" size="30" maxlength="255"></td>
                        <td>&nbsp;</td>
                        <td nowrap>
                            <script language="JavaScript">
                                <!--
                                document.write("<input type=\"button\" class=\"imcmsFormBtnSmall\" onClick=\"checkForName()\" value=\"&laquo;&nbsp;<? templates/sv/template_upload.html/2020 ?>\">");
                                //-->
                            </script>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><input type="checkbox" name="overwrite"></td>
                        <td class="imcmsAdmText"><? templates/sv/template_upload.html/10 ?></td>
                        <td><input type="checkbox" name="hidden"></td>
                        <td class="imcmsAdmText"><? templates/sv/template_upload.html/12 ?></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><? templates/sv/template_upload.html/11 ?></td>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td>
                            <select name="templategroup" size="5" multiple>
                                ${templategroups}
                            </select></td>
                        <td>&nbsp;&nbsp;</td>
                        <td class="imcmsAdmDim"><? templates/sv/template_upload.html/13 ?></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan="2"><ui:imcms_gui_hr wantedcolor="blue"/></td>
        </tr>
        <tr>
            <td colspan="2" align="right">
                <input type="submit" class="imcmsFormBtn" name="ok"
                       value="<? templates/sv/template_upload.html/2014 ?>">
                <input type="submit" class="imcmsFormBtn" name="cancel"
                       value="<? templates/sv/template_upload.html/2015 ?>"></td>
        </tr>
    </form>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>


</body>
</html>
