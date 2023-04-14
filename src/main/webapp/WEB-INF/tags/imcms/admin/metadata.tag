<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>


<%@ attribute name="document" required="false" %>

<c:set var="targetDocId" value="${empty document ? currentDocument.id : document}"/>

<c:set var="versionNo" value="${pageContext.request.getParameter('version-no')}"/>
<c:choose>
	<c:when test="${versionNo ne null and isPreviewMode}">
		<c:set var="metadataList" value="${documentMetadataService.getDocumentMetadataList(targetDocId, versionNo, language)}"/>
	</c:when>
	<c:when test="${(isEditMode or isPreviewMode)}">
		<c:set var="metadataList" value="${documentMetadataService.getDocumentMetadataList(targetDocId, language)}"/>
	</c:when>
	<c:otherwise>
		<c:set var="metadataList" value="${documentMetadataService.getPublicDocumentMetadataList(targetDocId,language)}"/>
	</c:otherwise>
</c:choose>

<c:if test="${not empty metadataList}">
	<c:forEach items="${metadataList}" var="metadata">
		<meta name="${metadata.metaTag.name}" property="${metadata.metaTag.name}" content="${metadata.content}">
	</c:forEach>
</c:if>

<%--@elvariable id="currentDocument" type="imcode.server.document.textdocument.TextDocumentDomainObject"--%>
<%--@elvariable id="documentMetadataService" type="com.imcode.imcms.domain.service.DocumentMetadataService"--%>
<%--@elvariable id="language" type="java.lang.String"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>
<%--@elvariable id="isPreviewMode" type="boolean"--%>
<%--@elvariable id="metadata" type="com.imcode.imcms.domain.dto.DocumentMetadataDTO"--%>
