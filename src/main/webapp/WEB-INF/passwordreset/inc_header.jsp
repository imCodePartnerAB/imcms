<%@ page
	
	import="com.imcode.imcms.servlet.PasswordReset,
	        java.util.List"
	
  pageEncoding="UTF-8"
	
%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui"
%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%!

/* Validation errors and common heading */

%>
<c:set var="heading">
    <fmt:message key="passwordreset.title"/>
</c:set>
<ui:imcms_gui_start_of_page titleAndHeading="${heading}"/>
<%
List<String> errors = (List<String>)request.getAttribute(PasswordReset.REQUEST_ATTR_VALIDATION_ERRORS);

if (errors != null) { %>
	<div style="border: 1px solid red; background-color: #ffc; padding: 5px 10px;">
		<p><fmt:message key="passwordreset.title.validation_errors" /></p>
		<p><%= errors.get(0) %></p>
	</div><%
} %>
