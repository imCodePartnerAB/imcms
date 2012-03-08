<%@ page import="com.imcode.imcms.util.l10n.LocalizedMessageFormat" %>
<%@ page import="com.imcode.imcms.servlet.ForgotPassword" %>
<%@ page import="imcode.server.Imcms" %>

<html>
    <body>
        <jsp:include page="inc_header.jsp" flush="true"/>

        <p>
            <%=
                new LocalizedMessageFormat("forgotpassord.confirmation.email_sent",
                        request.getParameter(ForgotPassword.REQUEST_PARAM_EMAIL),
                        Imcms.getServices().getSystemData().getServerMasterAddress()).toLocalizedString(request)
            %>
        </p>
    </body>
</html>