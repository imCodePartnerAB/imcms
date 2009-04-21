<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<spring:message var="title" code="archive.title.externalFiles" htmlEscape="true"/>
<spring:message var="pageHeading" code="archive.pageHeading.externalFiles" htmlEscape="true"/>
<c:set var="currentPage" value="externalFiles"/>
<c:set var="css">
    <c:if test="${activate}">
        <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/imcms/jscalendar/skins/aqua/theme.css.jsp"/>
    </c:if>
</c:set>
<c:set var="javascript">
    <c:if test="${activate}">
        <%@ include file="/WEB-INF/jsp/image_archive/pages/fragments/jscalendar.jsp" %>
    </c:if>
    <script type="text/javascript">
        initExternalFiles();
    </script>
</c:set>
<%@ include file="/WEB-INF/jsp/image_archive/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/top.jsp" %>

<div id="containerContent">
    <c:choose>
        <c:when test="${activate}">
            <%@ include file="/WEB-INF/jsp/image_archive/pages/external_files/activate_image.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="/WEB-INF/jsp/image_archive/pages/external_files/libraries.jsp" %>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/WEB-INF/jsp/image_archive/includes/footer.jsp" %>