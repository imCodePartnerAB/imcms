<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%@ attribute name="no" required="true" %>
<%@ attribute name="document"%>
<%@ attribute name="label"%>
<%@ attribute name="pre"%>
<%@ attribute name="post"%>

<%@ variable name-given="loop" scope="NESTED" variable-class="com.imcode.imcms.api.Loop" %>
<%@ variable name-given="loopNo" scope="NESTED" variable-class="java.lang.Integer" %>
<%@ variable name-given="loopDoc" scope="NESTED" variable-class="imcode.server.document.textdocument.TextDocumentDomainObject" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="currentDocument" type="com.imcode.imcms.api.TextDocument"--%>
<%--@elvariable id="targetDoc" type="com.imcode.imcms.api.TextDocument"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>

<c:set var="targetDoc" value="${empty document ? currentDocument : (imcms:getDocument(document, pageContext))}"/>
<c:set var="loop" value="${targetDoc.getLoop(no)}" scope="request"/>
<c:set var="loopNo" value="${no}" scope="request"/>
<c:set var="loopDoc" value="${targetDoc}" scope="request"/>

<c:if test="${loop.entries.size() gt 0}">
    <c:set var="loopContent">${pre}<jsp:doBody/>${post}</c:set>
</c:if>

<c:remove var="loop"/>
<c:remove var="loopNo"/>
<c:remove var="loopDoc"/>

<c:if test="${isEditMode}">
    <div class="imcms-editor-area imcms-editor-area--loop" data-doc-id="${targetDoc.id}" data-loop-index="${no}">
        <div class="imcms-editor-area__content imcms-editor-content">${loopContent}</div>
        <div class="imcms-editor-area__control-wrap">
            <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--loop">
                <div class="imcms-editor-area__control-title">Loop Editor</div>
            </div>
        </div>
    </div>
</c:if>
<c:if test="${not isEditMode}">${loopContent}</c:if>
