<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>

<%@ attribute name="titleAndHeading" %>

<c:set var="heading">
    <fmt:message key="${titleAndHeading}"/>
</c:set>

<!-- gui_start_of_page -->
<html>
<head>
    <title>${heading}</title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css.jsp">
    <script src="${contextPath}/js/imcms/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body>
<ui:imcms_gui_outer_start/>
<ui:imcms_gui_head heading="${heading}"/>
<ui:imcms_gui_mid/>
<!-- /gui_start_of_page -->
