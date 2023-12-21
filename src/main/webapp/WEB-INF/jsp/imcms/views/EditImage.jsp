<%@ page pageEncoding="UTF-8" %>
<%--
  Page for direct image editing
  Created by Serhii from Ubrainians for Imcode
  Date: 19.02.18
--%>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
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
    <link rel="stylesheet" href="${contextPath}/dist/imcms-imports_files.css">
    <script>
        <jsp:include page="/imcms/js/imcms_config.js.jsp"/>
    </script>
    <script src="${contextPath}/dist/imcms_image_edit_start.js"></script>
</head>
<body>

<c:if test="${StringUtils.isNotBlank(pageContext.request.getParameter(\"width\"))}">
    <c:set var="with" value="width:${pageContext.request.getParameter(\"width\")}px; "/>
</c:if>
<c:if test="${StringUtils.isNotBlank(pageContext.request.getParameter(\"height\"))}">
    <c:set var="height" value="height:${pageContext.request.getParameter(\"height\")}px; "/>
</c:if>
<c:if test="${StringUtils.isNotBlank(pageContext.request.getParameter(\"max-width\"))}">
    <c:set var="maxWidth" value="max-width:${pageContext.request.getParameter(\"max-width\")}px; "/>
</c:if>
<c:if test="${StringUtils.isNotBlank(pageContext.request.getParameter(\"max-height\"))}">
    <c:set var="maxHeight" value="max-height:${pageContext.request.getParameter(\"max-height\")}px; "/>
</c:if>

<input type="hidden" id="return-url" value="${returnUrl}">
<div class="imcms-editor-area imcms-editor-area--image" data-standalone="true" data-doc-id="${targetDocId}" data-lang-code="${langCode}"
     data-style="${with}${height}${maxWidth}${maxHeight}"
     data-label="${label}"
     data-index="${index}"${empty loopEntryRef
        ? '' : ' data-loop-index="'.concat(loopEntryRef.loopIndex).concat('" data-loop-entry-index="')
        .concat(loopEntryRef.loopEntryIndex).concat('"')}>
    <c:if test="${StringUtils.isNotBlank(pageContext.request.getParameter(\"label\"))}">
        <div class="imcms-editor-area__text-label" style="display: none"><%=pageContext.getRequest().getParameter("label")%></div>
    </c:if>
    <div class="imcms-editor-area__content imcms-editor-content"></div>
</div>

</body>
</html>
