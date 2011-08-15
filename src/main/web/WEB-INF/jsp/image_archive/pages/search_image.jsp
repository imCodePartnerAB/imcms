<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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

        function toggleTextAndFlag(btn, lessCriteria, moreCriteria) {
            if($("#moreCriteria").is(":visible")) {
                $(btn).val(lessCriteria);
                $("#formUnfolded").val(true);
            } else {
                $(btn).val(moreCriteria);
                $("#formUnfolded").val(false);
            }
        }

        $(document).ready(function(){
            var lessCriteria = '<spring:message code="archive.searchImage.lessCriteria" htmlEscape="true"/>';
            var moreCriteria = '<spring:message code="archive.searchImage.moreCriteria" htmlEscape="true"/>';

            $("#toggleMoreCriteriaBtn").click(function(){
                $("#moreCriteria").toggle();
                toggleTextAndFlag(this, lessCriteria, moreCriteria);
            });

            toggleTextAndFlag($("#toggleMoreCriteriaBtn"), lessCriteria, moreCriteria);
        });
    </script>

    <c:if test="${empty search.freetext}">
        <script type="text/javascript">
            $(document).ready(function(){
                var defaultText = '<spring:message code="archive.searchImage.searchPhrase" htmlEscape="true"/>';
                var freetext = $('#freetext');
                freetext.toggleClass('placeholder');
                freetext.val(defaultText);
                var focused = false;

                freetext.one('focus', function(){
                    $(this).val('');
                    $(this).toggleClass('placeholder');
                    focused = true;
                });

                $("#search").submit(function(){
                    if(!focused) {
                        freetext.val('');
                    }
                });
            })
        </script>
    </c:if>
