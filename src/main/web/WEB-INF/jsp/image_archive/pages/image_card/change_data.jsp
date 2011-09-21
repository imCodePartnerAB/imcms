<c:url var="changeDataUrl" value="/web/archive/image/${image.id}/change"/>
<form:form commandName="changeData" action="${changeDataUrl}" method="post" cssClass="clearfix" enctype="multipart/form-data">
    <div class="left">
        <form:hidden path="changedFile"/>
        <div class="clearfix" style="min-height:30px;">
            <label for="file" class="left" style="width:150px;">
                <spring:message code="archive.changeData.changeImage" htmlEscape="true"/>
            </label>
            <input type="file" id="file" name="file" class="left"/>
            <spring:message var="uploadText" code="archive.addImage.upload" htmlEscape="true"/>
            <input id="upload" class="imcmsFormBtnSmall right" type="submit" name="uploadAction" value="${uploadText}"/><br/>
            <form:errors path="file" cssClass="red"/>
        </div>
        <%@ include file="/WEB-INF/jsp/image_archive/pages/fragments/change_data.jsp" %>
        <div style="margin-top: 20px;text-align:right;">
            <spring:message var="saveText" code="archive.save" htmlEscape="true"/>
            <input id="save" type="submit" name="saveAction" value="${saveText}" class="imcmsFormBtn"/>

            <spring:message var="cancelText" code="archive.cancel" htmlEscape="true"/>
            <input id="cancel" type="submit" name="cancelAction" value="${cancelText}" class="imcmsFormBtn"/>
        </div>
    </div>
</form:form>