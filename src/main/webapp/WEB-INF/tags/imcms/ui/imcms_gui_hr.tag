<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="wantedcolor" %>

<c:if test="${wantedcolor eq 'blue'}">
    <c:set var="color" value="20568d"/>
</c:if>
<c:if test="${wantedcolor ne 'blue'}">
    <c:set var="color" value="${wantedcolor}"/>
</c:if>

<!-- gui_hr $wantedcolor -->
<img src="${contextPath}/imcms/${language}/images/admin/1x1_${color}.gif" width="100%" height="1" vspace="8">
<!-- /gui_hr $wantedcolor -->
