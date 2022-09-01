<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>


<%@ attribute name="document" required="false" %>

<c:set var="targetDocId" value="${empty document ? currentDocument.id : document}"/>

<c:set var="metadataList" value="${isEditMode or isPreviewMode
	? documentMetadataService.getDocumentMetadataList(targetDocId, language)
	: documentMetadataService.getPublicDocumentMetadataList(targetDocId,language)
}"/>

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
