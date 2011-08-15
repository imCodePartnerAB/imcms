<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<div>
    <c:url var="thumbSrc" value="${pageContext.servletContext.contextPath}/web/archive/thumb">
        <c:param name="id" value="${image.id}"/>
        <c:param name="size" value="${imageSize}"/>
    </c:url>
    <c:url var="imageCardUrl" value="/web/archive/image/${image.id}"/>

    <a href="${imageCardUrl}">
        <img src="${thumbSrc}" alt="${image.imageNm}"/>
    </a>

    <div>
        <a href="${imageCardUrl}">
            <span title="${fn:escapeXml(image.imageNm)}"><c:out
                    value="${archive:abbreviate(image.imageNm, 23)}"/></span><br/>
        </a>
    </div>
    <div>
        <c:set var="size" value="${image.width}x${image.height}"/>
        <span title="${size}">${archive:abbreviate(size, 23)} px</span><br/>

        <c:if test="${not empty image.metaIds}">
            <spring:message code="archive.usedInImcms" htmlEscape="true"/>: ${archive:join(image.metaIds, ', ')}
        </c:if>
    </div>
</div>