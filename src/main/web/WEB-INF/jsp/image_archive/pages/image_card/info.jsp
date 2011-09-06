<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<%@ page import="com.imcode.imcms.api.*" %>
<% pageContext.setAttribute("user", ContentManagementSystem.fromRequest(request).getCurrentUser()); %>
<spring:message var="notAvailable" code="archive.changeData.notAvailable"/>

<script type="text/javascript">
    /* setting the value of opposing dimension if aspect ration is checked and the opposite one is empty */
    var fillOnAspectRatio = function() {
        var initialWidth = ${image.width};
        var initialHeight = ${image.height};
        var width = $("#width");
        var height = $("#height");
        var keepRatio = $("#keepAspectRatio");
        var numberRegEx = /^\d+$/;

        if(keepRatio.is(":checked")) {
            var widthValue = width.val();
            var heightValue = height.val();

            if(widthValue.length > 0 && heightValue.length == 0) {
                if(widthValue.match(numberRegEx) && widthValue > 0){
                    var heightTmp = Math.round(initialHeight * (widthValue / initialWidth));
                    if(heightTmp > 0) {
                        height.val(heightTmp);
                    }
                }
            } else if(heightValue.length > 0 && widthValue.length == 0) {
                if(heightValue.match(numberRegEx) && heightValue > 0){
                    var widthTmp = Math.round(initialWidth * (heightValue / initialHeight));
                    if(widthTmp > 0) {
                        width.val(widthTmp);
                    }
                }
            }
        }
    }

    $(document).ready(function(){
       $("#width").blur(fillOnAspectRatio);
       $("#height").blur(fillOnAspectRatio);
    });
</script>
<c:if test="${not user.defaultUser and not image.archived}">
    <div class="modal" id="exportOverlay">
        <h4 class="section"><spring:message code="archive.imageCard.export.exportImage" htmlEscape="true"/></h4>
        <c:url var="exportUrl" value="/web/archive/image/${image.id}"/>
        <form:form action="${exportUrl}" commandName="exportImage" method="post" cssClass="right">
            <div class="clearboth minH30">
                <label for="width" class="left fixedWidth">
                    <spring:message code="archive.imageCard.export.width" htmlEscape="true"/>
                </label>
                <div class="left">
                    <form:input id="width" path="width" cssClass="left fixedWidthInput"/>
                </div>
                <div class="left" style="margin-left: 10px;">
                    <form:select  id="sizeUnit" path="sizeUnit">
                        <form:options items="${sizeUnits}" itemLabel="unitName"/>
                    </form:select>
                </div>
            </div>

            <div class="clearboth minH30">
                <label for="height" class="left fixedWidth">
                    <spring:message code="archive.imageCard.export.height" htmlEscape="true"/>
                </label>
                <div class="left">
                    <form:input id="height" path="height" cssClass="left fixedWidthInput"/>
                </div>
            </div>

            <div class="clearboth minH30">
                <div class="left fixedWidth">&nbsp;</div>
                <div class="left">
                    <form:checkbox id="keepAspectRatio" path="keepAspectRatio"/>
                    <label for="keepAspectRatio">
                        <spring:message code="archive.imageCard.export.keepAspectRatio" htmlEscape="true"/>
                    </label>
                </div>
            </div>

            <div class="clearboth minH30">
                <label for="fileFormat" class="left fixedWidth">
                    <spring:message code="archive.imageCard.export.fileFormat" htmlEscape="true"/>
                </label>
                <select id="fileFormat" name="fileFormat" class="left fixedWidthInput">
                    <c:forEach var="format" items="${exportImage.fileFormats}">
                        <option value="${format.ordinal}" ${exportImage.fileFormat eq format.ordinal ? 'selected="selected"' : ''} >
                            <c:out value="${fn:toLowerCase(format.format)}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="clearboth minH30">
                <label for="quality" class="left fixedWidth">
                    <spring:message code="archive.imageCard.export.quality" htmlEscape="true"/>
                </label>
                <select id="quality" name="quality" class="left fixedWidthInput">
                    <c:forEach var="quality" items="${exportImage.qualities}">
                        <option value="${quality}" ${exportImage.quality eq quality ? 'selected="selected"' : ''} >${quality}%</option>
                    </c:forEach>
                </select>
            </div>

            <div class="exportBtns">
                <spring:message var="exportText" code="archive.imageCard.export.exportButton" htmlEscape="true"/>
                <spring:message var="cancelText" code="archive.cancel" htmlEscape="true"/>
                <input type="button" class="btnBlue" name="cancel" value="${cancelText}" id="exportDialogCloseBtn"/>
                <input type="submit" class="btnBlue" name="export" value="${exportText}"/>
            </div>
        </form:form>
    </div>
</c:if>


<script type="text/javascript">
    var triggers;
    $(document).ready(function(){
        triggers = $(".modalInput").overlay({
            mask: {color:'gray', opacity:1.0},
            top: '25%',
            closeOnClick: false
        });

        $("#exportDialogCloseBtn").click(function(){
           triggers.eq(0).overlay().close();
        });
    });
</script>


<div class="m15t infoRow odd clearfix">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.imageName" htmlEscape="true"/>
    </span>
    <span><c:out value="${image.imageNm}"/></span>
</div>
<div class="infoRow clearfix">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.description" htmlEscape="true"/>
    </span>
    <p class="left" style="width:60%;">
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
    <span class="left" style="width:130px;">
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
    <span class="left" style="width:130px;">
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
    <span class="left" style="width:130px;">
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
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.originalSizeWidth" htmlEscape="true"/>
    </span>
    <span>
        ${image.width}x${image.height}
    </span>
</div>
<div class="infoRow odd clearfix">
    <span class="left" style="width:130px;">
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
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.originalFileSize" htmlEscape="true"/>
    </span>
    <span><spring:message code="archive.originalSizeKb" arguments="${image.fileSize / 1024.0}"/></span>
</div>
<div class="infoRow odd clearfix">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.id" htmlEscape="true"/>
    </span>
    <span>${image.id}</span>
</div>
<div class="infoRow clearfix">
    <span class="left" style="width:130px;">
        <spring:message code="archive.usedInImcms" htmlEscape="true"/>
    </span>
    <span>${archive:join(image.metaIds, ', ')}</span>
</div>
<div class="infoRow odd clearfix">
    <span class="left" style="width:130px;">
        <spring:message code="archive.changeData.uploadedBy" htmlEscape="true"/>
    </span>
    <span>

        <c:out value="${image.uploadedBy}"/>
    </span>
</div>
<div class="infoRow clearfix">
    <span class="left" style="width:130px;">
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
<div class="infoRow odd clearfix">
    <span class="left" style="width:130px;">
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
<div class="infoRow clearfix">
    <span class="left" style="width:130px;">
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