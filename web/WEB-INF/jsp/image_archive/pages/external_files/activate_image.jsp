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
        <h4>
            <spring:message code="archive.addImage.changeImageData" htmlEscape="true"/>
        </h4>
        <div class="hr"></div>

        <c:url var="changeUrl" value="/web/archive/external-files/change"/>
        <form:form commandName="changeData" action="${changeUrl}" method="post" cssClass="clearfix m15t">
            <%@ include file="/WEB-INF/jsp/image_archive/pages/fragments/change_data.jsp" %>
            <div class="hr m10t"></div>
            <div style="margin-top: 20px;text-align:center;">
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
    </c:otherwise>
</c:choose>
