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
<%--@elvariable id="imageService" type="com.imcode.imcms.domain.service.ImageService"--%>
<%--@elvariable id="image" type="com.imcode.imcms.domain.dto.ImageDTO"--%>
<%--@elvariable id="loopEntryRef" type="com.imcode.imcms.model.LoopEntryRef"--%>
<%--@elvariable id="langCode" type="java.lang.String"--%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Direct Image Editor</title>
    <link rel="stylesheet" href="${contextPath}/css_new/imcms-imports_files.css">
    <script>
        <jsp:include page="/js/imcms/imcms_config.js.jsp"/>
    </script>
    <script src="${contextPath}/js/imcms_new/imcms_main.js" data-name="imcms"
            data-main="${contextPath}/js/imcms_new/edit_starters/imcms_image_edit_start.js"></script>
</head>
<body>

<%--<c:set var="imageContent">--%>
<%--<c:set var="image" value="${imageService.getImage(targetDocId, index, langCode, loopEntryRef)}"/>--%>
<%--<c:set var="imgPath" value="${image.generatedFilePath}"/>--%>
<%--<c:set var="alt" value="${empty image.alternateText ? '' : ' alt=\"'.concat(image.alternateText).concat('\"')}"/>--%>

<%--&lt;%&ndash;<c:choose>&ndash;%&gt;--%>
<%--&lt;%&ndash;<c:when test="${empty image.linkUrl}">&ndash;%&gt;--%>
<%--&lt;%&ndash;<c:set var="href" value=""/>&ndash;%&gt;--%>
<%--&lt;%&ndash;</c:when>&ndash;%&gt;--%>
<%--&lt;%&ndash;<c:otherwise>&ndash;%&gt;--%>
<%--&lt;%&ndash;<c:choose>&ndash;%&gt;--%>
<%--&lt;%&ndash;<c:when test="${fn:startsWith(image.linkUrl, '//') || fn:startsWith(image.linkUrl, 'http')}">&ndash;%&gt;--%>
<%--&lt;%&ndash;<c:set var="href" value="${'href=\"'.concat(image.linkUrl).concat('\"')}"/>&ndash;%&gt;--%>
<%--&lt;%&ndash;</c:when>&ndash;%&gt;--%>
<%--&lt;%&ndash;<c:otherwise>&ndash;%&gt;--%>
<%--&lt;%&ndash;<c:set var="href" value="${'href=\"'.concat('//').concat(image.linkUrl).concat('\"')}"/>&ndash;%&gt;--%>
<%--&lt;%&ndash;</c:otherwise>&ndash;%&gt;--%>
<%--&lt;%&ndash;</c:choose>&ndash;%&gt;--%>
<%--&lt;%&ndash;</c:otherwise>&ndash;%&gt;--%>
<%--&lt;%&ndash;</c:choose>&ndash;%&gt;--%>

<%--&lt;%&ndash;<a ${href}>&ndash;%&gt;--%>
<%--<img src="${empty imgPath ? '' : contextPath}${imgPath}"${alt}/>--%>
<%--&lt;%&ndash;</a>&ndash;%&gt;--%>
<%--</c:set>--%>

<div class="imcms-editor-area imcms-editor-area--image" data-doc-id="${targetDocId}" data-lang-code="${langCode}"
     data-index="${index}"${empty loopEntryRef
        ? '' : ' data-loop-index="'.concat(loopEntryRef.loopIndex).concat('" data-loop-entry-index="')
        .concat(loopEntryRef.loopEntryIndex).concat('"')}>
    <div class="imcms-editor-area__content imcms-editor-content"><%--${imageContent}--%></div>
    <%--<div class="imcms-editor-area__control-wrap">--%>
    <%--<div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--image">--%>
    <%--<div class="imcms-editor-area__control-title">Image Editor</div>--%>
    <%--</div>--%>
    <%--</div>--%>
</div>

<%--<c:set var="textContent" value="${textService.getText(targetDocId, index, langCode, loopEntryRef).text}"/>--%>
<%--<c:set var="loopData">--%>
<%--<c:if test="${loopEntryRef ne null}"> data-loop-entry-ref.loop-entry-index="${loopEntryRef.loopEntryIndex}"--%>
<%--data-loop-entry-ref.loop-index="${loopEntryRef.loopIndex}"</c:if>--%>
<%--</c:set>--%>

<%--<div class="imcms-editor-area imcms-editor-area--text">--%>
<%--<div class="imcms-editor-area__text-toolbar mce-fullscreen-toolbar"></div>--%>
<%--<div class="imcms-editor-content imcms-editor-content--text imcms-mce-fullscreen-inline" data-index="${index}"--%>
<%--data-doc-id="${targetDocId}" data-lang-code="${langCode}" data-type="HTML"${loopData}>${textContent}</div>--%>
<%--</div>--%>

</body>
</html>