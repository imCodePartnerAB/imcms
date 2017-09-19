<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms_new" %>
<%@ attribute name="no" required="true" %>
<%@ attribute name="document" required="false" %>

<c:set var="document" value="${empty document ? currentDocId : document}"/>

<div class="imcms-editor-area imcms-editor-area--image" data-doc-id="${document}" data-image-id="${no}">
    <div class="imcms-editor-area__content imcms-editor-content">
        <%--<img src="/${pageContext.request.contextPath}${images}"/>--%>
    </div>
    <div class="imcms-editor-area__control-wrap">
        <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--image">
            <div class="imcms-editor-area__control-title">Image Editor</div>
        </div>
    </div>
</div>
