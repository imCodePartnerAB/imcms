<%@ tag import="org.apache.commons.text.StringEscapeUtils" %>
<%@ tag import="imcode.server.Imcms" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ attribute name="no" required="false" type="java.lang.Object" %><%-- old index name --%>
<%@ attribute name="id" required="false" %>
<%@ attribute name="index" required="false" %>
<%@ attribute name="document" required="false" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="currentDocument" type="imcode.server.document.textdocument.TextDocumentDomainObject"--%>
<%--@elvariable id="imageService" type="com.imcode.imcms.domain.service.ImageService"--%>
<%--@elvariable id="image" type="com.imcode.imcms.domain.dto.ImageDTO"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>
<%--@elvariable id="isPreviewMode" type="boolean"--%>
<%--@elvariable id="loopEntryRef" type="com.imcode.imcms.model.LoopEntryRef"--%>
<%--@elvariable id="language" type="java.lang.String"--%>
<%--@elvariable id="editOptions" type="com.imcode.imcms.domain.dto.RestrictedPermissionDTO"--%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>
<%--@elvariable id="isDocNew" type="boolean"--%>

<c:if test="${!isDocNew || editOptions.editImage}">
    <c:if test="${empty index}">
        <c:set var="index" value="${no}"/><%-- old attribute "no" support --%>
    </c:if>

    <c:set var="targetDocId" value="${empty document ? currentDocument.id : document}"/>

    <c:set var="versionNo" value="${pageContext.request.getParameter('version-no')}"/>
    <c:choose>
        <c:when test="${versionNo ne null and isPreviewMode}">
            <c:set var="image" value="${imageService.getImage(targetDocId, index, versionNo, language, loopEntryRef)}"/>
        </c:when>
        <c:when test="${isEditMode or isPreviewMode}">
            <c:set var="image" value="${imageService.getImage(targetDocId, index, language, loopEntryRef)}"/>
        </c:when>
        <c:otherwise>
            <c:set var="image" value="${imageService.getPublicImage(targetDocId, index, language, loopEntryRef)}"/>
        </c:otherwise>
    </c:choose>

    <%@ variable name-given="alternateText" scope="NESTED" variable-class="java.lang.String" %>
    <c:set var="alternateText" value="${image.alternateText}" scope="request"/>

    <%@ variable name-given="descriptionText" scope="NESTED" variable-class="java.lang.String" %>
    <c:set var="descriptionText" value="${image.descriptionText}" scope="request"/>

    <jsp:doBody/>
</c:if>
