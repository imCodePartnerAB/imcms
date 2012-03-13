<%@ page import="com.imcode.imcms.util.l10n.LocalizedMessage" %>

<%!
    static final LocalizedMessage confirmation = new LocalizedMessage("passwordreset.confirmation.password_changed");
    static final LocalizedMessage lnkStartPage = new LocalizedMessage("passwordreset.link.start_page");
%>

<html>
    <body>
        <jsp:include page="inc_header.jsp" flush="true"/>

        <p>
            <%=confirmation.toLocalizedString(request)%>
        </p>

        <a href="<%=request.getContextPath()%>/servlet/StartDoc"><%=lnkStartPage.toLocalizedString(request)%></a>
    </body>
</html>