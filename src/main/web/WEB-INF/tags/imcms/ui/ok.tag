<%@ tag import="com.imcode.imcms.flow.OkCancelPage"%><%@taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<ui:submit name="<%= OkCancelPage.REQUEST_PARAMETER__OK %>">
    <jsp:attribute name="value"><fmt:message key="global/OK"/></jsp:attribute>
</ui:submit>
