<%@ page
	
	import="com.imcode.imcms.api.*,
	        imcode.server.Imcms,
	        imcode.server.ImcmsServices"
	
%><%

ImcmsServices service = Imcms.getServices() ;

String sSql = "SELECT section_id, section_name FROM sections ORDER BY section_name" ;

String[][] sections = service.sqlQueryMulti(sSql, new String[]{}) ;

%>
<html>
<head>
<title>Sections</title>

<style type="text/css">
<!-- 
TD { font: 11px Verdana,sans-serif; }
-->
</style>

</head>
<body>

<table border="0" cellspacing="0" cellpadding="0">
<tr>
	<td bgcolor="#000066">
	<table border="0" cellspacing="1" cellpadding="6">
	<tr>
		<td bgcolor="#f0f0f2"><b>ID</b></td>
		<td bgcolor="#f0f0f2"><b>Name</b></td>
	</tr><%
if (sections != null) { 
	for (int i = 0; i < sections.length; i++) {
		String id   = sections[i][0] ;
		String name = sections[i][1] ; %>
	<tr valign="top">
		<td bgcolor="#ffffff"><%= id %></td>
		<td bgcolor="#ffffff"><%= name %></td>
	</tr><%
	}
} %>
	</table></td>
</tr>
</table>

</body>
</html>
