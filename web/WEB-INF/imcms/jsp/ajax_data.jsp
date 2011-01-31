<%@ page

	import="org.apache.commons.lang.StringUtils"

	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"

%><%

String action = StringUtils.defaultString(request.getParameter("action")) ;
String value  = StringUtils.defaultString(request.getParameter("value")) ;

if ("getCompleteHtmlForW3cValidation".equals(action)) {
	%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="sv" lang="sv">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Validation</title>
</head>
<body>
<%= value %>
</body>
</html><%
	return ;
} %>