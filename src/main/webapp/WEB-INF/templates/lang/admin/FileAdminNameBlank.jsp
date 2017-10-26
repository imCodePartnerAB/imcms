<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>
<title><? templates/sv/FileAdminNameBlank.html/1 ?></title>

    <link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/js/imcms/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body onLoad="focusField(1, 'no')">

<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="templates/sv/FileAdminNameBlank.html/1"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>
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
<ui:imcms_gui_mid/>

<table border="0" cellspacing="0" cellpadding="0" width="500">
<form method="post" action="FileAdmin" enctype="multipart/form-data">
<input type="HIDDEN" name="dir1" value="#dir1#">
<input type="HIDDEN" name="dir2" value="#dir2#">
<input type="HIDDEN" name="source" value="#source#">
<input type="HIDDEN" name="dest" value="#dest#">
<input type="HIDDEN" name="files" value="#files#">
<tr>
    <td>#gui_heading( "<? templates/sv/FileAdminNameBlank.html/2 ?>")</td>
</tr>
<tr>
    <td><? templates/sv/FileAdminNameBlank.html/3 ?></td>
</tr>
<tr>
    <td>#gui_hr( "blue" )</td>
</tr>
<tr>
    <td align="right"><input type="submit" class="imcmsFormBtn" style="width:70" name="no" value="<? templates/sv/FileAdminNameBlank.html/2001 ?>"></td>
</tr>
</form>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>


</body>
</html>