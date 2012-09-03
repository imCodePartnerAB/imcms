<%@ page import="com.imcode.imcms.api.*,imcode.server.Imcms" %>
<table border="0" cellspacing="0" cellpadding="0" class="imcmsAdmTable mainTable">
    <tbody>
    <tr>
    <td class="imcmsAdmTable" style="background:#f5f5f7;">
<div id="containerTop">
    <div id="archive_banner" class="clearfix" style="padding: 10px 0 13px 0;">
        <span class="left pageHeading">${pageHeading}</span>
        <% String isoLang2 = Language.getLanguageByISO639_1(Imcms.getUser().getDocGetterCallback().languages().selected().getCode()).getIsoCode639_2(); %>
        <img class="right" src="${pageContext.servletContext.contextPath}/imcms/<%= isoLang2 %>/images/admin/logo_imcms_admin.gif" width="100" height="20"/>
    </div>
    
    <c:url var="backUrl" value="/web/archive/back"/>
    <div id="backButton" class="clearfix">
        <a href="${backUrl}" class="imcmsFormBtnSmall btnBack ${sessionScope.returnToImcms eq null ? 'disabled' : ''}"
           onclick="${sessionScope.returnToImcms eq null ? 'return false;' : ''}">
            <span><spring:message code="archive.backToImcms" htmlEscape="true"/></span>
        </a>
    </div>
    
    <%
        User user = ContentManagementSystem.fromRequest(request).getCurrentUser();
        pageContext.setAttribute("user", user);
    %>
    <div class="clearboth">
        <ul class="tabs">
            <c:url var="searchUrl" value="/web/archive"/>
            <li class="first ${currentPage eq 'searchImage' ? 'sel' : ''}">
                <a href="${searchUrl}">
                    <spring:message code="archive.tab.searchImage" htmlEscape="true"/>
                </a>
            </li>
            <c:if test="${not user.defaultUser}">
                <c:url var="addImageUrl" value="/web/archive/add-image"/>
                <li class="${currentPage eq 'addImage' ? 'sel' : ''}">
                    <a href="${addImageUrl}">
                        <spring:message code="archive.tab.addImage" htmlEscape="true"/>
                    </a>
                </li>
                <c:url var="externalFilesUrl" value="/web/archive/external-files"/>
                <li class="${currentPage eq 'externalFiles' ? 'sel' : ''}">
                    <a href="${externalFilesUrl}">
                        <spring:message code="archive.tab.externalFiles" htmlEscape="true"/>
                    </a>
                </li>
            </c:if>
            <c:if test="${user.superAdmin}">
                <c:url var="preferencesUrl" value="/web/archive/preferences"/>
                <li class="${currentPage eq 'preferences' ? 'sel' : ''}">
                    <a href="${preferencesUrl}">
                        <spring:message code="archive.tab.preferences" htmlEscape="true"/>
                    </a>
                </li>
            </c:if>
        </ul>
        <span id="languageSwitch" class="right">
            <c:url var="enUrl" value="/web/archive/language">
                <c:param name="lang" value="en"/>
                <c:param name="redir" value="${requestScope.requestUrl}"/>
            </c:url>
            <c:set var="imagearchive.visited.page.with.top" value="${requestScope.requestUrl}" scope="session"/>
            <a href="${enUrl}" style="margin-right:4px;"><img src="${pageContext.servletContext.contextPath}/imcms/images/icons/flags_iso_639_1/en.gif" width="16" height="11"/></a>
            <c:url var="svUrl" value="/web/archive/language">
                <c:param name="lang" value="sv"/>
                <c:param name="redir" value="${requestScope.requestUrl}"/>
            </c:url>
            <a href="${svUrl}"><img src="${pageContext.servletContext.contextPath}/imcms/images/icons/flags_iso_639_1/sv.gif" width="16" height="11"/></a>
        </span>
    </div>
    <div class="clearheight"></div>
</div>
