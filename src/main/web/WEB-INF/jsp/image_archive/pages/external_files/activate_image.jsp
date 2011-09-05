<c:choose>
    <c:when test="${activateError}">
        <div style="text-align:center;">
            <h3><spring:message code="archive.externalFiles.activate.cantActivate" htmlEscape="true"/></h3><br/><br/>
            
            <c:url var="backUrl" value="/web/archive/external-files"/>
            <a href="${backUrl}" class="btnBlue">
                <span><spring:message code="archive.back" htmlEscape="true"/></span>
            </a>
        </div>
    </c:when>
    <c:otherwise>
        <h4 class="section">
            <spring:message code="archive.addImage.changeImageData" htmlEscape="true"/>
        </h4>
        <div style="width:45%;float:left;">
            <c:url var="thumbUrl" value="/web/archive/thumb">
                <c:param name="id" value="${image.id}"/>
                <c:param name="size" value="medium"/>
            </c:url>
            <div style="margin:30px 0;text-align:center;">
                <c:url var="previewUrl" value="/web/archive/preview">
                    <c:param name="id" value="${image.id}"/>
                </c:url>
                <a href="${previewUrl}" onclick="showPreview(${image.id}, ${image.width}, ${image.height});return false;" target="_blank">
                    <img src="${thumbUrl}" width="300" height="225"/>
                </a><br/>

                <form action="/" style="margin-top:5px;">
                    <spring:message var="rotateLeftText" code="archive.rotateLeft" htmlEscape="true"/>
                    <input type="button" class="btnBlue small" id="rotateLeft" value="${rotateLeftText}"/>

                    <spring:message var="rotateRightText" code="archive.rotateRight" htmlEscape="true"/>
                    <input type="button" class="btnBlue small" id="rotateRight" value="${rotateRightText}"/>
                </form>
            </div>
        </div>

            <c:url var="changeUrl" value="/web/archive/external-files/change"/>
            <form:form commandName="changeData" action="${changeUrl}" method="post" cssClass="clearfix m15t">
                <div style="width:50%;float:left;margin-left:20px;">
                    <%@ include file="/WEB-INF/jsp/image_archive/pages/fragments/change_data.jsp" %>
                </div>
                <div style="margin-top: 20px;text-align:right;">
                    <spring:message var="saveText" code="archive.save" htmlEscape="true"/>
                    <input type="submit" class="btnBlue" name="save" value="${saveText}"/>

                    <spring:message var="saveActivateText" code="archive.externalFiles.activate.saveActivate" htmlEscape="true"/>
                    <input type="submit" class="btnBlue" name="saveActivate" value="${saveActivateText}"/>

                    <c:set var="disabled" value="${sessionScope.returnToImcms eq null}"/>
                    <spring:message var="saveUseText" code="archive.saveUseInImcms" htmlEscape="true"/>
                    <input type="submit" class="btnBlue ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}" name="saveUse" value="${saveUseText}"/>

                    <spring:message var="saveImageCardText" code="archive.saveReturnImageCard" htmlEscape="true"/>
                    <input type="submit" class="btnBlue" name="saveImageCard" value="${saveImageCardText}"/>
                    
                    <spring:message var="cancelText" code="archive.cancel" htmlEscape="true"/>
                    <input type="submit" class="btnBlue" name="cancel" value="${cancelText}"/>
                </div>
            </form:form>

        <div style="clear:both;"></div>
    </c:otherwise>
</c:choose>
