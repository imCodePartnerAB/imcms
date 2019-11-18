<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ tag body-content="empty" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>

<imcms:ifAdmin>
    <script>
        <jsp:include page="/imcms/js/imcms_config.js.jsp"/>
    </script>

    <script src="${contextPath}/dist/imcms_start.js"></script>

    <%-- Custom styles --%>
    <link href="${contextPath}/imcms/css/imcms-text_editor_custom.css" rel="stylesheet">
</imcms:ifAdmin>
