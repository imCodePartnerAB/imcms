<%@ tag trimDirectiveWhitespaces="true" %>
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
<%@ attribute name="showlabel" required="false" type="java.lang.Boolean" %>

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
<%--@elvariable id="disableExternal" type="java.lang.Boolean"--%>

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

        <c:set var="classes" value=""/>

        <c:if test="${image.spaceAround.top ne 0}">
            <c:set var="spaceTop" value="img--mt-${image.spaceAround.top} "/>
        </c:if>
        <c:if test="${image.spaceAround.right ne 0}">
            <c:set var="spaceRight" value="img--mr-${image.spaceAround.right} "/>
        </c:if>
        <c:if test="${image.spaceAround.bottom ne 0}">
            <c:set var="spaceBottom" value="img--mb-${image.spaceAround.bottom} "/>
        </c:if>
        <c:if test="${image.spaceAround.left ne 0}">
            <c:set var="spaceLeft" value="img--ml-${image.spaceAround.left} "/>
        </c:if>
        <c:set var="spaces">${spaceTop}${spaceRight}${spaceBottom}${spaceLeft}</c:set>

        <c:set var="alignClass" value=""/>
        <c:if test="${'CENTER' eq image.align}"><c:set var="alignClass" value="imcms-image-align-center "/></c:if>
        <c:if test="${'LEFT' eq image.align}"><c:set var="alignClass" value="imcms-image-align-left "/></c:if>
        <c:if test="${'RIGHT' eq image.align}"><c:set var="alignClass" value="imcms-image-align-right "/></c:if>

        <c:if test="${not empty alignClass or not empty spaces}">
            <c:set var="classes" value=" class=\"${alignClass}${spaces}\""/>
        </c:if>

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
            <img src="${empty imgPath ? '' : contextPath}${imgPath}"${classes}${alt}/>
        </a>
        ${post}
    </c:set>

    <c:choose>
        <c:when test="${isEditMode && editOptions.editImage}">
            <c:set var="isInternal" value="${disableExternal or document eq null or document eq currentDocument.id}"/>
            <c:set value="${isInternal ? (language.equals('en') ? 'Image Editor' : 'Redigera Bild') : 'This image is edited on page '.concat(document)}"
                   var="editLabel"/>
            <c:set var="externalPart"
                   value="${(isInternal) ? '' : (' data-external=\"'.concat(document).concat('\"'))}"/>
            <c:set var="loopPart" value="${empty loopEntryRef ? ''
                    : ' data-loop-index=\"'.concat(loopEntryRef.loopIndex).concat('\" data-loop-entry-index=\"')
            .concat(loopEntryRef.loopEntryIndex).concat('\"')}"/>

            <c:if test="${not empty style}"><c:set var="style" value=" data-style=\"${style}\""/></c:if>

            <c:set var="isShowlabel" value="${empty showlabel ? 'true' : showlabel}"/>

            <div class="imcms-editor-area imcms-editor-area--image" data-doc-id="${targetDocId}"${externalPart}${style}
                 data-lang-code="${language}" data-index="${no}"${loopPart}>
                <c:if test="${not empty label && isShowlabel}">
                    <div class="imcms-editor-area__text-label">${label}</div>
                </c:if>
                <div class="imcms-editor-area__content imcms-editor-content">${imageContent}</div>
                <div class="imcms-editor-area__control-wrap">
                    <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--image">
                        <div class="imcms-editor-area__control-title">${editLabel}</div>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty imgPath}">${imageContent}</c:if>
        </c:otherwise>
    </c:choose>
</c:if>