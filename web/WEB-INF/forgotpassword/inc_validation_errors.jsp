<%@ page import="com.imcode.imcms.servlet.ForgotPassword" %>
<%@ page import="java.util.List" %>
<%
    List<String> errors = (List<String>)request.getAttribute(ForgotPassword.REQUEST_ATTR_VALIDATION_ERRORS);
    if (errors != null) {
        %>
        <div style="border-style: solid; border-width: 1; border-color: red; background-color: #f5deb3; padding: 3px;">
            <p>There was a problem with your request</p>
            <p><%=errors.get(0)%></p>
        </div>
        <%
    }
%>