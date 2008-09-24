<%@ page
	
	import="imcode.server.Imcms,
	        imcode.server.ImcmsServices,
	        imcode.server.document.SectionDomainObject"
	
%><%

ImcmsServices service = Imcms.getServices() ;

SectionDomainObject[] sections = service.getDocumentMapper().getAllSections() ;

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
		int id   = sections[i].getId() ;
		String name = sections[i].getName() ; %>
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
