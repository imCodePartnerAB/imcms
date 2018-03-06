<%@ taglib prefix="imcms" uri="imcms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag body-content="empty" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>
<%--@elvariable id="isAdmin" type="boolean"--%>
<%--@elvariable id="editOptions" type="com.imcode.imcms.domain.dto.RestrictedPermissionDTO"--%>

<link rel="stylesheet" href="${contextPath}/css_new/imcms-imports_files.css">
<script>
    <jsp:include page="/js/imcms/imcms_config.js.jsp"/>
</script>

<c:set var="isEditDocumentContent"
       value="${editOptions.editText or editOptions.editMenu or editOptions.editImage or editOptions.editLoop}"
/>

<c:if test="${isAdmin or isEditDocumentContent or editOptions.editDocInfo}">
    <script src="${contextPath}/js/imcms/imcms_main.js" data-name="imcms"
            data-main="${contextPath}/js/imcms/imcms_start.js"></script>
</c:if>
