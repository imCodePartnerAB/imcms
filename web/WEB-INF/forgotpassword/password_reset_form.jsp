<%@ page import="com.imcode.imcms.servlet.ForgotPassword" %>

<html>
    <body>
        <jsp:include page="inc_validation_errors.jsp" flush="true"/>

        <h2>
        Password Assistance
        </h2>

        <p>
        Create your new password.
        We'll ask you for this password when you want to login into the system.
        </p>

        <form method="POST" action="/servlet/ForgotPassword">
            <input type="hidden" name="<%=ForgotPassword.REQUEST_PARAM_OP%>" value="<%=ForgotPassword.Op.SAVE_NEW_PASSWORD%>"/>
            <input type="hidden" name="<%=ForgotPassword.REQUEST_PARAM_RESET_ID%>" value="<%=request.getParameter(ForgotPassword.REQUEST_PARAM_RESET_ID)%>"/>

            New password:<input type="password" name="<%=ForgotPassword.REQUEST_PARAM_PASSWORD%>">
            Reenter new password:<input type="password" name="<%=ForgotPassword.REQUEST_PARAM_PASSWORD_CHECK%>">

            <input type="submit" value="Save changes"/>
        </form>

        <p>
        Secure password tips:
        <ul>
            <li>Use at least 8 characters, a combination of numbers and letters is best.</li>
            <li>Do not use the same password you have used with us previously.</li>
            <li>Do not use dictionary words, your name, e-mail address, or other personal information that can be easily obtained.</li>
            <li>Do not use the same password for multiple online accounts.</li>
        </ul>
    </body>
</html>