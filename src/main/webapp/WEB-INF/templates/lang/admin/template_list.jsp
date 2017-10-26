<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<html>
<head>


    <title><? templates/sv/AdminManager_adminTask_element.htm/10 ?></title>

    <link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/js/imcms/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(2,'template')">

#gui_outer_start()
#gui_head( "<? templates/sv/AdminManager_adminTask_element.htm/10 ?>" )

<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<form action="AdminManager">
<tr>
    <td>
        <input type="submit" value="<? global/back ?>" title="<? global/back ?>" class="imcmsFormBtn">
        <input type="button" value="<? global/help ?>" title="<? global/help ?>" class="imcmsFormBtn" onClick="openHelpW('TemplateList')"></td>
</tr>
</form>
<tr>
    <td class="imcmsAdmText"><? templates/sv/template_list.html/3 ?></td>
</tr>
<form name="TemplateAdmin" action="TemplateAdmin" method="post">
<input type="HIDDEN" name="language" value="<? templates/default_lang ?>">
<tr>
    <td>
        <input type="submit" class="imcmsFormBtnSub" name="add_template" value="<? templates/sv/template_list.html/2001 ?>">
        <input type="submit" class="imcmsFormBtnSub" name="delete_template" value="<? templates/sv/template_list.html/2002 ?>">
        <input type="submit" class="imcmsFormBtnSub" name="rename_template" value="<? templates/sv/template_list.html/2003 ?>">
        <input type="submit" class="imcmsFormBtnSub" name="get_template" value="<? templates/sv/template_list.html/2004 ?>">
        <input type="submit" class="imcmsFormBtnSub" name="edit_template" value="<? templates/sv/template_list.html/2005 ?>"><br>
        <img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="2"><br>
        <input type="submit" class="imcmsFormBtnSub" name="add_demotemplate" value="<? templates/sv/template_list.html/2006 ?>">
        <input type="submit" class="imcmsFormBtnSubDisabled" name="show_templates" value="<? templates/sv/template_list.html/2007 ?>" disabled="disabled">
        <input type="submit" class="imcmsFormBtnSub" name="change_availability_template" value="<? templates/sv/template_admin.html/2012 ?>"></td>
</tr>
<tr>
    <td class="imcmsAdmText"><? templates/sv/template_list.html/4 ?></td>
</tr>
<tr>
    <td>
        <input type="submit" class="imcmsFormBtnSub" name="add_group" value="<? templates/sv/template_list.html/2008 ?>">
        <input type="submit" class="imcmsFormBtnSub" name="delete_group" value="<? templates/sv/template_list.html/2009 ?>">
        <input type="submit" class="imcmsFormBtnSub" name="rename_group" value="<? templates/sv/template_list.html/2010 ?>">
        <input type="submit" class="imcmsFormBtnSub" name="assign_group" value="<? templates/sv/template_list.html/2011 ?>"></td>
</tr>
</form>
</table>
#gui_mid()

<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<form name="TemplateChange" action="TemplateChange" method="post">
<input type="hidden" name="group_id" value="#group_id#">
<input type="hidden" name="language" value="#language#">
<tr>
    <td colspan="2">#gui_heading( "<? templates/sv/template_list.html/6/1 ?>" )</td>
</tr>
<tr>
    <td colspan="2">
        <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr>
                <td width="45%" class="imcmsAdmText" align="right"><? templates/sv/template_list.html/7 ?></td>
                <td>&nbsp;</td>
                <td width="45%" class="imcmsAdmText"><? templates/sv/template_list.html/8 ?></td>
            </tr>
            <tr>
                <td align="right">
                    <select name="template" size="10" style="width:100%" multiple>
                        #template_list#
                    </select></td>
                <td align="center">
                    &nbsp;<input type="submit" class="imcmsFormBtnSmall" name="list_templates_docs" value="<? templates/sv/template_list.html/2014 ?>" alt="<? templates/sv/template_list.html/2015 ?>" title="<? templates/sv/template_list.html/2016 ?>" style="width:70">&nbsp;</td>
                <td>
                    <select name="templates_doc" size="10" style="width:100%" multiple>
                        #templates_docs#
                    </select></td>
            </tr>
            <tr>
                <td colspan="2">&nbsp;</td>
                <td><input type="submit" class="imcmsFormBtnSmall" name="show_doc" value="<? templates/sv/template_list.html/2017 ?>"></td>
            </tr>
        </table></td>
</tr>
<tr>
    <td colspan="2">#gui_hr( "blue" )</td>
</tr>
<tr>
    <td colspan="2" align="right">
        <input type="submit" class="imcmsFormBtn" name="cancel" value="<? templates/sv/template_list.html/2018 ?>"></td>
</tr>
</form>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>


</body>
</html>
