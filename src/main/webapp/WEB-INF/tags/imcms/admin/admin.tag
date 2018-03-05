<%@ taglib prefix="imcms" uri="imcms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag body-content="empty" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>
<%--@elvariable id="isAdmin" type="boolean"--%>

<link rel="stylesheet" href="${contextPath}/css_new/imcms-imports_files.css">
<script>
    <jsp:include page="/js/imcms/imcms_config.js.jsp"/>
</script>

<c:if test="${isAdmin}">
    <script src="${contextPath}/js/imcms/imcms_main.js" data-name="imcms"
            data-main="${contextPath}/js/imcms/imcms_start.js"></script>
</c:if>
