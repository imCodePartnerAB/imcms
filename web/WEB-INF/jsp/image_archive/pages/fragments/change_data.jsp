<div class="minH30 clearboth">
    <label for="imageNm" class="left" style="width:130px;">
        <spring:message code="archive.changeData.imageName" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:input id="imageNm" path="imageNm" maxlength="255" cssStyle="width:300px;" htmlEscape="true"/><br/>
        <form:errors path="imageNm" cssClass="red"/>
    </div>
</div>
<div class="minH30 clearboth">
    <label for="description" class="left" style="width:130px;">
        <spring:message code="archive.changeData.description" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:textarea id="description" path="description" cols="40" rows="4" cssStyle="width:300px;height:80px;" htmlEscape="true"/><br/>
        <form:errors path="description" cssClass="red"/>
    </div>
</div>
<input type="hidden" id="categories" name="categories" value=""/>
<div class="minH30 clearboth clearfix" style="padding:10px 0;">
    <label for="availableCategories" class="left" style="width:130px;">
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
            <input id="addCategory" type="button" value="${rightText}" class="btnBlue small" style="width:30px;"/><br/><br/>
            <input id="deleteCategory" type="button" value="${leftText}" class="btnBlue small" style="width:30px;"/>
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
<div class="minH30 clearboth clearfix" style="padding:10px 0;">
    <label for="availableKeywords" class="left" style="width:130px;">
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
            <input id="addKeyword" type="button" value="${rightText}" class="btnBlue small" style="width:30px;"/><br/><br/>
            <input id="deleteKeyword" type="button" value="${leftText}" class="btnBlue small" style="width:30px;"/>
        </div>
        <select id="assignedKeywords" multiple="multiple" size="5" class="left" style="width:132px;">
            <c:forEach var="keyword" items="${imageKeywords}">
                <option value="${keyword}"><c:out value="${keyword}"/></option>
            </c:forEach>
        </select><br/>
    </div>
</div>
<div class="minH30 clearboth">
    <label for="keyword" class="left" style="width:130px;">
        <spring:message code="archive.changeData.addKeyword" htmlEscape="true"/>
    </label>
    <div class="left">
        <input type="text" id="keyword" value="" maxlength="50" style="width:170px;"/>
        <spring:message var="addText" code="archive.changeData.add" htmlEscape="true"/>
        <input type="button" id="createKeyword" value="${addText}" class="btnBlue small"/>
    </div>
</div>
<div class="minH30 clearboth">
    <label for="artist" class="left" style="width:130px;">
        <spring:message code="archive.changeData.photographer" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:input id="artist" path="artist" maxlength="255" cssStyle="width:300px;" htmlEscape="true"/><br/>
        <form:errors path="artist" cssClass="red"/>
    </div>
</div>
<div class="minH30 clearboth">
    <label class="left" style="width:130px;">
        <spring:message code="archive.changeData.originalSizeWidth" htmlEscape="true"/>
    </label>
    <span class="left">${image.width}x${image.height}</span>
</div>
<div class="minH30 clearboth">
    <label class="left" style="width:130px;">
        <spring:message code="archive.changeData.resolution" htmlEscape="true"/>
    </label>
    <span class="left">
        <spring:message code="archive.changeData.dpi" arguments="${image.changedExif.resolution}" htmlEscape="true"/>
    </span>
</div>
<div class="minH30 clearboth">
    <label class="left" style="width:130px;">
        <spring:message code="archive.changeData.originalFileSize" htmlEscape="true"/>
    </label>
    <span class="left"><spring:message code="archive.originalSizeKb" arguments="${image.fileSize / 1024.0}"/></span>
</div>
<div class="minH30 clearboth">
    <label class="left" style="width:130px;">
        <spring:message code="archive.changeData.id" htmlEscape="true"/>
    </label>
    <span class="left">${image.id}</span>
</div>
<div class="minH30 clearboth">
    <label for="uploadedBy" class="left" style="width:130px;">
        <spring:message code="archive.changeData.uploadedBy" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:input id="uploadedBy" path="uploadedBy" maxlength="130" cssStyle="width:300px;" htmlEscape="true"/><br/>
        <form:errors path="uploadedBy" cssClass="red"/>
    </div>
</div>
<div class="minH30 clearboth">
    <label for="copyright" class="left" style="width:130px;">
        <spring:message code="archive.changeData.copyright" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:input id="copyright" path="copyright" maxlength="255" cssStyle="width:300px;" htmlEscape="true"/><br/>
        <form:errors path="copyright" cssClass="red"/>
    </div>
</div>
<div class="minH30 clearboth">
    <label for="licenseDt" class="left" style="width:130px;">
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
<div class="minH30 clearboth">
    <label for="publishDt" class="left" style="width:130px;">
        <spring:message code="archive.changeData.publish" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:input id="publishDt" path="publishDt" maxlength="10" cssStyle="width:100px;" htmlEscape="true"/>
        <a href="#" id="publishDtBtn">
            <img src="${pageContext.servletContext.contextPath}/imcms/jscalendar/images/img.gif" width="20" height="14"/>
        </a><br/>
        <form:errors path="publishDt" cssClass="red"/>
    </div>
</div>
<div class="minH30 clearboth">
    <label for="archiveDt" class="left" style="width:130px;">
        <spring:message code="archive.changeData.archive" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:input id="archiveDt" path="archiveDt" maxlength="10" cssStyle="width:100px;" htmlEscape="true"/>
        <a href="#" id="archiveDtBtn">
            <img src="${pageContext.servletContext.contextPath}/imcms/jscalendar/images/img.gif" width="20" height="14"/>
        </a><br/>
        <form:errors path="archiveDt" cssClass="red"/>
    </div>
</div>
<div class="minH30 clearboth clearfix">
    <label for="publishEndDt" class="left" style="width:130px;">
        <spring:message code="archive.changeData.publishEnd" htmlEscape="true"/>
    </label>
    <div class="left">
        <form:input id="publishEndDt" path="publishEndDt" maxlength="10" cssStyle="width:100px;" htmlEscape="true"/>
        <a href="#" id="publishEndDtBtn">
            <img src="${pageContext.servletContext.contextPath}/imcms/jscalendar/images/img.gif" width="20" height="14"/>
        </a><br/>
        <form:errors path="publishEndDt" cssClass="red"/>
    </div>
</div>
