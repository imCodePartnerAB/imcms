<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<spring:message var="title" code="archive.title.previewImage" arguments="${imageId}"/>
<c:url var="imageUrl" value="/web/archive/preview_img">
    <c:param name="id" value="${imageId}"/>
    <c:param name="tmp" value="${temporary}"/>
</c:url>
<img id="image" src="${fn:escapeXml(imageUrl)}" alt="${title}" style="width:100%;height:100%"/>
