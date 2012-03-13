<%@ page import="com.imcode.imcms.servlet.PasswordReset" %>
<%@ page import="com.imcode.imcms.util.l10n.LocalizedMessage" %>

<%!
    static final LocalizedMessage formInfo = new LocalizedMessage("passwordreset.password_reset_form_info");
    static final LocalizedMessage formLabelPassword = new LocalizedMessage("passwordreset.password_reset_form.lbl_password");
    static final LocalizedMessage formLabelPasswordCheck = new LocalizedMessage("passwordreset.password_reset_form.lbl_password_check");
    static final LocalizedMessage formSubmit = new LocalizedMessage("passwordreset.password_reset_form.submit");
%>

<html>
    <body>
        <jsp:include page="inc_header.jsp" flush="true"/>

        <p>
            <%=formInfo.toLocalizedString(request)%>
        </p>

        <form method="POST" action="/servlet/PasswordReset">
            <input type="hidden" name="<%=PasswordReset.REQUEST_PARAM_OP%>" value="<%=PasswordReset.Op.SAVE_NEW_PASSWORD%>"/>
            <input type="hidden" name="<%=PasswordReset.REQUEST_PARAM_RESET_ID%>" value="<%=request.getParameter(PasswordReset.REQUEST_PARAM_RESET_ID)%>"/>

            <%=formLabelPassword.toLocalizedString(request)%><input type="password" name="<%=PasswordReset.REQUEST_PARAM_PASSWORD%>">
            <%=formLabelPasswordCheck.toLocalizedString(request)%><input type="password" name="<%=PasswordReset.REQUEST_PARAM_PASSWORD_CHECK%>">

            <input type="submit" value="<%=formSubmit.toLocalizedString(request)%>"/>
        </form>

        <%--<p>--%>
        <%--Secure password tips:--%>
        <%--<ul>--%>
            <%--<li>Use at least 8 characters, a combination of numbers and letters is best.</li>--%>
            <%--<li>Do not use the same password you have used with us previously.</li>--%>
            <%--<li>Do not use dictionary words, your name, e-mail address, or other personal information that can be easily obtained.</li>--%>
            <%--<li>Do not use the same password for multiple online accounts.</li>--%>
        <%--</ul>--%>
    </body>
</html>