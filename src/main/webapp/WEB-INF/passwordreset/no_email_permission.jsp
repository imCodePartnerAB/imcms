<%@ page
        pageEncoding="UTF-8"
        import="com.imcode.imcms.util.l10n.LocalizedMessage"
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="inc_header.jsp" flush="true"/>

<%! static final LocalizedMessage info = new LocalizedMessage("passwordreset.no_email_permission");%>

<div class="imcms-title" style="word-break: break-word">
    <%=info.toLocalizedStringByIso639_1((String) request.getAttribute("userLanguage"))%>
</div>

<jsp:include page="inc_footer.jsp" flush="true"/>
