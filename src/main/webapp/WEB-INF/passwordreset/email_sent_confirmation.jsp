<%@ page import="com.imcode.imcms.servlet.PasswordReset" %>
<%@ page import="imcode.server.Imcms" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="inc_header.jsp" flush="true"/>

<div>
    <fmt:message key="passwordreset.confirmation.email_sent" bundle="${requestScope['resource_property']}">
        <fmt:param value="<%=request.getParameter(PasswordReset.REQUEST_USER_IDENTITY)%>"/>
        <fmt:param value="<%=Imcms.getServices().getSystemData().getServerMasterAddress()%>"/>
    </fmt:message>
</div>

<jsp:include page="inc_footer.jsp" flush="true"/>
