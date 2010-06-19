<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<%@ page import="com.imcode.imcms.api.*" %>
<% pageContext.setAttribute("user", ContentManagementSystem.fromRequest(request).getCurrentUser()); %>

<div class="clearfix">
    <h4 class="left">
        <spring:message code="archive.imageCard.imageInfo" htmlEscape="true"/>
    </h4>
    
    <c:if test="${not user.defaultUser and not image.archived}">
        <c:url var="exportUrl" value="/web/archive/image/${image.id}"/>
        <form:form action="${exportUrl}" commandName="exportImage" method="post" cssClass="right" cssStyle="margin-right:30px;display:inline;">
            <label for="width" class="left">
                <spring:message code="archive.imageCard.export.width" htmlEscape="true"/>
            </label>
            <form:input id="width" path="width" cssClass="left" cssStyle="width:80px;margin-left:5px;"/>
        
        
            <label for="height" class="left" style="margin-left:20px;">
                <spring:message code="archive.imageCard.export.height" htmlEscape="true"/>
            </label>
            <form:input id="height" path="height" cssClass="left" cssStyle="width:80px;margin-left:5px;"/>
            
        
            <label for="fileFormat" class="left" style="margin-left:20px;">
                <spring:message code="archive.imageCard.export.fileFormat" htmlEscape="true"/>
            </label>
            <select id="fileFormat" name="fileFormat" class="left" style="width:80px;margin-left:5px;">
                <c:forEach var="format" items="${exportImage.fileFormats}">
                    <option value="${format.ordinal}" ${exportImage.fileFormat eq format.ordinal ? 'selected="selected"' : ''} >
                        <c:out value="${fn:toLowerCase(format.format)}"/>
                    </option>
                </c:forEach>
            </select>
        
            <label for="quality" class="left" style="margin-left:20px;">
                <spring:message code="archive.imageCard.export.quality" htmlEscape="true"/>
            </label>
            <select id="quality" name="quality" class="left" style="width:80px;margin-left:5px;">
                <c:forEach var="quality" items="${exportImage.qualities}">
                    <option value="${quality}" ${exportImage.quality eq quality ? 'selected="selected"' : ''} >${quality}%</option>
                </c:forEach>
            </select>
        
            <spring:message var="exportText" code="archive.imageCard.export.exportButton" htmlEscape="true"/>
            <input type="submit" class="left btnBlue small" style="margin-left:20px;" name="export" value="${exportText}"/>
        </form:form>
    </c:if>
</div>

<div class="hr"></div>


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
    <p class="left" style="width:60%;">
        ${archive:newlineToBr(fn:escapeXml(image.changedExif.description))}
    </p>
</div>
<div class="minH20 clearboth">
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
