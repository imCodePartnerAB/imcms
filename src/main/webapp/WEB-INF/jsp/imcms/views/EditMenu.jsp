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

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Direct Menu Editor</title>
    <link rel="stylesheet" href="${contextPath}/css_new/imcms-imports_files.css">
    <script>
        <jsp:include page="/js/imcms/imcms_config.js.jsp"/>
    </script>
    <script src="${contextPath}/js/imcms_new/imcms_main.js" data-name="imcms"
            data-main="${contextPath}/js/imcms_new/edit_starters/imcms_menu_edit_start.js"></script>
</head>
<body>

<div class="imcms-editor-area imcms-editor-area--menu" data-doc-id="${targetDocId}" data-menu-index="${index}">
    <div class="imcms-editor-area__content imcms-editor-content" data-doc-id="${targetDocId}"
         data-menu-index="${index}"></div>
</div>

</body>
</html>