<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<spring:message var="title" code="archive.title.addImage" htmlEscape="true"/>
<spring:message var="pageHeading" code="archive.pageHeading.addImage" htmlEscape="true"/>
<c:set var="currentPage" value="addImage"/>
<c:set var="css">
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/imcms/jscalendar/skins/aqua/theme.css.jsp"/>
</c:set>
<c:set var="javascript">
    <%@ include file="/WEB-INF/jsp/image_archive/pages/fragments/jscalendar.jsp" %>
    <script type="text/javascript">
        initAddImage();
    </script>
</c:set>
<c:set var="css">
    <link href="${contextPath}/js/jquery.uploadify-v2.1.4/uploadify.css" rel="stylesheet" type="text/css" />
</c:set>
<%@ include file="/WEB-INF/jsp/image_archive/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/top.jsp" %>
<div id="containerContent">
    <c:url var="uploadUrl" value="/web/archive/add-image/upload"/>
    <form:form commandName="upload" action="${uploadUrl}" method="post" enctype="multipart/form-data" cssClass="m15t clearfix">
        <label for="uploadify" style="margin-right:10px;">
            <spring:message code="archive.addImage.selectImage" htmlEscape="true"/>
        </label>
            <input id="uploadify" type="file" name="file"/>
            <spring:message var="uploadText" code="archive.addImage.upload" htmlEscape="true"/>
            <input id="uploadButton" type="button" value="${uploadText}" class="btnBlue"/>
            <a href="${latestUploadsUrl}">Latest uploads</a>
            <h4 class="section"><spring:message code="archive.addImage.selectedImages" htmlEscape="true"/></h4>
            <div id="uploadifyQueue" class="uploadifyQueue"></div>
            <c:url var="latestUploadsUrl" value="/web/archive?show=1&sortBy=2&sortOrder=1"/>
    </form:form>
    
    <c:if test="${image ne null}">
        <c:url var="thumbUrl" value="/web/archive/thumb">
            <c:param name="id" value="${image.id}"/>
            <c:param name="size" value="medium"/>
        </c:url>
        <div style="margin:30px 0;text-align:center;">
            <c:url var="previewUrl" value="/web/archive/preview">
                <c:param name="id" value="${image.id}"/>
            </c:url>
            <a href="${previewUrl}" onclick="showPreview(${image.id}, ${image.width}, ${image.height});return false;" target="_blank">
                <img src="${thumbUrl}" width="300" height="225" alt="${image.filename}"/>
            </a><br/>

            <form action="/" style="margin-top:5px;">
                <spring:message var="rotateLeftText" code="archive.rotateLeft" htmlEscape="true"/>
                <input type="button" class="btnBlue small" id="rotateLeft" value="${rotateLeftText}"/>
                
                <spring:message var="rotateRightText" code="archive.rotateRight" htmlEscape="true"/>
                <input type="button" class="btnBlue small" id="rotateRight" value="${rotateRightText}"/>
            </form>
        </div>
    
        <h4>
            <spring:message code="archive.addImage.changeImageData" htmlEscape="true"/>
        </h4>
        <c:url var="changeDataUrl" value="/web/archive/add-image/change"/>
        <form:form commandName="changeData" action="${changeDataUrl}" method="post" cssClass="m15t">
            <%@ include file="/WEB-INF/jsp/image_archive/pages/fragments/change_data.jsp" %>
            <div class="clearboth m10t hr"></div>
            <div style="margin-top: 20px;text-align:center;">
                <spring:message var="saveText" code="archive.save" htmlEscape="true"/>
                <input id="save" type="submit" name="saveAction" value="${saveText}" class="btnBlue"/>
                <spring:message var="saveAddText" code="archive.addImage.saveAddNew" htmlEscape="true"/>
                <input id="saveAdd" type="submit" name="addAction" value="${saveAddText}" class="btnBlue"/>
                <spring:message var="saveUseText" code="archive.saveUseInImcms" htmlEscape="true"/>
                <c:set var="disabled" value="${sessionScope.returnToImcms eq null}"/>
                <input id="saveUse" type="submit" name="useAction" value="${saveUseText}" class="btnBlue ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}"/>
                <spring:message var="saveReturnText" code="archive.saveReturnImageCard" htmlEscape="true"/>
                <input id="saveImageCard" type="submit" name="imageCardAction" value="${saveReturnText}" class="btnBlue"/>
                <spring:message var="discontinueText" code="archive.addImage.discontinue" htmlEscape="true"/>
                <input id="discontinue" type="submit" name="discontinueAction" value="${discontinueText}" class="btnBlue"/>
            </div>
        </form:form>
    </c:if>
</div>

<%@ include file="/WEB-INF/jsp/image_archive/includes/footer.jsp" %>