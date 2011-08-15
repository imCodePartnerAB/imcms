<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<h4 class="section">
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
            <input type="file" id="uploadify" name="file" ${disabled ? 'disabled="disabled"' : ''} />
            <input type="button" id="uploadButton" name="upload" value="${uploadText}" class="btnBlue ${disabled ? 'disabled' : ''}"
                    onclick="${disabled ? 'return false;' : ''}" ${disabled ? 'disabled="disabled"' : ''}/>
            <div id="uploadifyQueue" class="uploadifyQueue"></div>
        </div>
    </div>
    <h4>
        <spring:message code="archive.externalFiles.uploadedImagesGoIntoPersonalFolder" htmlEscape="true"/>
    </h4>
</form:form>

<form:form commandName="externalFiles" action="${processUrl}" method="post" enctype="multipart/form-data" cssClass="m15t clearfix">
    <h4 class="section"><spring:message code="archive.externalFiles.libraries" htmlEscape="true"/></h4>
    <c:choose>
        <c:when test="${currentLibrary eq null}">
            <h3><spring:message code="archive.externalFiles.noLibraryAccess" htmlEscape="true"/></h3>
        </c:when>
        <c:otherwise>
                    <style type="text/css">
                        ul {
                            list-style-type: none;
                        }

                        li {
                            cursor: pointer;
                            padding-top:2px;
                            padding-bottom:2px;
                        }

                        li img {
                            padding-right: 5px;
                        }

                        #listOfLibraries {
                            float:left;
                            border:1px solid gray;
                            padding:5px 30px 30px 5px;
                            margin-right:15px;
                        }

                        #listOfLibraries ul {
                            padding-left:40px;
                        }
                    </style>
                    <div style="text-align:right;">
                        <spring:message var="activeImageText" code="archive.externalFiles.activateImage" htmlEscape="true"/>
                        <input type="submit" name="activate" class="btnBlue small" value="${activeImageText}"/>

                        <spring:message var="showImageText" code="archive.externalFiles.showImage" htmlEscape="true"/>
                        <input type="button" name="show" id="show" class="btnBlue small" value="${showImageText}"/>

                        <c:set var="disabled" value="${not currentLibrary.userLibrary }"/>
                        <spring:message var="eraseImageText" code="archive.externalFiles.eraseImage" htmlEscape="true"/>
                        <input type="submit" name="erase" class="btnBlue small ${disabled ? 'disabled' : ''}"
                               onclick="${disabled ? 'return false;' : ''}" value="${eraseImageText}"/>
                    </div>
                    <ul id="listOfLibraries">
                        <li data-library-id="-1" ${currentLibrary.id eq -1 ? 'class="currentLibrary"' : ''}>
                            <spring:message code="archive.externalFiles.myPersonalFiles" htmlEscape="true"/>
                        </li>
                        <c:forEach var="library" items="${libraries}">
                            <archive:libraryChildren library="${library}" currentLibrary="${currentLibrary}" libraries="${allLibraries}"/>
                        </c:forEach>
                    </ul>

                    <div class="left" style="width:45%;">
                        <table id="fileNames" class="tablesorter">
                            <thead>
                                <th><spring:message code="archive.externalFiles.useDeleteOrShow" htmlEscape="true"/></th>
                                <th><spring:message code="archive.externalFiles.fileName" htmlEscape="true"/></th>
                                <th><spring:message code="archive.externalFiles.fileSize" htmlEscape="true"/></th>
                                <th><spring:message code="archive.externalFiles.lastModified" htmlEscape="true"/></th>
                            </thead>
                            <tbody>
                                <c:forEach var="entry" items="${libraryEntries}" varStatus="status">
                                    <c:set var="odd" value="${status.count % 2 != 0}"/>
                                    <c:choose>
                                        <c:when test="${entry.fileSizeMB}">
                                            <spring:message var="fileSize" code="archive.fileSizeMB" arguments="${entry.fileSize div (1024.0 * 1024.0)}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <spring:message var="fileSize" code="archive.fileSizeKB" arguments="${entry.fileSize div 1024.0}"/>
                                        </c:otherwise>
                                    </c:choose>

                                    <spring:message var="lastModifiedText" code="archive.dateFormat" arguments="${entry.lastModifiedDate}"/>

                                    <tr class="${odd ? 'odd' : ''}">
                                        <td>
                                            <c:if test="${archive:isInArchive(entry, pageContext)}">
                                                <span style="font-weight:bold;font-size:20px;color:green;">&#10003;</span>
                                            </c:if>
                                            <input type="checkbox" name="fileNames" value="${fn:escapeXml(entry.fileName)}"/>
                                        </td>
                                        <td class="fileName">${entry.fileName}</td>
                                        <td>${fileSize}</td>
                                        <td>${lastModifiedText}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
        </c:otherwise>
    </c:choose>
</form:form>