<%@ page import="com.imcode.imcms.servlet.ForgotPassword" %>
<%@ page import="com.imcode.imcms.util.l10n.LocalizedMessage" %>

<%!
    static final LocalizedMessage formInfo = new LocalizedMessage("forgotpassord.email_form_info");
    static final LocalizedMessage formLabelEmail = new LocalizedMessage("forgotpassord.email_form.lbl_email");
    static final LocalizedMessage formSubmit = new LocalizedMessage("forgotpassord.email_form.submit");
%>

<html>
    <body>
        <jsp:include page="inc_header.jsp" flush="true"/>

        <p>
            <%=formInfo.toLocalizedString(request)%>
        </p>

        <form method="POST" action="/servlet/ForgotPassword">
            <input type="hidden" name="<%=ForgotPassword.REQUEST_PARAM_OP%>" value="<%=ForgotPassword.Op.SEND_RESET_URL%>"/>

            <%=formLabelEmail.toLocalizedString(request)%> <input type="text" name="<%=ForgotPassword.REQUEST_PARAM_EMAIL%>">

            <input type="submit" value="<%=formSubmit.toLocalizedString(request)%>" />
        </form>
    </body>
</html>