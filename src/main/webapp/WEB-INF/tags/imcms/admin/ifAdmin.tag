<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="isSuperAdmin" type="boolean"--%>
<%--@elvariable id="editOptions" type="com.imcode.imcms.domain.dto.RestrictedPermissionDTO"--%>
<%--@elvariable id="accessToAdminPages" type="boolean"--%>
<%--@elvariable id="accessToDocumentEditor" type="boolean"--%>
<%--@elvariable id="accessToPublishCurrentDoc" type="boolean"--%>

<c:set var="isEditDocumentContent"
       value="${editOptions.editText or editOptions.editMenu or editOptions.editImage or editOptions.editLoop}"
/>

<c:if test="${isSuperAdmin or isEditDocumentContent or editOptions.editDocInfo or accessToAdminPages or accessToDocumentEditor or accessToPublishCurrentDoc}">
    <jsp:doBody/>
</c:if>