</c:set>
<%@ include file="/WEB-INF/jsp/image_archive/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/top.jsp" %>
<div id="containerContent">
    <c:url var="searchUrl" value="/web/archive/"/>
    <form:form commandName="search" action="${searchUrl}" method="get" cssClass="m15t clearfix" cssStyle="width:860px;">
        <form:hidden path="unfolded" id="formUnfolded"/>
        <div class="minH30 clearboth">
            <div class="left">
                <form:input id="freetext" path="freetext" maxlength="120" cssStyle="width:350px;"/><br/>
                <form:errors path="freetext" cssClass="red"/>
            </div>
            <div class="left" style="margin-left:8px;">
                <input type="checkbox" name="fileNamesOnly" id="fileNamesOnly"/>
                <label for="fileNamesOnly"><spring:message code="archive.searchImage.searchOnFileNameOnly" htmlEscape="true"/></label>
            </div>
            <div class="right">
                <label for="category"><spring:message code="archive.searchImage.category" htmlEscape="true"/></label>
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
        <div class="clearfix" id="moreCriteria" style="margin-bottom:20px;padding:0 10px;${ search.unfolded ? '' : ' display:none;' }">
            <div class="left" style="width:30%;">
                <label><spring:message code="archive.searchImage.show" htmlEscape="true"/></label>

                <label for="showAll" style="margin:5px 8px 5px 0;display:block;">
                    <form:radiobutton id="showAll" path="show" value="0"/>
                    <spring:message code="archive.searchImage.showAll" htmlEscape="true"/>
                </label>

                <label for="showNew" style="margin:5px 8px 5px 0;display:block;">
                    <form:radiobutton id="showNew" path="show" value="1"/>
                    <spring:message code="archive.searchImage.showNew" htmlEscape="true"/>
                </label>

                <label for="showErased" style="margin:5px 8px 5px 0;display:block;">
                    <form:radiobutton id="showErased" path="show" value="2"/>
                    <spring:message code="archive.searchImage.showErased" htmlEscape="true"/>
                </label>

                <label for="showWithValidLicence" style="margin:5px 8px 0 0;display:block;">
                    <form:radiobutton id="showWithValidLicence" path="show" value="3"/>
                    <spring:message code="archive.searchImage.showWithValidLicence" htmlEscape="true"/>
                </label>
            </div>
            <div class="left" style="width:30%;">
                <div>
                    <label for="keyword" style="display:block;margin-bottom:5px;"><spring:message code="archive.searchImage.keyword" htmlEscape="true"/></label>
                    <form:select id="keyword" path="keywordId" cssStyle="width:128px;">
                        <option value="-1"><spring:message code="archive.searchImage.selectAll" htmlEscape="true"/></option>
                        <form:options items="${keywords}" itemValue="id" itemLabel="keywordNm" htmlEscape="true"/>
                    </form:select>
                </div>
                <div style="margin-top:15px;">
                    <label for="artist" style="display:block;margin-bottom:5px;"><spring:message code="archive.searchImage.photographer" htmlEscape="true"/></label>
                    <form:select id="artist" path="artist" cssStyle="width:128px;">
                        <option value=""><spring:message code="archive.searchImage.selectAll" htmlEscape="true"/></option>
                        <form:options items="${artists}" htmlEscape="true"/>
                    </form:select>
                </div>
            </div>
            <div class="right">
                <div>
                    <label for="licenseDt" style="display:block;margin-bottom:5px;"><spring:message code="archive.searchImage.licensingPeriod" htmlEscape="true"/></label>
                    <div>
                        <form:input id="licenseDt" path="licenseDt" maxlength="10" cssStyle="width:95px;"/>
                        <a href="#" id="licenseDtBtn"><img src="${pageContext.servletContext.contextPath}/imcms/jscalendar/images/img.gif" width="20" height="14"/></a>
                        &#8211;
                        <form:input id="licenseEndDt" path="licenseEndDt" maxlength="10" cssStyle="width:95px;"/>
                        <a href="#" id="licenseEndDtBtn"><img src="${pageContext.servletContext.contextPath}/imcms/jscalendar/images/img.gif" width="20" height="14"/></a><br/>
                        <form:errors path="license*" cssClass="red"/>
                    </div>
                </div>
                <div style="margin-top:15px;">
                    <label for="activeDt" style="display:block;margin-bottom:5px;"><spring:message code="archive.searchImage.dateOfActivation" htmlEscape="true"/></label>
                    <div>
                        <form:input id="activeDt" path="activeDt" maxlength="10" cssStyle="width:95px;"/>
                        <a href="#" id="activeDtBtn"><img src="${pageContext.servletContext.contextPath}/imcms/jscalendar/images/img.gif" width="20" height="14"/></a>
                        &#8211;
                        <form:input id="activeEndDt" path="activeEndDt" maxlength="10" cssStyle="width:95px;"/>
                        <a href="#" id="activeEndDtBtn"><img src="${pageContext.servletContext.contextPath}/imcms/jscalendar/images/img.gif" width="20" height="14"/></a><br/>
                        <form:errors path="active*" cssClass="red"/>
                    </div>
                </div>
            </div>
        </div>

        <div class="minH30 clearboth">
                <div class="minH30">
                    <div class="left" style="margin-right:8px;">
                        <label for="sortBy"><spring:message code="archive.searchImage.sortBy" htmlEscape="true"/></label>
                        <form:select id="sortBy" path="sortBy" cssStyle="width:128px;margin-left:8px;">
                            <spring:message var="photographerText" code="archive.searchImage.photographer"/>
                            <form:option value="0" label="${photographerText}"/>
                            <spring:message var="alphabetical" code="archive.searchImage.alphabetical"/>
                            <form:option value="1" label="${alphabetical}"/>
                            <spring:message var="entryDateText" code="archive.searchImage.entryDate"/>
                            <form:option value="2" label="${entryDateText}"/>
                        </form:select>
                        <form:select id="sortOrder" path="sortOrder">
                            <spring:message var="sortAscending" code="archive.searchImage.ascending"/>
                            <form:option value="0" label="${sortAscending}"/>
                            <spring:message var="sortDescending" code="archive.searchImage.descending"/>
                            <form:option value="1" label="${sortDescending}"/>
                        </form:select>
                        
                        <span style="margin-left:8px;">
                            <label for="resultsPerPage"><spring:message code="archive.searchImage.resultsPerPage" htmlEscape="true"/></label>
                            <form:select id="resultsPerPage" path="resultsPerPage" cssStyle="width:50px;">
                                <form:option value="10" label="10"/>
                                <form:option value="15" label="15"/>
                                <form:option value="20" label="20"/>
                                <form:option value="25" label="25"/>
                                <form:option value="40" label="40"/>
                            </form:select>
                        </span>
                    </div>
                    <div class="right">
                        <input type="button" id="toggleMoreCriteriaBtn" value="More criteria" class="btnBlue"/>

                        <spring:message var="clearText" code="archive.searchImage.clear" htmlEscape="true"/>
                        <input type="submit" name="clearAction" value="${clearText}" class="btnBlue"/>

                        <spring:message var="searchText" code="archive.searchImage.search" htmlEscape="true"/>
                        <input type="submit" value="${searchText}" class="btnBlue"/>
                    </div>
            </div>
            <h4 class="section">
                    <c:choose>
                        <c:when test="${imageCount gt 0}">
                            <spring:message code="archive.searchImage.hitListFound" arguments="${imageCount}" htmlEscape="true"/>
                        </c:when>
                        <c:otherwise>
                            <spring:message code="archive.searchImage.hitList" htmlEscape="true"/>
                        </c:otherwise>
                    </c:choose>
                </h4>
            <div class="left">
                <div class="clearfix" style="margin:15px auto 0 auto;" id="searchResults">
                    <c:choose>
                        <c:when test="${empty images}">
                            <div style="text-align:center">
                                <h3><spring:message code="archive.searchImage.noImagesFound" htmlEscape="true"/></h3>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="image" items="${images}" varStatus="status">
                                <c:set var="lastInRow" value="${status.index % 5 eq 4}"/>
                                <div class="left detailedTooltipThumb" data-image-id="${image.id}">
                                    <div style="text-align:center;">
                                        <c:url var="imageCardUrl" value="/web/archive/image/${image.id}"/>
                                        <c:url var="thumbUrl" value="/web/archive/thumb">
                                            <c:param name="id" value="${image.id}"/>
                                            <c:param name="size" value="small"/>
                                        </c:url>
                                        <a href="${imageCardUrl}"><img src="${thumbUrl}" width="150" height="113"/></a>
                                    </div>

                                    <div style="min-height:16px;" class="center">
                                        <span title="${fn:escapeXml(image.imageNm)}"><c:out value="${archive:abbreviate(image.imageNm, 23)}"/></span><br/>
                                    </div>
                                </div>

                                <c:if test="${lastInRow or status.last}">
                                    <div class="clearboth h10"></div>
                                </c:if>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </form:form>
    
    <c:if test="${pag.pageCount gt 1}">
        <spring:message var="nextText" code="archive.pageNext"/>
        <spring:message var="previousText" code="archive.pagePrevious"/>
        <archive:pagination capacity="13" pagesBeforeEllipse="2" pag="${pag}" pageUrl="/web/archive/page/[page]"
                        nextText="${nextText}" prevText="${previousText}" contStyle="text-align:center;margin:10px 0 20px 0;font-size:1.1em;">
        </archive:pagination>
    </c:if>
</div>

<%@ include file="/WEB-INF/jsp/image_archive/includes/footer.jsp" %>