<%@ page contentType="text/html" pageEncoding="UTF-8" %>
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
        $(document).ready(function(){
            initAddImage();
        });
    </script>
</c:set>
<c:set var="css">
    <link href="${pageContext.servletContext.contextPath}/js/jquery.uploadify-v2.1.4/uploadify.css" rel="stylesheet" type="text/css" />
</c:set>
<%@ include file="/WEB-INF/jsp/image_archive/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/top.jsp" %>
<div id="containerContent">
    <c:url var="uploadUrl" value="/web/archive/add-image/upload"/>
    <form:form commandName="upload" action="${uploadUrl}" method="post" enctype="multipart/form-data" cssClass="m15t clearfix">
        <div class="addImageControls">
            <label for="uploadify" class="left" style="margin:3px 20px 3px 0;">
                <spring:message code="archive.addImage.selectImage" htmlEscape="true"/>
            </label>
            <div class="UploadifyButtonWrapper">
                <button type="button" class="imcmsFormBtn"><spring:message code="archive.addImage.browse" htmlEscape="true"/></button>
                <div class="UploadifyObjectWrapper">
                    <input id="uploadify" type="file" name="file"/>
                </div>
            </div>
            <spring:message var="uploadText" code="archive.addImage.upload" htmlEscape="true"/>
            <input id="uploadButton" type="button" value="${uploadText}" class="imcmsFormBtn"/>
        </div>
        <h4 class="imcmsAdmHeading"><spring:message code="archive.addImage.selectedImages" htmlEscape="true"/></h4>
        <div id="uploadifyQueue" class="uploadifyQueue"></div>
    </form:form>
    
    <c:if test="${image ne null}">
        <c:url var="changeDataUrl" value="/web/archive/add-image/change"/>
        <form:form commandName="changeData" action="${changeDataUrl}" method="post" cssClass="m15t clearfix">
            <div class="clearfix left">
                <div class="clearfix">
                <div style="float:left;">
                    <c:url var="thumbUrl" value="/web/archive/thumb">
                        <c:param name="id" value="${image.id}"/>
                        <c:param name="size" value="medium"/>
                    </c:url>
                    <div style="text-align:center;">
                        <c:url var="previewUrl" value="/web/archive/preview">
                            <c:param name="id" value="${image.id}"/>
                        </c:url>
                        <a href="${previewUrl}" onclick="showPreview(${image.id}, ${image.width}, ${image.height});return false;" target="_blank">
                            <img src="${thumbUrl}" width="300" height="225" alt="${image.imageNm}"/>
                        </a><br/>

                        <span class="hint"><spring:message code="archive.imageCard.clickToEnlarge" htmlEscape="true"/></span>

                        <div style="margin-top:10px;">
                            <spring:message var="rotateLeftText" code="archive.rotateLeft" htmlEscape="true"/>
                            <input type="button" class="imcmsFormBtnSmall" id="rotateLeft" value="${rotateLeftText}"/>

                            <spring:message var="rotateRightText" code="archive.rotateRight" htmlEscape="true"/>
                            <input type="button" class="imcmsFormBtnSmall" id="rotateRight" value="${rotateRightText}"/>
                        </div>
                    </div>
                </div>
                <div style="float:left;margin-left:20px;">
                    <%@ include file="/WEB-INF/jsp/image_archive/pages/fragments/change_data.jsp" %>
                </div>
            </div>

            <div style="margin-top: 20px;text-align:right;">
                <spring:message var="saveText" code="archive.save" htmlEscape="true"/>
                <input id="save" type="submit" name="saveAction" value="${saveText}" class="imcmsFormBtn"/>
                <spring:message var="saveAddText" code="archive.addImage.saveAddNew" htmlEscape="true"/>
                <input id="saveAdd" type="submit" name="addAction" value="${saveAddText}" class="imcmsFormBtn"/>
                <c:set var="disabled" value="${sessionScope.returnToImcms eq null}"/>
                <c:if test="${not disabled}">
                    <spring:message var="saveUseText" code="archive.saveUseInImcms" htmlEscape="true"/>
                    <input id="saveUse" type="submit" name="useAction" value="${saveUseText}" class="imcmsFormBtn ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}"/>
                </c:if>
                <spring:message var="saveReturnText" code="archive.saveReturnImageCard" htmlEscape="true"/>
                <input id="saveImageCard" type="submit" name="imageCardAction" value="${saveReturnText}" class="imcmsFormBtn"/>
                <spring:message var="discontinueText" code="archive.addImage.discontinue" htmlEscape="true"/>
                <input id="discontinue" type="submit" name="discontinueAction" value="${discontinueText}" class="imcmsFormBtn"/>
            </div>
                </div>
        </form:form>
    </c:if>
</div>

<%@ include file="/WEB-INF/jsp/image_archive/includes/footer.jsp" %>