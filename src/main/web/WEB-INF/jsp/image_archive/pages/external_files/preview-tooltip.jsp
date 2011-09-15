<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<form action="/" style="display:none;">
    <input type="hidden" id="contextPath" value="${contextPath}"/>
    <c:url var="dummyUrl" value="/"/>
    <input type="hidden" id="jsessionid" value="${dummyUrl}"/>
</form>
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
        <img src="${imageUrl}" width="100%" style="width:100%;"/>
        <div style="margin-top:5px;">${name}</div>
        <div style="margin-top:5px;"><spring:message code="archive.originalSizeKb" arguments="${size / 1024.0}"/></div>
    </c:otherwise>
</c:choose>