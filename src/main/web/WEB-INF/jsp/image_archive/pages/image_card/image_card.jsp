<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<%@ page import="com.imcode.imcms.api.*" %>
<spring:message var="title" code="archive.title.imageCard" htmlEscape="true"/>
<spring:message var="pageHeading" code="archive.pageHeading.imageCard" htmlEscape="true"/>
<c:set var="css">
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/imcms/jscalendar/skins/aqua/theme.css.jsp"/>
</c:set>
<c:set var="javascript">
    <%@ include file="/WEB-INF/jsp/image_archive/pages/fragments/jscalendar.jsp" %>
    <script type="text/javascript">
        initImageCard();

    /* setting the value of opposing dimension if aspect ration is checked and the opposite one is empty */
    var fillOnAspectRatio = function() {
        var initialWidth = ${image.width};
        var initialHeight = ${image.height};
        var width = $("#width", $('#exportOverlay', top.document));
        var height = $("#height", $('#exportOverlay', top.document));
        var keepRatio = $("#keepAspectRatio", $('#exportOverlay', top.document));
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
    };

        function closeExportOverlay(){
            $('#exportOverlay', top.document).hide();
            $('#overlay-shadow', top.document).hide();
        }

        function exportOverlay() {
            if($('#exportOverlay').length || $('#exportOverlay', top.document).length){
                var theShadow = $('<div id="overlay-shadow"/>');
                var overlay = $('#exportOverlay');
                $(theShadow).click(function(){
                    closeExportOverlay();
                });

                if($($('#overlay-shadow'), top.document).size() == 0){
                    $('body', top.document).append(theShadow);
                }

                $("#width").blur(fillOnAspectRatio);
                $("#height").blur(fillOnAspectRatio);
                $('body', top.document).append(overlay);
            }

            $('#exportOverlay', top.document).css('margin-left', (-$('#exportOverlay', top.document).width() / 2)+'px');
            $('#exportOverlay', top.document).css('margin-top', (-$('#exportOverlay', top.document).height() / 2)+'px');

            $('#exportOverlay', top.document).show();
            $('#overlay-shadow', top.document).show();
        }

        $(document).ready(function(){
            $('#exportBtn').click(function(){
                exportOverlay();
            });
            $("#exportDialogCloseBtn").click(function(){
                closeExportOverlay();
            });
        });
    </script>
</c:set>
<%@ include file="/WEB-INF/jsp/image_archive/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/top.jsp" %>

