<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ attribute name="no" required="true" %>
<%@ attribute name="index" required="false" %>
<%@ attribute name="document" required="false" %>
<%@ attribute name="placeholder" required="false" %>
<%@ attribute name="label" required="false" %>
<%@ attribute name="rows" required="false" %>
<%@ attribute name="mode" required="false" %>
<%@ attribute name="formats" required="false" %>
<%@ attribute name="pre" required="false" %>
<%@ attribute name="post" required="false" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="currentDocument" type="com.imcode.imcms.api.TextDocument"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>
<%--@elvariable id="isPreviewMode" type="boolean"--%>
<%--@elvariable id="textService" type="com.imcode.imcms.domain.service.TextService"--%>
<%--@elvariable id="loopEntryRef" type="com.imcode.imcms.model.LoopEntryRef"--%>
<%--@elvariable id="textField" type="com.imcode.imcms.model.Text"--%>
<%--@elvariable id="language" type="java.lang.String"--%>

<c:if test="${empty index}">
    <c:set var="index" value="${no}"/><%-- old attribute "no" support --%>
</c:if>

<c:set var="targetDocId" value="${empty document ? currentDocument.id : document}"/>

<c:set var="textField" value="${isEditMode or isPreviewMode
     ? textService.getText(targetDocId, index, language, loopEntryRef)
     : textService.getPublicText(targetDocId, index, language, loopEntryRef)}"/>

<c:set var="textContent" value="${pre}${textField.text}${post}"/>

<c:set var="loopData">
    <c:if test="${loopEntryRef ne null}"> data-loop-entry-ref.loop-entry-index="${loopEntryRef.loopEntryIndex}"
        data-loop-entry-ref.loop-index="${loopEntryRef.loopIndex}"</c:if>
</c:set>

<c:if test="${isEditMode}">
    <div class="imcms-editor-area imcms-editor-area--text">
        <c:if test="${not empty label}">
            <div class="imcms-editor-area__text-label">${label}</div>
        </c:if>
        <div class="imcms-editor-area__text-toolbar"></div>
        <div class="imcms-editor-content imcms-editor-content--text" data-index="${index}" data-doc-id="${targetDocId}"
             data-lang-code="${language}" data-type="HTML"${loopData}>${textContent}</div>
        <div class="imcms-editor-area__control-wrap">
            <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--text">
                <div class="imcms-editor-area__control-title">Text Editor</div>
            </div>
        </div>
    </div>
</c:if>
<c:if test="${not isEditMode}">${textContent}</c:if>
