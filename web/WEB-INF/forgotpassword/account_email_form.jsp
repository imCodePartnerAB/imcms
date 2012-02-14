<%@ page import="com.imcode.imcms.servlet.ForgotPassword" %>
<html>
    <body>
        <h2>
        Password Assistance
        </h2>

        <p>
        Enter the e-mail address associated with your account, then click Continue.
        We'll email you a link to a page where you can easily create a new password.
        </p>

        <form method="POST" action="/servlet/ForgotPassword">
            <input type="hidden" name="<%=ForgotPassword.REQUEST_PARAM_OP%>" value="<%=ForgotPassword.Op.SEND_RESET_URL%>"/>

            Email address: <input type="text" name="<%=ForgotPassword.REQUEST_PARAM_EMAIL%>">
            <%-- ?captcha? --%>
            <input type="submit" value="Send" />
        </form>
    </body>
</html>