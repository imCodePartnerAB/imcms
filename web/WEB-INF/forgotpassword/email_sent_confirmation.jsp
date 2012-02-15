<%@ page import="com.imcode.imcms.servlet.ForgotPassword" %>
<html>
    <body>
        <h2>
        Password Assistance
        </h2>

        <p>
        Check your e-mail.
        If the e-mail address you entered
            <span style="font-weight: bold; color: #708090;">
                <%=request.getParameter(ForgotPassword.REQUEST_PARAM_EMAIL)%>
            </span>
        is associated with a customer account in our records,
        you will receive an e-mail from us with instructions for resetting your password.
        If you don't receive this e-mail, please check your junk mail folder
        or visit our Help pages to contact Customer Service for further assistance.
        </p>
    </body>
</html>