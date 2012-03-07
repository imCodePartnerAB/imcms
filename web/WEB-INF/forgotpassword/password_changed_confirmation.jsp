<%@ page import="com.imcode.imcms.util.l10n.LocalizedMessage" %>

<%!
    static final LocalizedMessage confirmation = new LocalizedMessage("forgotpassord.confirmation.password_changed");
    static final LocalizedMessage lnkStartPage = new LocalizedMessage("forgotpassord.link.start_page");
%>

<html>
    <body>
        <jsp:include page="inc_header.jsp" flush="true"/>

        <p>
            <%=confirmation.toLocalizedString(request)%>
        </p>

        <a href="<%=request.getContextPath()%>"><%=lnkStartPage.toLocalizedString(request)%></a>
    </body>
</html>