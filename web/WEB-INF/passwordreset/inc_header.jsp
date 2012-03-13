<%-- Validation errors and common heading --%>

<%@ page import="com.imcode.imcms.servlet.PasswordReset" %>
<%@ page import="java.util.List" %>
<%@ page import="com.imcode.imcms.util.l10n.LocalizedMessage" %>

<%!
    static final LocalizedMessage title = new LocalizedMessage("passwordreset.title");
    static final LocalizedMessage validationErrorsTitle = new LocalizedMessage("passwordreset.title.validation_errors");
%>

<%
    List<String> errors = (List<String>)request.getAttribute(PasswordReset.REQUEST_ATTR_VALIDATION_ERRORS);

    if (errors != null) {
        %>
        <div style="border-style: solid; border-width: 1; border-color: red; background-color: #f5deb3; padding: 3px;">
            <p><%=validationErrorsTitle.toLocalizedString(request)%></p>
            <p><%=errors.get(0)%></p>
        </div>
        <%
    }
%>

<h2><%=title.toLocalizedString(request)%></h2>