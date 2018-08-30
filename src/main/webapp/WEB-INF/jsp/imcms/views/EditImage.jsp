<%@ page pageEncoding="UTF-8" %>
<%--
  Page for direct image editing
  Created by Serhii from Ubrainians for Imcode
  Date: 19.02.18
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="targetDocId" type="int"--%>
<%--@elvariable id="index" type="int"--%>
<%--@elvariable id="loopEntryRef" type="com.imcode.imcms.model.LoopEntryRef"--%>
<%--@elvariable id="langCode" type="java.lang.String"--%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>
<%--@elvariable id="returnUrl" type="java.lang.String"--%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Direct Image Editor</title>
    <script>
        <jsp:include page="/js/imcms/imcms_config.js.jsp"/>
    </script>
    <script src="${contextPath}/dist/imcms_image_edit_start.js"></script>
</head>
<body>

<input type="hidden" id="return-url" value="${returnUrl}">
<div class="imcms-editor-area imcms-editor-area--image" data-doc-id="${targetDocId}" data-lang-code="${langCode}"
     data-index="${index}"${empty loopEntryRef
        ? '' : ' data-loop-index="'.concat(loopEntryRef.loopIndex).concat('" data-loop-entry-index="')
        .concat(loopEntryRef.loopEntryIndex).concat('"')}>
    <div class="imcms-editor-area__content imcms-editor-content"></div>
</div>

</body>
</html>