<h4 class="imcmsAdmHeading">
    <spring:message code="archive.addImage.changeImageData" htmlEscape="true"/>
</h4>
<c:url var="changeUrl" value="/web/archive/external-files/change"/>
<form:form commandName="changeData" action="${changeUrl}" method="post" cssClass="m15t clearfix">
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
                    <img src="${thumbUrl}" width="300" height="225"/>
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
            <input type="submit" class="imcmsFormBtn" name="save" value="${saveText}"/>

            <spring:message var="saveActivateText" code="archive.externalFiles.activate.saveActivate" htmlEscape="true"/>
            <input type="submit" class="imcmsFormBtn" name="saveActivate" value="${saveActivateText}"/>

            <c:set var="disabled" value="${sessionScope.returnToImcms eq null}"/>
            <c:if test="${not disabled}">
                <spring:message var="saveUseText" code="archive.saveUseInImcms" htmlEscape="true"/>
                <input type="submit" class="imcmsFormBtn ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}" name="saveUse" value="${saveUseText}"/>
            </c:if>            

            <spring:message var="saveImageCardText" code="archive.saveReturnImageCard" htmlEscape="true"/>
            <input type="submit" class="imcmsFormBtn" name="saveImageCard" value="${saveImageCardText}"/>

            <spring:message var="cancelText" code="archive.cancel" htmlEscape="true"/>
            <input type="submit" class="imcmsFormBtn" name="cancel" value="${cancelText}"/>
        </div>

    </div>
</form:form>
