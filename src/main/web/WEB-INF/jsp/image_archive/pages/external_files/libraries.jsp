<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<h4 class="imcmsAdmHeading">
    <spring:message code="archive.externalFiles.uploadImageZip" htmlEscape="true"/>
</h4>
<c:url var="processUrl" value="/web/archive/external-files/process"/>
<c:url var="uploadUrl" value="/web/archive/external-files/upload"/>
<form:form modelAttribute="externalFilesUpload" method="post" enctype="multipart/form-data" action="${uploadUrl}">
    <input type="hidden" id="libraryId" value="${currentLibrary.id}"/>
    <div class="clearfix">
        <label class="left" for="uploadify" style="margin:3px 20px 3px 0;">
            <spring:message code="archive.externalFiles.selectImageFile" htmlEscape="true"/>
        </label>
        <div class="left">
            <c:set var="disabled" value="${not currentLibrary.canChange}"/>
            <spring:message var="uploadText" code="archive.addImage.upload" htmlEscape="true"/>
            <div class="UploadifyButtonWrapper">
            <button type="button" class="imcmsFormBtn"><spring:message code="archive.addImage.browse" htmlEscape="true"/></button>
            <div class="UploadifyObjectWrapper">
            <input type="file" id="uploadify" name="file" ${disabled ? 'disabled="disabled"' : ''} />
                 </div>
        </div>
            <input type="button" id="uploadButton" name="upload" value="${uploadText}" class="imcmsFormBtn ${disabled ? 'disabled' : ''}"
                    onclick="${disabled ? 'return false;' : ''}" ${disabled ? 'disabled="disabled"' : ''}/>
            <div id="uploadifyQueue" class="uploadifyQueue"></div>
        </div>
    </div>
    <h4 class="hint" style="margin-top:5px;">
        <spring:message code="archive.externalFiles.uploadedImagesGoIntoPersonalFolder" htmlEscape="true"/>
    </h4>
</form:form>

<h4 class="imcmsAdmHeading"><spring:message code="archive.externalFiles.libraries" htmlEscape="true"/></h4>
<form:form commandName="externalFiles" action="${processUrl}" method="post" enctype="multipart/form-data" cssClass="m15t clearfix">
    <c:choose>
        <c:when test="${currentLibrary eq null}">
            <h3><spring:message code="archive.externalFiles.noLibraryAccess" htmlEscape="true"/></h3>
        </c:when>
        <c:otherwise>
            <table class="externalFilesLibrariesAndEntries" cellpadding="0" cellspacing="0">
                <tr>
                    <td></td>
                    <td class="tableSeparatorTop"></td>
                    <td>
                        <div style="text-align:right;margin-bottom:10px;">
                            <spring:message var="activeImageText" code="archive.externalFiles.activateImage" htmlEscape="true"/>
                            <input type="submit" name="activate" class="imcmsFormBtnSmall" value="${activeImageText}"/>

                            <c:set var="disabled" value="${not currentLibrary.userLibrary }"/>
                            <spring:message var="eraseImageText" code="archive.externalFiles.eraseImage" htmlEscape="true"/>
                            <input type="submit" name="erase" class="imcmsFormBtnSmall ${disabled ? 'disabled' : ''}"
                                   onclick="${disabled ? 'return false;' : ''}" value="${eraseImageText}"/>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td class="listOfLibrariesCell">
                        <ul id="listOfLibraries">
                            <li data-library-id="-1"><span${currentLibrary.id eq -1 ? ' class="currentLibrary"' : ''}><spring:message code="archive.externalFiles.myPersonalFiles" htmlEscape="true"/></span>
                            </li>
                            <c:forEach var="library" items="${libraries}">
                                <archive:libraryChildren library="${library}" currentLibrary="${currentLibrary}" libraries="${allLibraries}"/>
                            </c:forEach>
                        </ul>
                    </td>
                    <td class="tableSeparatorMiddle"></td>
                    <td style="vertical-align:top;">
                        <table id="fileNames" class="tablesorter" cellpadding="0" cellspacing="1">
                            <thead>
                                <th></th>
                                <th></th>
                                <th><spring:message code="archive.externalFiles.sortByInArchive" htmlEscape="true"/></th>
                                <th><spring:message code="archive.externalFiles.fileName" htmlEscape="true"/></th>
                                <th><spring:message code="archive.externalFiles.fileSize" htmlEscape="true"/></th>
                                <th><spring:message code="archive.externalFiles.lastModified" htmlEscape="true"/></th>
                            </thead>
                            <tbody>
                                <c:forEach var="entry" items="${libraryEntries}" varStatus="status">
                                    <c:choose>
                                        <c:when test="${entry.fileSizeMB}">
                                            <spring:message var="fileSize" code="archive.fileSizeMB" arguments="${entry.fileSize div (1024.0 * 1024.0)}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <spring:message var="fileSize" code="archive.fileSizeKB" arguments="${entry.fileSize div 1024.0}"/>
                                        </c:otherwise>
                                    </c:choose>

                                    <spring:message var="lastModifiedText" code="archive.dateFormat" arguments="${entry.lastModifiedDate}"/>

                                    <tr>
                                        <td>
                                            <button class="show imcmsFormBtn" value="${fn:escapeXml(entry.fileName)}"><spring:message code="archive.externalFiles.show" htmlEscape="true"/></button>
                                        </td>
                                        <td>
                                            <input type="checkbox" name="fileNames" value="${fn:escapeXml(entry.fileName)}"/>
                                        </td>
                                        <td>
                                            <c:if test="${archive:isInArchive(entry, pageContext)}">
                                                <span style="font-weight:bold;font-size:20px;color:green;">&#10003;</span>
                                            </c:if>
                                        </td>
                                        <td class="fileName">${entry.fileName}</td>
                                        <td>${fileSize}</td>
                                        <td>${lastModifiedText}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <div style="text-align:right;margin-top:5px;">
                            <spring:message var="activeImageText" code="archive.externalFiles.activateImage" htmlEscape="true"/>
                            <input type="submit" name="activate" class="imcmsFormBtnSmall" value="${activeImageText}"/>

                            <c:set var="disabled" value="${not currentLibrary.userLibrary }"/>
                            <spring:message var="eraseImageText" code="archive.externalFiles.eraseImage" htmlEscape="true"/>
                            <input type="submit" name="erase" class="imcmsFormBtnSmall ${disabled ? 'disabled' : ''}"
                                   onclick="${disabled ? 'return false;' : ''}" value="${eraseImageText}"/>
                        </div>
                    </td>
                </tr>
            </table>


        </c:otherwise>
    </c:choose>
</form:form>