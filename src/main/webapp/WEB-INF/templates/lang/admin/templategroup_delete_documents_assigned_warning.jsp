<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<html>
<head>
<title><? templates/sv/templategroup_delete_warning.html/1 ?></title>

    <link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/js/imcms/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF">

#gui_outer_start()
#gui_head( "<? templates/sv/templategroup_delete_warning.html/1 ?>" )
<table border="0" cellpadding="0" cellspacing="0">
<form>
<tr>
    <td><input type="button" value="<? templates/sv/templategroup_delete_warning.html/2001 ?>" class="imcmsFormBtn" onClick="history.go(-1); return false"></td>
    <td>&nbsp;</td>
    <td><input type="button" value="<? templates/sv/templategroup_delete_warning.html/2002 ?>" title="<? templates/sv/templategroup_delete_warning.html/2003 ?>" class="imcmsFormBtn" onClick="openHelpW('TemplateGroupRemove')"></td>
</tr>
</form>
</table>
#gui_mid()

<table border="0" cellspacing="0" cellpadding="2" width="310">
<form method="post" action="TemplateChange">
<input type="HIDDEN" name="templategroup" value="#templategroup#">
<input type="HIDDEN" name="group_delete_cancel" value="Avbryt">
<tr>
    <td align="center" class="imcmsAdmText"><b><? templates/sv/templategroup_delete_documents_assigned_warning.html/1 ?></b>
<blockquote>#templates#</blockquote></td>
</tr>
<tr>
    <td>#gui_hr( "blue" )</td>
</tr>
<tr>
    <td align="right">
        <input type="submit" class="imcmsFormBtn" name="group_delete_cancel" value="<? templates/sv/templategroup_delete_warning.html/2005 ?>"></td>
</tr>

</form>
</table>
<ui:imcms_gui_bottom/>
#gui_outer_end()


</body>
</html>

