<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<h4>
    <spring:message code="archive.imageCard.imageInfo" htmlEscape="true"/>
</h4><div class="hr"></div>
<div class="m15t minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.imageName" htmlEscape="true"/>
    </span>
    <span><c:out value="${image.imageNm}"/></span>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.description" htmlEscape="true"/>
    </span>
    <p class="left">
        ${archive:newlineToBr(fn:escapeXml(image.changedExif.description))}
    </p>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.category" htmlEscape="true"/>
    </span>
    <p class="left">
        <c:out value="${categories}"/>
    </p>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.keywords" htmlEscape="true"/>
    </span>
    <p class="left">
        <c:out value="${keywords}"/>
    </p>
</div>
<div class="minH20 clearboth">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.photographer" htmlEscape="true"/>
    </span>
    <span><c:out value="${image.changedExif.artist}"/></span>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.originalSizeWidth" htmlEscape="true"/>
    </span>
    <span>${image.width}x${image.height}</span>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.resolution" htmlEscape="true"/>
    </span>
    <span>
        <spring:message code="archive.changeData.dpi" arguments="${image.changedExif.resolution}" htmlEscape="true"/>
    </span>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.originalFileSize" htmlEscape="true"/>
    </span>
    <span><spring:message code="archive.originalSizeKb" arguments="${image.fileSize / 1024.0}"/></span>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.id" htmlEscape="true"/>
    </span>
    <span>${image.id}</span>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.usedInImcms" htmlEscape="true"/>
    </span>
    <span>${archive:join(image.metaIds, ', ')}</span>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.uploadedBy" htmlEscape="true"/>
    </span>
    <span><c:out value="${image.uploadedBy}"/></span>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.copyright" htmlEscape="true"/>
    </span>
    <span><c:out value="${image.changedExif.copyright}"/></span>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.licensePeriod" htmlEscape="true"/>
    </span>
    <span>
        <c:if test="${image.licenseDt ne null and image.licenseEndDt ne null}">
            <c:if test="${image.licenseDt ne null}">
                <spring:message code="archive.dateFormat" arguments="${image.licenseDt}" htmlEscape="true"/>
            </c:if>
            &#8211;
            <c:if test="${image.licenseEndDt ne null}">
                <spring:message code="archive.dateFormat" arguments="${image.licenseEndDt}" htmlEscape="true"/>
            </c:if>
        </c:if>
    </span>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.publish" htmlEscape="true"/>
    </span>
    <span>
        <c:if test="${image.publishDt ne null}">
            <spring:message code="archive.dateFormat" arguments="${image.publishDt}" htmlEscape="true"/>
        </c:if>
    </span>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.archive" htmlEscape="true"/>
    </span>
    <span>
        <c:if test="${image.archiveDt ne null}">
            <spring:message code="archive.dateFormat" arguments="${image.archiveDt}" htmlEscape="true"/>
        </c:if>
    </span>
</div>
<div class="minH20">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.publishEnd" htmlEscape="true"/>
    </span>
    <span>
        <c:if test="${image.publishEndDt ne null}">
            <spring:message code="archive.dateFormat" arguments="${image.publishEndDt}" htmlEscape="true"/>
        </c:if>
    </span>
</div>
