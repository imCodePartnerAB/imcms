<%@ page pageEncoding="UTF-8" %>
<%--
  Created by Serhii from Ubrainians for Imcode
  Date: 20.02.18
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>
<%--@elvariable id="returnUrl" type="java.lang.String"--%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Direct Content Manager</title>
    <link rel="stylesheet" href="${contextPath}/dist/imcms-imports_files.css">
    <script>
        <jsp:include page="/imcms/js/imcms_config.js.jsp"/>
    </script>
    <script src="${contextPath}/dist/imcms_content_manager_start.js"></script>
</head>
<body>
<input type="hidden" id="return-url" value="${returnUrl}">
</body>
</html>
