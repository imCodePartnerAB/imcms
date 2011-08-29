<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<archive:params var="titleArgs">
    <archive:param value="${name}"/>
</archive:params>
<spring:message var="title" code="archive.title.externalFiles.preview" arguments="${titleArgs}" htmlEscape="true"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<body>
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
        <div>${name} ${size}</div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jsp/image_archive/includes/footer.jsp" %>