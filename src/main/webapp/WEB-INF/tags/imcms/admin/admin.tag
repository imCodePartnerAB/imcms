<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ tag body-content="empty" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>

<imcms:ifAdmin>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/dist/imcms-imports_files.css">

    <script>
        <jsp:include page="/imcms/js/imcms_config.js.jsp"/>
    </script>

    <script src="${contextPath}/dist/imcms_start.js"></script>

    <%-- Custom styles --%>
    <link href="${contextPath}/imcms/css/imcms-text_editor_custom.css" rel="stylesheet">
</imcms:ifAdmin>
