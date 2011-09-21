<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<%@ page import="com.imcode.imcms.api.*" %>
<% pageContext.setAttribute("user", ContentManagementSystem.fromRequest(request).getCurrentUser()); %>
<spring:message var="notAvailable" code="archive.changeData.notAvailable"/>

<div class="infoRow odd clearfix">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.imageName" htmlEscape="true"/>
    </span>
    <span><c:out value="${image.imageNm}"/></span>
</div>
<div class="infoRow clearfix">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.description" htmlEscape="true"/>
    </span>
    <p class="left">
        <c:choose>
            <c:when test="${not empty image.changedExif.description}">
                ${archive:newlineToBr(fn:escapeXml(image.changedExif.description))}
            </c:when>
            <c:otherwise>
                <c:out value="${notAvailable}"/>
            </c:otherwise>
        </c:choose>
    </p>
</div>
<div class="infoRow clearfix odd">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.category" htmlEscape="true"/>
    </span>
    <p class="left">
        <c:choose>
            <c:when test="${not empty categories}">
                <c:out value="${categories}"/>
            </c:when>
            <c:otherwise>
                <c:out value="${notAvailable}"/>
            </c:otherwise>
        </c:choose>
    </p>
</div>
<div class="infoRow clearfix">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.keywords" htmlEscape="true"/>
    </span>
    <p class="left">
        <c:choose>
            <c:when test="${not empty keywords}">
                <c:out value="${keywords}"/>
            </c:when>
            <c:otherwise>
                <c:out value="${notAvailable}"/>
            </c:otherwise>
        </c:choose>
    </p>
</div>
<div class="infoRow clearfix odd">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.photographer" htmlEscape="true"/>
    </span>
    <span>
        <c:choose>
            <c:when test="${not empty image.changedExif.artist}">
                <c:out value="${image.changedExif.artist}"/>
            </c:when>
            <c:otherwise>
                <c:out value="${notAvailable}"/>
            </c:otherwise>
        </c:choose>
    </span>
</div>
<div class="infoRow clearfix">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.originalSizeWidth" htmlEscape="true"/>
    </span>
    <span>
        ${image.width}x${image.height}
    </span>
</div>
<div class="infoRow odd clearfix">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.resolution" htmlEscape="true"/>
    </span>
    <span>
        <c:choose>
            <c:when test="${empty image.changedExif.xResolution and empty image.changedExif.yResolution}">
                <c:out value="${notAvailable}"/>
            </c:when>
            <c:otherwise>
                <c:set var="xResolution" value="${empty image.changedExif.xResolution ? notAvailable : image.changedExif.xResolution}"/>
                <c:set var="yResolution" value="${empty image.changedExif.yResolution ? notAvailable : image.changedExif.yResolution}"/>
                <c:choose>
                    <c:when test="${3 eq image.changedExif.resolutionUnit}">
                        <spring:message code="archive.changeData.xResolutionYResolutionDPCM" arguments="${xResolution}, ${yResolution}" htmlEscape="true"/>
                    </c:when>
                    <c:when test="${2 eq image.changedExif.resolutionUnit}">
                        <spring:message code="archive.changeData.xResolutionYResolutionDPI" arguments="${xResolution}, ${yResolution}" htmlEscape="true"/>
                    </c:when>
                    <c:otherwise>
                        <spring:message code="archive.changeData.xResolutionYResolutionUnknown" arguments="${xResolution}, ${yResolution}" htmlEscape="true"/>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </span>
</div>
<div class="infoRow clearfix">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.originalFileSize" htmlEscape="true"/>
    </span>
    <span><spring:message code="archive.originalSizeKb" arguments="${image.fileSize / 1024.0}"/></span>
</div>
<div class="infoRow odd clearfix">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.originalFileType" htmlEscape="true"/>
    </span>
    <span><c:out value="${format.format}"/></span>
</div>
<div class="infoRow clearfix">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.id" htmlEscape="true"/>
    </span>
    <span>${image.id}</span>
</div>
<div class="infoRow odd clearfix">
    <span class="left" style="width:150px;">
        <spring:message code="archive.usedInImcms" htmlEscape="true"/>
    </span>
    <span>${archive:join(image.metaIds, ', ')}</span>
</div>
<div class="infoRow clearfix">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.uploadedBy" htmlEscape="true"/>
    </span>
    <span>

        <c:out value="${image.uploadedBy}"/>
    </span>
</div>
<div class="infoRow odd clearfix">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.copyright" htmlEscape="true"/>
    </span>
    <span>
        <c:choose>
            <c:when test="${not empty image.changedExif.copyright}">
                <c:out value="${image.changedExif.copyright}"/>
            </c:when>
            <c:otherwise>
                <c:out value="${notAvailable}"/>
            </c:otherwise>
        </c:choose>
    </span>
</div>
<div class="infoRow clearfix">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.licensePeriod" htmlEscape="true"/>
    </span>
    <span>
        <c:choose>
            <c:when test="${image.licenseDt ne null and image.licenseEndDt ne null}">
                <c:choose>
                    <c:when test="${image.licenseDt ne null}">
                        <spring:message code="archive.dateFormat" arguments="${image.licenseDt}" htmlEscape="true"/>
                    </c:when>
                    <c:otherwise>
                        <c:out value="${notAvailable}"/>
                    </c:otherwise>
                </c:choose>
                &#8211;
                <c:choose>
                    <c:when test="${image.licenseEndDt ne null}">
                        <spring:message code="archive.dateFormat" arguments="${image.licenseEndDt}" htmlEscape="true"/>
                    </c:when>
                    <c:otherwise>
                        <c:out value="${notAvailable}"/>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <c:out value="${notAvailable}"/>
            </c:otherwise>
        </c:choose>
    </span>
</div>
<div class="infoRow odd clearfix">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.altText" htmlEscape="true"/>
    </span>
    <span>
        <c:choose>
            <c:when test="${empty image.altText}">
                <c:out value="${notAvailable}"/>
            </c:when>
            <c:otherwise>
                <c:out value="${image.altText}"/>
            </c:otherwise>
        </c:choose>
    </span>
</div>