<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ attribute name="no" required="true" %>
<%@ attribute name="document" required="false" %>
<%@ attribute name="style" required="false" %>

<c:set var="targetDoc" value="${empty document ? currentDocument : (imcms:getDocument(document, pageContext))}"/>

<c:set var="imageContent">
    <c:set var="imgPath" value="${targetDoc.getImage(no).getSrc(pageContext.request.contextPath)}"/>
    <c:set var="imgPath"
           value="${empty imgPath ? pageContext.request.contextPath.concat('/imcms/eng/images/admin/ico_image.gif') : imgPath}"/>
    <img src="${imgPath}"${empty style ? '' : ' style=\"'.concat(style).concat('\"')}/>
</c:set>

<c:if test="${isEditMode}">
    <div class="imcms-editor-area imcms-editor-area--image" data-doc-id="${targetDoc.id}" data-image-id="${no}">
        <div class="imcms-editor-area__content imcms-editor-content">${imageContent}</div>
        <div class="imcms-editor-area__control-wrap">
            <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--image">
                <div class="imcms-editor-area__control-title">Image Editor</div>
            </div>
        </div>
    </div>
</c:if>
<c:if test="${not isEditMode}">${imageContent}</c:if>
