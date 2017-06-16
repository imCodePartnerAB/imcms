<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@ page import="com.imcode.imcms.util.l10n.LocalizedMessage, imcode.server.Imcms"%><%@taglib prefix="vel" uri="imcmsvelocity"%>
<vel:velocity>
<html>
<head>
<title><fmt:message key="templates/sv/no_page.html/1"/></title>

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms_admin.css.jsp">

</head>
<body bgcolor="#FFFFFF">

#gui_outer_start()
#gui_head( "<fmt:message key="templates/sv/no_page.html/1"/>" )
<table border="0" cellspacing="0" cellpadding="0">
<tr>
	<td>
	<table border="0" cellpadding="0" cellspacing="0">
	<form action="<%= request.getContextPath() %>/servlet/StartDoc">
	<tr>
		<td><input type="Submit" value="<fmt:message key="templates/Startpage"/>" class="imcmsFormBtn"></td>
	</tr>
	</form>
	</table></td>
	<td>&nbsp;</td>
	<td>
	<table border="0" cellpadding="0" cellspacing="0">
	<form action="<%= request.getContextPath() %>/servlet/BackDoc">
	<tr>
		<td><input type="Submit" value="<fmt:message key="templates/Back"/>" class="imcmsFormBtn"></td>
	</tr>
	</form>
	</table></td>
</tr>
</table>
#gui_mid()

<table border="0" cellspacing="0" cellpadding="2" width="310">
<tr>
	<td align="center" class="imcmsAdmText"><b><fmt:message key="templates/sv/no_page.html/2"><fmt:param value="<%= Imcms.getServices().getSystemData().getServerMasterAddress() %>"/></fmt:message></b><br>
	</td>
</tr>
</table>
#gui_bottom()
#gui_outer_end()


<script language="JavaScript">
<!--
if (document.forms[1]) {
    var f = document.forms[1]
    if (f.elements[0]) {
        f.elements[0].blur();
    }
}
//-->
</script>

</body>
</html>
</vel:velocity>