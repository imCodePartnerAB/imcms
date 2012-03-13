<%@ page import="com.imcode.imcms.servlet.PasswordReset" %>
<%@ page import="com.imcode.imcms.util.l10n.LocalizedMessage" %>

<%!
    static final LocalizedMessage formInfo = new LocalizedMessage("passwordreset.email_form_info");
    static final LocalizedMessage formLabelEmail = new LocalizedMessage("passwordreset.email_form.lbl_email");
    static final LocalizedMessage formSubmit = new LocalizedMessage("passwordreset.email_form.submit");
%>

<html>
    <body>
        <jsp:include page="inc_header.jsp" flush="true"/>

        <p>
            <%=formInfo.toLocalizedString(request)%>
        </p>

        <form method="POST" action="/servlet/PasswordReset">
            <input type="hidden" name="<%=PasswordReset.REQUEST_PARAM_OP%>" value="<%=PasswordReset.Op.SEND_RESET_URL%>"/>

            <%=formLabelEmail.toLocalizedString(request)%> <input type="text" name="<%=PasswordReset.REQUEST_PARAM_EMAIL%>">

            <input type="submit" value="<%=formSubmit.toLocalizedString(request)%>" />
        </form>
    </body>
</html>