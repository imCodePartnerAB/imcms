<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<html>
<head>
<title><? templates/sv/FileAdminMoveOverwriteWarning.html/1 ?></title>

    <link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/js/imcms/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body onLoad="focusField(1, 'moveok')">

#gui_outer_start()
#gui_head( "<? templates/sv/FileAdminMoveOverwriteWarning.html/1 ?>" )
<table border="0" cellspacing="0" cellpadding="0">
<form method="post" action="FileAdmin" enctype="multipart/form-data">
<input type="HIDDEN" name="dir1" value="#dir1#">
<input type="HIDDEN" name="dir2" value="#dir2#">
<input type="HIDDEN" name="source" value="#source#">
<input type="HIDDEN" name="dest" value="#dest#">
<input type="HIDDEN" name="files" value="#files#">
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
<input type="HIDDEN" name="source" value="#source#">
<input type="HIDDEN" name="dest" value="#dest#">
<input type="HIDDEN" name="files" value="#files#">
<tr>
    <td>#gui_heading( "<? templates/sv/FileAdminMoveOverwriteWarning.html/3 ?>" )</td>
</tr>
<tr>
    <td>
        <select size="10" style="width:100%">
            #filelist#
        </select></td>
</tr>
<tr>
    <td height="20"><? templates/sv/FileAdminMoveOverwriteWarning.html/5 ?> </td>
</tr>
<tr>
    <td>#gui_hr( "blue" )</td>
</tr>
<tr>
    <td align="right">
        <input type="submit" class="imcmsFormBtn" style="width:70" name="moveok" value="<? templates/sv/FileAdminMoveOverwriteWarning.html/2001 ?>">
        <input type="submit" class="imcmsFormBtn" style="width:70" name="no" value="<? templates/sv/FileAdminMoveOverwriteWarning.html/2002 ?>"></td>
</tr>
</form>
</table>
<ui:imcms_gui_bottom/>
#gui_outer_end()


</body>
</html>