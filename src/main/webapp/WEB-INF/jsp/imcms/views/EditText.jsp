<%@ page pageEncoding="UTF-8" %>
<%--
  Page for direct text editing
  Created by Serhii from Ubrainians for Imcode
  Date: 16.02.18
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="targetDocId" type="int"--%>
<%--@elvariable id="index" type="int"--%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>
<%--@elvariable id="returnUrl" type="java.lang.String"--%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Direct Text Editor</title>
    <link rel="stylesheet" href="${contextPath}/css/imcms-imports_files.css">
    <script>
        <jsp:include page="/js/imcms/imcms_config.js.jsp"/>
    </script>
    <script src="${contextPath}/js/imcms/imcms_main.js" data-name="imcms"
            data-main="${contextPath}/js/imcms/starters/imcms_text_edit_start.js"></script>
</head>
<body>

<input id="targetDocId" type="hidden" value="${targetDocId}">
<input type="hidden" id="return-url" value="${returnUrl}">

<imcms:text document="${targetDocId}" index="${index}"/>

</body>
</html>