<div id="containerContent">
    <div style="margin-bottom: 10px;">
        <a href="${pageContext.request.contextPath}/web/archive" class="imcmsFormBtn">
            <spring:message code="archive.imageCard.backToSearchResults" htmlEscape="true"/>
        </a>
    </div>
    <h4 class="imcmsAdmHeading">
        <spring:message code="archive.imageCard.imagePropertiesFor" arguments="${image.imageNm}" htmlEscape="true"/>
    </h4>
    <c:url var="thumbUrl" value="/web/archive/thumb">
        <c:param name="id" value="${image.id}"/>
        <c:param name="size" value="medium"/>
        <c:param name="tmp" value="${action eq 'change'}"/>
    </c:url>
    <div class="clearfix m15t">
        <div style="float:left;">
            <div style="text-align:center;">
                <c:url var="previewUrl" value="/web/archive/preview">
                    <c:param name="id" value="${image.id}"/>
                    <c:param name="tmp" value="${action eq 'change'}"/>
                </c:url>
                <a href="${previewUrl}" onclick="return showPreview(${image.id}, ${image.width}, ${image.height}, ${action eq 'change'});return false;" target="_blank">
                    <img src="${thumbUrl}" width="300" height="225" alt="${image.imageNm}"/>
                </a><br/>
                <span class="hint"><spring:message code="archive.imageCard.clickToEnlarge" htmlEscape="true"/></span>

                <c:if test="${action eq 'change'}">
                    <form action="/" style="margin-top:10px;">
                        <spring:message var="rotateLeftText" code="archive.rotateLeft" htmlEscape="true"/>
                        <input type="button" class="imcmsFormBtnSmall" id="rotateLeft" value="${rotateLeftText}"/>

                        <spring:message var="rotateRightText" code="archive.rotateRight" htmlEscape="true"/>
                        <input type="button" class="imcmsFormBtnSmall" id="rotateRight" value="${rotateRightText}"/>
                    </form>
                </c:if>
            </div>
        </div>
        <div style="width:530px;float:right;">
        <c:choose>
            <c:when test="${action eq 'change'}">
                <%@ include file="/WEB-INF/jsp/image_archive/pages/image_card/change_data.jsp" %>
            </c:when>
            <c:when test="${action eq 'exif'}">
                <%@ include file="/WEB-INF/jsp/image_archive/pages/image_card/exportOverlay.jsp" %>
                <%@ include file="/WEB-INF/jsp/image_archive/pages/image_card/exif.jsp" %>
            </c:when>
            <c:otherwise>
                <%@ include file="/WEB-INF/jsp/image_archive/pages/image_card/exportOverlay.jsp" %>
                <%@ include file="/WEB-INF/jsp/image_archive/pages/image_card/info.jsp" %>
            </c:otherwise>
        </c:choose>
            <c:if test="${'change' ne action}">
                <div style="margin-top:20px;text-align:right;">
                <% pageContext.setAttribute("user", ContentManagementSystem.fromRequest(request).getCurrentUser()); %>
                <c:if test="${canUseInImcms and not image.archived}">
                    <c:url var="useUrl" value="/web/archive/use">
                        <c:param name="id" value="${image.id}"/>
                    </c:url>
                    <c:set var="disabled" value="${sessionScope.returnToImcms eq null}"/>
                    <a href="${useUrl}" style="margin-right:2px;" class="imcmsFormBtn ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}">
                        <span><spring:message code="archive.useInImcms" htmlEscape="true"/></span>
                    </a>
                </c:if>

                <c:if test="${image.canChange and not image.archived}">
                    <c:set var="disabled" value="${!image.canChange}"/>
                    <c:url var="changeUrl" value="/web/archive/image/${image.id}/change"/>
                    <a href="${changeUrl}" style="margin-right:2px;" class="imcmsFormBtn ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}">
                        <spring:message code="archive.imageCard.changeImageData" htmlEscape="true"/>
                    </a>
                </c:if>

                <c:if test="${'exif' eq action or 'erase' eq action}">
                    <c:url var="infoUrl" value="/web/archive/image/${image.id}"/>
                    <a href="${infoUrl}" style="margin-right:2px;" class="imcmsFormBtn">
                        <spring:message code="archive.imageCard.showImageInfo" htmlEscape="true"/>
                    </a>
                </c:if>

                <c:if test="${'exif' ne action}">
                    <c:url var="exifUrl" value="/web/archive/image/${image.id}/exif"/>
                    <a href="${exifUrl}" style="margin-right:2px;" class="imcmsFormBtn">
                        <spring:message code="archive.imageCard.showImageDataExif" htmlEscape="true"/>
                    </a>
                </c:if>

                <c:if test="${image.canChange and not image.archived}">
                    <spring:message var="eraseConfirm" code="archive.imageCard.eraseConfirm"/>
                    <c:if test="${not empty image.metaIds}">
                        <archive:params var="infoParams">
                            <archive:param value="${archive:join(image.metaIds, ', ')}"/>
                        </archive:params>

                        <spring:message var="eraseConfirmInfo" code="archive.imageCard.eraseConfirmInfo" arguments="${infoParams}"/>
                        <c:set var="eraseConfirm" value="${eraseConfirm} ${eraseConfirmInfo}"/>
                    </c:if>
                    <script type="text/javascript">
                        $(document).ready(function(){
                            var eraseWarningMessage = '${eraseConfirm}';
                            $("#eraseBtn").click(function(){
                                return confirm(eraseWarningMessage);
                            });
                        });
                    </script>
                    <c:set var="disabled" value="${!image.canChange}"/>
                    <c:url var="eraseUrl" value="/web/archive/image/${image.id}/erase"/>
                    <a id="eraseBtn" href="${eraseUrl}" style="margin-right:2px;" class="imcmsFormBtn ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}">
                        <spring:message code="archive.imageCard.erase" htmlEscape="true"/>
                    </a>
                </c:if>

                <c:if test="${user.superAdmin and image.archived}">
                    <c:url var="unarchiveUrl" value="/web/archive/image/${image.id}/unarchive"/>
                    <a href="${unarchiveUrl}" class="imcmsFormBtn">
                        <spring:message code="archive.imageCard.unarchive" htmlEscape="true"/>
                    </a>
                </c:if>

                <c:if test="${canExport and not image.archived}">
                    <spring:message var="exportBtnText" code="archive.imageCard.export" htmlEscape="true"/>
                    <span class="imcmsFormBtn" id="exportBtn">${exportBtnText}</span>
                </c:if>
                </div>
            </c:if>
    </div>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/image_archive/includes/footer.jsp" %>
