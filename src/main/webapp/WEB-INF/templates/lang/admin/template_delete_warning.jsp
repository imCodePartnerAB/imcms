<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<html>
<head>
<title><? templates/sv/template_delete_warning.html/1 ?></title>


    <link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/js/imcms/imcms_admin.js.jsp" type="text/javascript"></script>


</head>
<body bgcolor="#FFFFFF">

#gui_outer_start()
#gui_head( "<? templates/sv/template_delete_warning.html/1 ?>" )
<table border="0" cellpadding="0" cellspacing="0">
<form>
<tr>
    <td><input type="button" value="<? templates/sv/template_delete_warning.html/2001 ?>" class="imcmsFormBtn" onClick="history.go(-1); return false"></td>
    <td>&nbsp;</td>
    <td><input type="button" value="<? templates/sv/template_delete_warning.html/2002 ?>" title="<? templates/sv/template_delete_warning.html/2003 ?>" class="imcmsFormBtn" onClick="openHelpW('TemplateRemoveAlert')"></td>
</tr>
</form>
</table>
#gui_mid()

<table border="0" cellspacing="0" cellpadding="2" width="660">
<form method="post" action="TemplateChange">
<input type="HIDDEN" name="template" value="#template#">
<input type="HIDDEN" name="language" value="#language#">
<tr>
    <td class="imcmsAdmText">
        <? templates/sv/template_delete_warning.html/1001 ?>&nbsp;</td>
</tr>
<tr>
    <td class="imcmsAdmText">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr valign="top">
                <td class="imcmsAdmText" nowrap><? templates/sv/template_delete_warning.html/1002 ?> &nbsp;</td>
                <td>
                    <select name="select" size="7">
                        #docs#
                    </select></td>
                <td nowrap>&nbsp; &nbsp;</td>
                <td class="imcmsAdmText" nowrap><? templates/sv/template_delete_warning.html/1003 ?> &nbsp;</td>
                <td>
                    <select name="new_template">
                        #templates#
                    </select></td>
            </tr>
        </table></td>
</tr>
<tr>
    <td>#gui_hr( "blue" )</td>
</tr>
<tr>
    <td align="right">
        <input type="submit" class="imcmsFormBtn" name="template_delete" value="  <? templates/sv/template_delete_warning.html/2004 ?>  ">
        <input type="submit" class="imcmsFormBtn" name="template_delete_cancel" value="<? templates/sv/template_delete_warning.html/2005 ?>"></td>
</tr>
</form>
</table>
<ui:imcms_gui_bottom/>
#gui_outer_end()


</body>
</html>
