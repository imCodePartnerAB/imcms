<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<archive:params var="titleArgs">
    <archive:param value="${name}"/>
</archive:params>
<spring:message var="title" code="archive.title.externalFiles.preview" arguments="${titleArgs}" htmlEscape="true"/>

<c:choose>
    <c:when test="${user eq null}">
        <div style="text-align:center;margin-top:30px;">
            <h1><spring:message code="archive.externalFiles.preview.loginError" htmlEscape="true"/></h1>
        </div>
    </c:when>
    <c:when test="${library eq null or !library.canUse}">
        <div style="text-align:center;margin-top:30px;">
            <h1><spring:message code="archive.externalFiles.preview.noAccess" htmlEscape="true"/></h1>
        </div>
    </c:when>
    <c:when test="${imageInfo eq null}">
        <div style="text-align:center;margin-top:30px;">
            <h1><spring:message code="archive.externalFiles.preview.notImage" htmlEscape="true"/></h1>
        </div>
    </c:when>
    <c:otherwise>
        <c:url var="imageUrl" value="/web/archive/external-files/image">
            <c:param name="id" value="${library.id}"/>
            <c:param name="name" value="${name}"/>
        </c:url>
        <img src="${imageUrl}" alt="${name}" style="width:100%;height:100%;"/>
    </c:otherwise>
</c:choose>