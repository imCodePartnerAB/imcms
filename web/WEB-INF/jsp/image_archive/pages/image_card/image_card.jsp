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
    <h4>
        <spring:message code="archive.imageCard.image" htmlEscape="true"/>
    </h4><div class="hr"></div>
    <c:url var="thumbUrl" value="/web/archive/thumb">
        <c:param name="id" value="${image.id}"/>
        <c:param name="size" value="medium"/>
        <c:param name="tmp" value="${changeData.changedFile eq true}"/>
    </c:url>
    <div style="margin:30px 0;text-align:center;">
        <c:url var="previewUrl" value="/web/archive/preview">
            <c:param name="id" value="${image.id}"/>
            <c:param name="tmp" value="${changeData.changedFile eq true}"/>
        </c:url>
        <a href="${previewUrl}" onclick="showPreview(${image.id}, ${image.width}, ${image.height}, ${changeData.changedFile eq true});return false;" target="_blank"><img src="${thumbUrl}" width="300" height="225"/></a>
    </div>
    <div style="text-align:center;margin-bottom:20px;">
        <% pageContext.setAttribute("user", ContentManagementSystem.fromRequest(request).getCurrentUser()); %>
        <c:if test="${not user.defaultUser and not image.archived}">
            <c:url var="useUrl" value="/web/archive/use">
                <c:param name="id" value="${image.id}"/>
            </c:url>
            <c:set var="disabled" value="${sessionScope.returnToImcms eq null}"/>
            <a href="${useUrl}" style="margin-right:2px;" class="btnBlue ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}">
                <span><spring:message code="archive.useInImcms" htmlEscape="true"/></span>
            </a>
        </c:if>
        <c:url var="infoUrl" value="/web/archive/image/${image.id}"/>
        <a href="${infoUrl}" style="margin-right:2px;" class="btnBlue">
            <span><spring:message code="archive.imageCard.showImageInfo" htmlEscape="true"/></span>
        </a>
        <c:if test="${not user.defaultUser and not image.archived}">
            <c:set var="disabled" value="${!image.canChange}"/>
            <c:url var="changeUrl" value="/web/archive/image/${image.id}/change"/>
            <a href="${changeUrl}" style="margin-right:2px;" class="btnBlue ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}">
                <span><spring:message code="archive.imageCard.changeImageData" htmlEscape="true"/></span>
            </a>
        </c:if>
        <c:url var="exifUrl" value="/web/archive/image/${image.id}/exif"/>
        <a href="${exifUrl}" style="margin-right:2px;" class="btnBlue">
            <span><spring:message code="archive.imageCard.showImageDataExif" htmlEscape="true"/></span>
        </a>
        <c:if test="${not user.defaultUser and not image.archived}">
            <c:url var="exportUrl" value="/web/archive/image/${image.id}/export"/>
            <a href="${exportUrl}" style="margin-right:2px;" class="btnBlue">
                <span><spring:message code="archive.imageCard.export" htmlEscape="true"/></span>
            </a>
            <c:set var="disabled" value="${!image.canChange}"/>
            <c:url var="eraseUrl" value="/web/archive/image/${image.id}/erase"/>
            <a href="${eraseUrl}" class="btnBlue ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}">
                <span><spring:message code="archive.imageCard.erase" htmlEscape="true"/></span>
            </a>
        </c:if>
    </div>
    
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
        <c:when test="${action eq 'export'}">
            <%@ include file="/WEB-INF/jsp/image_archive/pages/image_card/export.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="/WEB-INF/jsp/image_archive/pages/image_card/info.jsp" %>
            <div class="hr m10t"></div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/WEB-INF/jsp/image_archive/includes/footer.jsp" %>
