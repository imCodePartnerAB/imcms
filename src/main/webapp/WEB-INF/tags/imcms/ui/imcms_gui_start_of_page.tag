<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>

<%@ attribute name="titleAndHeading" %>

<!-- gui_start_of_page -->
<html>
<head>
    <title>${titleAndHeading}</title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">
    <script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>

</head>
<body>
<ui:imcms_gui_outer_start/>
<ui:imcms_gui_head heading="${titleAndHeading}"/>
<ui:imcms_gui_mid/>
<!-- /gui_start_of_page -->
