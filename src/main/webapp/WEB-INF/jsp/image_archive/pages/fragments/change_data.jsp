<div class="minH30 clearfix">
    <label for="imageNm" class="left" style="width:150px;">
        <spring:message code="archive.changeData.imageName" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:input id="imageNm" path="imageNm" maxlength="255" cssStyle="width:300px;" htmlEscape="true"/><br/>
        <form:errors path="imageNm" cssClass="red"/>
    </div>
</div>
<div class="minH30 clearfix">
    <label for="description" class="left" style="width:150px;">
        <spring:message code="archive.changeData.description" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:textarea id="description" path="description" cols="40" rows="4" cssStyle="width:300px;height:80px;" htmlEscape="true"/><br/>
        <form:errors path="description" cssClass="red"/>
    </div>
</div>
<input type="hidden" id="categories" name="categories" value=""/>
<div class="minH30 clearfix" style="padding:10px 0;">
    <label for="availableCategories" class="left" style="width:150px;">
        <spring:message code="archive.changeData.category" htmlEscape="true"/>
    </label>
    <div class="left">
        <select id="availableCategories" multiple="multiple" size="5" class="left" style="width:132px;">
            <c:forEach var="category" items="${categories}">
                <option value="${category.id}"><c:out value="${category.name}"/></option>
            </c:forEach>
        </select>
        <div class="left" style="padding:15px 5px;">
            <spring:message var="rightText" code="archive.moveRight" htmlEscape="true"/>
            <spring:message var="leftText" code="archive.moveLeft" htmlEscape="true"/>
            <input id="addCategory" type="button" value="${rightText}" class="imcmsFormBtnSmall" style="width:30px;"/><br/><br/>
            <input id="deleteCategory" type="button" value="${leftText}" class="imcmsFormBtnSmall" style="width:30px;"/>
        </div>
        <select id="imageCategories" multiple="multiple" size="5" class="left" style="width:132px;">
            <c:forEach var="category" items="${imageCategories}">
                <option value="${category.id}"><c:out value="${category.name}"/></option>
            </c:forEach>
        </select><br/>
        <form:errors path="categories" cssClass="red"/>
    </div>
</div>
<input type="hidden" id="keywords" name="keywords"/>
<input type="hidden" id="imageKeywords" name="imageKeywords"/>
<div class="minH30 clearfix" style="padding:10px 0;">
    <label for="availableKeywords" class="left" style="width:150px;">
        <spring:message code="archive.changeData.keywords" htmlEscape="true"/>
    </label>
    <div class="left">
        <select id="availableKeywords" multiple="multiple" size="5" class="left" style="width:132px;">
            <c:forEach var="keyword" items="${keywords}">
                <option value="${keyword}"><c:out value="${keyword}"/></option>
            </c:forEach>
        </select>
        <div class="left" style="padding:15px 5px;">
            <spring:message var="rightText" code="archive.moveRight" htmlEscape="true"/>
            <spring:message var="leftText" code="archive.moveLeft" htmlEscape="true"/>
            <input id="addKeyword" type="button" value="${rightText}" class="imcmsFormBtnSmall" style="width:30px;"/><br/><br/>
            <input id="deleteKeyword" type="button" value="${leftText}" class="imcmsFormBtnSmall" style="width:30px;"/>
        </div>
        <select id="assignedKeywords" multiple="multiple" size="5" class="left" style="width:132px;">
            <c:forEach var="keyword" items="${imageKeywords}">
                <option value="${keyword}"><c:out value="${keyword}"/></option>
            </c:forEach>
        </select><br/>
    </div>
</div>
<div class="minH30 clearfix">
    <label for="keyword" class="left" style="width:150px;">
        <spring:message code="archive.changeData.addKeyword" htmlEscape="true"/>
    </label>
    <input type="text" id="keyword" value="" maxlength="50" style="width:55%;"/>
    <spring:message var="addText" code="archive.changeData.add" htmlEscape="true"/>
    <input type="button" id="createKeyword" value="${addText}" class="imcmsFormBtnSmall right"/>
