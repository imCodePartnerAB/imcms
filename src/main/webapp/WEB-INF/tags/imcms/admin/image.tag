<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ attribute name="no" required="true" %>
<%@ attribute name="document" required="false" %>
<%@ attribute name="style" required="false" %>
<%@ attribute name="pre" required="false" %>
<%@ attribute name="post" required="false" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="currentDocument" type="com.imcode.imcms.api.TextDocument"--%>
<%--@elvariable id="targetDoc" type="com.imcode.imcms.api.TextDocument"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>
<%--@elvariable id="loopEntryRef" type="com.imcode.imcms.mapping.container.LoopEntryRef"--%>
<%--@elvariable id="image" type="com.imcode.imcms.api.Image"--%>

<c:set var="targetDoc" value="${empty document ? currentDocument : (imcms:getDocument(document, pageContext))}"/>

<c:set var="imageContent">
    <c:set var="image" value="${loopEntryRef eq null ? targetDoc.getImage(no) : targetDoc.getLoopImage(loopEntryRef.loopNo, loopEntryRef.entryNo, no)}"/>
    <c:set var="imgPath" value="${image.getSrc(pageContext.request.contextPath)}"/>
    <c:set var="imgPath"
           value="${empty imgPath ? pageContext.request.contextPath.concat('/imcms/eng/images/admin/ico_image.gif') : imgPath}"/>
    ${pre}<img src="${imgPath}"${empty style ? '' : ' style=\"'.concat(style).concat('\"')}/>${post}
</c:set>

<c:if test="${isEditMode}">
    <div class="imcms-editor-area imcms-editor-area--image" data-doc-id="${targetDoc.id}" data-index="${no}">
        <div class="imcms-editor-area__content imcms-editor-content">${imageContent}</div>
        <div class="imcms-editor-area__control-wrap">
            <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--image">
                <div class="imcms-editor-area__control-title">Image Editor</div>
            </div>
        </div>
    </div>
</c:if>
<c:if test="${not isEditMode}">${imageContent}</c:if>
