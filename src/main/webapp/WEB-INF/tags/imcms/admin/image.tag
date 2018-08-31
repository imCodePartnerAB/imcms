${"<!--"}
<%@ tag trimDirectiveWhitespaces="true" %>
${"-->"}
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ attribute name="no" required="false" type="java.lang.Object" %><%-- old index name --%>
<%@ attribute name="index" required="false" %>
<%@ attribute name="document" required="false" %>
<%@ attribute name="style" required="false" %>
<%@ attribute name="pre" required="false" %>
<%@ attribute name="post" required="false" %>
<%@ attribute name="label" required="false" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="currentDocument" type="imcode.server.document.textdocument.TextDocumentDomainObject"--%>
<%--@elvariable id="imageService" type="com.imcode.imcms.domain.service.ImageService"--%>
<%--@elvariable id="image" type="com.imcode.imcms.domain.dto.ImageDTO"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>
<%--@elvariable id="isPreviewMode" type="boolean"--%>
<%--@elvariable id="loopEntryRef" type="com.imcode.imcms.model.LoopEntryRef"--%>
<%--@elvariable id="language" type="java.lang.String"--%>
<%--@elvariable id="editOptions" type="com.imcode.imcms.domain.dto.RestrictedPermissionDTO"--%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>
<%--@elvariable id="isDocNew" type="boolean"--%>

<c:if test="${!isDocNew || editOptions.editImage}">
    <c:if test="${empty index}">
        <c:set var="index" value="${no}"/><%-- old attribute "no" support --%>
    </c:if>

    <c:set var="targetDocId" value="${empty document ? currentDocument.id : document}"/>

    <c:set var="imageContent">
        <c:set var="image" value="${isEditMode || isPreviewMode
     ? imageService.getImage(targetDocId, index, language, loopEntryRef)
     : imageService.getPublicImage(targetDocId, index, language, loopEntryRef)}"/>
        <c:set var="imgPath" value="${image.generatedFilePath}"/>

        <c:set var="spaceTop">margin-top: ${image.spaceAround.top}px;</c:set>
        <c:set var="spaceRight">margin-right: ${image.spaceAround.right}px;</c:set>
        <c:set var="spaceBottom">margin-bottom: ${image.spaceAround.bottom}px;</c:set>
        <c:set var="spaceLeft">margin-left: ${image.spaceAround.left}px;</c:set>

        <c:set var="spaceStyle">${spaceTop} ${spaceRight} ${spaceBottom} ${spaceLeft}</c:set>

        <c:set var="alignClass" value=""/>
        <c:if test="${'CENTER' eq image.align}"><c:set var="alignClass" value="imcms-image-align-center"/></c:if>
        <c:if test="${'LEFT' eq image.align}"><c:set var="alignClass" value="imcms-image-align-left"/></c:if>
        <c:if test="${'RIGHT' eq image.align}"><c:set var="alignClass" value="imcms-image-align-right"/></c:if>

        <c:if test="${not empty alignClass}"><c:set var="alignClass" value=" class=\"${alignClass}\""/></c:if>

        <c:set var="style" value=" style=\"${not empty style?style.concat(' '):''}${spaceStyle}\""/>
        <c:set var="alt"
               value="${empty image.alternateText ? '' : ' alt=\"'.concat(image.alternateText).concat('\"')}"/>

        <c:choose>
            <c:when test="${empty image.linkUrl}">
                <c:set var="href" value=""/>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${fn:startsWith(image.linkUrl, '//') || fn:startsWith(image.linkUrl, 'http')}">
                        <c:set var="href" value="${' href=\"'.concat(image.linkUrl).concat('\"')}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="href" value="${' href=\"'.concat('//').concat(image.linkUrl).concat('\"')}"/>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>

        ${pre}
        <a${href}>
            <img src="${empty imgPath ? '' : contextPath}${imgPath}"${alignClass}${style}${alt}/>
        </a>
        ${post}
    </c:set>

    <c:choose>
        <c:when test="${isEditMode && editOptions.editImage}">
            <c:set var="isInternal" value="${document eq null or document eq currentDocument.id}"/>
            <c:set value="${isInternal ? 'Image Editor' : 'This image is edited on page '.concat(document)}"
                   var="label"/>

            <div class="imcms-editor-area imcms-editor-area--image" data-doc-id="${targetDocId}"
                 data-lang-code="${language}"${(isInternal) ? '' : (' data-external=\"'.concat(document).concat('\" '))}
                 data-index="${no}"${empty loopEntryRef ? ''
                    : ' data-loop-index="'.concat(loopEntryRef.loopIndex).concat('" data-loop-entry-index="')
                    .concat(loopEntryRef.loopEntryIndex).concat('"')}>
                <div class="imcms-editor-area__content imcms-editor-content">${imageContent}</div>
                <div class="imcms-editor-area__control-wrap">
                    <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--image">
                        <div class="imcms-editor-area__control-title">${label}</div>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty imgPath}">${imageContent}</c:if>
        </c:otherwise>
    </c:choose>
</c:if>