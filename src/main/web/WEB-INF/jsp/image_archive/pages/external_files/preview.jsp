<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<archive:params var="titleArgs">
    <archive:param value="${name}"/>
</archive:params>
<spring:message var="title" code="archive.title.externalFiles.preview" arguments="${titleArgs}" htmlEscape="true"/>

<%@ include file="/WEB-INF/jsp/image_archive/includes/header.jsp" %>

<c:choose>
    <c:when test="${user eq null}">
        <div style="text-align:center;margin-top:30px;">
            <h1><spring:message code="archive.externalFiles.preview.loginError" htmlEscape="true"/></h1>
        </div>
    </c:when>
    <c:when test="${library eq null or !library.canUse}">
        <div style="text-align:center;margin-top:30px;">
            <h1><spring:message code="archive.externalFiles.preview.noAccess" htmlEscape="true"/></h1>
        </div>
    </c:when>
    <c:when test="${imageInfo eq null}">
        <div style="text-align:center;margin-top:30px;">
            <h1><spring:message code="archive.externalFiles.preview.notImage" htmlEscape="true"/></h1>
        </div>
    </c:when>
    <c:otherwise>
        <c:url var="imageUrl" value="/web/archive/external-files/image">
            <c:param name="id" value="${library.id}"/>
            <c:param name="name" value="${name}"/>
        </c:url>

        <img src="${imageUrl}" width="${imageInfo.width}" height="${imageInfo.height}"/>
        
        <script type="text/javascript">
            var width = ${imageInfo.width}, 
                height = ${imageInfo.height};
            
            width = Math.min(screen.availWidth, width + 20);
            height = Math.min(screen.availHeight, height + 20);

            var left = Math.floor((screen.availWidth - width) * 0.5), 
                top = Math.floor((screen.availHeight - height) * 0.5);

            left = Math.max(left, 0);
            top = Math.max(top, 0);
            
            window.resizeTo(width, height);
            window.moveTo(left, top);
        </script>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jsp/image_archive/includes/footer.jsp" %>