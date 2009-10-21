<%@ page
	
	import="com.imcode.imcms.api.*,
            org.apache.commons.lang.StringEscapeUtils"
    errorPage="no_category_type_by_that_name.jsp"
    contentType="text/html; charset=UTF-8"

%><%

ContentManagementSystem imcms   = ContentManagementSystem.fromRequest(request) ;
DocumentService documentService = imcms.getDocumentService();
String categoryTypeName         = StringEscapeUtils.unescapeHtml( request.getParameter("category_type_name") ) ;
CategoryType categoryType       = documentService.getCategoryType(categoryTypeName);
Category[] categories           = documentService.getAllCategoriesOfType(categoryType) ;

%>
<html>
<head>
<title><? install/htdocs/sv/jsp/category_descriptions.jsp/1/1 ?> <%= categoryTypeName %></title>

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
		<td bgcolor="#f0f0f2"><b>Description</b></td>
	</tr><%
	for (int i = 0; i < categories.length; i++) {
		Category category = categories[i] ; %>
	<tr valign="top">
		<td bgcolor="#ffffff"><%= category.getId() %></td>
		<td bgcolor="#ffffff"><%= category.getName() %></td>
		<td bgcolor="#ffffff"><%= (category.getDescription().length() > 0) ? category.getDescription() : "-" %></td>
	</tr><%
	} %>
	</table></td>
</tr>
</table>


</body>
</html>
