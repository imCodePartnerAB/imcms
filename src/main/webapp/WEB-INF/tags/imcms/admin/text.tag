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

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="currentDocument" type="com.imcode.imcms.api.TextDocument"--%>
<%--@elvariable id="targetDoc" type="imcode.server.document.textdocument.TextDocumentDomainObject"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>
<%--@elvariable id="loopEntryRef" type="com.imcode.imcms.domain.dto.LoopEntryRefDTO"--%>
<%--@elvariable id="textField" type="com.imcode.imcms.api.TextDocument.TextField"--%>

<c:set var="targetDoc"
       value="${empty document ? currentDocument : (imcms:getTextDocumentDomainObject(document, pageContext))}"/>
<c:set var="textField"
       value="${loopEntryRef eq null ? targetDoc.getText(no) : targetDoc.getText(loopEntryRef)}"/>
<c:set var="textContent" value="${pre}${textField.text}${post}"/>

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
