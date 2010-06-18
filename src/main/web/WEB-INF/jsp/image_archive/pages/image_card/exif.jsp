<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<h4><spring:message code="archive.imageCard.originalExif" htmlEscape="true"/></h4><div class="hr"></div>
<div class="m15t minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.description" htmlEscape="true"/>
    </span>
    <p class="left" style="width:60%;">
        ${archive:newlineToBr(fn:escapeXml(image.originalExif.description))}
    </p>
</div>
<div class="clearboth minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.photographer" htmlEscape="true"/>
    </span>
    <span><c:out value="${image.originalExif.artist}"/></span>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.resolution" htmlEscape="true"/>
    </span>
    <span>
        <spring:message code="archive.changeData.dpi" arguments="${image.originalExif.resolution}" htmlEscape="true"/>
    </span>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.copyright" htmlEscape="true"/>
    </span>
    <span><c:out value="${image.originalExif.copyright}"/></span>
</div>
<div class="hr m10t"></div>
