<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="vel" uri="imcmsvelocity"%>
<vel:velocity>
<html>
<head>
<title><? templates/login/logged_out.html/1 ?></title>


<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">



</head>
<body bgcolor="#FFFFFF">

#gui_outer_start()
#gui_head( "<? templates/login/logged_out.html/2 ?>" )
<table border="0" cellspacing="0" cellpadding="0">
<tr>
	<td>
	<table border="0" cellpadding="0" cellspacing="0">
	<form action="$contextPath/servlet/StartDoc">
	<tr>
		<td><input type="Submit" value="<? templates/login/logged_out.html/2001 ?>" class="imcmsFormBtn" style="width:90"></td>
	</tr>
	</form>
	</table></td>
	<td>&nbsp;</td>
	<td>
	<table border="0" cellpadding="0" cellspacing="0">
	<form action="$contextPath/login/">
	<tr>
		<td><input type="Submit" value="<? templates/login/logged_out.html/2002 ?>" class="imcmsFormBtn" style="width:90"></td>
	</tr>
	</form>
	</table></td>
</tr>
</table>
#gui_mid()

<table border="0" cellspacing="0" cellpadding="2" width="310">
<tr>
	<td align="center" class="imcmsAdmText"><? templates/login/logged_out.html/4 ?></td>
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