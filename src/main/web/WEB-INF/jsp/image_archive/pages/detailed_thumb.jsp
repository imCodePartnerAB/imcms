<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<div>
    <c:url var="thumbSrc" value="/web/archive/thumb">
        <c:param name="id" value="${image.id}"/>
        <c:param name="size" value="${imageSize}"/>
    </c:url>
    <c:url var="imageCardUrl" value="/web/archive/image/${image.id}"/>

    <a href="${imageCardUrl}">
        <img src="${thumbSrc}" alt="${image.imageNm}"/>
    </a>

    <div style="padding-bottom:5px;">
        <a href="${imageCardUrl}">
            <span title="${fn:escapeXml(image.imageNm)}"><c:out
                    value="${archive:abbreviate(image.imageNm, 23)}"/></span><br/>
        </a>
    </div>
    
    <div style="padding-bottom:5px;">
        <c:set var="size" value="${image.width}x${image.height}"/>
        <span title="${size}">${archive:abbreviate(size, 23)} px</span><br/>
    </div>

    <c:if test="${not empty image.metaIds}">
    <div style="padding-bottom: 5px">
        <spring:message code="archive.usedInImcms" htmlEscape="true"/>: ${archive:join(image.metaIds, ', ')}
    </div>
    </c:if>
</div>