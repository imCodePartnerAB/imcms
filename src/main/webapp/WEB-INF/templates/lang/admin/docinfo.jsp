<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<html>
<head>


    <title><? templates/sv/docinfo.html/1 ?></title>

    <link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">


</head>
<body bgcolor="#FFFFFF">

<ui:imcms_gui_outer_start/>
#gui_head( "<? global/imcms_administration ?>" )

<table border="0" cellspacing="0" cellpadding="0">
<form action="BackDoc">
<tr>
    <td><input type="submit" class="imcmsFormBtn" value="<? templates/sv/docinfo.html/2001 ?>"></td>
</tr>
</form>
</table>
<ui:imcms_gui_mid/>

#doc_type_description#
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>


<div align="center">#adminMode#</div>

</body>
</html>
