<%@ taglib prefix="imcms" uri="imcms" %>
<%@ tag body-content="empty" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="currentDocument" type="com.imcode.imcms.api.TextDocument"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>

<link rel="stylesheet" href="${contextPath}/css_new/imcms-imports_files.css">
<script src="${contextPath}/js/imcms/imcms.js.jsp?meta_id=${currentDocument.id}${isEditMode?'&amp;flags=65536':''}"></script>
<script src="${contextPath}/js/imcms_new/imcms_main.js" data-name="imcms"
        data-main="${contextPath}/js/imcms_new/imcms_start.js"></script>
