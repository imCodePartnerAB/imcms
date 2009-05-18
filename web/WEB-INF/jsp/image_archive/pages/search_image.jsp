<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<spring:message var="title" code="archive.title.searchImage" htmlEscape="true"/>
<spring:message var="pageHeading" code="archive.pageHeading.searchImage" htmlEscape="true"/>
<c:set var="currentPage" value="searchImage"/>
<c:set var="css">
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/imcms/jscalendar/skins/aqua/theme.css.jsp"/>
</c:set>
<c:set var="javascript">
    <%@ include file="/WEB-INF/jsp/image_archive/pages/fragments/jscalendar.jsp" %>
    <script type="text/javascript">
        initSearchImage();
    </script>
</c:set>
<%@ include file="/WEB-INF/jsp/image_archive/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/top.jsp" %>

<div id="containerContent">
    <h4><spring:message code="archive.searchImage.searchImage" htmlEscape="true"/></h4><div class="hr"></div>
    <c:url var="searchUrl" value="/web/archive/"/>
    <form:form commandName="search" action="${searchUrl}" method="get" cssClass="m15t clearfix">
        <div class="minH30 clearboth">
            <span class="left" style="width:130px">
                <label><spring:message code="archive.searchImage.show" htmlEscape="true"/></label>
            </span>
            
            <form:radiobutton id="showAll" path="show" value="0"/>
            <label for="showAll" style="margin-right:8px;"><spring:message code="archive.searchImage.showAll" htmlEscape="true"/></label>
            
            <form:radiobutton id="showNew" path="show" value="1"/>
            <label for="showNew" style="margin-right:8px;"><spring:message code="archive.searchImage.showNew" htmlEscape="true"/></label>
            
            <form:radiobutton id="showErased" path="show" value="2"/>
            <label for="showErased"><spring:message code="archive.searchImage.showErased" htmlEscape="true"/></label>
        </div>
        <div class="minH30 clearboth">
            <span class="left" style="width:130px;">
                <label for="category"><spring:message code="archive.searchImage.category" htmlEscape="true"/></label>
            </span>
            <div class="left">
                <form:select id="category" path="categoryId" cssStyle="width:128px;">
                    <option value="-1"><spring:message code="archive.searchImage.selectAll" htmlEscape="true"/></option>
                    <c:if test="${sessionScope.user ne null}">
                        <option value="-2" ${search.categoryId eq -2 ? 'selected="selected"' : ''} ><spring:message code="archive.searchImage.noCategory" htmlEscape="true"/></option>
                    </c:if>
                    <form:options items="${categories}" itemValue="id" itemLabel="name" htmlEscape="true"/>
                </form:select><br/>
                <form:errors path="categoryId" cssClass="red"/>
            </div>
        </div>
        <div class="minH30 clearboth">
            <span class="left" style="width:130px;">
                <label for="keyword"><spring:message code="archive.searchImage.keyword" htmlEscape="true"/></label>
            </span>
            <div class="left">
                <form:select id="keyword" path="keywordId" cssStyle="width:128px;">
                    <option value="-1"><spring:message code="archive.searchImage.selectAll" htmlEscape="true"/></option>
                    <form:options items="${keywords}" itemValue="id" itemLabel="keywordNm" htmlEscape="true"/>
                </form:select>
            </div>
        </div>
        <div class="minH30 clearboth">
            <span class="left" style="width:130px;">
                <label for="freetext"><spring:message code="archive.searchImage.freetext" htmlEscape="true"/></label>
            </span>
            <div class="left">
                <form:input id="freetext" path="freetext" maxlength="120" cssStyle="width:350px;"/><br/>
                <form:errors path="freetext" cssClass="red"/>
            </div>
        </div>
        <div class="minH30 clearboth">
            <span class="left" style="width:130px;">
                <label for="artist"><spring:message code="archive.searchImage.photographer" htmlEscape="true"/></label>
            </span>
            <div class="left">
                <form:select id="artist" path="artist" cssStyle="width:128px;">
                    <option value=""><spring:message code="archive.searchImage.selectAll" htmlEscape="true"/></option>
                    <form:options items="${artists}" htmlEscape="true"/>
                </form:select>
            </div>
        </div>
        <div class="minH30 clearboth">
            <span class="left" style="width:130px;">
                <label for="licenseDt"><spring:message code="archive.searchImage.licensingPeriod" htmlEscape="true"/></label>
            </span>
            <div class="left">
                <form:input id="licenseDt" path="licenseDt" maxlength="10" cssStyle="width:95px;"/>
                <a href="#" id="licenseDtBtn"><img src="${pageContext.servletContext.contextPath}/imcms/jscalendar/images/img.gif" width="20" height="14"/></a>
                &#8211;
                <form:input id="licenseEndDt" path="licenseEndDt" maxlength="10" cssStyle="width:95px;"/>
                <a href="#" id="licenseEndDtBtn"><img src="${pageContext.servletContext.contextPath}/imcms/jscalendar/images/img.gif" width="20" height="14"/></a><br/>
                <form:errors path="license*" cssClass="red"/>
            </div>
        </div>
        <div class="minH30 clearboth">
            <span class="left" style="width:130px;">
                <label for="activeDt"><spring:message code="archive.searchImage.dateOfActivation" htmlEscape="true"/></label>
            </span>
            <div class="left">
                <form:input id="activeDt" path="activeDt" maxlength="10" cssStyle="width:95px;"/>
                <a href="#" id="activeDtBtn"><img src="${pageContext.servletContext.contextPath}/imcms/jscalendar/images/img.gif" width="20" height="14"/></a>
                &#8211;
                <form:input id="activeEndDt" path="activeEndDt" maxlength="10" cssStyle="width:95px;"/>
                <a href="#" id="activeEndDtBtn"><img src="${pageContext.servletContext.contextPath}/imcms/jscalendar/images/img.gif" width="20" height="14"/></a><br/>
                <form:errors path="active*" cssClass="red"/>
            </div>
        </div>
        <div class="minH30 clearboth">
            <span class="left" style="width:130px;">
                <label for="resultsPerPage"><spring:message code="archive.searchImage.resultsPerPage" htmlEscape="true"/></label>
            </span>
            <form:select id="resultsPerPage" path="resultsPerPage" cssClass="left" cssStyle="width:50px;">
                <form:option value="8" label="8"/>
                <form:option value="16" label="16"/>
                <form:option value="24" label="24"/>
                <form:option value="32" label="32"/>
                <form:option value="40" label="40"/>
            </form:select>
        </div>
        <div class="minH30 clearboth">
            <span class="left" style="width:130px;">
                <label for="sortBy"><spring:message code="archive.searchImage.sortBy" htmlEscape="true"/></label>
            </span>
            <div class="left" style="width:290px;">
                <form:select id="sortBy" path="sortBy" cssStyle="width:128px;">
                    <spring:message var="photographerText" code="archive.searchImage.photographer"/>
                    <form:option value="0" label="${photographerText}"/>
                    <spring:message var="freetextText" code="archive.searchImage.freetext"/>
                    <form:option value="1" label="${freetextText}"/>
                    <spring:message var="entryDateText" code="archive.searchImage.entryDate"/>
                    <form:option value="2" label="${entryDateText}"/>
                </form:select>
            </div>
            
            <spring:message var="clearText" code="archive.searchImage.clear" htmlEscape="true"/>
            <input type="submit" name="clearAction" value="${clearText}" class="btnBlue"/>

            <spring:message var="searchText" code="archive.searchImage.search" htmlEscape="true"/>
            <input type="submit" value="${searchText}" class="btnBlue"/>
        </div>
    </form:form>
    
    <h4 style="margin-top:30px;">
        <c:choose>
            <c:when test="${imageCount gt 0}">
                <spring:message code="archive.searchImage.hitListFound" arguments="${imageCount}" htmlEscape="true"/>
            </c:when>
            <c:otherwise>
                <spring:message code="archive.searchImage.hitList" htmlEscape="true"/>
            </c:otherwise>
        </c:choose>
    </h4><div class="hr"></div>
    <div class="clearfix" style="margin:15px auto 0 auto;width:888px;">
        <c:choose>
            <c:when test="${empty images}">
                <div style="text-align:center">
                    <h3><spring:message code="archive.searchImage.noImagesFound" htmlEscape="true"/></h3>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="image" items="${images}" varStatus="status">
                    <c:set var="lastInRow" value="${status.index % 4 eq 3}"/>
                    <div class="left" style="border:1px solid #888;width:210px;height:191px;margin-right:10px;padding:5px 0;">
                        <div style="text-align:center;">
                            <c:url var="imageCardUrl" value="/web/archive/image/${image.id}"/>
                            <c:url var="thumbUrl" value="/web/archive/thumb">
                                <c:param name="id" value="${image.id}"/>
                                <c:param name="size" value="small"/>
                            </c:url>
                            <a href="${imageCardUrl}"><img src="${thumbUrl}" width="150" height="113"/></a>
                        </div>

                        <div style="min-height:16px;" class="m10t center">
                            <a href="${imageCardUrl}">
                                <span title="${fn:escapeXml(image.imageNm)}"><c:out value="${archive:abbreviate(image.imageNm, 23)}"/></span><br/>
    
                                <c:set var="size" value="${image.width}x${image.height}"/>
                                <span title="${size}">${archive:abbreviate(size, 23)}</span><br/>
    
                                <c:if test="${not empty image.changedExif.artist}">
                                    <span title="${fn:escapeXml(image.changedExif.artist)}"><c:out value="${archive:abbreviate(image.changedExif.artist, 23)}"/></span><br/>
                                </c:if>
        
                                <spring:message code="archive.usedInImcms" htmlEscape="true"/>:
                                <spring:message code="archive.searchImage.usedInImcmsYesNo" arguments="${image.usedInImcms ? 1 : 0}" htmlEscape="true"/>
                            </a>
                        </div>
                    </div>
                    
                    <c:if test="${lastInRow or status.last}">
                        <div class="clearboth h10"></div>
                    </c:if>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
    
    <c:if test="${pag.pageCount gt 1}">
        <spring:message var="nextText" code="archive.pageNext"/>
        <spring:message var="previousText" code="archive.pagePrevious"/>
        <archive:pagination capacity="13" pagesBeforeEllipse="2" pag="${pag}" pageUrl="/web/archive/page/[page]"
                        nextText="${nextText}" prevText="${previousText}" contStyle="text-align:center;margin:10px 0 20px 0;font-size:1.1em;">
        </archive:pagination>
    </c:if>
</div>

<%@ include file="/WEB-INF/jsp/image_archive/includes/footer.jsp" %>