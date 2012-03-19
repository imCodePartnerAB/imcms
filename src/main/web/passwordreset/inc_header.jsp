<%@ page
	
	import="com.imcode.imcms.servlet.PasswordReset,
	        java.util.List"
	
  pageEncoding="UTF-8"
	
%><%@ taglib prefix="vel" uri="imcmsvelocity"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%!

/* Validation errors and common heading */

%><vel:velocity>
#gui_start_of_page( "<fmt:message key="passwordreset.title" />" "$contextPath/login" "<fmt:message key="global/back" />" "" "" )

<%
List<String> errors = (List<String>)request.getAttribute(PasswordReset.REQUEST_ATTR_VALIDATION_ERRORS);

if (errors != null) { %>
	<div style="border: 1px solid red; background-color: #ffc; padding: 5px 10px;">
		<p><fmt:message key="passwordreset.title.validation_errors" /></p>
		<p><%= errors.get(0) %></p>
	</div><%
} %>
</vel:velocity>