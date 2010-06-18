<h4><spring:message code="archive.changeData.chageImageData" htmlEscape="true"/></h4><div class="hr"></div>
<c:url var="changeDataUrl" value="/web/archive/image/${image.id}/change"/>
<form:form commandName="changeData" action="${changeDataUrl}" method="post" cssClass="m15t" enctype="multipart/form-data">
    <form:hidden path="changedFile"/>
    <div class="clearboth" style="min-height:30px;">
        <label for="file" class="left" style="width:130px;">
            <spring:message code="archive.changeData.changeImage" htmlEscape="true"/>
        </label>
        <div class="left">
            <input type="file" id="file" name="file"/>
            <spring:message var="uploadText" code="archive.addImage.upload" htmlEscape="true"/>
            <input id="upload" class="btnBlue small" type="submit" name="uploadAction" value="${uploadText}"/><br/>
            <form:errors path="file" cssClass="red"/>
        </div>
    </div>
    <%@ include file="/WEB-INF/jsp/image_archive/pages/fragments/change_data.jsp" %>
    <div class="hr m10t"></div>
    <div style="margin-top: 20px;text-align:center;">
        <spring:message var="saveText" code="archive.save" htmlEscape="true"/>
        <input id="save" type="submit" name="saveAction" value="${saveText}" class="btnBlue"/>

        <spring:message var="saveUseText" code="archive.saveUseInImcms" htmlEscape="true"/>
        <c:set var="disabled" value="${sessionScope.returnToImcms eq null}"/>
        <input id="saveUse" type="submit" name="useAction" value="${saveUseText}" class="btnBlue ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}"/>

        <spring:message var="saveReturnText" code="archive.saveReturnImageCard" htmlEscape="true"/>
        <input id="saveImageCard" type="submit" name="imageCardAction" value="${saveReturnText}" class="btnBlue"/>

        <spring:message var="cancelText" code="archive.cancel" htmlEscape="true"/>
        <input id="cancel" type="submit" name="cancelAction" value="${cancelText}" class="btnBlue"/>
    </div>
</form:form>