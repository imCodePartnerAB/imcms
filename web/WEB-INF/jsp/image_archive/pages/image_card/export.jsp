<%@ include file="/WEB-INF/jsp/image_archive/pages/image_card/info.jsp" %>

<h4 class="m15t">
    <spring:message code="archive.imageCard.export.export" htmlEscape="true"/>
</h4><div class="hr"></div>
<c:url var="exportUrl" value="/web/archive/image/${image.id}/export"/>
<form:form action="${exportUrl}" commandName="exportImage" method="post" cssClass="clearfix m15t">
    <div class="minH30">
        <span class="left" style="width:130px;">
            <label for="width">
                <spring:message code="archive.imageCard.export.width" htmlEscape="true"/>
            </label>
        </span>
        <div class="left">
            <form:input id="width" path="width" cssStyle="width:100px;"/><br/>
            <form:errors path="width" cssClass="red"/>
        </div>
    </div>
    <div class="minH30 clearboth">
        <span class="left" style="width:130px;">
            <label for="height">
                <spring:message code="archive.imageCard.export.height" htmlEscape="true"/>
            </label>
        </span>
        <div class="left">
            <form:input id="height" path="height" cssStyle="width:100px;"/><br/>
            <form:errors path="height" cssClass="red"/>
        </div>
    </div>
    <div class="minH30 clearboth">
        <span class="left" style="width:130px;">
            <label for="fileFormat">
                <spring:message code="archive.imageCard.export.fileFormat" htmlEscape="true"/>
            </label>
        </span>
        <select id="fileFormat" name="fileFormat" class="left" style="width:100px;">
            <c:forEach var="format" items="${exportImage.fileFormats}">
                <option value="${format.imageFormat}" ${exportImage.fileFormat eq format.imageFormat ? 'selected="selected"' : ''} >
                    <c:out value="${fn:toLowerCase(format.format)}"/>
                </option>
            </c:forEach>
        </select>
    </div>
    <div class="minH30">
        <span class="left" style="width:130px;">
            <label for="quality">
                <spring:message code="archive.imageCard.export.quality" htmlEscape="true"/>
            </label>
        </span>
        <select id="quality" name="quality" class="left" style="width:100px;">
            <c:forEach var="quality" items="${exportImage.qualities}">
                <option value="${quality}" ${exportImage.quality eq quality ? 'selected="selected"' : ''} >${quality}%</option>
            </c:forEach>
        </select>
    </div>
    <div class="hr"></div>
    <spring:message var="exportText" code="archive.imageCard.export.exportButton" htmlEscape="true"/>
    <input type="submit" class="btnBlue m15t" style="margin-left:130px;" name="export" value="${exportText}"/>
</form:form>
