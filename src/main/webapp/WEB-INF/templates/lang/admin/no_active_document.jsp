<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<html>
<head>
<title><? templates/sv/no_active_document.html/1 ?></title>


    <link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">


</head>
<body bgcolor="#FFFFFF">

<ui:imcms_gui_outer_start/>
#gui_head( "<? templates/sv/no_active_document.html/2 ?>" )
<table border="0" cellspacing="0" cellpadding="0">
<tr>
    <td>
        <table border="0" cellpadding="0" cellspacing="0">
            <form action="$contextPath/servlet/StartDoc">
                <tr>
                    <td><input type="Submit" value="<? templates/sv/no_active_document.html/3 ?>" class="imcmsFormBtn"></td>
                </tr>
            </form>
        </table></td>
    <td>&nbsp;</td>
    <td>
        <table border="0" cellpadding="0" cellspacing="0">
            <form action="$contextPath/servlet/BackDoc">
                <tr>
                    <td><input type="Submit" value="<? templates/sv/no_active_document.html/4 ?>" class="imcmsFormBtn"></td>
                </tr>
            </form>
        </table></td>
</tr>
</table>
#gui_mid()

<table border="0" cellspacing="0" cellpadding="2" width="310">
<tr>
    <td align="center" class="imcmsAdmText"><b><? templates/sv/no_active_document.html/5 ?>!</b><br>
    </td>
</tr>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>


<script language="JavaScript">
<!--
if (document.forms[1]) {
    var f = document.forms[1];
    if (f.elements[0]) {
        f.elements[0].blur();
    }
}
//-->
</script>

</body>
</html>