<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ attribute name="no" required="true" %>
<%@ attribute name="document" required="false" %>
<%@ attribute name="placeholder" required="false" %>
<%@ attribute name="label" required="false" %>
<%@ attribute name="rows" required="false" %>
<%@ attribute name="mode" required="false" %>
<%@ attribute name="formats" required="false" %>
<%@ attribute name="pre" required="false" %>
<%@ attribute name="post" required="false" %>

<c:set var="targetDoc" value="${empty document ? currentDocument : (imcms:getDocument(document, pageContext))}"/>
<c:set var="textField" value="${loopEntryRef eq null ? targetDoc.getTextField(no) : targetDoc.getLoopTextField(loopEntryRef.loopNo, loopEntryRef.entryNo, no)}"/>
<c:set var="textContent" value="${textField.text}"/>

<c:if test="${isEditMode}">
    <div class="imcms-editor-area imcms-editor-area--text">
        <c:if test="${not empty label}">
            <div class="imcms-editor-area__text-label">${label}</div>
        </c:if>
        <div class="imcms-editor-area__text-toolbar"></div>
        <div class="imcms-editor-area__content-wrap">
            <div class="imcms-editor-content imcms-editor-content--text">${textContent}</div>
        </div>
        <div class="imcms-editor-area__control-wrap">
            <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--text">
                <div class="imcms-editor-area__control-title">Text Editor</div>
            </div>
        </div>
    </div>
</c:if>
<c:if test="${not isEditMode}">${textContent}</c:if>
