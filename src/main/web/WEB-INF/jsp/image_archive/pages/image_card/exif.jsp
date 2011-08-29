<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<h4><spring:message code="archive.imageCard.originalExif" htmlEscape="true"/></h4>
<spring:message var="notAvailable" code="archive.changeData.notAvailable"/>
<div class="m15t minH20">
    <span class="left odd" style="width:150px;">
        <spring:message code="archive.changeData.exif.manufacturer" htmlEscape="true"/>
    </span>
    <span>
        <c:out value="${image.originalExif.manufacturer}" default="${notAvailable}"/>
    </span>
</div>
<div class="clearboth minH20">
    <span class="left even" style="width:150px;">
        <spring:message code="archive.changeData.exif.model" htmlEscape="true"/>
    </span>
    <span><c:out value="${image.originalExif.model}" default="${notAvailable}"/></span>
</div>
<div class="clearboth minH20">
    <span class="left odd" style="width:150px;">
        <spring:message code="archive.changeData.exif.compression" htmlEscape="true"/>
    </span>
    <span><c:out value="${image.originalExif.compression}" default="${notAvailable}"/></span>
</div>
<div class="minH20">
    <span class="left even" style="width:150px;">
        <spring:message code="archive.changeData.xResolution" htmlEscape="true"/>
    </span>
    <span>
        <c:out value="${image.originalExif.xResolution}" default="${notAvailable}"/>
    </span>
</div>
<div class="minH20">
    <span class="left odd" style="width:150px;">
        <spring:message code="archive.changeData.yResolution" htmlEscape="true"/>
    </span>
    <span>
        <c:out value="${image.originalExif.yResolution}" default="${notAvailable}"/>
    </span>
</div>
<div class="minH20">
    <span class="left even" style="width:150px;">
        <spring:message code="archive.changeData.exif.resolutionUnits" htmlEscape="true"/>
    </span>
    <span>
        <c:choose>
            <c:when test="${empty image.originalExif.resolutionUnit}">
                <c:out value="${notAvailable}"/>
            </c:when>
            <c:otherwise>
                <c:out value="${2 eq image.originalExif.resolutionUnit ? 'Inches' : 'Centimeters'}"/>
            </c:otherwise>
        </c:choose>
    </span>
</div>
<div class="minH20">
    <span class="left odd" style="width:150px;">
        <spring:message code="archive.changeData.exif.exposure" htmlEscape="true"/>
    </span>
    <span>
        <c:set var="exposureValue" value="${image.originalExif.exposure}"/>
        <c:choose>
            <c:when test="${empty exposureValue}">
                <spring:message code="archive.changeData.notAvailable" htmlEscape="true"/>
            </c:when>
            <c:otherwise>
                <c:out value="${archive:doubleToFractionsString(exposureValue)} sec."/>
            </c:otherwise>
        </c:choose>
    </span>
</div>
<div class="minH20">
    <span class="left even" style="width:150px;">
        <spring:message code="archive.changeData.exif.fNumber" htmlEscape="true"/>
    </span>
    <span>
        <c:choose>
            <c:when test="${empty image.originalExif.fStop}">
                <spring:message code="archive.changeData.notAvailable" htmlEscape="true"/>
            </c:when>
            <c:otherwise>
                <c:out value="f/${image.originalExif.fStop}"/>
            </c:otherwise>
        </c:choose>
    </span>
</div>
<div class="minH20">
    <span class="left odd" style="width:150px;">
        <spring:message code="archive.changeData.exif.exposureProgram" htmlEscape="true"/>
    </span>
    <span>
        <c:out value="${image.originalExif.exposureProgram}" default="${notAvailable}"/>
    </span>
</div>
<div class="minH20">
    <span class="left even" style="width:150px;">
        <spring:message code="archive.changeData.exif.datetimeDigitized" htmlEscape="true"/>
    </span>
    <span>
        <c:choose>
            <c:when test="${not empty image.originalExif.dateDigitized}">
                <fmt:formatDate value="${image.originalExif.dateDigitized}" pattern="yyyy:MM:dd HH:mm:ss"/>
            </c:when>
            <c:otherwise>
                <spring:message code="archive.changeData.notAvailable" htmlEscape="true"/>
            </c:otherwise>
        </c:choose>
    </span>
</div>
<div class="minH20">
    <span class="left odd" style="width:150px;">
        <spring:message code="archive.changeData.exif.datetimeOriginal" htmlEscape="true"/>
    </span>
    <span>
        <c:choose>
            <c:when test="${not empty image.originalExif.dateOriginal}">
                <fmt:formatDate value="${image.originalExif.dateOriginal}" pattern="yyyy:MM:dd HH:mm:ss"/>
            </c:when>
            <c:otherwise>
                <spring:message code="archive.changeData.notAvailable" htmlEscape="true"/>
            </c:otherwise>
        </c:choose>
    </span>
