${"<!--"}
<%@ tag trimDirectiveWhitespaces="true" %>
${"-->"}
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ tag body-content="empty" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>

<link rel="stylesheet" href="${contextPath}/css/imcms-imports_files.css">

<script>
    <jsp:include page="/js/imcms/imcms_config.js.jsp"/>
</script>

<imcms:ifAdmin>
    <script src="${contextPath}/js/imcms/imcms_main.js" data-name="imcms"
            data-main="${contextPath}/js/imcms/imcms_start.js"></script>
</imcms:ifAdmin>
