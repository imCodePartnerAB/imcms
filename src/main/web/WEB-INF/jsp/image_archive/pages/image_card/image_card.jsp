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
    </script>
</c:set>
<%@ include file="/WEB-INF/jsp/image_archive/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/top.jsp" %>

<div id="containerContent">
    <div style="margin-bottom: 10px;">
        <a href="${pageContext.request.contextPath}/web/archive" class="btnBlue">
            <span><spring:message code="archive.imageCard.backToSearchResults" htmlEscape="true"/></span>
        </a>
    </div>
    <h4 class="section">
        <spring:message code="archive.imageCard.imagePropertiesFor" arguments="${image.imageNm}" htmlEscape="true"/>
    </h4>
    <c:url var="thumbUrl" value="/web/archive/thumb">
        <c:param name="id" value="${image.id}"/>
        <c:param name="size" value="medium"/>
        <c:param name="tmp" value="${action eq 'change'}"/>
    </c:url>
    <div class="clearfix">
        <div style="float:left;">
        <div style="text-align:center;">
            <c:url var="previewUrl" value="/web/archive/preview">
                <c:param name="id" value="${image.id}"/>
                <c:param name="tmp" value="${action eq 'change'}"/>
            </c:url>
            <a href="${previewUrl}" onclick="showPreview(${image.id}, ${image.width}, ${image.height}, ${action eq 'change'});return false;" target="_blank">
                <img src="${thumbUrl}" width="300" height="225" alt="${image.imageNm}"/>
            </a><br/>
            <span class="hint"><spring:message code="archive.imageCard.clickToEnlarge" htmlEscape="true"/></span>

            <c:if test="${action eq 'change'}">
                <form action="/" style="margin-top:5px;">
                    <spring:message var="rotateLeftText" code="archive.rotateLeft" htmlEscape="true"/>
                    <input type="button" class="btnBlue small" id="rotateLeft" value="${rotateLeftText}"/>

                    <spring:message var="rotateRightText" code="archive.rotateRight" htmlEscape="true"/>
                    <input type="button" class="btnBlue small" id="rotateRight" value="${rotateRightText}"/>
                </form>
            </c:if>
        </div>
    </div>
        <div style="float:left;margin-left:20px;">
        <c:choose>
            <c:when test="${action eq 'change'}">
                <%@ include file="/WEB-INF/jsp/image_archive/pages/image_card/change_data.jsp" %>
            </c:when>
            <c:when test="${action eq 'erase'}">
                <%@ include file="/WEB-INF/jsp/image_archive/pages/image_card/erase.jsp" %>
            </c:when>
            <c:when test="${action eq 'exif'}">
                <%@ include file="/WEB-INF/jsp/image_archive/pages/image_card/exif.jsp" %>
            </c:when>
            <c:otherwise>
                <%@ include file="/WEB-INF/jsp/image_archive/pages/image_card/info.jsp" %>
            </c:otherwise>
        </c:choose>
        <div style="margin-top:20px;text-align:right;">
            <% pageContext.setAttribute("user", ContentManagementSystem.fromRequest(request).getCurrentUser()); %>
            <c:if test="${'change' ne action}">
                <c:if test="${canUseInImcms and not image.archived}">
                    <c:url var="useUrl" value="/web/archive/use">
                        <c:param name="id" value="${image.id}"/>
                    </c:url>
                    <c:set var="disabled" value="${sessionScope.returnToImcms eq null}"/>
                    <a href="${useUrl}" style="margin-right:2px;" class="btnBlue ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}">
                        <span><spring:message code="archive.useInImcms" htmlEscape="true"/></span>
                    </a>
                </c:if>

                <c:if test="${'exif' eq action}">
                    <c:url var="infoUrl" value="/web/archive/image/${image.id}"/>
                    <a href="${infoUrl}" style="margin-right:2px;" class="btnBlue">
                        <span><spring:message code="archive.imageCard.showImageInfo" htmlEscape="true"/></span>
                    </a>
                </c:if>

                <c:if test="${image.canChange and not image.archived}">
                    <c:set var="disabled" value="${!image.canChange}"/>
                    <c:url var="changeUrl" value="/web/archive/image/${image.id}/change"/>
                    <a href="${changeUrl}" style="margin-right:2px;" class="btnBlue ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}">
                        <span><spring:message code="archive.imageCard.changeImageData" htmlEscape="true"/></span>
                    </a>
                </c:if>

                <c:if test="${'exif' ne action}">
                    <c:url var="exifUrl" value="/web/archive/image/${image.id}/exif"/>
                    <a href="${exifUrl}" style="margin-right:2px;" class="btnBlue">
                        <span><spring:message code="archive.imageCard.showImageDataExif" htmlEscape="true"/></span>
                    </a>
                </c:if>

                <c:if test="${image.canChange and not image.archived}">
                    <c:set var="disabled" value="${!image.canChange}"/>
                    <c:url var="eraseUrl" value="/web/archive/image/${image.id}/erase"/>
                    <a href="${eraseUrl}" class="btnBlue ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}">
                        <span><spring:message code="archive.imageCard.erase" htmlEscape="true"/></span>
                    </a>
                </c:if>

                <c:if test="${user.superAdmin and image.archived}">
                    <c:url var="unarchiveUrl" value="/web/archive/image/${image.id}/unarchive"/>
                    <a href="${unarchiveUrl}" class="btnBlue">
                        <span><spring:message code="archive.imageCard.unarchive" htmlEscape="true"/></span>
                    </a>
                </c:if>

                <c:if test="${canExport and not image.archived}">
                    <input type="button" class="btnBlue modalInput" name="export" value="Export" rel="#exportOverlay"/>
                </c:if>
            </c:if>
        </div>
    </div>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/image_archive/includes/footer.jsp" %>
