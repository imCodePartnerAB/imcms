<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<h4>
    <spring:message code="archive.externalFiles.selectImageFile" htmlEscape="true"/>
</h4><div class="hr"></div>
<c:url var="processUrl" value="/web/archive/external-files/process"/>
<form:form commandName="externalFiles" action="${processUrl}" method="post" enctype="multipart/form-data" cssClass="m15t clearfix">
    <c:choose>
        <c:when test="${currentLibrary eq null}">
            <h3><spring:message code="archive.externalFiles.noLibraryAccess" htmlEscape="true"/></h3>
        </c:when>
        <c:otherwise>
            <input type="hidden" id="libraryId" value="${currentLibrary.id}"/>
            <div class="clearfix" style="width:85%;margin:0 auto;">
                <div class="left" style="width:80%;">
                    <label for="libraries" class="left"><spring:message code="archive.externalFiles.libraries" htmlEscape="true"/></label>
                    <label for="fileNames" class="right"><spring:message code="archive.externalFiles.imageFiles" htmlEscape="true"/></label>
                    <div class="clearboth"></div>

                    <select id="libraries" class="left" size="10" style="width:35%;">
                        <option value="-1" ${currentLibrary.id eq -1 ? 'selected="selected"' : ''} >
                            <spring:message code="archive.externalFiles.myPersonalFiles" htmlEscape="true"/>
                        </option>
                        <c:forEach var="library" items="${libraries}">
                            <option value="${library.id}" ${currentLibrary.id eq library.id ? 'selected="selected"' : ''} >
                                <c:out value="${library.libraryNm}"/>
                            </option>
                        </c:forEach>
                    </select>
                    <div class="left" style="width:29%;">
                        <spring:message var="changeLibraryText" code="archive.externalFiles.changeLibrary" htmlEscape="true"/>
                        <input id="changeLibrary" style="margin:0 auto;display:block;" type="button" class="btnBlue small" value="${changeLibraryText}"/>
                    </div>
                    <div class="right" style="width:35%;">
                        <select id="fileNames" size="10" multiple="true" name="fileNames" style="width:100%;">
                            <c:forEach var="entry" items="${libraryEntries}">
                                <c:choose>
                                    <c:when test="${entry.fileSizeMB}">
                                        <spring:message var="fileSize" code="archive.fileSizeMB" arguments="${entry.fileSize div (1024.0 * 1024.0)}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:message var="fileSize" code="archive.fileSizeKB" arguments="${entry.fileSize div 1024.0}"/>
                                    </c:otherwise>
                                </c:choose>
                                
                                <spring:message var="lastModifiedText" code="archive.dateTimeFormat" arguments="${entry.lastModifiedDate}"/>

                                <archive:params var="fileNameArgs">
                                    <archive:param value="${entry.fileName}"/>
                                    <archive:param value="${lastModifiedText}  ${fileSize}"/>
                                </archive:params>

                                <spring:message var="fileNameText" code="archive.externalFiles.fileName" arguments="${fileNameArgs}" htmlEscape="true"/>
                                <option value="${fn:escapeXml(entry.fileName)}" title="${fileNameText}">
                                    ${fileNameText}
                                </option>
                            </c:forEach>
                        </select><br/>

                        <span style="display:block;" class="m10t">
                            <label><spring:message code="archive.externalFiles.sortBy" htmlEscape="true"/></label>
                            <input id="sortByFileName" type="radio" name="sortBy" value="fileName" ${sortBy.name eq 'fileName' ? 'checked="checked"' : ''} />
                            <label for="sortByFileName"><spring:message code="archive.externalFiles.sortByFileName" htmlEscape="true"/></label>
                            <input id="sortByDate" type="radio" name="sortBy" value="date" ${sortBy.name eq 'date' ? 'checked="checked"' : ''} />
                            <label for="sortByDate"><spring:message code="archive.externalFiles.sortByDate" htmlEscape="true"/></label>
                        </span>
                    </div>
                </div>

                <div class="right" style="margin-top:17px;">
                    <spring:message var="activeImageText" code="archive.externalFiles.activateImage" htmlEscape="true"/>
                    <input type="submit" name="activate" class="btnBlue small" value="${activeImageText}" style="width:100px;margin-bottom:2px;"/><br/>

                    <spring:message var="showImageText" code="archive.externalFiles.showImage" htmlEscape="true"/>
                    <input id="show" type="button" name="show" class="btnBlue small" value="${showImageText}" style="width:100px;margin-bottom:2px;"/><br/>

                    <c:set var="disabled" value="${not currentLibrary.canChange}"/>
                    <spring:message var="eraseImageText" code="archive.externalFiles.eraseImage" htmlEscape="true"/>
                    <input type="submit" name="erase" style="width:100px;" class="btnBlue small ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}" value="${eraseImageText}"/>
                </div>
            </div>

            <div style="margin-top:30px;">
                <label class="left" for="file" style="margin:3px 20px 3px 0;">
                    <spring:message code="archive.externalFiles.uploadImageZip" htmlEscape="true"/>
                </label>
                <div class="left">
                    <c:set var="disabled" value="${not currentLibrary.canChange}"/>
                    <spring:message var="uploadText" code="archive.addImage.upload" htmlEscape="true"/>
                    <input type="file" id="file" name="file" ${disabled ? 'disabled="disabled"' : ''} />
                    <input type="submit" name="upload" value="${uploadText}" class="btnBlue ${disabled ? 'disabled' : ''}" onclick="${disabled ? 'return false;' : ''}" /><br/>
                    <form:errors path="file" cssClass="red"/>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</form:form>