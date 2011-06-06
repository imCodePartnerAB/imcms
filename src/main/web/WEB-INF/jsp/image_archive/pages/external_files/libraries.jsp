<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<%
    String cp = request.getContextPath();
%>
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
            <div class="clearfix">
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
            <div class="clearfix" style="width:85%;margin:0 auto;margin-top:30px;">
                <div class="left" style="width:80%;">
                    <label for="libraries" class="left"><spring:message code="archive.externalFiles.libraries" htmlEscape="true"/></label>
                    <label for="fileNames" class="right"><spring:message code="archive.externalFiles.imageFiles" htmlEscape="true"/></label>
                    <div class="clearboth"></div>

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
           width: 35%;
            float:left;
        }

        #listOfLibraries ul {
            padding-left:40px;
        }
    </style>

    <script type="text/javascript">

        var folded = $('<img src="<%=cp%>/css/tree/folded.png"/>');
        var unfolded = $('<img src="<%=cp%>/css/tree/unfolded.png"/>');
        var blank = $('<img class="blank" src="<%=cp%>/css/tree/blank.png"/>');

        function toggleVisibility() {
            $("#listOfLibraries li:not(:has(ul))").each(function() {
                var indicator = $(this).find(" > img");
                if (!indicator.length) {
                    $(this).prepend(blank.clone());
                }
            });

            $("#listOfLibraries li ul:hidden").each(function() {
                var parent = $(this).parent();
                var indicator = parent.find(" > img");
                if (indicator.length) {
                    indicator.attr("src", folded.attr("src"));
                } else {
                    parent.prepend(folded.clone());
                }
            });

            $("#listOfLibraries li ul:visible").each(function() {
                var parent = $(this).parent();
                var indicator = parent.find(" > img");
                if (indicator.length) {
                    indicator.attr("src", unfolded.attr("src"));
                } else {
                    parent.prepend(unfolded.clone());
                }
            })
        }

        $(document).ready(function() {
            toggleVisibility();

            $("#listOfLibraries li img[class != 'blank']").click(function(event) {
                event.stopPropagation();
                $(" > ul", $(this).parent()).toggle();
                toggleVisibility();
            });

            /* google dictionary extension on chrome seems to throw an exception.
            *
            * many lines instead of one to prevent tablesorter exception in case of empty table */
            $("#fileNames").tablesorter({ headers: { 0 : {sorter:false}}});
            if($("#fileNames td").length > 0) {
                $("#fileNames").trigger("update");
                $("#fileNames").trigger("sorton",[[[${sortBy.ordinal}, ${sortBy.direction.ordinal}]]]);
            }

            $(".fileName").each(function(){
                var name = $(this).parent().find(":checkbox").val();
                $(this).qtip({
                  content: {
                      url: '<%=cp%>/web/archive/external-files/preview-tooltip',
                      data: { id : $("#libraryId").val(), name : name}
                  },
                    style: {
                        width:300
                    }
                })
            });
        });

    </script>
                    <ul id="listOfLibraries">
                        <li data-library-id="-1" ${currentLibrary.id eq -1 ? 'class="currentLibrary"' : ''}>
                            <spring:message code="archive.externalFiles.myPersonalFiles" htmlEscape="true"/>
                        </li>
                        <c:forEach var="library" items="${libraries}">
                            <archive:libraryChildren library="${library}" currentLibrary="${currentLibrary}" libraries="${allLibraries}"/>
                        </c:forEach>
                    </ul>

                    <div class="right" style="width:35%;">
                        <table id="fileNames" class="tablesorter">
                            <thead>
                                <th>Archive</th>
                                <th>Name</th>
                                <th>Size</th>
                                <th>Date</th>
                            </thead>
                            <tbody>
                                <c:forEach var="entry" items="${libraryEntries}">
                                    <c:choose>
                                        <c:when test="${entry.fileSizeMB}">
                                            <spring:message var="fileSize" code="archive.fileSizeMB" arguments="${entry.fileSize div (1024.0 * 1024.0)}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <spring:message var="fileSize" code="archive.fileSizeKB" arguments="${entry.fileSize div 1024.0}"/>
                                        </c:otherwise>
                                    </c:choose>

                                    <spring:message var="lastModifiedText" code="archive.dateFormat" arguments="${entry.lastModifiedDate}"/>

                                    <archive:params var="fileNameArgs">
                                        <archive:param value="${entry.fileName}"/>
                                        <archive:param value="${lastModifiedText}  ${fileSize}"/>
                                    </archive:params>

                                    <spring:message var="fileNameText" code="archive.externalFiles.fileName" arguments="${fileNameArgs}" htmlEscape="true"/>
                                    <tr>
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

        </c:otherwise>
    </c:choose>
</form:form>