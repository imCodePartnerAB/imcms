<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<html>
<head>
<title><? templates/sv/FileAdminFileExisted.html/1 ?></title>

    <link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/js/imcms/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body onLoad="focusField(1, 'no')">

#gui_outer_start()
#gui_head( "<? templates/sv/FileAdminFileExisted.html/1 ?>" )
<table border="0" cellspacing="0" cellpadding="0">
<form method="post" action="FileAdmin" enctype="multipart/form-data">
<input type="HIDDEN" name="dir1" value="#dir1#">
<input type="HIDDEN" name="dir2" value="#dir2#">
<tr>
    <td><input type="submit" class="imcmsFormBtn" name="no" value="<? global/back ?>"></td>
    <td>&nbsp;</td>
    <td><input type="button" class="imcmsFormBtn" value="<? global/help ?>" title="<? global/openthehelppage ?>" onClick="openHelpW('FileManager')"></td>
</tr>
</form>
</table>
#gui_mid()

<table border="0" cellspacing="0" cellpadding="0" width="500">
<form method="post" action="FileAdmin" enctype="multipart/form-data">
<input type="HIDDEN" name="dir1" value="#dir1#">
<input type="HIDDEN" name="dir2" value="#dir2#">
<tr>
    <td>#gui_heading( "<? templates/sv/FileAdminFileExisted.html/2 ?>" )</td>
</tr>
<tr>
    <td><? templates/sv/FileAdminFileExisted.html/3 ?></td>
</tr>
<tr>
    <td>#gui_hr( "blue" )</td>
</tr>
<tr>
    <td align="right"><input type="submit" class="imcmsFormBtn" style="width:70" name="no" value="<? templates/sv/FileAdminFileExisted.html/2001 ?>"></td>
</tr>
</form>
</table>
<ui:imcms_gui_bottom/>
#gui_outer_end()


</body>
</html>