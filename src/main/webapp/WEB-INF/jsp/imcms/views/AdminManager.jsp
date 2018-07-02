<%@ page pageEncoding="UTF-8" %>
<%--
  Created by Serhii from Ubrainians for Imcode
  Date: 22.06.18
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="webapp/imcms/lang/jsp/admin/admin_manager.jsp/6"/></title>
    <link rel="stylesheet" href="${contextPath}/css/imcms-imports_files.css">
    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css">
    <link rel="stylesheet" type="text/css" href="${contextPath}/css/admin/imcms-super-admin.css">
    <script>
        <jsp:include page="/js/imcms/imcms_config.js.jsp"/>
    </script>
    <script src="${contextPath}/js/imcms/imcms_main.js" data-name="imcms"
            data-main="${contextPath}/js/imcms/starters/imcms_admin_manager_start.js"></script>
</head>
<body></body>
</html>
