<%@ page
        pageEncoding="UTF-8"
        import="com.imcode.imcms.util.l10n.LocalizedMessage"
%>
<%!

    static final LocalizedMessage confirmation = new LocalizedMessage("passwordreset.confirmation.password_changed");
    static final LocalizedMessage lnkLoginPage = new LocalizedMessage("passwordreset.link.login_page");

%>
<jsp:include page="inc_header.jsp" flush="true"/>

<div style="width:310px;">
    <p><%= confirmation.toLocalizedString(request) %>
    </p>

    <a href="${contextPath}/login"><%= lnkLoginPage.toLocalizedString(request) %>
    </a>
</div>

<jsp:include page="inc_footer.jsp" flush="true"/>