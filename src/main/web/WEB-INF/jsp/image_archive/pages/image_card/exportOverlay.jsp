<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<c:if test="${not user.defaultUser and not image.archived}">
    <div class="modal" id="exportOverlay">
        <h4 class="imcmsAdmHeading"><spring:message code="archive.imageCard.export.exportImage" htmlEscape="true"/></h4>
        <c:url var="exportUrl" value="/web/archive/image/${image.id}"/>
        <form:form action="${exportUrl}" commandName="exportImage" method="post" cssClass="right">
            <div class="clearboth minH30">
                <label for="width" class="left fixedWidth">
                    <spring:message code="archive.imageCard.export.width" htmlEscape="true"/>
                </label>
                <div class="left">
                    <form:input id="width" path="width" cssClass="left fixedWidthInput"/>
                </div>
                <div class="left" style="margin-left: 10px;">
                    <form:select  id="sizeUnit" path="sizeUnit">
                        <form:options items="${sizeUnits}" itemLabel="unitName"/>
                    </form:select>
                </div>
            </div>

            <div class="clearboth minH30">
                <label for="height" class="left fixedWidth">
                    <spring:message code="archive.imageCard.export.height" htmlEscape="true"/>
                </label>
                <div class="left">
                    <form:input id="height" path="height" cssClass="left fixedWidthInput"/>
                </div>
            </div>

            <div class="clearboth minH30">
                <div class="left fixedWidth">&nbsp;</div>
                <div class="left">
                    <form:checkbox id="keepAspectRatio" path="keepAspectRatio"/>
                    <label for="keepAspectRatio">
                        <spring:message code="archive.imageCard.export.keepAspectRatio" htmlEscape="true"/>
                    </label>
                </div>
            </div>

            <div class="clearboth minH30">
                <label for="fileFormat" class="left fixedWidth">
                    <spring:message code="archive.imageCard.export.fileFormat" htmlEscape="true"/>
                </label>
                <select id="fileFormat" name="fileFormat" class="left fixedWidthInput">
                    <c:forEach var="format" items="${exportImage.fileFormats}">
                        <option value="${format.ordinal}" ${exportImage.fileFormat eq format.ordinal ? 'selected="selected"' : ''} >
                            <c:out value="${fn:toLowerCase(format.format)}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="clearboth minH30">
                <label for="quality" class="left fixedWidth">
                    <spring:message code="archive.imageCard.export.quality" htmlEscape="true"/>
                </label>
                <select id="quality" name="quality" class="left fixedWidthInput">
                    <c:forEach var="quality" items="${exportImage.qualities}">
                        <option value="${quality}" ${exportImage.quality eq quality ? 'selected="selected"' : ''} >${quality}%</option>
                    </c:forEach>
                </select>
            </div>

            <div class="exportBtns">
                <spring:message var="exportText" code="archive.imageCard.export.exportButton" htmlEscape="true"/>
                <spring:message var="cancelText" code="archive.cancel" htmlEscape="true"/>
                <input type="button" class="imcmsFormBtn" name="cancel" value="${cancelText}" id="exportDialogCloseBtn"/>
                <input type="submit" class="imcmsFormBtn" name="export" value="${exportText}"/>
            </div>
        </form:form>
    </div>
</c:if>