</div>
<div class="minH30 clearfix">
    <label for="artist" class="left" style="width:150px;">
        <spring:message code="archive.changeData.photographer" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:input id="artist" path="artist" maxlength="255" cssStyle="width:300px;" htmlEscape="true"/><br/>
        <form:errors path="artist" cssClass="red"/>
    </div>
</div>
<div class="minH30 clearfix">
    <label class="left" style="width:150px;">
        <spring:message code="archive.changeData.originalSizeWidth" htmlEscape="true"/>
    </label>
    <span class="left">${image.width}x${image.height}</span>
</div>
<div class="minH30 clearfix">
    <label class="left" style="width:150px;">
        <spring:message code="archive.changeData.resolution" htmlEscape="true"/>
    </label>
    <span class="left">
        <c:choose>
            <c:when test="${not empty image.changedExif.xResolution}">
                <c:choose>
                    <c:when test="${3 eq image.changedExif.resolutionUnit}">
                        <spring:message code="archive.changeData.dpcm" arguments="${image.changedExif.xResolution}" htmlEscape="true"/>
                    </c:when>
                    <c:otherwise>
                        <spring:message code="archive.changeData.dpi" arguments="${image.changedExif.xResolution}" htmlEscape="true"/>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <c:out value="${notAvailable}"/>
            </c:otherwise>
        </c:choose>
    </span>
</div>
<div class="minH30 clearfix">
    <label class="left" style="width:150px;">
        <spring:message code="archive.changeData.originalFileSize" htmlEscape="true"/>
    </label>
    <span class="left"><spring:message code="archive.originalSizeKb" arguments="${image.fileSize / 1024.0}"/></span>
</div>
<div class="minH30 clearfix">
    <span class="left" style="width:150px;">
        <spring:message code="archive.changeData.originalFileType" htmlEscape="true"/>
    </span>
    <span><c:out value="${format.format}"/></span>
</div>
<div class="minH30 clearfix">
    <label class="left" style="width:150px;">
        <spring:message code="archive.changeData.id" htmlEscape="true"/>
    </label>
    <span class="left">${image.id}</span>
</div>
<div class="minH30 clearfix">
    <label for="uploadedBy" class="left" style="width:150px;">
        <spring:message code="archive.changeData.uploadedBy" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:input id="uploadedBy" path="uploadedBy" maxlength="150" cssStyle="width:300px;" htmlEscape="true"/><br/>
        <form:errors path="uploadedBy" cssClass="red"/>
    </div>
</div>
<div class="minH30 clearfix">
    <label for="copyright" class="left" style="width:150px;">
        <spring:message code="archive.changeData.copyright" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:input id="copyright" path="copyright" maxlength="255" cssStyle="width:300px;" htmlEscape="true"/><br/>
        <form:errors path="copyright" cssClass="red"/>
    </div>
</div>
<div class="minH30 clearfix">
    <label for="licenseDt" class="left" style="width:150px;">
        <spring:message code="archive.changeData.licensePeriod" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:input id="licenseDt" path="licenseDt" maxlength="10" cssStyle="width:100px;" htmlEscape="true"/>
        <a href="#" id="licenseDtBtn"><img src="${pageContext.servletContext.contextPath}/imcms/jscalendar/images/img.gif" width="20" height="14"/></a>
        &#8211;
        <form:input id="licenseEndDt" path="licenseEndDt" maxlength="10" cssStyle="width:100px;" htmlEscape="true"/>
        <a href="#" id="licenseEndDtBtn">
            <img src="${pageContext.servletContext.contextPath}/imcms/jscalendar/images/img.gif" width="20" height="14"/>
        </a><br/>
        <form:errors path="license*" cssClass="red"/>
    </div>
</div>
<div class="minH30 clearfix">
    <label for="altText" class="left" style="width:150px;">
        <spring:message code="archive.changeData.altText" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:input id="altText" path="altText" maxlength="50" cssStyle="width:300px;" htmlEscape="true"/>
        <form:errors path="altText" cssClass="red"/>
    </div>
</div>
