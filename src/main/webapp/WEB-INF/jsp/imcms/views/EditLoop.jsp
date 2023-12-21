<%@ page pageEncoding="UTF-8" %>
<%--
  Created by Serhii from Ubrainians for Imcode
  Date: 19.02.18
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
    <title>Direct Loop Editor</title>
    <link rel="stylesheet" href="${contextPath}/dist/imcms-imports_files.css">
    <script>
        <jsp:include page="/imcms/js/imcms_config.js.jsp"/>
    </script>
    <script src="${contextPath}/dist/imcms_loop_edit_start.js"></script>
</head>
<body>

<input type="hidden" id="return-url" value="${returnUrl}">
<div class="imcms-editor-area imcms-editor-area--loop" data-doc-id="${targetDocId}" data-index="${index}" data-label="${label}">
    <div class="imcms-editor-area__content imcms-editor-content" data-doc-id="${targetDocId}" data-index="${index}" data-label="${label}"></div>
</div>

</body>
</html>
