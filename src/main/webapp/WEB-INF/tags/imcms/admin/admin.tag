<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ tag body-content="empty" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>

<script>
    <jsp:include page="/js/imcms/imcms_config.js.jsp"/>
</script>

<imcms:ifAdmin>
    <script src="${contextPath}/dist/imcms_start.js"></script>

    <%-- Custom styles --%>
    <link href="${contextPath}/css/imcms-text_editor_custom.css" rel="stylesheet">
</imcms:ifAdmin>
