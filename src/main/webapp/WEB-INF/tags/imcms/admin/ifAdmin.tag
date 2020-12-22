<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="isAdmin" type="boolean"--%>
<%--@elvariable id="editOptions" type="com.imcode.imcms.domain.dto.RestrictedPermissionDTO"--%>
<%--@elvariable id="accessToDocumentEditor" type="boolean"--%>

<c:set var="isEditDocumentContent"
       value="${editOptions.editText or editOptions.editMenu or editOptions.editImage or editOptions.editLoop}"
/>

<c:if test="${isAdmin or isEditDocumentContent or editOptions.editDocInfo or accessToDocumentEditor}">
    <jsp:doBody/>
</c:if>