</div>
<div class="minH20">
    <span class="left even" style="width:150px;">
        <spring:message code="archive.changeData.exif.flash" htmlEscape="true"/>
    </span>
    <span>
        <c:choose>
            <c:when test="${'NOT_FIRED' eq image.originalExif.flash}">
                <spring:message code="archive.changeData.exif.flashNotFired" htmlEscape="true"/>
            </c:when>
            <c:when test="${'FIRED' eq image.originalExif.flash}">
                <spring:message code="archive.changeData.exif.flashFired" htmlEscape="true"/>
            </c:when>
            <c:when test="${'FIRED_WITH_RED_EYES_REDUCTION' eq image.originalExif.flash}">
                <spring:message code="archive.changeData.exif.flashFiredWithRedEyeReduction" htmlEscape="true"/>
            </c:when>
            <c:otherwise>
                <spring:message code="archive.changeData.notAvailable" htmlEscape="true"/>
            </c:otherwise>
        </c:choose>
    </span>
</div>
<div class="minH20">
    <span class="left odd" style="width:150px;">
        <spring:message code="archive.changeData.exif.focalLength" htmlEscape="true"/>
    </span>
    <span>
        <c:choose>
            <c:when test="${empty image.originalExif.focalLength}">
                <spring:message code="archive.changeData.notAvailable" htmlEscape="true"/>
            </c:when>
            <c:otherwise>
                <c:out value="${image.originalExif.focalLength} m.m."/>
            </c:otherwise>
        </c:choose>
    </span>
</div>
<div class="minH20">
    <span class="left even" style="width:150px;">
        <spring:message code="archive.changeData.exif.colorSpace" htmlEscape="true"/>
    </span>
    <span>
        <c:out value="${image.originalExif.colorSpace}" default="${notAvailable}"/>
    </span>
</div>
<div class="minH20">
    <span class="left odd" style="width:150px;">
        <spring:message code="archive.changeData.exif.pixelXDimension" htmlEscape="true"/>
    </span>
    <span>
        <c:out value="${image.originalExif.pixelXDimension}" default="${notAvailable}"/>
    </span>
</div>
<div class="minH20">
    <span class="left even" style="width:150px;">
        <spring:message code="archive.changeData.exif.pixelYDimension" htmlEscape="true"/>
    </span>
    <span>
        <c:out value="${image.originalExif.pixelYDimension}" default="${notAvailable}"/>
    </span>
</div>
<div class="clearboth minH20">
    <span class="left odd" style="width:150px;">
        <spring:message code="archive.changeData.exif.ISO" htmlEscape="true"/>
    </span>
    <span><c:out value="${image.originalExif.ISO}" default="${notAvailable}"/></span>
</div>
<%--<div class="clearboth minH20">--%>
    <%--<span class="left" style="width:150px;">--%>
        <%--<spring:message code="archive.changeData.description"/>--%>
    <%--</span>--%>
    <%--<p class="left" style="width:60%;">--%>
        <%--<c:choose>--%>
            <%--<c:when test="${empty image.originalExif.description}">--%>
                <%--<spring:message code="archive.changeData.notAvailable" htmlEscape="true"/>--%>
            <%--</c:when>--%>
            <%--<c:otherwise>--%>
                <%--${archive:newlineToBr(fn:escapeXml(image.originalExif.description))}--%>
            <%--</c:otherwise>--%>
        <%--</c:choose>--%>
    <%--</p>--%>
<%--</div>--%>
<%--<div class="clearboth minH20">--%>
    <%--<span class="left" style="width:150px;">--%>
        <%--<spring:message code="archive.changeData.photographer" htmlEscape="true"/>--%>
    <%--</span>--%>
    <%--<span>--%>
        <%--<c:choose>--%>
            <%--<c:when test="${empty image.originalExif.artist}">--%>
                <%--<spring:message code="archive.changeData.notAvailable" htmlEscape="true"/>--%>
            <%--</c:when>--%>
            <%--<c:otherwise>--%>
                <%--${archive:newlineToBr(fn:escapeXml(image.originalExif.artist))}--%>
            <%--</c:otherwise>--%>
        <%--</c:choose>--%>
    <%--</span>--%>
<%--</div>--%>
<%--<div class="minH20">--%>
    <%--<span class="left" style="width:150px;">--%>
        <%--<spring:message code="archive.changeData.copyright" htmlEscape="true"/>--%>
    <%--</span>--%>
    <%--<span>--%>
        <%--<c:choose>--%>
            <%--<c:when test="${empty image.originalExif.copyright}">--%>
                <%--<spring:message code="archive.changeData.notAvailable" htmlEscape="true"/>--%>
            <%--</c:when>--%>
            <%--<c:otherwise>--%>
                <%--${archive:newlineToBr(fn:escapeXml(image.originalExif.copyright))}--%>
            <%--</c:otherwise>--%>
        <%--</c:choose>--%>
    <%--</span>--%>
<%--</div>--%>